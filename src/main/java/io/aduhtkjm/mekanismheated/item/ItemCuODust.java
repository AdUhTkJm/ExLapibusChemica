package io.aduhtkjm.mekanismheated.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class ItemCuODust extends Item {
    public static final int TINT = 0xFF010101;
    private static final Component TOOLTIP = Component.literal("CuO").withStyle(ChatFormatting.GOLD);

    public ItemCuODust(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(TOOLTIP);
    }
}
