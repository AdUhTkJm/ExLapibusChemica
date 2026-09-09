package io.aduhtkjm.mekanismheated.integration.jade;

import io.aduhtkjm.mekanismheated.tile.TileEntityAtmosphereHeater;
import mekanism.common.integration.lookingat.LookingAtUtils;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

/**
 * Removes the duplicate default renderings for the atmosphere heater: Jade's universal energy widget and Mekanism's own
 * chemical gauge for the gas tank. Mekanism's energy element (stored energy) is kept; the current consumption line is
 * added on top by {@link AtmosphereHeaterMekRenderer}.
 */
public enum AtmosphereHeaterBuiltinRemover implements IComponentProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("mekanismheated", "atmosphere_heater_remover");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof TileEntityAtmosphereHeater) {
            tooltip.remove(JadeIds.UNIVERSAL_ENERGY_STORAGE);
            tooltip.remove(LookingAtUtils.CHEMICAL);
        }
    }

    @Override
    public int getDefaultPriority() {
        //Run in the tail to ensure we are after Mekanism's JadeTooltipRenderer and JadeBuiltinRemover,
        // which add the mek_data chemical elements and strip the universal widgets respectively.
        return TooltipPosition.TAIL;
    }
}