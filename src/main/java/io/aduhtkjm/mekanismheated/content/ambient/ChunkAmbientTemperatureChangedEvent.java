package io.aduhtkjm.mekanismheated.content.ambient;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.Event;

/**
 * Custom event fired on the NeoForge game event bus ({@code NeoForge.EVENT_BUS}) whenever the ambient
 * temperature delta of a chunk changes, including when it is cleared back to zero.
 * <p>
 * Custom events are plain subclasses of {@link Event}: post them with {@code NeoForge.EVENT_BUS.post(...)}
 * and listen with {@code @SubscribeEvent} (registered via {@code NeoForge.EVENT_BUS.register(...)}) or with
 * {@code NeoForge.EVENT_BUS.addListener(...)}.
 */
public class ChunkAmbientTemperatureChangedEvent extends Event {

    private final ServerLevel level;
    private final ChunkPos chunkPos;
    private final double previousDelta;
    private final double newDelta;

    public ChunkAmbientTemperatureChangedEvent(ServerLevel level, ChunkPos chunkPos, double previousDelta, double newDelta) {
        this.level = level;
        this.chunkPos = chunkPos;
        this.previousDelta = previousDelta;
        this.newDelta = newDelta;
    }

    /**
     * The dimension the changed chunk is in.
     */
    public ServerLevel getLevel() {
        return level;
    }

    /**
     * The chunk whose ambient temperature delta changed.
     */
    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    /**
     * The delta before the change, in Kelvin.
     */
    public double getPreviousDelta() {
        return previousDelta;
    }

    /**
     * The delta after the change, in Kelvin (0 when the delta was cleared).
     */
    public double getNewDelta() {
        return newDelta;
    }
}
