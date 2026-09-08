package io.aduhtkjm.mekanismheated.recipe;

import io.aduhtkjm.mekanismheated.Config;
import io.aduhtkjm.mekanismheated.Mod;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.heat.ISidedHeatHandler;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Input: ItemStack
 * <br>
 * Output: ItemStack
 * <br>
 * Condition: the processing machine must be at least {@link #getTemperatureThreshold()} Kelvin to process the recipe.
 *
 * @apiNote Heated smelting recipes are used by heat-powered machines that require a minimum temperature, in Kelvin, to
 * process a recipe. Whether a given machine is hot enough is checked via {@link #canProcess(ISidedHeatHandler)} against the
 * machine's current temperature.
 */
@NothingNullByDefault
public abstract class HeatedItemStackToItemStackRecipe extends ItemStackToItemStackRecipe {

    private static final Holder<Item> HEAT_SMELTER = DeferredHolder.create(Registries.ITEM,
          ResourceLocation.fromNamespaceAndPath(Mod.MODID, "heat_smelter"));

    protected final double temperatureThreshold;

    /**
     * The recipe's total heat cost over its whole processing, or {@code null} to use the {@link Config.HeatSmelter#HEAT_PER_SMELT}
     * default. The default is resolved lazily (see {@link #getHeatConsumed()}) so config changes apply without a recipe reload.
     */
    @SuppressWarnings("all") // use optional
    protected final Optional<Double> heatConsumption;

    /**
     * @param recipeType          The recipe type this recipe belongs to.
     * @param temperatureThreshold Minimum temperature, in Kelvin, the processing machine must have to process this recipe.
     *                             Must be greater than zero.
     */
    @SuppressWarnings("all")
    public HeatedItemStackToItemStackRecipe(RecipeType<HeatedItemStackToItemStackRecipe> recipeType, double temperatureThreshold) {
        this(recipeType, temperatureThreshold, null);
    }

    /**
     * @param recipeType          The recipe type this recipe belongs to.
     * @param temperatureThreshold Minimum temperature, in Kelvin, the processing machine must have to process this recipe.
     *                             Must be greater than zero.
     * @param heatConsumption     Total heat the recipe consumes over its whole processing, in heat units (Joules); must be
     *                            greater than zero when present. {@code null} falls back to {@link Config.HeatSmelter#HEAT_PER_SMELT}.
     */
    @SuppressWarnings("all")
    public HeatedItemStackToItemStackRecipe(RecipeType<HeatedItemStackToItemStackRecipe> recipeType, double temperatureThreshold,
          Optional<Double> heatConsumption) {
        // We have to do this because Java generics are invariant.
        // It should be safe.
        super((RecipeType<ItemStackToItemStackRecipe>) (RecipeType<?>) recipeType);
        if (temperatureThreshold <= 0) {
            throw new IllegalArgumentException("Temperature threshold must be greater than zero.");
        }
        this.temperatureThreshold = temperatureThreshold;
        if (heatConsumption.orElse(0.0D) <= 0) {
            throw new IllegalArgumentException("Heat consumption must be greater than zero.");
        }
        this.heatConsumption = heatConsumption;
    }

    /**
     * Gets the minimum temperature, in Kelvin, the processing machine must have to process this recipe.
     */
    public double getTemperatureThreshold() {
        return temperatureThreshold;
    }

    /**
     * Gets the recipe's raw total heat consumption, or {@code null} if it was not specified and the config default applies.
     * For serializer use.
     */
    public Optional<Double> getHeatConsumption() {
        return heatConsumption;
    }

    /**
     * Gets the total heat this recipe consumes over its whole processing, in heat units (Joules). The heat is drawn from
     * the processing machine's heat capacitor spread proportionally over the recipe's processing ticks (so faster
     * processing consumes heat faster). Recipes that do not specify their own "heat" cost use
     * {@link Config.HeatSmelter#HEAT_PER_SMELT}.
     */
    public double getHeatConsumed() {
        return heatConsumption.orElse(Config.HeatSmelter.HEAT_PER_SMELT.get());
    }

    /**
     * Checks if the given machine is hot enough to process this recipe.
     *
     * @param machine The machine that would process the recipe.
     *
     * @return {@code true} if the machine's temperature is at least {@link #getTemperatureThreshold()}.
     */
    public boolean canProcess(ISidedHeatHandler machine) {
        return machine.getTotalTemperature() >= temperatureThreshold;
    }

    @Override
    public String getGroup() {
        return "heated_smelting";
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(HEAT_SMELTER);
    }
}
