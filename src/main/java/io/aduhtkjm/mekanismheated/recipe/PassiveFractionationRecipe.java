package io.aduhtkjm.mekanismheated.recipe;

import java.util.List;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.vanilla_input.SingleFluidRecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A fractionation recipe with no input: it generates its banked outputs from the environment (think of "harvesting" the
 * ambient air). It only runs while the tower's feed sump is empty, and otherwise obeys the same temperature window rules as
 * {@link BasicFractionationRecipe}.
 */
@NothingNullByDefault
public class PassiveFractionationRecipe extends FractionationRecipe {

    private final List<BankOutput> outputs;
    private final double minTemperature;
    private final double maxTemperature;
    private final double baseTemperature;

    /**
     * @param outputs         One or more outputs, each targeting a bank index counted from the bottom of the tower.
     * @param minTemperature  Minimum temperature in Kelvin required to process; must be greater than zero.
     * @param baseTemperature Temperature in Kelvin for nominal (one operation per tick) speed; must be at least the minimum.
     */
    public PassiveFractionationRecipe(List<BankOutput> outputs, double minTemperature, double maxTemperature, double baseTemperature) {
        validate(outputs, minTemperature, baseTemperature);
        this.outputs = List.copyOf(outputs);
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.baseTemperature = baseTemperature;
    }

    /**
     * Passive recipes produce their outputs from the environment, so they match whenever they are complete; the (empty)
     * fluid input is ignored. The tower enforces the "sump must be empty" restriction itself, not the recipe.
     */
    @Override
    public boolean matches(SingleFluidRecipeInput input, Level level) {
        return !isIncomplete();
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
    public RecipeType<PassiveFractionationRecipe> getType() {
        return ModRecipeTypes.TYPE_FRACTIONATING_PASSIVE.value();
    }

    @Override
    public RecipeSerializer<PassiveFractionationRecipe> getSerializer() {
        return ModRecipeSerializers.FRACTIONATING_PASSIVE.get();
    }
}
