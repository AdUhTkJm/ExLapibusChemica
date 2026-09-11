package io.aduhtkjm.mekanismheated.recipe;

import java.util.List;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public abstract class QuenchingRecipe extends MekanismRecipe<QuenchingRecipeInput> {
    
    public abstract boolean test(ItemStack item, FluidStack second);

    @Override
    public boolean matches(QuenchingRecipeInput input, Level level) {
        return !isIncomplete() && test(input.item(), input.fluid());
    }

    /**
     * Gets the item input ingredient.
     */
    public abstract ItemStackIngredient getItemInput();

    /**
     * Gets the fluid input ingredient.
     */
    public abstract FluidStackIngredient getFluidInput();

    /**
     * Gets a new output based on the given inputs.
     *
     * @param itemStack  Specific item input.
     * @param fluidStack Specific fluid input.
     *
     * @return Fluid produced by the recipe.
     *
     * @apiNote While Mekanism does not currently make use of the inputs, it is important to support them and pass the
     * proper values in case any addons define input based outputs where things like NBT may be different.
     * @implNote The passed in inputs should <strong>NOT</strong> be modified.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public abstract FluidStack getOutput(ItemStack itemStack, FluidStack fluidStack);

    /**
     * Gets the output fluid ingredient.
     */
    public abstract FluidStackIngredient getOutputIngredient();

    /**
     * For recipe viewers, gets the output representations to display.
     *
     * @return Representation of the output, <strong>MUST NOT</strong> be modified.
     */
    public abstract List<FluidStack> getOutputDefinition();

    @Override
    public boolean isIncomplete() {
        return getItemInput().hasNoMatchingInstances() || getFluidInput().hasNoMatchingInstances() || getOutputIngredient().hasNoMatchingInstances();
    }

    @Override
    public void logMissingTags() {
        getItemInput().logMissingTags();
        getFluidInput().logMissingTags();
        getOutputIngredient().logMissingTags();
    }
}
