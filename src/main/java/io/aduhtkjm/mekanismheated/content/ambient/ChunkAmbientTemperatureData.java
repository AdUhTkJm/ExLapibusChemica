package io.aduhtkjm.mekanismheated.content.ambient;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.lwjgl.system.NonnullDefault;

/**
 * Per-dimension saved data mapping chunk coordinates to an ambient temperature delta in Kelvin, which is
 * added on top of the biome-based ambient temperature (see {@link ChunkAmbientTemperature}).
 * <p>
 * Deltas are stored per dimension (unlike {@code FusedNetworkSavedData} which is overworld-global) because
 * a chunk's climate is inherently dimension-specific: the Nether and the Overworld may have independent
 * deltas at the same chunk coordinates.
 */
@NonnullDefault
public class ChunkAmbientTemperatureData extends SavedData {

    public static final String DATA_NAME = "mekanismheated_chunk_ambient_temperatures";

    private final Long2DoubleMap deltas = new Long2DoubleOpenHashMap();

    public static ChunkAmbientTemperatureData create() {
        return new ChunkAmbientTemperatureData();
    }

    public static ChunkAmbientTemperatureData load(CompoundTag tag, HolderLookup.Provider provider) {
        ChunkAmbientTemperatureData data = create();
        ListTag list = tag.getList("deltas", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            data.deltas.put(ChunkPos.asLong(entry.getInt("x"), entry.getInt("z")), entry.getDouble("delta"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Long2DoubleMap.Entry entry : deltas.long2DoubleEntrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("x", ChunkPos.getX(entry.getLongKey()));
            entryTag.putInt("z", ChunkPos.getZ(entry.getLongKey()));
            entryTag.putDouble("delta", entry.getDoubleValue());
            list.add(entryTag);
        }
        tag.put("deltas", list);
        return tag;
    }

    /**
     * Gets the delta for the given chunk; 0 if none is set.
     */
    public double getDelta(ChunkPos chunkPos) {
        return deltas.get(chunkPos.toLong());
    }

    /**
     * Stores the delta for the given chunk. A delta of 0 removes the entry so untouched chunks do not bloat
     * the save file. Marks the data dirty so it persists on the next autosave.
     */
    public void setDelta(ChunkPos chunkPos, double delta) {
        if (delta == 0) {
            deltas.remove(chunkPos.toLong());
        } else {
            deltas.put(chunkPos.toLong(), delta);
        }
        setDirty();
    }

    /**
     * Convenience: obtains or creates the {@link ChunkAmbientTemperatureData} attached to the given dimension.
     */
    public static ChunkAmbientTemperatureData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
              new Factory<>(ChunkAmbientTemperatureData::create, ChunkAmbientTemperatureData::load),
              DATA_NAME
        );
    }
}
