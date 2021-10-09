package tv.darkosto.sevpatches;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import realdrops.entities.EntityItemLoot;
import slimeknights.tconstruct.tools.ranged.RangedEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod(modid = "sevpatches", acceptableRemoteVersions = "*")
public class SevPatches {
    public static Logger LOGGER = LogManager.getLogger("sevpatches");
    List<EntityItem> itemList = new ArrayList<>();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        if (event.getSide().isServer())
            registerRangedEvents();
        MinecraftForge.EVENT_BUS.register(this);
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

    /**
     * DarkPacks/SevTech-Ages#4179 pt2
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || event.isCanceled() || event.getEntity().isDead) return;

        if (event.getEntity().getClass() != EntityItem.class) return;

        itemList.add((EntityItem) event.getEntity());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (EntityItem item : itemList) {
            if (item.isDead || item.getItem().isEmpty()) continue;

            item.world.spawnEntity(new EntityItemLoot(item));

            item.setDead();
            item.setItem(ItemStack.EMPTY);
        }

        itemList.clear();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        itemList.clear();
    }
}
