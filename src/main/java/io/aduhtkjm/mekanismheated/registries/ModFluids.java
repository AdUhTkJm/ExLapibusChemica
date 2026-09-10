package io.aduhtkjm.mekanismheated.registries;

import io.aduhtkjm.mekanismheated.Mod;
import io.aduhtkjm.mekanismheated.content.unstablelava.UnstableLavaFluid;
import io.aduhtkjm.mekanismheated.item.ItemChemicalFormulaBucket;
import java.util.function.UnaryOperator;

import io.aduhtkjm.mekanismheated.item.ItemFe2O3Dust;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidDeferredRegister.FluidTypeRenderProperties;
import mekanism.common.registration.impl.FluidDeferredRegister.MekanismFluidType;
import mekanism.common.registration.impl.FluidRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ModFluids {

    private ModFluids() {
    }

    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(Mod.MODID);

    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, BucketItem> WOOD_TAR =
        FLUIDS.register("wood_tar", renderProperties -> renderProperties.tint(0xFF513721));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, BucketItem> ASPHALT =
        FLUIDS.register("asphalt", renderProperties -> renderProperties.tint(0xFF234623));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> METHANOL =
        FLUIDS.register("methanol", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "CH\u2083OH"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFCDCDB2));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> ACETIC_ACID =
        FLUIDS.register("acetic_acid", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "CH\u2083COOH"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFEEF0C7));

    // Liquid from gases
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> LIQUID_NITROGEN =
        FLUIDS.register("liquid_nitrogen", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "N\u2082"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFF85CBEE));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, BucketItem> LIQUID_AIR_REMNANT =
        FLUIDS.register("liquid_air_remnant", renderProperties -> renderProperties.tint(0xFFF7F7F7));

    // Molten fluids
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> MOLTEN_IRON =
        FLUIDS.register("molten_iron", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "Fe"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFCECECE));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> MOLTEN_COPPER =
        FLUIDS.register("molten_copper", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "Cu"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFEBAD41));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> MOLTEN_TIN =
        FLUIDS.register("molten_tin", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "Sn"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFC9CBDC));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> MOLTEN_BRONZE =
        FLUIDS.register("molten_bronze", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "Cu-Sn"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFF9A648));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> MOLTEN_OSMIUM =
        FLUIDS.register("molten_osmium", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "Os"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFC8CCF3));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> MOLTEN_THERMOENERGETIC_ALLOY =
        FLUIDS.register("molten_thermoenergetic_alloy", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "Fe-Cu"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(0xFFFC7E11));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, BucketItem> MOLTEN_INFUSED_ALLOY =
        FLUIDS.register("molten_infused_alloy", renderProperties -> renderProperties.tint(0xFFE64141));
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, BucketItem> MOLTEN_REINFORCED_ALLOY =
        FLUIDS.register("molten_reinforced_alloy", renderProperties -> renderProperties.tint(0xFF58D7F0));

    // Slurry
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, ItemChemicalFormulaBucket> SLURRY_FE2O3 =
        FLUIDS.register("slurry_fe2o3", (fluid, properties) -> new ItemChemicalFormulaBucket(fluid, properties, "Fe\u2082O\u2083"),
            UnaryOperator.identity(), renderProperties -> renderProperties.tint(ItemFe2O3Dust.TINT));

    //Unstable lava
    //Registered separately from the fluids above because Mekanism's FluidDeferredRegister only ever creates plain
    // BaseFlowingFluid instances with water-like flow settings, while unstable lava needs vanilla lava's flow and
    // fire-spreading behaviour (see UnstableLavaFluid).
    public static final DeferredRegister<FluidType> UNSTABLE_LAVA_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Mod.MODID);
    public static final DeferredRegister<Fluid> UNSTABLE_LAVA_FLUID_REGISTER = DeferredRegister.create(Registries.FLUID, Mod.MODID);
    public static final DeferredRegister<Block> UNSTABLE_LAVA_BLOCK_REGISTER = DeferredRegister.create(Registries.BLOCK, Mod.MODID);
    public static final DeferredRegister<Item> UNSTABLE_LAVA_ITEM_REGISTER = DeferredRegister.create(Registries.ITEM, Mod.MODID);

    /** Vanilla's still lava texture, reused (tinted) by unstable lava. */
    public static final ResourceLocation UNSTABLE_LAVA_STILL_TEXTURE = ResourceLocation.withDefaultNamespace("block/lava_still");
    /** Vanilla's flowing lava texture, reused (tinted) by unstable lava. */
    public static final ResourceLocation UNSTABLE_LAVA_FLOWING_TEXTURE = ResourceLocation.withDefaultNamespace("block/lava_flow");
    /** Orange tint applied on top of the lava textures. */
    public static final int UNSTABLE_LAVA_TINT = 0xFFFF8C00;

    public static final DeferredHolder<FluidType, MekanismFluidType> UNSTABLE_LAVA_TYPE = UNSTABLE_LAVA_TYPES.register("unstable_lava", () ->
        new MekanismFluidType(FluidDeferredRegister.getMekBaseBuilder()
              .descriptionId("block.mekanismheated.unstable_lava")
              //Copied from NeoForge's vanilla lava fluid type so entities move through it exactly like lava.
              .canSwim(false)
              .canDrown(false)
              .pathType(PathType.LAVA)
              .adjacentPathType(null)
              .motionScale(0.007D)
              .lightLevel(15)
              .density(3000)
              .viscosity(6000)
              .temperature(1300)
              .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
              .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA),
              FluidTypeRenderProperties.builder()
                    .texture(UNSTABLE_LAVA_STILL_TEXTURE, UNSTABLE_LAVA_FLOWING_TEXTURE)
                    .tint(UNSTABLE_LAVA_TINT)));

    public static final DeferredHolder<Fluid, UnstableLavaFluid.Source> UNSTABLE_LAVA =
        UNSTABLE_LAVA_FLUID_REGISTER.register("unstable_lava", UnstableLavaFluid.Source::new);
    public static final DeferredHolder<Fluid, UnstableLavaFluid.Flowing> FLOWING_UNSTABLE_LAVA =
        UNSTABLE_LAVA_FLUID_REGISTER.register("flowing_unstable_lava", UnstableLavaFluid.Flowing::new);

    public static final DeferredHolder<Block, LiquidBlock> UNSTABLE_LAVA_BLOCK = UNSTABLE_LAVA_BLOCK_REGISTER.register("unstable_lava", () ->
        new LiquidBlock(UNSTABLE_LAVA.get(), BlockBehaviour.Properties.of()
              //Same block properties as vanilla lava, including random ticks so LavaFluid#randomTick can spread fire.
              .mapColor(MapColor.FIRE)
              .replaceable()
              .noCollission()
              .randomTicks()
              .strength(100.0F)
              .lightLevel(state -> 15)
              .pushReaction(PushReaction.DESTROY)
              .noLootTable()
              .liquid()
              .sound(SoundType.EMPTY)));

    public static final DeferredHolder<Item, BucketItem> UNSTABLE_LAVA_BUCKET = UNSTABLE_LAVA_ITEM_REGISTER.register("unstable_lava_bucket", () ->
        new BucketItem(UNSTABLE_LAVA.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

    /**
     * Registers the dispense behaviour for the unstable lava bucket. {@link FluidDeferredRegister} does this for the
     * fluids it manages, but unstable lava is registered by hand, so it needs its own copy of vanilla's bucket
     * dispensing logic.
     */
    public static void registerUnstableLavaDispenserBehavior() {
        DispenserBlock.registerBehavior(UNSTABLE_LAVA_BUCKET.get(), new DefaultDispenseItemBehavior() {
            @NotNull
            @Override
            public ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
                Level level = source.level();
                DispensibleContainerItem bucket = (DispensibleContainerItem) stack.getItem();
                BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                if (bucket.emptyContents(null, level, pos, null, stack)) {
                    bucket.checkExtraContent(null, level, stack, pos);
                    return new ItemStack(Items.BUCKET);
                }
                return super.execute(source, stack);
            }
        });
    }
}
