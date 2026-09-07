package io.aduhtkjm.mekanismheated.integration.jade;

import io.aduhtkjm.mekanismheated.tile.multiblock.TileEntityFractionationBlock;
import mekanism.common.integration.lookingat.LookingAtUtils;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

/**
 * Removes the default renderings for the thermal fractionation tower so only the contents added by
 * {@link FractionationMekRenderer} remain: Jade's universal fluid widget and Mekanism's mek_data fluid gauges for every
 * tank of the tower (empty banks included). Registered on the shared {@code BlockBasicMultiblock} class, so it only
 * acts when the looked-at block is a part of the fractionation tower.
 */
public enum FractionationBuiltinRemover implements IComponentProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("mekanismheated", "fractionation_remover");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof TileEntityFractionationBlock) {
            tooltip.remove(JadeIds.UNIVERSAL_FLUID_STORAGE);
            tooltip.remove(LookingAtUtils.FLUID);
        }
    }

    @Override
    public int getDefaultPriority() {
        //Run in the tail to ensure we are after Mekanism's JadeTooltipRenderer and JadeBuiltinRemover,
        // which add the mek_data fluid elements and strip the universal widgets respectively.
        return TooltipPosition.TAIL;
    }
}
