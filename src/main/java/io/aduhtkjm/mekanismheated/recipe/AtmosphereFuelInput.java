package io.aduhtkjm.mekanismheated.recipe;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Combined input for {@link AtmosphereFuelRecipe}s: the atmosphere heater's item slot contents and gas tank contents.
 * Both are optional (the item may be empty and the chemical may be null); a fuel recipe only ever consumes one of the
 * two.
 */
public record AtmosphereFuelInput(@NotNull ItemStack item, @Nullable ChemicalStack chemical) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? item : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
