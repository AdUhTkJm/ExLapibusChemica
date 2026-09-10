package io.aduhtkjm.mekanismheated.content.ambient;

import io.aduhtkjm.mekanismheated.Config;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Decides whether a block is a valid target for ambient melting, based on the
 * {@link Config.AmbientMelting#MODE mode} and {@link Config.AmbientMelting#BLOCKS list} configuration.
 * <p>
 * Both whitelist and blacklist entries may be either a namespace (e.g. {@code minecraft}) or the qualified id of a
 * single block (e.g. {@code minecraft:stone}). A block matches if its namespace or its full id was configured.
 * <ul>
 *     <li>{@link Mode#WHITELIST}: only matching blocks melt. This is the default, with {@code minecraft} configured,
 *         so only vanilla blocks melt.</li>
 *     <li>{@link Mode#BLACKLIST}: every block melts except matching ones.</li>
 * </ul>
 * Air (of any kind) is always rejected regardless of configuration, as are fluid blocks (melting water or another
 * fluid into unstable lava would be nonsensical and would swallow existing fluids).
 */
public final class BlockMeltFilter {

    public enum Mode {
        WHITELIST,
        BLACKLIST
    }

    private final Mode mode;
    private final Set<String> namespaces;
    private final Set<ResourceLocation> blocks;

    private BlockMeltFilter(Mode mode, Set<String> namespaces, Set<ResourceLocation> blocks) {
        this.mode = mode;
        this.namespaces = namespaces;
        this.blocks = blocks;
    }

    /**
     * Builds a filter from the current config values.
     */
    public static BlockMeltFilter fromConfig() {
        Set<String> namespaces = new HashSet<>();
        Set<ResourceLocation> blocks = new HashSet<>();
        for (String raw : Config.AmbientMelting.BLOCKS.get()) {
            if (raw == null) {
                continue;
            }
            String entry = raw.trim().toLowerCase(Locale.ROOT);
            if (entry.isEmpty()) {
                continue;
            }
            if (entry.indexOf(':') >= 0) {
                ResourceLocation id = ResourceLocation.tryParse(entry);
                if (id != null) {
                    blocks.add(id);
                }
            } else if (ResourceLocation.isValidNamespace(entry)) {
                namespaces.add(entry);
            }
        }
        return new BlockMeltFilter(Config.AmbientMelting.MODE.get(), namespaces, blocks);
    }

    /**
     * Validator for the config list entries: a namespace or a valid block id.
     */
    public static boolean isValidEntry(@Nullable Object value) {
        if (!(value instanceof String raw)) {
            return false;
        }
        String entry = raw.trim().toLowerCase(Locale.ROOT);
        if (entry.isEmpty()) {
            return false;
        }
        if (entry.indexOf(':') >= 0) {
            return ResourceLocation.tryParse(entry) != null;
        }
        return ResourceLocation.isValidNamespace(entry);
    }

    /**
     * @return {@code true} if the given block should be melted into unstable lava.
     */
    public boolean test(BlockState state) {
        if (state.isAir()) {
            //Air is always rejected, no matter the configuration.
            return false;
        }
        if (!state.getFluidState().isEmpty()) {
            //Never melt a fluid into a fluid.
            return false;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id == null) {
            return false;
        }
        boolean listed = namespaces.contains(id.getNamespace()) || blocks.contains(id);
        return mode == Mode.WHITELIST ? listed : !listed;
    }
}
