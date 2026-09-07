package io.aduhtkjm.mekanismheated.tile.multiblock;

import io.aduhtkjm.mekanismheated.Config;
import io.aduhtkjm.mekanismheated.recipe.HeatSmelterRecipe;
import io.aduhtkjm.mekanismheated.tile.HeatSmelterLogic;
import io.aduhtkjm.mekanismheated.tile.TileEntityHeatSmelter;
import io.aduhtkjm.mekanismheated.tank.MultiFluidTank;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.lib.multiblock.Structure;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Shared "brain" for a formed Large Heat Smelter. Holds the (fixed) item slots and the volume-scaled fluid tank +
 * heat capacitor, and drives the fuel → heat → smelting / alloying logic each tick from the master block.
 *
 * <p>Because the smelting recipe is temperature-gated via the tile-bound
 * {@code HeatSmelterRecipeCacheLookupMonitor}, this data replicates that processing in a self-contained loop using
 * {@link HeatSmelterLogic} rather than the {@code RecipeCacheLookupMonitor}. The single-block and multiblock paths
 * therefore share identical math.</p>
 */
public class LargeHeatSmelterData extends MultiblockData {

    private final BasicInventorySlot inputSlot;
    private final BasicInventorySlot outputSlot;
    private final BasicInventorySlot fuelSlot;
    private final MultiFluidTank fluidTank;
    private final VariableHeatCapacitor heatCapacitor;

    private double biomeAmbientTemp;
    private double progress;
    private boolean processing;
    private boolean fluidChanged;
    @Nullable
    private HeatSmelterLogic.AlloyConfig lastAlloy;
    private double lastEnvironmentLoss;
    private double lastTransferLoss;

    public LargeHeatSmelterData(BlockEntity tile) {
        super(tile);
        biomeAmbientTemp = HeatAPI.AMBIENT_TEMP;
        IContentsListener listener = createSaveAndComparator();
        //Use the same GUI positions as the standalone smelter so the reused GUI lays out identically
        inputSlot = BasicInventorySlot.at(ConstantPredicates.alwaysTrue(), listener, 64, 17);
        outputSlot = BasicInventorySlot.at(ConstantPredicates.alwaysTrue(), listener, 116, 35);
        fuelSlot = BasicInventorySlot.at(ConstantPredicates.alwaysTrue(), listener, 64, 55);
        IContentsListener fluidListener = () -> {
            if (!isRemote()) {
                fluidChanged = true;
            }
            listener.onContentsChanged();
        };
        //The initial capacities are for a single block; they are scaled up by {@link #configure(int)} once the
        // structure forms (called from the validator's postcheck on the server and from readUpdateTag on the client).
        fluidTank = MultiFluidTank.output(TileEntityHeatSmelter.MAX_FLUID, fluidListener);
        //The heat capacitor must be registered in the heat capacitors list, otherwise the multiblock exposes no
        // heat handler at all and nothing can add or receive heat
        heatCapacitor = VariableHeatCapacitor.create(Config.HeatSmelter.HEAT_CAPACITY.get(), () -> biomeAmbientTemp, listener);
        inventorySlots.add(inputSlot);
        inventorySlots.add(outputSlot);
        inventorySlots.add(fuelSlot);
        fluidTanks.addAll(fluidTank.getSlots());
        heatCapacitors.add(heatCapacitor);
    }

    /**
     * Scales the fluid tank and heat capacitor capacities by the structure's volume. Safe to call multiple times
     * (idempotent) and called on both the server (during formation) and the client (on update-tag load).
     *
     * @param volume the volume (L*W*H) of the formed structure
     */
    public void configure(int volume) {
        fluidTank.setTotalCapacity(TileEntityHeatSmelter.MAX_FLUID * volume);
        heatCapacitor.setHeatCapacity(Config.HeatSmelter.HEAT_CAPACITY.get() * volume, true);
    }

    public BasicInventorySlot getInputSlot() {
        return inputSlot;
    }

    public BasicInventorySlot getOutputSlot() {
        return outputSlot;
    }

    public BasicInventorySlot getFuelSlot() {
        return fuelSlot;
    }

    public MultiFluidTank getFluidTank() {
        return fluidTank;
    }

    public VariableHeatCapacitor getHeatCapacitor() {
        return heatCapacitor;
    }

