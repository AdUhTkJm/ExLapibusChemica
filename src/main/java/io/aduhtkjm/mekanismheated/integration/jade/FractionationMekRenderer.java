package io.aduhtkjm.mekanismheated.integration.jade;

import io.aduhtkjm.mekanismheated.ModLang;
import io.aduhtkjm.mekanismheated.tile.multiblock.TileEntityFractionationBlock;
import mekanism.api.SerializationConstants;
import mekanism.common.integration.lookingat.FluidElement;
import mekanism.common.integration.lookingat.LookingAtElement;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;

/**
 * Renders the thermal fractionation tower's state sent by {@link FractionationMekDataProvider}. Only the non-empty feed
 * sump and output banks get a fluid gauge (mirroring how Mekanism renders machine contents), followed by the tower's
 * temperature, height and bank count as plain text.
 */
public enum FractionationMekRenderer implements IComponentProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return FractionationMekDataProvider.UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof TileEntityFractionationBlock)) {
            return;
        }
        CompoundTag serverData = accessor.getServerData();
        if (!serverData.contains(FractionationMekDataProvider.KEY, Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag mhData = serverData.getCompound(FractionationMekDataProvider.KEY);
        var registryAccess = accessor.getLevel().registryAccess();
        //Feed sump
        CompoundTag sumpTag = mhData.getCompound("sump");
        FluidStack sump = FluidStack.parseOptional(registryAccess, sumpTag.getCompound(SerializationConstants.FLUID));
        int sumpCapacity = sumpTag.getInt(SerializationConstants.MAX);
        if (!sump.isEmpty() && sumpCapacity > 0) {
            tooltip.add(new MekElement(new FluidElement(sump, sumpCapacity)));
        }
        //Output banks, ordered bottom to top
        ListTag bankList = mhData.getList("banks", Tag.TAG_COMPOUND);
        for (int i = 0; i < bankList.size(); i++) {
            CompoundTag bankTag = bankList.getCompound(i);
            FluidStack bank = FluidStack.parseOptional(registryAccess, bankTag.getCompound(SerializationConstants.FLUID));
            int capacity = bankTag.getInt(SerializationConstants.MAX);
            if (!bank.isEmpty() && capacity > 0) {
                tooltip.add(new MekElement(new FluidElement(bank, capacity)));
            }
        }
        //Temperature, height and bank count as plain text
        double temperature = mhData.getDouble("temperature");
        tooltip.add(Component.translatable("mekanismheated.jade.temperature",
              MekanismUtils.getTemperatureDisplay(temperature, TemperatureUnit.KELVIN, true)));
        tooltip.add(ModLang.GUI_FRACTIONATION_HEIGHT.translate(mhData.getInt("height")));
        tooltip.add(ModLang.GUI_FRACTIONATION_LAYERS.translate(mhData.getInt("bank_count")));
    }

    private static class MekElement extends Element {

        private final LookingAtElement element;

        public MekElement(LookingAtElement element) {
            this.element = element;
        }

        @Override
        public Vec2 getSize() {
            return new Vec2(element.getWidth(), element.getHeight() + 2);
        }

        @Override
        public void render(GuiGraphics guiGraphics, float rawX, float rawY, float maxX, float maxY) {
            int x = Mth.floor(rawX);
            int y = Mth.floor(rawY);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x, y, 0);
            element.render(guiGraphics, 0, 1);
            guiGraphics.pose().popPose();
        }
    }
}
