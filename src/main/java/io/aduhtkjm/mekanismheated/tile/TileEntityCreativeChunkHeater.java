package io.aduhtkjm.mekanismheated.tile;

import io.aduhtkjm.mekanismheated.content.ambient.ChunkAmbientTemperature;
import io.aduhtkjm.mekanismheated.registries.ModBlocks;
import mekanism.api.heat.HeatAPI;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Creative-only chunk heater: instead of emitting heat like {@link TileEntityCreativeHeatBlock}, it maintains the
 * per-chunk ambient temperature delta (see {@link ChunkAmbientTemperature}) so that the effective ambient
 * temperature of the chunk it is placed in equals a configured absolute target.
 * <p>
 * The GUI accepts an absolute temperature in Kelvin; the required delta is computed as
 * {@code target - biomeAmbient}, where {@code biomeAmbient} is the plain biome-based ambient temperature of the
 * block's position (i.e. {@link HeatAPI#getAmbientTemp} minus the currently applied chunk delta). The delta is
 * re-applied every server tick, so biome changes take effect automatically. A target of 0 K means "off" (delta
 * cleared), mirroring the behaviour of the creative heat block.
 */
public class TileEntityCreativeChunkHeater extends TileEntityMekanism {

    private static final String TAG_TARGET_TEMPERATURE = "targetTemperature";
    public static final double MAX_TARGET_TEMPERATURE = 1_000_000_000;

    private double targetTemperature;
    private double lastAppliedDelta;
    // Effective ambient temperature of this chunk as of the last server tick, synced for GUI display
    private double chunkAmbientTemperature;

    public TileEntityCreativeChunkHeater(BlockPos pos, BlockState state) {
        super(ModBlocks.CREATIVE_CHUNK_HEATER, pos, state);
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        if (level instanceof ServerLevel serverLevel) {
            applyChunkTemperature(serverLevel);
            setActive(targetTemperature > 0);
        }
        return sendUpdatePacket;
    }

    private void applyChunkTemperature(ServerLevel serverLevel) {
        double currentDelta = ChunkAmbientTemperature.getDelta(serverLevel, worldPosition);
        if (targetTemperature <= 0) {
            chunkAmbientTemperature = biomeAmbient(serverLevel, currentDelta);
            return;
        }
        // HeatAPI.getAmbientTemp includes the chunk delta (see MixinHeatAPI), so strip it off again to get the
        // plain biome-based ambient temperature the delta is applied on top of.
        double desiredDelta = targetTemperature - biomeAmbient(serverLevel, currentDelta);
        ChunkAmbientTemperature.setDelta(serverLevel, new ChunkPos(worldPosition), desiredDelta);
        lastAppliedDelta = ChunkAmbientTemperature.getDelta(serverLevel, worldPosition);
        chunkAmbientTemperature = targetTemperature;
    }

    private double biomeAmbient(ServerLevel serverLevel, double currentDelta) {
        return HeatAPI.getAmbientTemp(serverLevel, worldPosition) - currentDelta;
    }

    public void setTargetTemperature(double temperature) {
        this.targetTemperature = Math.clamp(temperature, 0, MAX_TARGET_TEMPERATURE);
        markForSave();
    }

    public double getTargetTemperature() {
        return targetTemperature;
    }

    public double getLastAppliedDelta() {
        return lastAppliedDelta;
    }

    public double getChunkAmbientTemperature() {
        return chunkAmbientTemperature;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableDouble.create(this::getTargetTemperature, value -> targetTemperature = value));
        container.track(SyncableDouble.create(this::getLastAppliedDelta, value -> lastAppliedDelta = value));
        container.track(SyncableDouble.create(this::getChunkAmbientTemperature, value -> chunkAmbientTemperature = value));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        nbt.putDouble(TAG_TARGET_TEMPERATURE, targetTemperature);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        targetTemperature = nbt.getDouble(TAG_TARGET_TEMPERATURE);
    }
}
