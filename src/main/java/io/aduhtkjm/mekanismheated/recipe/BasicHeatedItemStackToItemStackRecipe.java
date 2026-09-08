package io.aduhtkjm.mekanismheated.recipe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@NothingNullByDefault
public class BasicHeatedItemStackToItemStackRecipe extends HeatedItemStackToItemStackRecipe {

    protected final ItemStackIngredient input;
    protected final ItemStack output;

    /**
     * @param input               Input.
     * @param output              Output.
     * @param temperatureThreshold Minimum temperature, in Kelvin, the processing machine must have to process this recipe.
     *                             Must be greater than zero.
     */
    public BasicHeatedItemStackToItemStackRecipe(ItemStackIngredient input, ItemStack output, double temperatureThreshold) {
        this(input, output, temperatureThreshold, Optional.empty());
    }

    /**
     * @param input               Input.
     * @param output              Output.
     * @param temperatureThreshold Minimum temperature, in Kelvin, the processing machine must have to process this recipe.
     *                             Must be greater than zero.
     * @param heatConsumption     Total heat the recipe consumes over its whole processing, in heat units (Joules); must be
     *                            greater than zero when present. {@code null} falls back to the config default.
     */
    @SuppressWarnings("all") // use optional
    public BasicHeatedItemStackToItemStackRecipe(ItemStackIngredient input, ItemStack output, double temperatureThreshold,
          Optional<Double> heatConsumption) {
        super(ModRecipeTypes.TYPE_HEATED_SMELTING.value(), temperatureThreshold, heatConsumption);
        this.input = Objects.requireNonNull(input, "Input cannot be null.");
        Objects.requireNonNull(output, "Output cannot be null.");
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Output cannot be empty.");
        }
        this.output = output.copy();
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return input.test(itemStack);
    }

    @Override
    public ItemStackIngredient getInput() {
        return input;
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public ItemStack getOutput(ItemStack input) {
        return output.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public List<ItemStack> getOutputDefinition() {
        return Collections.singletonList(output);
    }

    /**
     * For Serializer use. DO NOT MODIFY RETURN VALUE.
     *
     * @return the uncopied basic output
     */
    public ItemStack getOutputRaw() {
        return output;
    }

    @Override
    public RecipeSerializer<BasicHeatedItemStackToItemStackRecipe> getSerializer() {
        return ModRecipeSerializers.HEATED_SMELTING.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicHeatedItemStackToItemStackRecipe other = (BasicHeatedItemStackToItemStackRecipe) o;
        return Double.compare(temperatureThreshold, other.temperatureThreshold) == 0 && Objects.equals(heatConsumption, other.heatConsumption)
              && input.equals(other.input) && ItemStack.matches(output, other.output);
    }

    @Override
    public int hashCode() {
        int result = input.hashCode();
        result = 31 * result + ItemStack.hashItemAndComponents(output);
        result = 31 * result + output.getCount();
        result = 31 * result + Double.hashCode(temperatureThreshold);
        result = 31 * result + Objects.hashCode(heatConsumption);
        return result;
    }
}
