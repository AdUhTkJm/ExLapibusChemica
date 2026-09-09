package io.aduhtkjm.mekanismheated;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Mod.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static class HeatSmelter {
        public static ModConfigSpec.IntValue BASE_SPEED;
        public static ModConfigSpec.IntValue FLUID_CAPACITY;
        public static ModConfigSpec.DoubleValue FULL_SPEED_TEMPERATURE;
        public static ModConfigSpec.DoubleValue BASE_TEMPERATURE;
        public static ModConfigSpec.DoubleValue HEAT_CAPACITY;
        public static ModConfigSpec.DoubleValue INVERSE_CONDUCTION_COEFFICIENT;
        public static ModConfigSpec.DoubleValue INVERSE_INSULATION_COEFFICIENT;
        public static ModConfigSpec.DoubleValue MAX_FUEL_TEMPERATURE;
        public static ModConfigSpec.DoubleValue HEAT_PER_SMELT;
        public static ModConfigSpec.DoubleValue MAX_HEAT_MULTIPLIER;
    }

    public static class Shaker {
        public static ModConfigSpec.IntValue BASE_SPEED;
        public static ModConfigSpec.LongValue ENERGY_PER_TICK;
        public static ModConfigSpec.LongValue MAX_ENERGY;
    }

    public static class Condenser {
        public static ModConfigSpec.IntValue BASE_SPEED;
        public static ModConfigSpec.IntValue FLUID_CAPACITY;
        public static ModConfigSpec.DoubleValue MAX_TEMPERATURE;
        public static ModConfigSpec.DoubleValue FULL_SPEED_TEMPERATURE;
        public static ModConfigSpec.DoubleValue HEAT_CAPACITY;
        public static ModConfigSpec.DoubleValue INVERSE_CONDUCTION_COEFFICIENT;
        public static ModConfigSpec.DoubleValue INVERSE_INSULATION_COEFFICIENT;
    }

    public static class Fractionation {
        public static ModConfigSpec.IntValue TOWER_MAX_HEIGHT;
        public static ModConfigSpec.IntValue FLUID_PER_LAYER;
        public static ModConfigSpec.DoubleValue HEAT_CAPACITY_PER_HEIGHT;
        public static ModConfigSpec.DoubleValue HEAT_DISSIPATION;
    }

    public static class FusedNetwork {
        public static ModConfigSpec.IntValue ITEM_PULL_INTERVAL;
        public static ModConfigSpec.IntValue CHEMICAL_PULL_INTERVAL;
        public static ModConfigSpec.IntValue FLUID_PULL_INTERVAL;
        public static ModConfigSpec.IntValue ENERGY_PULL_INTERVAL;
        public static ModConfigSpec.IntValue HEAT_SIM_INTERVAL;
    }

    public static class Cooler {
        public static ModConfigSpec.DoubleValue EFFICIENCY;
        public static ModConfigSpec.DoubleValue HEAT_CAPACITY;
        public static ModConfigSpec.DoubleValue INVERSE_CONDUCTION_COEFFICIENT;
        public static ModConfigSpec.DoubleValue INVERSE_INSULATION_COEFFICIENT;
    }

    public static class ReactionChamber {
        public static ModConfigSpec.IntValue REACTION_INTERVAL;
        public static ModConfigSpec.IntValue CAPACITY;
        public static ModConfigSpec.IntValue MAX_OPERATIONS;
        public static ModConfigSpec.DoubleValue HEAT_CAPACITY;
        public static ModConfigSpec.DoubleValue INVERSE_CONDUCTION_COEFFICIENT;
        public static ModConfigSpec.DoubleValue INVERSE_INSULATION_COEFFICIENT;
    }

    public static class AtmosphereHeater {
        public static ModConfigSpec.IntValue WORK_INTERVAL;
        public static ModConfigSpec.LongValue ENERGY_PER_TICK;
        public static ModConfigSpec.LongValue MAX_ENERGY;
        public static ModConfigSpec.DoubleValue BASE_TEMP_RISE;
        public static ModConfigSpec.DoubleValue TEMPERATURE_SCALE;
        public static ModConfigSpec.DoubleValue CENTER_EFFECT;
        public static ModConfigSpec.DoubleValue OUTER_EFFECT;
        public static ModConfigSpec.IntValue CHUNK_RADIUS;
    }

    public static ModConfigSpec SPEC;
    static {
        BUILDER.push("heatSmelter");
        HeatSmelter.BASE_SPEED = BUILDER
            .comment("Base number of game ticks the heat smelter takes to complete a recipe when running at full speed.")
            .defineInRange("baseSpeed", 100, 1, Integer.MAX_VALUE);
        HeatSmelter.FLUID_CAPACITY = BUILDER
            .comment("The capacity of the output fluid buffer in heat smelter, in buckets.")
            .defineInRange("fluidCapacity", 36, 1, Integer.MAX_VALUE);
        HeatSmelter.FULL_SPEED_TEMPERATURE = BUILDER
            .comment("Temperature in Kelvin the Heat Smelter must reach to process recipes at 100% base speed.")
            .defineInRange("fullSpeedTemperature", 1_000D, 0D, Double.MAX_VALUE);
        HeatSmelter.BASE_TEMPERATURE = BUILDER
            .comment("Temperature in Kelvin below which the Heat Smelter cannot process recipes.")
            .defineInRange("baseTemperature", 300D, 0D, Double.MAX_VALUE);
        HeatSmelter.HEAT_CAPACITY = BUILDER
            .comment("Heat capacity of the Heat Smelter in J/K, controlling how quickly its temperature changes. Must be at least one.")
            .defineInRange("heatCapacity", 50D, 1D, Double.MAX_VALUE);
        HeatSmelter.INVERSE_CONDUCTION_COEFFICIENT = BUILDER
            .comment("Inverse conduction coefficient of the Heat Smelter, controlling how readily it exchanges heat with adjacent blocks (smaller means slower). Must be at least one.")
            .defineInRange("inverseConductionCoefficient", 5D, 1D, Double.MAX_VALUE);
        HeatSmelter.INVERSE_INSULATION_COEFFICIENT = BUILDER
            .comment("Inverse insulation coefficient of the Heat Smelter, controlling how readily it loses heat to the environment (smaller means slower). Must be at least one.")
            .defineInRange("inverseInsulationCoefficient", 3D, 1D, Double.MAX_VALUE);
        HeatSmelter.MAX_FUEL_TEMPERATURE = BUILDER
            .comment("Temperature in Kelvin at which the Heat Smelter stops burning fuel. Note the temperature can be raised by, e.g., resistive heaters beyond this point.")
            .defineInRange("maxFuelTemperature", 1_000D, 0D, Double.MAX_VALUE);
        HeatSmelter.HEAT_PER_SMELT = BUILDER
            .comment("Total heat consumed by the Heat Smelter per plain smelting recipe (spread over the recipe's processing ticks). Heated smelting and melting recipes can override their heat cost via their recipe's optional \"heat\" field; omitted values fall back to this.")
            .defineInRange("heatPerSmelt", 120D, 0D, Double.MAX_VALUE);
        HeatSmelter.MAX_HEAT_MULTIPLIER = BUILDER
            .comment("Cap on the large heat smelter's parallel processing heat multiplier. A batch of operations costs sqrt(operationCount)x one operation's heat (e.g. 8 operations cost sqrt(8)x, 64 operations cost 8x), making a large smelter more heat-efficient than the equivalent number of separate smelters. This caps the multiplier: 8 corresponds to sqrt(64), so structures larger than 64 blocks stop paying more heat while still processing everything at once.")
            .defineInRange("maxHeatMultiplier", 8D, 1D, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("shaker");
        Shaker.BASE_SPEED = BUILDER
            .comment("Base number of game ticks the shaker takes to complete a recipe.")
            .defineInRange("baseSpeed", 200, 1, Integer.MAX_VALUE);
        Shaker.ENERGY_PER_TICK = BUILDER
            .comment("Energy consumed per tick.")
            .defineInRange("energyPerTick", 40, 0, Long.MAX_VALUE);
        Shaker.MAX_ENERGY = BUILDER
            .comment("Maximum amount of energy the shaker can hold.")
            .defineInRange("maxEnergy", 80000, 0, Long.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("condenser");
        Condenser.BASE_SPEED = BUILDER
            .comment("Base number of game ticks the condenser takes to complete a recipe when running at full speed (coldest).")
            .defineInRange("baseSpeed", 200, 1, Integer.MAX_VALUE);
        Condenser.FLUID_CAPACITY = BUILDER
            .comment("The capacity of the input fluid buffer in the condenser, in buckets.")
            .defineInRange("fluidCapacity", 10, 1, Integer.MAX_VALUE);
        Condenser.MAX_TEMPERATURE = BUILDER
            .comment("Temperature in Kelvin at which the Condenser cannot process recipes (0% speed).")
            .defineInRange("maxTemperature", 500D, 0D, Double.MAX_VALUE);
        Condenser.FULL_SPEED_TEMPERATURE = BUILDER
            .comment("Temperature in Kelvin at which the Condenser processes recipes at 100% base speed.")
            .defineInRange("fullSpeedTemperature", 100D, 0D, Double.MAX_VALUE);
        Condenser.HEAT_CAPACITY = BUILDER
            .comment("Heat capacity of the Condenser in J/K, controlling how quickly its temperature changes. Must be at least one.")
            .defineInRange("heatCapacity", 50D, 1D, Double.MAX_VALUE);
        Condenser.INVERSE_CONDUCTION_COEFFICIENT = BUILDER
            .comment("Inverse conduction coefficient of the Condenser, controlling how readily it exchanges heat with adjacent blocks (smaller means faster). Must be at least one.")
            .defineInRange("inverseConductionCoefficient", 5D, 1D, Double.MAX_VALUE);
        Condenser.INVERSE_INSULATION_COEFFICIENT = BUILDER
            .comment("Inverse insulation coefficient of the Condenser, controlling how readily it exchanges heat with the environment (smaller means faster). Must be at least one.")
            .defineInRange("inverseInsulationCoefficient", 5D, 1D, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("fractionation");
        Fractionation.TOWER_MAX_HEIGHT = BUILDER
            .comment("The maximum height of fractionation tower.")
            .defineInRange("towerMaxHeight", 27, 4, 127);
        Fractionation.FLUID_PER_LAYER = BUILDER
            .comment("Fluid capacity in mB each interior block of height contributes to the feed sump or an output bank.")
            .defineInRange("fluidPerLayer", 10_000, 1, Integer.MAX_VALUE);
        Fractionation.HEAT_CAPACITY_PER_HEIGHT = BUILDER
            .comment("Heat capacity in J/K added per block of tower height. Must be at least one.")
            .defineInRange("heatCapacityPerHeight", 100D, 1D, Double.MAX_VALUE);
        Fractionation.HEAT_DISSIPATION = BUILDER
            .comment("Coefficient controlling how quickly the tower loses heat to the environment (larger means faster loss). Must be positive.")
            .defineInRange("heatDissipation", 1.0E-6D, 0D, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("fusedNetwork");
        FusedNetwork.ITEM_PULL_INTERVAL = BUILDER
            .comment("How often (in ticks) the fused network pulls items from pull sides. The pulled amount is scaled by this interval to keep the average rate constant. Pushing always happens every tick.")
            .defineInRange("itemPullInterval", 10, 1, Integer.MAX_VALUE);
        FusedNetwork.CHEMICAL_PULL_INTERVAL = BUILDER
            .comment("How often (in ticks) the fused network pulls chemicals from pull sides. The pulled amount is scaled by this interval to keep the average rate constant. Pushing always happens every tick.")
            .defineInRange("chemicalPullInterval", 2, 1, Integer.MAX_VALUE);
        FusedNetwork.FLUID_PULL_INTERVAL = BUILDER
            .comment("How often (in ticks) the fused network pulls fluids from pull sides. The pulled amount is scaled by this interval to keep the average rate constant. Pushing always happens every tick.")
            .defineInRange("fluidPullInterval", 2, 1, Integer.MAX_VALUE);
        FusedNetwork.ENERGY_PULL_INTERVAL = BUILDER
            .comment("How often (in ticks) the fused network pulls energy from pull sides. The pulled amount is scaled by this interval to keep the average rate constant. Pushing always happens every tick.")
            .defineInRange("energyPullInterval", 2, 1, Integer.MAX_VALUE);
        FusedNetwork.HEAT_SIM_INTERVAL = BUILDER
            .comment("How often (in ticks) the fused network runs its heat simulation. Heat transfers are scaled by this interval to keep the average rate constant.")
            .defineInRange("heatSimInterval", 1, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("cooler");
        Cooler.EFFICIENCY = BUILDER
            .comment("Heat pump coefficient of performance (COP). Heat moved per joule of energy consumed.")
            .defineInRange("efficiency", 2.0D, 0, Double.MAX_VALUE);
        Cooler.HEAT_CAPACITY = BUILDER
            .comment("Heat capacity of the Cooler in J/K, controlling how quickly its temperature changes. Must be at least one.")
            .defineInRange("heatCapacity", 100D, 1D, Double.MAX_VALUE);
        Cooler.INVERSE_CONDUCTION_COEFFICIENT = BUILDER
            .comment("Inverse conduction coefficient of the Cooler, controlling how readily it exchanges heat with adjacent blocks (smaller means faster). Must be at least one.")
            .defineInRange("inverseConductionCoefficient", 5D, 1D, Double.MAX_VALUE);
        Cooler.INVERSE_INSULATION_COEFFICIENT = BUILDER
            .comment("Inverse insulation coefficient of the Cooler, controlling how readily it loses heat to the environment (smaller means slower). Must be at least one.")
            .defineInRange("inverseInsulationCoefficient", 10D, 1D, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("reactionChamber");
        ReactionChamber.REACTION_INTERVAL = BUILDER
            .comment("How often (in game ticks) the reaction chamber executes its recipes. A reaction is also triggered immediately whenever the chamber's contents change.")
            .defineInRange("reactionInterval", 10, 1, Integer.MAX_VALUE);
        ReactionChamber.CAPACITY = BUILDER
            .comment("The total capacity of the reaction chamber's mixed fluid/chemical buffer, in buckets. Fluids and chemicals share this pool.")
            .defineInRange("capacity", 16, 1, Integer.MAX_VALUE);
        ReactionChamber.MAX_OPERATIONS = BUILDER
            .comment("Maximum number of reaction operations a single execution (tick-triggered or content-triggered) may perform before it stops and waits for the next execution. "
                  + "Guards against recipes that cyclically regenerate their own inputs, which could otherwise keep reacting forever.")
            .defineInRange("maxOperations", 4096, 1, Integer.MAX_VALUE);
        ReactionChamber.HEAT_CAPACITY = BUILDER
            .comment("Heat capacity of the reaction chamber in J/K, controlling how quickly its temperature changes. Must be at least one.")
            .defineInRange("heatCapacity", 100D, 1D, Double.MAX_VALUE);
        ReactionChamber.INVERSE_CONDUCTION_COEFFICIENT = BUILDER
            .comment("Inverse conduction coefficient of the reaction chamber, controlling how readily it exchanges heat with adjacent blocks (smaller means slower). Must be at least one.")
            .defineInRange("inverseConductionCoefficient", 5D, 1D, Double.MAX_VALUE);
        ReactionChamber.INVERSE_INSULATION_COEFFICIENT = BUILDER
            .comment("Inverse insulation coefficient of the reaction chamber, controlling how readily it loses heat to the environment (smaller means slower). Must be at least one.")
            .defineInRange("inverseInsulationCoefficient", 5D, 1D, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("atmosphereHeater");
        AtmosphereHeater.WORK_INTERVAL = BUILDER
            .comment("How often (in game ticks) the atmosphere heater performs a work cycle: it consumes fuel and energy and then warms the surrounding chunks.")
            .defineInRange("workInterval", 40, 1, Integer.MAX_VALUE);
        AtmosphereHeater.ENERGY_PER_TICK = BUILDER
            .comment("Base energy consumed per tick of a work cycle, in FE/RF per tick (converted to Mekanism Joules with Mekanism's FE conversion rate). Fuel inputs subtract from this per work cycle; the consumption never goes below zero, and any fuel reducing it beyond zero is still consumed in full.")
            .defineInRange("energyPerTick", 5_000, 0, Long.MAX_VALUE);
        AtmosphereHeater.MAX_ENERGY = BUILDER
            .comment("Maximum amount of energy the atmosphere heater can hold, in FE/RF.")
            .defineInRange("maxEnergy", 1_000_000, 0, Long.MAX_VALUE);
        AtmosphereHeater.BASE_TEMP_RISE = BUILDER
            .comment("Ambient temperature rise in Kelvin per work cycle at 0 K ambient, following dT = baseTempRise / 2^(T / temperatureScale) where T is the current effective ambient temperature in Kelvin.")
            .defineInRange("baseTempRise", 5D, 0D, Double.MAX_VALUE);
        AtmosphereHeater.TEMPERATURE_SCALE = BUILDER
            .comment("Temperature scale (Kelvin) in the denominator of the heater's dT formula: larger values make the temperature rise fall off more slowly as the ambient temperature climbs. Must be greater than zero.")
            .defineInRange("temperatureScale", 1_000D, 1.0E-9D, Double.MAX_VALUE);
        AtmosphereHeater.CENTER_EFFECT = BUILDER
            .comment("Fraction of the temperature rise applied to the chunk containing the machine (1.0 = 100%).")
            .defineInRange("centerEffect", 1.0D, 0D, 1D);
        AtmosphereHeater.OUTER_EFFECT = BUILDER
            .comment("Fraction of the temperature rise applied to each chunk surrounding the machine's chunk (0.5 = 50%).")
            .defineInRange("outerEffect", 0.5D, 0D, 1D);
        AtmosphereHeater.CHUNK_RADIUS = BUILDER
            .comment("Radius in chunks around the machine's own chunk that receive the outer effect. 1 corresponds to a 3x3 chunk area.")
            .defineInRange("chunkRadius", 1, 0, 32);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
