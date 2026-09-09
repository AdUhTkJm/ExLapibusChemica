package io.aduhtkjm.mekanismheated.content.ambient;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/**
 * Public API for the per-chunk ambient temperature feature. A chunk's "delta" (in Kelvin) is added on top of
 * Mekanism's biome-based ambient temperature for every position inside that chunk.
 * <p>
 * Application happens through mixins:
 * <ul>
 *   <li>{@code MixinHeatAPI} adds the delta in {@code HeatAPI.getAmbientTemp(LevelReader, BlockPos)} — the single
 *       chokepoint used by all machines and heat pipes (via {@code CachedAmbientTemperature}).</li>
 *   <li>{@code MixinMultiblockData} adds the average delta of the chunks a multiblock spans in
 *       {@code MultiblockData.calculateAverageAmbientTemperature}, which bypasses the chokepoint above.</li>
 *   <li>{@code MixinCachedAmbientTemperature} invalidates Mekanism's ambient-temperature caches whenever the
 *       global version counter below advances, so runtime changes take effect without a chunk reload.</li>
 * </ul>
 */
public final class ChunkAmbientTemperature {

    /**
     * Global monotonic counter bumped whenever any chunk's delta changes anywhere. Ambient-temperature caches
     * (Mekanism's {@code CachedAmbientTemperature}, our {@code FusedNetwork} average) compare against this to
     * detect staleness. A change in one chunk invalidates every cache exactly once, which is cheap: the caches
     * lazily recompute a handful of biome lookups and are then stable until the next change.
     */
    private static final AtomicLong VERSION = new AtomicLong(0);

    private ChunkAmbientTemperature() {
    }

    /**
     * Current global delta version, for cache invalidation.
     */
    public static long getVersion() {
        return VERSION.get();
    }

    /**
     * Gets the ambient temperature delta (Kelvin) configured for the chunk containing the given position.
     * Always returns 0 on the client side (deltas live in server-only saved data) and for null inputs.
     */
    public static double getDelta(@Nullable LevelReader level, @Nullable BlockPos pos) {
        if (level == null || pos == null) {
            return 0;
        }
        return getDelta(level, new ChunkPos(pos));
    }

    /**
     * Gets the ambient temperature delta (Kelvin) configured for the given chunk.
     */
    public static double getDelta(@Nullable LevelReader level, ChunkPos chunkPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }
        return ChunkAmbientTemperatureData.get(serverLevel).getDelta(chunkPos);
    }

    /**
     * Sets the ambient temperature delta (Kelvin) of a chunk; pass 0 to clear it. No-op when the value is
     * unchanged. Bumps the global version (invalidating the ambient caches) and fires
     * {@link ChunkAmbientTemperatureChangedEvent} on the NeoForge game bus.
     */
    public static void setDelta(ServerLevel level, ChunkPos chunkPos, double delta) {
        ChunkAmbientTemperatureData data = ChunkAmbientTemperatureData.get(level);
        double previous = data.getDelta(chunkPos);
        if (Double.compare(previous, delta) == 0) {
            return;
        }
        data.setDelta(chunkPos, delta);
        VERSION.incrementAndGet();
        NeoForge.EVENT_BUS.post(new ChunkAmbientTemperatureChangedEvent(level, chunkPos, previous, delta));
    }
}
