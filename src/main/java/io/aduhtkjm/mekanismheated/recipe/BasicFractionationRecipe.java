package io.aduhtkjm.mekanismheated.recipe;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.vanilla_input.SingleFluidRecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * A fractionation recipe that consumes a matching fluid from the tower's feed sump and splits it into its banked outputs.
 *
 * @see PassiveFractionationRecipe for the inputless variant that generates outputs from the environment.
 */
@NothingNullByDefault
public class BasicFractionationRecipe extends FractionationRecipe implements Predicate<@NotNull FluidStack> {

    private final FluidStackIngredient input;
    private final List<BankOutput> outputs;
    private final double minTemperature;
    private final double maxTemperature;
    private final double baseTemperature;

    /**
     * @param input           Fluid input ingredient, fed into the tower's sump.
     * @param outputs         One or more outputs, each targeting a bank index counted from the bottom of the tower.
     * @param minTemperature  Minimum temperature in Kelvin required to process; must be greater than zero.
     * @param baseTemperature Temperature in Kelvin for nominal (one operation per tick) speed; must be at least the minimum.
     */
    public BasicFractionationRecipe(FluidStackIngredient input, List<BankOutput> outputs, double minTemperature, double maxTemperature, double baseTemperature) {
        this.input = Objects.requireNonNull(input, "Fluid input cannot be null.");
        validate(outputs, minTemperature, baseTemperature);
        this.outputs = List.copyOf(outputs);
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.baseTemperature = baseTemperature;
    }

    @Override
    public boolean matches(SingleFluidRecipeInput input, Level level) {
        return !isIncomplete() && test(input.fluid());
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        return input.test(fluidStack);
    }

    /**
     * Gets the fluid ingredient fed through the valves into the sump.
     */
    public FluidStackIngredient getInput() {
        return input;
    }

    @Override
    public List<BankOutput> getOutputs() {
        return outputs;
    }

    /**
     * For serializer use. DO NOT MODIFY RETURN VALUE.
     */
    public List<BankOutput> getOutputsRaw() {
        return outputs;
    }

    @Override
    public double getMinTemperature() {
        return minTemperature;
    }

    @Override
    public double getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    public double getBaseTemperature() {
        return baseTemperature;
    }

    @Override
    public RecipeType<BasicFractionationRecipe> getType() {
        return ModRecipeTypes.TYPE_FRACTIONATING.value();
    }

    @Override
    public RecipeSerializer<BasicFractionationRecipe> getSerializer() {
        return ModRecipeSerializers.FRACTIONATING.get();
    }

    /**
     * Convenience helper returning the amount of input fluid one operation consumes, or zero if the stack does not match.
     */
    public int getInputAmount(FluidStack stored) {
        FluidStack match = input.getMatchingInstance(stored);
        return match.isEmpty() ? 0 : match.getAmount();
    }
}
