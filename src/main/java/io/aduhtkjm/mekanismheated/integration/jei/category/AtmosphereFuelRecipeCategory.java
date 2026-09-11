package io.aduhtkjm.mekanismheated.integration.jei.category;

import io.aduhtkjm.mekanismheated.ModLang;
import io.aduhtkjm.mekanismheated.recipe.AtmosphereFuelRecipe;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.jei.HolderRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.text.EnergyDisplay;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.lwjgl.system.NonnullDefault;

/**
 * Atmosphere fuel recipes for the Atmosphere Heater: a single item <strong>or</strong> gas input that reduces the
 * machine's energy consumption by a fixed FE/t amount when consumed during a work cycle. The layout mirrors the
 * machine's GUI (gas gauge left of the item slot, vertical power bar on the right); the power bar always shows as full
 * and its tooltip instead states how much energy the fuel reduces instead of displaying stored energy.
 */
@NonnullDefault
public class AtmosphereFuelRecipeCategory extends HolderRecipeCategory<AtmosphereFuelRecipe> {

    private final GuiSlot inputItem;
    private final GuiGauge<?> gasInput;
    private final GuiVerticalPowerBar energyBar;

    public AtmosphereFuelRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<AtmosphereFuelRecipe> recipeType) {
        super(helper, recipeType);
        gasInput = addElement(GuiChemicalGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 6, 10));
        inputItem = addSlot(SlotType.INPUT, 26, 35);
        //Stands in for the machine's energy bar, but communicates the recipe's energy reduction via its tooltip
        energyBar = addElement(new GuiVerticalPowerBar(this, new IBarInfoHandler() {
            @Override
            public Component getTooltip() {
                //The actual reduction tooltip is added per recipe in getTooltip
                return Component.empty();
            }

            @Override
            public double getLevel() {
                return 1;
            }
        }, 164, 16));
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<AtmosphereFuelRecipe> recipeHolder,
                           IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (energyBar.isMouseOver(mouseX, mouseY)) {
            tooltip.add(getReductionText(recipeHolder.value()));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AtmosphereFuelRecipe> recipeHolder,
                          IFocusGroup focusGroup) {
        AtmosphereFuelRecipe recipe = recipeHolder.value();
        recipe.getItemInput().ifPresent(ingredient ->
              initItem(builder, RecipeIngredientRole.INPUT, inputItem, ingredient.getRepresentations()));
        recipe.getChemicalInput().ifPresent(ingredient ->
              initChemical(builder, RecipeIngredientRole.INPUT, gasInput, ingredient.getRepresentations()));
    }

    /**
     * Formats the recipe's energy-consumption reduction (given in FE/t) with Mekanism's energy display.
     */
    private static Component getReductionText(AtmosphereFuelRecipe recipe) {
        return ModLang.ATMOSPHERE_HEATER_REDUCTION.translate(EnergyDisplay.of(recipe.getReduction()));
    }
}