package io.aduhtkjm.mekanismheated.network;

import io.aduhtkjm.mekanismheated.Mod;
import io.aduhtkjm.mekanismheated.tile.TileEntityCreativeChunkHeater;
import io.netty.buffer.ByteBuf;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.network.PacketUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Sets the absolute ambient temperature target (Kelvin) of a {@link TileEntityCreativeChunkHeater}; the tile
 * derives the chunk delta to apply from it.
 */
public record PacketSetChunkTargetTemperature(BlockPos pos, double targetTemperature) implements IMekanismPacket {

    public static final CustomPacketPayload.Type<PacketSetChunkTargetTemperature> TYPE = new CustomPacketPayload.Type<>(Mod.rl("set_chunk_target_temperature"));
    public static final StreamCodec<ByteBuf, PacketSetChunkTargetTemperature> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, PacketSetChunkTargetTemperature::pos,
          ByteBufCodecs.DOUBLE, PacketSetChunkTargetTemperature::targetTemperature,
          PacketSetChunkTargetTemperature::new
    );

    @NotNull
    @Override
    public CustomPacketPayload.Type<PacketSetChunkTargetTemperature> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (PacketUtils.blockEntity(context, pos) instanceof TileEntityCreativeChunkHeater tile) {
            tile.setTargetTemperature(targetTemperature);
        }
    }
}
