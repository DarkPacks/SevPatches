package tv.darkosto.sevpatches.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = "sevpatches")
@Mod.EventBusSubscriber
public class SevPatchesConfig {
    @Config.Name("World Spawn Blocks Spawns")
    public static boolean worldSpawnBlocksSpawns = false;

    @SubscribeEvent
    public static void onConfigChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!event.getModID().equals("sevpatches")) {
            return;
        }
        ConfigManager.sync("SevPatches", Config.Type.INSTANCE);
    }
}
