package io.aduhtkjm.mekanismheated.block.creative;

import io.aduhtkjm.mekanismheated.tile.TileEntityCreativeChunkHeater;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.Machine;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CreativeChunkHeaterBlock extends BlockTile<TileEntityCreativeChunkHeater, Machine<TileEntityCreativeChunkHeater>> {

    public CreativeChunkHeaterBlock(Machine<TileEntityCreativeChunkHeater> type, BlockBehaviour.Properties properties) {
        super(type, properties);
    }
}
