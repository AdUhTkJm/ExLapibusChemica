package io.aduhtkjm.mekanismheated.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.aduhtkjm.mekanismheated.content.ambient.ChunkAmbientTemperature;
import mekanism.api.heat.HeatAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Adds the per-chunk ambient temperature delta on top of Mekanism's biome-based ambient temperature.
 * <p>
 * {@code HeatAPI.getAmbientTemp(LevelReader, BlockPos)} is the single chokepoint through which every machine
 * and heat pipe resolves the ambient temperature for a position (all via {@code CachedAmbientTemperature}, and
 * multiblock constructors use it as their pre-formation default), so one injection covers them all.
 * The multiblock corner-averaging path is handled separately by {@link MixinMultiblockData}.
 */
@Mixin(value = HeatAPI.class, remap = false)
public abstract class MixinHeatAPI {

    @ModifyReturnValue(method = "getAmbientTemp(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)D", at = @At("RETURN"))
    private static double mekanismheated$addChunkDelta(double original, @Nullable LevelReader world, BlockPos pos) {
        return original + ChunkAmbientTemperature.getDelta(world, pos);
    }
}
