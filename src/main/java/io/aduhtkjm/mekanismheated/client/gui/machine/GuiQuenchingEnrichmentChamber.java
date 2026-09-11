package io.aduhtkjm.mekanismheated.client.gui.machine;

import io.aduhtkjm.mekanismheated.tile.TileEntityQuenchingEnrichmentChamber;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiUpArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.tile.component.config.DataType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/** Mekanism-style screen for the powered Quenching Enrichment Chamber. */
public class GuiQuenchingEnrichmentChamber extends GuiConfigurableTile<TileEntityQuenchingEnrichmentChamber, MekanismTileContainer<TileEntityQuenchingEnrichmentChamber>> {

    public GuiQuenchingEnrichmentChamber(MekanismTileContainer<TileEntityQuenchingEnrichmentChamber> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiProgress(tile::getScaledProgress, ProgressType.BAR, this, 86, 38).recipeViewerCategory(tile))
            .warning(WarningType.INPUT_DOESNT_PRODUCE_OUTPUT, tile.getWarningCheck(RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT));
        addRenderableWidget(new GuiFluidGauge(() -> tile.inputFluidTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD.with(DataType.INPUT), this, 26, 10))
            .warning(WarningType.NO_MATCHING_RECIPE, tile.getWarningCheck(TileEntityQuenchingEnrichmentChamber.NOT_ENOUGH_FLUID_INPUT_ERROR));
        addRenderableWidget(new GuiFluidGauge(() -> tile.outputFluidTank, () -> tile.getFluidTanks(null), GaugeType.STANDARD.with(DataType.OUTPUT), this, 115, 10))
            .warning(WarningType.NO_SPACE_IN_OUTPUT, tile.getWarningCheck(TileEntityQuenchingEnrichmentChamber.NOT_ENOUGH_FLUID_OUTPUT_SPACE_ERROR));
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 16));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
