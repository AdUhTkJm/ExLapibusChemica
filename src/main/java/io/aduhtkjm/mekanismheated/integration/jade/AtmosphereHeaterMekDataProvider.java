package io.aduhtkjm.mekanismheated.integration.jade;

import io.aduhtkjm.mekanismheated.tile.TileEntityAtmosphereHeater;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.config.MekanismConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/**
 * Sends the atmosphere heater's gas tank and fuel item contents plus its current energy consumption (in Mekanism
 * Joules per tick, i.e. the base consumption minus the reduction granted by the fuel of the last completed cycle) to
 * Jade, for {@link AtmosphereHeaterMekRenderer} to display.
 */
public enum AtmosphereHeaterMekDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("mekanismheated", "atmosphere_heater_mek_data");
    static final String KEY = "mh_atmosphere_heater_contents";

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof TileEntityAtmosphereHeater heater)) {
            return;
        }
        CompoundTag mhData = new CompoundTag();
        IChemicalTank gasTank = heater.getGasTank();
        if (!gasTank.getStack().isEmpty()) {
            CompoundTag chemTag = new CompoundTag();
            chemTag.putLong(SerializationConstants.MAX, gasTank.getCapacity());
            chemTag.put(SerializationConstants.CHEMICAL, gasTank.getStack().save(accessor.getLevel().registryAccess()));
            mhData.put("chemical", chemTag);
        }
        ItemStack item = heater.getInputSlot().getStack();
        if (!item.isEmpty()) {
            mhData.put("item", item.save(accessor.getLevel().registryAccess()));
        }
        //Current effective energy consumption in J/t: the base consumption minus the fuel's reduction, floored at zero
        long consumptionJoules = heater.getEnergyContainer().getEnergyPerTick();
        long reductionJoules = Math.round(heater.getReduction() * MekanismConfig.general.forgeConversionRate.get());
        mhData.putLong("consumption", Math.max(0, consumptionJoules - reductionJoules));
        data.put(KEY, mhData);
    }
}