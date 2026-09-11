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

/**
 * Input: ItemStack (required)
 * <br>
 * Input: FluidStack (required)
 * <br>
 * Output: single FluidStack
 *
 * <p>Both inputs are required: a quenching recipe only runs while the machine's item slot holds a matching item
 * <em>and</em> its input tank holds a matching amount of fluid. Both are consumed when the recipe finishes.</p>
 */
@NonnullDefault
public abstract class QuenchingRecipe extends MekanismRecipe<QuenchingRecipeInput> {

    /**
     * Checks if this recipe matches the given item and fluid.
     *
     * @param itemStack  Item being quenched.
     * @param fluidStack Contents of the quenching chamber's input tank.
     */
    public abstract boolean test(ItemStack itemStack, FluidStack fluidStack);

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
