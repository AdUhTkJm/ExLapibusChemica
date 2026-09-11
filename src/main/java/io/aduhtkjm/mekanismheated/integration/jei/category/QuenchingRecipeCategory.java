package io.aduhtkjm.mekanismheated.integration.jei.category;

import io.aduhtkjm.mekanismheated.recipe.QuenchingRecipe;
import mekanism.client.gui.element.GuiUpArrow;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.jei.HolderRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.tile.component.config.DataType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.lwjgl.system.NonnullDefault;

/** Quenching recipes for the Quenching Enrichment Chamber: item plus fluid to a single fluid. Layout mirrors the machine's GUI. */
@NonnullDefault
public class QuenchingRecipeCategory extends HolderRecipeCategory<QuenchingRecipe> {

    private final GuiGauge<?> inputTank;
    private final GuiGauge<?> outputTank;
    private final GuiSlot input;

    public QuenchingRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<QuenchingRecipe> recipeType) {
        super(helper, recipeType);
        inputTank = addElement(GuiFluidGauge.getDummy(GaugeType.STANDARD.with(DataType.INPUT), this, 26, 10));
        input = addSlot(SlotType.INPUT, 64, 35);
        addSimpleProgress(ProgressType.BAR, 86, 38);
        outputTank = addElement(GuiFluidGauge.getDummy(GaugeType.STANDARD.with(DataType.OUTPUT), this, 115, 10));
        addElement(new GuiVerticalPowerBar(this, RecipeViewerUtils.FULL_BAR, 164, 16));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<QuenchingRecipe> recipeHolder, IFocusGroup focusGroup) {
        QuenchingRecipe recipe = recipeHolder.value();
        initFluid(builder, RecipeIngredientRole.INPUT, inputTank, recipe.getFluidInput().getRepresentations());
        initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getItemInput().getRepresentations());
        initFluid(builder, RecipeIngredientRole.OUTPUT, outputTank, recipe.getOutputDefinition());
    }
}
