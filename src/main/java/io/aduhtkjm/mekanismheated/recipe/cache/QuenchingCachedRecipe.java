package io.aduhtkjm.mekanismheated.recipe.cache;

import io.aduhtkjm.mekanismheated.recipe.QuenchingRecipe;
import io.aduhtkjm.mekanismheated.tile.TileEntityQuenchingEnrichmentChamber;
import java.util.function.BooleanSupplier;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cached recipe for the Quenching Enrichment Chamber, whose recipes take a required item plus a required fluid input and
 * produce a single fluid. Both inputs are consumed on completion.
 */
@NothingNullByDefault
public class QuenchingCachedRecipe extends CachedRecipe<QuenchingRecipe> {

    private final IInputHandler<@NotNull ItemStack> itemInputHandler;
    private final IInputHandler<@NotNull FluidStack> fluidInputHandler;
    private final IOutputHandler<@NotNull FluidStack> fluidOutputHandler;

    private ItemStack recipeItem = ItemStack.EMPTY;
    private FluidStack recipeFluid = FluidStack.EMPTY;
    //Note: Our output shouldn't be null in places it is actually used, but we mark it as nullable, so we don't have to initialize it
    @Nullable
    private FluidStack output;

    /**
     * @param recipe             Recipe.
     * @param recheckAllErrors   Returns {@code true} if processing should be continued even if an error is hit in order to gather all the errors. It is recommended
     *                           to not do this every tick or if there is no one viewing recipes.
     * @param itemInputHandler   Item input handler.
     * @param fluidInputHandler  Fluid input handler.
     * @param fluidOutputHandler Fluid output handler.
     */
    public QuenchingCachedRecipe(QuenchingRecipe recipe, BooleanSupplier recheckAllErrors, IInputHandler<@NotNull ItemStack> itemInputHandler,
          IInputHandler<@NotNull FluidStack> fluidInputHandler, IOutputHandler<@NotNull FluidStack> fluidOutputHandler) {
        super(recipe, recheckAllErrors);
        this.itemInputHandler = itemInputHandler;
        this.fluidInputHandler = fluidInputHandler;
        this.fluidOutputHandler = fluidOutputHandler;
    }

    @Override
    protected void calculateOperationsThisTick(OperationTracker tracker) {
        super.calculateOperationsThisTick(tracker);
        if (tracker.shouldContinueChecking()) {
            recipeItem = itemInputHandler.getRecipeInput(recipe.getItemInput());
            //Test to make sure we can even perform a single operation. This is akin to !recipe.test(item)
            if (recipeItem.isEmpty()) {
                //No item, we don't know if the recipe matches or not so treat it as not matching
                tracker.mismatchedRecipe();
                return;
            }
            recipeFluid = fluidInputHandler.getRecipeInput(recipe.getFluidInput());
            //Test to make sure the tank can supply the fluid this recipe needs
            if (recipeFluid.isEmpty()) {
                //Reset progress so a recipe that regains its fluid does not carry over partial processing
                tracker.resetProgress(TileEntityQuenchingEnrichmentChamber.NOT_ENOUGH_FLUID_INPUT_ERROR);
                return;
            }
            //Calculate the current max based on the item input
            itemInputHandler.calculateOperationsCanSupport(tracker, recipeItem);
            if (tracker.shouldContinueChecking()) {
                //Calculate the current max based on the fluid input
                fluidInputHandler.calculateOperationsCanSupport(tracker, recipeFluid);
                if (tracker.shouldContinueChecking()) {
                    output = recipe.getOutput(recipeItem, recipeFluid);
                    //Calculate the max based on the space in the output
                    fluidOutputHandler.calculateOperationsCanSupport(tracker, output);
                }
            }
        }
    }

    @Override
    public boolean isInputValid() {
        ItemStack item = itemInputHandler.getInput();
        if (item.isEmpty()) {
            return false;
        }
        return recipe.test(item, fluidInputHandler.getInput());
    }

    @Override
    protected void finishProcessing(int operations) {
        //Validate something didn't go horribly wrong
        if (output != null && !recipeItem.isEmpty() && !recipeFluid.isEmpty()) {
            itemInputHandler.use(recipeItem, operations);
            fluidInputHandler.use(recipeFluid, operations);
            fluidOutputHandler.handleOutput(output, operations);
        }
    }

    @Override
    protected void resetCache() {
        super.resetCache();
        recipeItem = ItemStack.EMPTY;
        recipeFluid = FluidStack.EMPTY;
        output = null;
    }
}