    public double getTemperature() {
        return heatCapacitor.getTemperature();
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getLastTransferLoss() {
        return lastTransferLoss;
    }

    public void setLastTransferLoss(double lastTransferLoss) {
        this.lastTransferLoss = lastTransferLoss;
    }

    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    public void setLastEnvironmentLoss(double lastEnvironmentLoss) {
        this.lastEnvironmentLoss = lastEnvironmentLoss;
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        boolean burning = burnFuel();
        HeatTransfer transfer = simulate();
        lastEnvironmentLoss = transfer.environmentTransfer();
        lastTransferLoss = transfer.adjacentTransfer();
        updateHeatCapacitors(null);
        needsPacket |= processRecipes(world);
        tryAlloying();
        if (burning) {
            needsPacket = true;
        }
        return needsPacket;
    }

    private boolean burnFuel() {
        Level level = getLevel();
        if (level == null || level.isClientSide) {
            return false;
        }
        int consumed = HeatSmelterLogic.burnFuel(level, heatCapacitor.getTemperature(), heatCapacitor, fuelSlot.getStack());
        if (consumed > 0) {
            fuelSlot.extractItem(consumed, Action.EXECUTE, AutomationType.INTERNAL);
            return true;
        }
        return false;
    }

    private boolean processRecipes(Level world) {
        boolean wasProcessing = processing;
        if (world == null || world.isClientSide) {
            return wasProcessing != (processing = false);
        }
        ItemStack input = inputSlot.getStack();
        double temperature = heatCapacitor.getTemperature();
        HeatSmelterRecipe recipe = HeatSmelterLogic.findRecipeFor(world, input, temperature, true);
        double speed = HeatSmelterLogic.speedFactor(temperature);
        if (recipe == null || speed <= 0 || !canOutput(recipe, input)) {
            if (recipe == null) {
                //No valid input; discard any accumulated progress
                progress = 0;
            }
            return wasProcessing != (processing = false);
        }
        progress += speed;
        int required = Config.HeatSmelter.BASE_SPEED.get();
        int performed = 0;
        int maxOperations = (int) (progress / required);
        while (performed < maxOperations && operate(recipe, input)) {
            performed++;
            progress -= required;
        }
        processing = performed > 0;
        return wasProcessing != processing;
    }

    private boolean canOutput(HeatSmelterRecipe recipe, ItemStack input) {
        if (input.isEmpty()) {
            return false;
        }
        if (recipe.isItemOutput()) {
            return outputSlot.insertItem(recipe.getItemOutput(input), Action.SIMULATE, AutomationType.INTERNAL).isEmpty();
        }
        return fluidTank.insert(recipe.getFluidOutput(input), Action.SIMULATE, AutomationType.INTERNAL).isEmpty();
    }

    private boolean operate(HeatSmelterRecipe recipe, ItemStack input) {
        if (input.isEmpty() || !recipe.test(input)) {
            return false;
        }
        if (!canOutput(recipe, input)) {
            return false;
        }
        inputSlot.extractItem(1, Action.EXECUTE, AutomationType.INTERNAL);
        if (recipe.isItemOutput()) {
            outputSlot.insertItem(recipe.getItemOutput(input), Action.EXECUTE, AutomationType.INTERNAL);
        } else {
            fluidTank.insert(recipe.getFluidOutput(input), Action.EXECUTE, AutomationType.INTERNAL);
        }
        return true;
    }

    private void tryAlloying() {
        if (!fluidChanged) {
            return;
        }
        Level level = getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        fluidChanged = false;
        lastAlloy = HeatSmelterLogic.tryAlloyOnce(level, fluidTank, lastAlloy);
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        biomeAmbientTemp = calculateAverageAmbientTemperature(world);
        //Absorb each member block's standalone containers into the shared brain, then empty them so the per-block
        // containers become dormant. Each member's heat already includes its own ambient baseline, so summing the
        // member heat directly yields the correct combined temperature for the (now volume-scaled) shared capacitor.
        double totalHeat = 0;
        for (BlockPos pos : locations) {
            BlockEntity tile = WorldUtils.getTileEntity(world, pos);
            if (tile instanceof TileEntityHeatSmelter smelter) {
                inputSlot.insertItem(smelter.getInputSlot().getStack().copy(), Action.EXECUTE, AutomationType.INTERNAL);
                outputSlot.insertItem(smelter.getOutputSlot().getStack().copy(), Action.EXECUTE, AutomationType.INTERNAL);
                fuelSlot.insertItem(smelter.getFuelSlot().getStack().copy(), Action.EXECUTE, AutomationType.INTERNAL);
                smelter.getInputSlot().setStackUnchecked(ItemStack.EMPTY);
                smelter.getOutputSlot().setStackUnchecked(ItemStack.EMPTY);
                smelter.getFuelSlot().setStackUnchecked(ItemStack.EMPTY);
                for (FluidStack fluid : smelter.getFluidTank().getFluids()) {
                    fluidTank.insert(fluid.copy(), Action.EXECUTE, AutomationType.INTERNAL);
                }
                smelter.getFluidTank().setEmpty();
                totalHeat += smelter.getHeatCapacitor().getHeat();
            }
        }
        heatCapacitor.setHeat(totalHeat);
    }

    @Override
    public void readUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.readUpdateTag(tag, provider);
        configure(getVolume());
        NBTUtils.setCompoundIfPresent(tag, "smelter_fluid", nbt -> fluidTank.deserializeNBT(provider, nbt));
        NBTUtils.setCompoundIfPresent(tag, "smelter_heat", nbt -> heatCapacitor.deserializeNBT(provider, nbt));
        inputSlot.deserializeNBT(provider, tag.getCompound("smelter_input"));
        outputSlot.deserializeNBT(provider, tag.getCompound("smelter_output"));
        fuelSlot.deserializeNBT(provider, tag.getCompound("smelter_fuel"));
    }

    @Override
    public void writeUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.writeUpdateTag(tag, provider);
        tag.put("smelter_fluid", fluidTank.serializeNBT(provider));
        tag.put("smelter_heat", heatCapacitor.serializeNBT(provider));
        CompoundTag inputTag = inputSlot.serializeNBT(provider);
        if (!inputTag.isEmpty()) {
            tag.put("smelter_input", inputTag);
        }
        CompoundTag outputTag = outputSlot.serializeNBT(provider);
        if (!outputTag.isEmpty()) {
            tag.put("smelter_output", outputTag);
        }
        CompoundTag fuelTag = fuelSlot.serializeNBT(provider);
        if (!fuelTag.isEmpty()) {
            tag.put("smelter_fuel", fuelTag);
        }
    }

    @Override
    public void remove(Level world, Structure oldStructure) {
        super.remove(world, oldStructure);
        lastAlloy = null;
        progress = 0;
        processing = false;
        fluidChanged = false;
    }
}
