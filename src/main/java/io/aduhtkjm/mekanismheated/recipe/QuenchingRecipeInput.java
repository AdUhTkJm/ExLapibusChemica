package io.aduhtkjm.mekanismheated.recipe;

import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Recipe input for {@link QuenchingRecipe}s: the item being quenched plus the current contents of the quenching
 * chamber's input tank. Both are required for a recipe to match.
 */
@NothingNullByDefault
public record QuenchingRecipeInput(ItemStack item, FluidStack fluid) implements RecipeInput {

    public static final QuenchingRecipeInput EMPTY = new QuenchingRecipeInput(ItemStack.EMPTY, FluidStack.EMPTY);

    @Override
    public ItemStack getItem(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("No item for index " + index);
        }
        return item;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty() || fluid.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuenchingRecipeInput other = (QuenchingRecipeInput) o;
        return ItemStack.matches(item, other.item) && FluidStack.matches(fluid, other.fluid);
    }

    @Override
    public int hashCode() {
        int hash = ItemStack.hashItemAndComponents(item);
        hash = 31 * hash + item.getCount();
        hash = 31 * hash + FluidStack.hashFluidAndComponents(fluid);
        hash = 31 * hash + fluid.getAmount();
        return hash;
    }
}
