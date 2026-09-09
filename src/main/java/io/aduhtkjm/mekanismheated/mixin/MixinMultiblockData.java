package io.aduhtkjm.mekanismheated.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.aduhtkjm.mekanismheated.content.ambient.ChunkAmbientTemperature;
import mekanism.common.lib.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mekanism's multiblocks (fission, fusion, boiler, evaporation) average the biome temperature of their
 * bounding box in {@link MultiblockData#calculateAverageAmbientTemperature} and then convert via the
 * position-less {@code HeatAPI.getAmbientTemp(double)} overload — so the {@code MixinHeatAPI} injection
 * cannot reach them. This adds the average chunk delta of the structure on top.
 */
@Mixin(value = MultiblockData.class, remap = false)
public abstract class MixinMultiblockData {

    @ModifyReturnValue(method = "calculateAverageAmbientTemperature(Lnet/minecraft/world/level/Level;)D", at = @At("RETURN"))
    private double mekanismheated$addChunkDelta(double original, Level world) {
        MultiblockData self = (MultiblockData) (Object) this;
        BlockPos min = self.getMinPos();
        BlockPos max = self.getMaxPos();
        //Unlike the upstream biome approximation (which samples only the 8 corners of the bounding box),
        //iterate every chunk the structure spans: chunk deltas are per-chunk by design and a hot or cold
        //chunk in the middle of a large structure should still count. Only runs on (re)formation.
        double sum = 0;
        int count = 0;
        for (int chunkX = min.getX() >> 4; chunkX <= max.getX() >> 4; chunkX++) {
            for (int chunkZ = min.getZ() >> 4; chunkZ <= max.getZ() >> 4; chunkZ++) {
                sum += ChunkAmbientTemperature.getDelta(world, new ChunkPos(chunkX, chunkZ));
                count++;
            }
        }
        return original + sum / count;
    }
}
