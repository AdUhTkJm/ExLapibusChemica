package io.aduhtkjm.mekanismheated.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.system.NonnullDefault;

/**
 * Dust of a metal mixed into obsidian, condensed from that metal's unstable lava. Every variant shares the same
 * behaviour and only differs in the metal's formula shown as the tooltip; the icon distinguishing them is a texture
 * layer (see {@code io.aduhtkjm.mekanismheated.content.obsidiandust.ObsidianDustVariant}).
 */
@NonnullDefault
public class ItemObsidianDust extends Item {

    private final Component tooltip;

    public ItemObsidianDust(Properties properties, String formula) {
        super(properties);
        this.tooltip = Component.literal(formula).withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(this.tooltip);
    }
}
