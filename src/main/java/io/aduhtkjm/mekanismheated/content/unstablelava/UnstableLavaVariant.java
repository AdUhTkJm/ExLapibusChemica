package io.aduhtkjm.mekanismheated.content.unstablelava;

import io.aduhtkjm.mekanismheated.Mod;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidDeferredRegister.FluidTypeRenderProperties;
import mekanism.common.registration.impl.FluidDeferredRegister.MekanismFluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

/**
 * A single flavour of unstable lava: plain unstable lava, or unstable lava with a metal melted into it.
 * <p>
 * Unstable lava cannot go through Mekanism's {@link FluidDeferredRegister} because that only ever creates plain
 * {@code BaseFlowingFluid} instances with water-like flow settings, while unstable lava needs vanilla lava's flow
 * and fire-spreading behaviour (see {@link UnstableLavaFluid}). Instead, every variant registers its own fluid
 * type, source/flowing fluids, liquid block and bucket against the shared {@link Registers}, and bundles the
 * resulting holders here so the rest of the mod can reach them without knowing about the individual registries.
 * <p>
 * Adding a variant therefore only means one call to {@link #register} (plus the matching assets — blockstate,
 * block and bucket models, lang entries — and a line in the {@code minecraft:lava} fluid tag).
 */
public final class UnstableLavaVariant {

    /** Vanilla's still lava texture, reused (and tinted by {@link #tint()}) by every unstable lava variant. */
    public static final ResourceLocation STILL_TEXTURE = ResourceLocation.withDefaultNamespace("block/lava_still");
    /** Vanilla's flowing lava texture, reused (and tinted by {@link #tint()}) by every unstable lava variant. */
    public static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.withDefaultNamespace("block/lava_flow");

    /**
     * Vanilla's bucket dispensing logic. {@code FluidDeferredRegister} installs this for the fluids it manages,
     * but unstable lava is registered by hand, so it needs its own copy. The behaviour is stateless, so one
     * instance is shared by every variant's bucket.
     */
    private static final DispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
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
    };

    /**
     * The four registries every unstable lava variant registers into. They are shared by all variants so that the
     * whole family of fluids can be registered from a single place.
     */
    public record Registers(DeferredRegister<FluidType> fluidTypes, DeferredRegister<Fluid> fluids,
          DeferredRegister<Block> blocks, DeferredRegister<Item> items) {

        /** Wires all four registries up to the mod event bus. */
        public void register(IEventBus bus) {
            fluidTypes.register(bus);
            fluids.register(bus);
            blocks.register(bus);
            items.register(bus);
        }
    }

    /**
     * Registers a new unstable lava variant.
     *
     * @param registers the shared registries to register into.
     * @param name      the registry name, used for all five entries (the source fluid, the {@code flowing_}-prefixed
     *                  flowing fluid, the fluid type, the liquid block and the {@code _bucket} item).
     * @param tint      the ARGB tint applied on top of vanilla's lava textures, and used for the fluid's fog.
     */
    public static UnstableLavaVariant register(Registers registers, String name, int tint) {
        return new UnstableLavaVariant(registers, name, tint);
    }

    private final String name;
    private final int tint;
    private final DeferredHolder<FluidType, MekanismFluidType> fluidType;
    private final DeferredHolder<Fluid, UnstableLavaFluid.Source> source;
    private final DeferredHolder<Fluid, UnstableLavaFluid.Flowing> flowing;
    private final DeferredHolder<Block, LiquidBlock> block;
    private final DeferredHolder<Item, BucketItem> bucket;

    private UnstableLavaVariant(Registers registers, String name, int tint) {
        this.name = name;
        this.tint = tint;
        //All of these are deferred, so the suppliers (and the "this" they capture) are only used once the
        //registries fire, long after this constructor has finished.
        this.fluidType = registers.fluidTypes().register(name, () -> createFluidType(name, tint));
        this.source = registers.fluids().register(name, () -> new UnstableLavaFluid.Source(this));
        this.flowing = registers.fluids().register("flowing_" + name, () -> new UnstableLavaFluid.Flowing(this));
        this.block = registers.blocks().register(name, () -> createBlock(source.get()));
        this.bucket = registers.items().register(name + "_bucket", () -> createBucket(source.get()));
    }

    /** The registry name shared by this variant's fluid, fluid type and liquid block. */
    public String name() {
        return name;
    }

    /** The ARGB tint applied to vanilla's lava textures for this variant. */
    public int tint() {
        return tint;
    }

    public DeferredHolder<FluidType, MekanismFluidType> fluidType() {
        return fluidType;
    }

    public DeferredHolder<Fluid, UnstableLavaFluid.Source> source() {
        return source;
    }

    public DeferredHolder<Fluid, UnstableLavaFluid.Flowing> flowing() {
        return flowing;
    }

    public DeferredHolder<Block, LiquidBlock> block() {
        return block;
    }

    public DeferredHolder<Item, BucketItem> bucket() {
        return bucket;
    }

    /**
     * @return whether the given fluid is this variant's source or flowing fluid. Only valid once fluids have been
     *       registered.
     */
    public boolean is(Fluid fluid) {
        return fluid == source.get() || fluid == flowing.get();
    }

    /** Installs the bucket dispensing behaviour for this variant. Must run after item registration. */
    public void registerDispenserBehavior() {
        DispenserBlock.registerBehavior(bucket.get(), BUCKET_DISPENSE_BEHAVIOR);
    }

    private static MekanismFluidType createFluidType(String name, int tint) {
        return new MekanismFluidType(FluidDeferredRegister.getMekBaseBuilder()
              .descriptionId("block." + Mod.MODID + "." + name)
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
                    .texture(STILL_TEXTURE, FLOWING_TEXTURE)
                    .tint(tint));
    }

    private static LiquidBlock createBlock(Fluid source) {
        return new LiquidBlock(source, BlockBehaviour.Properties.of()
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
              .sound(SoundType.EMPTY));
    }

    private static BucketItem createBucket(Fluid source) {
        return new BucketItem(source, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
    }
}
