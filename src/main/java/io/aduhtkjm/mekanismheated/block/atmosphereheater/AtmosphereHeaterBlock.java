package io.aduhtkjm.mekanismheated.block.atmosphereheater;

import io.aduhtkjm.mekanismheated.tile.TileEntityAtmosphereHeater;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.Machine;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class AtmosphereHeaterBlock extends BlockTile<TileEntityAtmosphereHeater, Machine<TileEntityAtmosphereHeater>> {

    public AtmosphereHeaterBlock(Machine<TileEntityAtmosphereHeater> type, BlockBehaviour.Properties properties) {
        super(type, properties);
    }
}
