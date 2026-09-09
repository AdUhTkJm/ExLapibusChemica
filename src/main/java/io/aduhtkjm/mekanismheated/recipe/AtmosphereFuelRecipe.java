package io.aduhtkjm.mekanismheated.recipe;

import java.util.Optional;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Input: a single item <strong>or</strong> a single chemical (gas).
 * <br>
 * Output: an energy-consumption reduction (in FE/RF per tick) for the Atmosphere Heater.
 *
 * @apiNote Atmosphere fuel recipes are used by the Atmosphere Heater to reduce the energy it consumes per work cycle.
 * A recipe has exactly one input: either an item ingredient or a chemical ingredient.
 */
@NothingNullByDefault
public abstract class AtmosphereFuelRecipe extends MekanismRecipe<AtmosphereFuelInput> {

    /**
     * Gets the item input ingredient, if this recipe consumes an item.
     */
    public abstract Optional<ItemStackIngredient> getItemInput();

    /**
     * Gets the chemical (gas) input ingredient, if this recipe consumes a chemical.
     */
    public abstract Optional<ChemicalStackIngredient> getChemicalInput();

    /**
     * @return The energy consumption reduction (in FE/RF per tick) one full operation of this recipe provides, applied
     * once per work cycle of the Atmosphere Heater.
     */
    public abstract long getReduction();

    /**
     * Checks whether this recipe consumes the given item stack (ignoring amount).
     */
    public boolean testItem(@NotNull ItemStack stack) {
        return getItemInput().map(ingredient -> ingredient.testType(stack)).orElse(false);
    }

    /**
     * Checks whether this recipe consumes the given chemical (ignoring amount).
     */
    public boolean testChemical(@NotNull ChemicalStack stack) {
        return getChemicalInput().map(ingredient -> ingredient.testType(stack)).orElse(false);
    }

    @Override
    public boolean matches(@NotNull AtmosphereFuelInput input, @NotNull Level level) {
        if (isIncomplete()) {
            return false;
        }
        return testItem(input.item()) || (input.chemical() != null && testChemical(input.chemical()));
    }

    @Override
    public boolean isIncomplete() {
        return getItemInput().map(ItemStackIngredient::hasNoMatchingInstances).orElse(false)
              || getChemicalInput().map(ChemicalStackIngredient::hasNoMatchingInstances).orElse(false);
    }

    @Override
    public void logMissingTags() {
        getItemInput().ifPresent(ItemStackIngredient::logMissingTags);
        getChemicalInput().ifPresent(ChemicalStackIngredient::logMissingTags);
    }

    @Override
    public final RecipeType<AtmosphereFuelRecipe> getType() {
        return ModRecipeTypes.TYPE_ATMOSPHERE_FUEL.value();
    }

    @Override
    public String getGroup() {
        return "atmosphere_fuel";
    }
}
