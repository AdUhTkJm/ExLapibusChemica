package io.aduhtkjm.mekanismheated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.aduhtkjm.mekanismheated.content.ambient.ChunkAmbientTemperature;
import mekanism.api.heat.HeatAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Debug/test command for the per-chunk ambient temperature feature:
 * <pre>
 *   /mekheatedtemp get [x z]    - show the delta, biome ambient and effective ambient of the chunk
 *   /mekheatedtemp set <delta>  - set the delta (Kelvin) of the player's current chunk
 *   /mekheatedtemp set <x> <z> <delta> - set the delta of an explicit chunk
 *   /mekheatedtemp clear [x z]  - reset the delta back to 0
 * </pre>
 * Requires op permission level 2. Without explicit coordinates, the player's current chunk is used.
 */
public final class ChunkTemperatureCommand {

    private ChunkTemperatureCommand() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mekheatedtemp")
              .requires(source -> source.hasPermission(2))
              .then(Commands.literal("get")
                    .executes(context -> get(context.getSource(), currentChunk(context.getSource())))
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                          .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(context -> get(context.getSource(),
                                      new ChunkPos(IntegerArgumentType.getInteger(context, "x"), IntegerArgumentType.getInteger(context, "z")))))))
              .then(Commands.literal("set")
                    .then(Commands.argument("delta", DoubleArgumentType.doubleArg())
                          .executes(context -> set(context.getSource(), currentChunk(context.getSource()),
                                DoubleArgumentType.getDouble(context, "delta"))))
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                          .then(Commands.argument("z", IntegerArgumentType.integer())
                                .then(Commands.argument("delta", DoubleArgumentType.doubleArg())
                                      .executes(context -> set(context.getSource(),
                                            new ChunkPos(IntegerArgumentType.getInteger(context, "x"), IntegerArgumentType.getInteger(context, "z")),
                                            DoubleArgumentType.getDouble(context, "delta")))))))
              .then(Commands.literal("clear")
                    .executes(context -> set(context.getSource(), currentChunk(context.getSource()), 0))
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                          .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(context -> set(context.getSource(),
                                      new ChunkPos(IntegerArgumentType.getInteger(context, "x"), IntegerArgumentType.getInteger(context, "z")),
                                      0)))))
        );
    }

    private static ChunkPos currentChunk(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return player.chunkPosition();
    }

    private static int get(CommandSourceStack source, ChunkPos chunkPos) {
        ServerLevel level = source.getLevel();
        double delta = ChunkAmbientTemperature.getDelta(level, chunkPos);
        //HeatAPI.getAmbientTemp already includes the chunk delta through the MixinHeatAPI injection
        double effective = HeatAPI.getAmbientTemp(level, chunkPos.getWorldPosition());
        double biomeAmbient = effective - delta;
        source.sendSuccess(() -> Component.literal(String.format(
              "Chunk [%d, %d] in %s: delta %+.2f K, biome ambient %.2f K, effective ambient %.2f K",
              chunkPos.x, chunkPos.z, level.dimension().location(), delta, biomeAmbient, effective)), false);
        return 1;
    }

    private static int set(CommandSourceStack source, ChunkPos chunkPos, double delta) {
        ServerLevel level = source.getLevel();
        ChunkAmbientTemperature.setDelta(level, chunkPos, delta);
        double applied = ChunkAmbientTemperature.getDelta(level, chunkPos);
        source.sendSuccess(() -> Component.literal(String.format(
              "Chunk [%d, %d] in %s: ambient temperature delta set to %+.2f K (takes effect on nearby machines within a tick)",
              chunkPos.x, chunkPos.z, level.dimension().location(), applied)), true);
        return 1;
    }
}
