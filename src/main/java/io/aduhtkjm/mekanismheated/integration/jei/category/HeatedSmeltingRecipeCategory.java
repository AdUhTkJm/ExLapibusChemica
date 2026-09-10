package io.aduhtkjm.mekanismheated.integration.jei.category;

import io.aduhtkjm.mekanismheated.recipe.HeatedItemStackToItemStackRecipe;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.lwjgl.system.NonnullDefault;

/** Heated smelting recipes for the Heat Smelter: item to item above the normal smelting threshold. */
@NonnullDefault
public class HeatedSmeltingRecipeCategory extends AbstractHeatSmelterRecipeCategory<HeatedItemStackToItemStackRecipe> {

    private final GuiSlot output;

    public HeatedSmeltingRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<HeatedItemStackToItemStackRecipe> recipeType) {
        super(helper, recipeType);
        output = addSlot(SlotType.OUTPUT, 116, 35);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<HeatedItemStackToItemStackRecipe> recipeHolder, IFocusGroup focusGroup) {
        HeatedItemStackToItemStackRecipe recipe = recipeHolder.value();
        IRecipeSlotBuilder inputSlot = initItem(builder, RecipeIngredientRole.INPUT, input, recipe.getInput().getRepresentations());
        addTooltip(inputSlot, recipe.getTemperatureThreshold(), recipe.getHeatConsumed());
        initItem(builder, RecipeIngredientRole.OUTPUT, output, recipe.getOutputDefinition());
    }
}
