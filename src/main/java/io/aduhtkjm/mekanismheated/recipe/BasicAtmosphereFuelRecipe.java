package io.aduhtkjm.mekanismheated.recipe;

import java.util.Objects;
import java.util.Optional;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @param itemInput     Item input, mutually exclusive with {@code chemicalInput}.
 * @param chemicalInput Chemical (gas) input, mutually exclusive with {@code itemInput}.
 * @param reduction     Energy consumption reduction (FE/RF per tick) one full operation provides, must be greater than zero.
 */
@NothingNullByDefault
public class BasicAtmosphereFuelRecipe extends AtmosphereFuelRecipe {
    @SuppressWarnings("all")
    private final Optional<ItemStackIngredient> itemInput;
    @SuppressWarnings("all")
    private final Optional<ChemicalStackIngredient> chemicalInput;
    private final long reduction;

    @SuppressWarnings("all") // use optional
    public BasicAtmosphereFuelRecipe(Optional<ItemStackIngredient> itemInput,
                                     Optional<ChemicalStackIngredient> chemicalInput, long reduction) {
        this.itemInput = Objects.requireNonNull(itemInput, "Item input cannot be null.");
        this.chemicalInput = Objects.requireNonNull(chemicalInput, "Chemical input cannot be null.");
        if (itemInput.isEmpty() == chemicalInput.isEmpty()) {
            throw new IllegalArgumentException("Exactly one of item input or chemical input must be present.");
        }
        if ((this.reduction = reduction) <= 0) {
            throw new IllegalArgumentException("Reduction must be greater than zero.");
        }
    }

    public BasicAtmosphereFuelRecipe(ItemStackIngredient itemInput, long reduction) {
        this(Optional.of(itemInput), Optional.empty(), reduction);
    }

    public BasicAtmosphereFuelRecipe(ChemicalStackIngredient chemicalInput, long reduction) {
        this(Optional.empty(), Optional.of(chemicalInput), reduction);
    }

    @Override
    public Optional<ItemStackIngredient> getItemInput() {
        return itemInput;
    }

    @Override
    public Optional<ChemicalStackIngredient> getChemicalInput() {
        return chemicalInput;
    }

    @Override
    public long getReduction() {
        return reduction;
    }

    @Override
    public RecipeSerializer<BasicAtmosphereFuelRecipe> getSerializer() {
        return ModRecipeSerializers.ATMOSPHERE_FUEL.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicAtmosphereFuelRecipe other = (BasicAtmosphereFuelRecipe) o;
        return reduction == other.reduction && itemInput.equals(other.itemInput) && chemicalInput.equals(other.chemicalInput);
    }

    @Override
    public int hashCode() {
        int result = itemInput.hashCode();
        result = 31 * result + chemicalInput.hashCode();
        result = 31 * result + Long.hashCode(reduction);
        return result;
    }
}
