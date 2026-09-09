package io.aduhtkjm.mekanismheated.mixin;

import io.aduhtkjm.mekanismheated.content.ambient.ChunkAmbientTemperature;
import java.util.Arrays;
import java.util.function.Supplier;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mekanism caches the ambient temperature per side ({@code -1} sentinel per array slot) and never invalidates
 * it: a tile keeps its first-computed ambient temperature for as long as it exists. Since chunk deltas can
 * change at runtime, watch the global delta version and clear the cached values whenever any chunk's delta
 * changed, so the next lookup recomputes through the (mixin-extended) {@code HeatAPI.getAmbientTemp}.
 * <p>
 * A change in one chunk invalidates every cache exactly once — the recompute is a handful of cheap biome and
 * map lookups, after which the cache is stable again until the next change.
 */
@Mixin(value = CachedAmbientTemperature.class, remap = false)
public abstract class MixinCachedAmbientTemperature {

    @Shadow
    @Final
    private double[] ambientTemperature;

    @Shadow
    @Final
    private Supplier<Level> worldSupplier;

    /**
     * Sentinel so the first real call always treats the cache as stale and stores the actual version.
     */
    @Unique
    private long mekanismheated$lastDeltaVersion = Long.MIN_VALUE;

    @Inject(method = "getTemperature(Lnet/minecraft/core/Direction;)D", at = @At("HEAD"))
    private void mekanismheated$invalidateStaleCache(@Nullable Direction side, CallbackInfoReturnable<Double> cir) {
        if (worldSupplier.get() == null) {
            //No world yet: the base implementation falls back to AMBIENT_TEMP and never caches; nothing to invalidate.
            return;
        }
        long version = ChunkAmbientTemperature.getVersion();
        if (version != mekanismheated$lastDeltaVersion) {
            mekanismheated$lastDeltaVersion = version;
            Arrays.fill(ambientTemperature, -1);
        }
    }
}
