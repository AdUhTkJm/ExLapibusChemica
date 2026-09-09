package io.aduhtkjm.mekanismheated.client.gui.machine;

import io.aduhtkjm.mekanismheated.tile.TileEntityAtmosphereHeater;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Screen for the Atmosphere Heater: a gas tank gauge and the fuel item slot on the left, an energy bar mirroring
 * Mekanism's own machines on the right. The item slot is rendered dynamically by the config component, matching the
 * tile's inventory position ({@code InputInventorySlot.at(..., 26, 35)}).
 */
public class GuiAtmosphereHeater extends GuiConfigurableTile<TileEntityAtmosphereHeater, MekanismTileContainer<TileEntityAtmosphereHeater>> {

    public GuiAtmosphereHeater(MekanismTileContainer<TileEntityAtmosphereHeater> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiChemicalGauge(() -> tile.getGasTank(), () -> tile.getChemicalTanks(null), GaugeType.STANDARD, this, 6, 10));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 16));
        addRenderableWidget(new GuiEnergyTab(this, tile.getEnergyContainer(), tile::getActive));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}