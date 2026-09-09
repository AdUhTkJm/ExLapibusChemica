package io.aduhtkjm.mekanismheated.block.atmosphereheater;

import io.aduhtkjm.mekanismheated.tile.TileEntityAtmosphereHeater;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.Machine;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class AtmosphereHeaterBlock extends BlockTile<TileEntityAtmosphereHeater, Machine<TileEntityAtmosphereHeater>> {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AtmosphereHeaterBlock(Machine<TileEntityAtmosphereHeater> type, BlockBehaviour.Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
