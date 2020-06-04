package tv.darkosto.sevpatches;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.tools.ranged.RangedEvents;

import java.util.Optional;

@Mod(modid = "sevpatches")
public class SevPatches {
    public static Logger LOGGER = LogManager.getLogger("sevpatches");

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        if (event.getSide().isServer())
            registerRangedEvents();
    }

    /**
     * DarkPacks/SevTech-Ages#4098
     */
    private void registerRangedEvents() {
        Optional<ModContainer> tcon = Loader.instance().getModList().stream().filter(mod -> mod.getModId().equals("tconstruct")).findAny();
        if (tcon.isPresent() && (new ComparableVersion(tcon.get().getVersion())).compareTo(new ComparableVersion("1.12.2-2.13.0.183")) <= 0) {
            LOGGER.info("Tinkers' Construct detected, registering RangedEvents");
            MinecraftForge.EVENT_BUS.register(RangedEvents.class);
        } else {
            LOGGER.info("Tinkers' Construct not found or is a fixed version");
        }
    }
}
