package io.aduhtkjm.mekanismheated.content.unstablelava;

import io.aduhtkjm.mekanismheated.registries.ModFluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * The fluid implementation behind "unstable lava", the fluid that ambient melting turns blocks into.
 * <p>
 * It deliberately extends vanilla's {@link LavaFluid} rather than {@code BaseFlowingFluid} so that it inherits
 * essentially everything that makes lava lava: the dimension-aware flow speed, slope reach and level drop-off
 * ({@link LavaFluid#getTickDelay}, {@link LavaFluid#getSlopeFindDistance}, {@link LavaFluid#getDropOff}), the
 * randomly-ticking fire spread, the lava drip particles/sounds and the water-to-stone interaction. Only the
 * pieces that point at vanilla objects (source, flowing variant, bucket, legacy block) are overridden to point
 * at our own registrations, along with {@link #getFluidType()} so the fluid reports our custom
 * {@link FluidType} instead of NeoForge's vanilla lava type.
 * <p>
 * Because the fluid type is our own rather than {@code NeoForgeMod.LAVA_TYPE}, {@code Entity#isInLava} does not
 * cover it; the entity damage/burning that lava normally applies through that check is reproduced by
 * {@code MoltenFluidHandler}.
 */
public abstract class UnstableLavaFluid extends LavaFluid {

    @Override
    public FluidType getFluidType() {
        return ModFluids.UNSTABLE_LAVA_TYPE.get();
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_UNSTABLE_LAVA.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.UNSTABLE_LAVA.get();
    }

    @Override
    public Item getBucket() {
        return ModFluids.UNSTABLE_LAVA_BUCKET.get();
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.UNSTABLE_LAVA.get() || fluid == ModFluids.FLOWING_UNSTABLE_LAVA.get();
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return ModFluids.UNSTABLE_LAVA_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Source extends UnstableLavaFluid {

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends UnstableLavaFluid {

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
