package io.aduhtkjm.mekanismheated.tile.multiblock;

import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockManager;

public class ModLargeHeatSmelter {

    private ModLargeHeatSmelter() {
    }

    public static final MultiblockManager<LargeHeatSmelterData> LARGE_HEAT_SMELTER_MANAGER =
          new MultiblockManager<>("large_heat_smelter", MultiblockCache::new, LargeHeatSmelterValidator::new);
}
