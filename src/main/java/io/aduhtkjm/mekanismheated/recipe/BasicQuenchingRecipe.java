package io.aduhtkjm.mekanismheated.recipe;

import io.aduhtkjm.mekanismheated.Mod;
import java.util.List;
import java.util.Objects;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Contract;

@NothingNullByDefault
public class BasicQuenchingRecipe extends QuenchingRecipe {

    private static final Holder<Item> QUENCHING_ENRICHMENT_CHAMBER = DeferredHolder.create(Registries.ITEM,
          ResourceLocation.fromNamespaceAndPath(Mod.MODID, "quenching_enrichment_chamber"));

    protected final ItemStackIngredient itemInput;
    protected final FluidStackIngredient fluidInput;
    protected final FluidStackIngredient output;

    /**
     * @param itemInput  Item input.
     * @param fluidInput Fluid input.
     * @param output     Output fluid.
     */
    public BasicQuenchingRecipe(ItemStackIngredient itemInput, FluidStackIngredient fluidInput, FluidStackIngredient output) {
        this.itemInput = Objects.requireNonNull(itemInput, "Item input cannot be null.");
        this.fluidInput = Objects.requireNonNull(fluidInput, "Fluid input cannot be null.");
        this.output = Objects.requireNonNull(output, "Output cannot be null.");
    }

    @Override
    public boolean test(ItemStack itemStack, FluidStack fluidStack) {
        return itemInput.test(itemStack) && fluidInput.test(fluidStack);
    }

    @Override
    public ItemStackIngredient getItemInput() {
        return itemInput;
    }

    @Override
    public FluidStackIngredient getFluidInput() {
        return fluidInput;
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public FluidStack getOutput(ItemStack itemStack, FluidStack fluidStack) {
        for (FluidStack representation : output.getRepresentations()) {
            if (!(representation.getFluid() instanceof Flowing)) {
                return representation.copy();
            }
        }
        List<FluidStack> reps = output.getRepresentations();
        return reps.isEmpty() ? FluidStack.EMPTY : reps.getFirst().copy();
    }

    @Override
    public FluidStackIngredient getOutputIngredient() {
        return output;
    }

    @Override
    public List<FluidStack> getOutputDefinition() {
        return output.getRepresentations();
    }

    /**
     * For Serializer use. DO NOT MODIFY RETURN VALUE.
     */
    public FluidStackIngredient getOutputRaw() {
        return output;
    }

    @Override
    public RecipeSerializer<BasicQuenchingRecipe> getSerializer() {
        return ModRecipeSerializers.QUENCHING.get();
    }

    @Override
    public RecipeType<QuenchingRecipe> getType() {
        return ModRecipeTypes.TYPE_QUENCHING.value();
    }

    @Override
    public String getGroup() {
        return "quenching";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(QUENCHING_ENRICHMENT_CHAMBER);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicQuenchingRecipe other = (BasicQuenchingRecipe) o;
        return itemInput.equals(other.itemInput) && fluidInput.equals(other.fluidInput) && output.equals(other.output);
    }

    @Override
    public int hashCode() {
        int result = itemInput.hashCode();
        result = 31 * result + fluidInput.hashCode();
        result = 31 * result + output.hashCode();
        return result;
    }
}
