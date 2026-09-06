package io.aduhtkjm.mekanismheated.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.aduhtkjm.mekanismheated.Mod;
import java.util.List;
import java.util.Objects;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.vanilla_input.SingleFluidRecipeInput;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Contract;

/**
 * Common base for the fractionation tower's recipes.
 *
 * <p>A fractionation recipe produces one or more fluid outputs, each targeting a specific output bank of the tower, and
 * runs within a temperature window: it cannot process below {@link #getMinTemperature()} Kelvin, reaches nominal speed at
 * {@link #getBaseTemperature()} Kelvin, and stops processing above {@link #getMaxTemperature()} Kelvin.</p>
 *
 * <p>Two concrete forms exist: {@link BasicFractionationRecipe}, which consumes a matching fluid from the tower's feed
 * sump, and {@link PassiveFractionationRecipe}, which generates its outputs from the environment and only runs while the
 * sump is empty.</p>
 */
public abstract class FractionationRecipe extends MekanismRecipe<SingleFluidRecipeInput> {

    /** Maximum number of banks a fractionation tower can have (interior layers of an 18-high tower minus the sump). */
    public static final int MAX_BANKS = 15;

    private static final Holder<Item> THERMAL_FRACTIONATION_CONTROLLER = DeferredHolder.create(Registries.ITEM,
          ResourceLocation.fromNamespaceAndPath(Mod.MODID, "thermal_fractionation_controller"));

    /**
     * @param bank Zero-based index of the output bank, counted from the bottom of the tower.
     * @param stack The fluid to deposit into that bank.
     */
    public record BankOutput(int bank, FluidStack stack) {

        public static final Codec<BankOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
              Codec.intRange(0, MAX_BANKS - 1).fieldOf("bank").forGetter(BankOutput::bank),
              FluidStack.CODEC.fieldOf("fluid").forGetter(BankOutput::stack)
        ).apply(instance, BankOutput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BankOutput> STREAM_CODEC = StreamCodec.composite(
              ByteBufCodecs.VAR_INT, BankOutput::bank,
              FluidStack.STREAM_CODEC, BankOutput::stack,
              BankOutput::new);
    }

    /**
     * For JEI/display purposes, the outputs to display.
     *
     * @return Representation of the outputs, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<BankOutput> getOutputs();

    /**
     * Minimum temperature in Kelvin; below it the recipe cannot process at all.
     */
    public abstract double getMinTemperature();

    /**
     * Maximum temperature in Kelvin; above it the recipe stops processing.
     */
    public abstract double getMaxTemperature();

    /**
     * Temperature in Kelvin at which the recipe processes one operation per tick. Between min and base the speed scales
     * linearly from zero to one; above base it keeps scaling proportionally.
     */
    public abstract double getBaseTemperature();

    /**
     * Validates the parts shared by every fractionation recipe: at least one non-empty output with a positive amount and
     * no duplicate bank indices, a minimum temperature above zero, and a base temperature of at least the minimum.
     */
    public static void validate(List<BankOutput> outputs, double minTemperature, double baseTemperature) {
        Objects.requireNonNull(outputs, "Outputs cannot be null.");
        if (outputs.isEmpty()) {
            throw new IllegalArgumentException("Fractionation recipes must have at least one output.");
        }
        boolean[] seenBanks = new boolean[MAX_BANKS];
        for (BankOutput output : outputs) {
            Objects.requireNonNull(output, "Output cannot be null.");
            Objects.requireNonNull(output.stack(), "Output fluid cannot be null.");
            if (output.stack().isEmpty() || output.stack().getAmount() <= 0) {
                throw new IllegalArgumentException("Output fluid amount must be positive.");
            }
            if (output.bank() < 0 || output.bank() >= MAX_BANKS) {
                throw new IllegalArgumentException("Output bank index must be between 0 and " + (MAX_BANKS - 1) + ", got " + output.bank() + ".");
            }
            if (seenBanks[output.bank()]) {
                throw new IllegalArgumentException("Duplicate output bank index " + output.bank() + ".");
            }
            seenBanks[output.bank()] = true;
        }
        if (minTemperature <= 0) {
            throw new IllegalArgumentException("Minimum temperature must be greater than zero.");
        }
        if (baseTemperature < minTemperature) {
            throw new IllegalArgumentException("Base temperature must be at least the minimum temperature.");
        }
    }

    @Override
    public boolean isIncomplete() {
        return getOutputs().isEmpty();
    }

    @Contract(value = "_ -> new", pure = true)
    public List<FluidStack> getOutputDefinition() {
        return getOutputs().stream().map(BankOutput::stack).toList();
    }

    @Override
    public abstract RecipeType<?> getType();

    @Override
    public String getGroup() {
        return "fractionating";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(THERMAL_FRACTIONATION_CONTROLLER);
    }
}
