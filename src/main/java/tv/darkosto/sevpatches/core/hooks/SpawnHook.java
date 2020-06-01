package tv.darkosto.sevpatches.core.hooks;

import net.minecraft.util.math.BlockPos;
import tv.darkosto.sevpatches.config.SevPatchesConfig;

public class SpawnHook {
    public static double distanceSqRedirect(BlockPos blockPos, double x, double y, double z) {
        if (SevPatchesConfig.worldSpawnBlocksSpawns) return blockPos.distanceSq(x, y, z);
        return 576.0;
    }
}
