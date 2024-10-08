package tv.darkosto.sevpatches.core;

import net.minecraft.launchwrapper.IClassTransformer;
import tv.darkosto.sevpatches.core.patches.*;

public class SevPatchesTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            case "net.minecraft.util.MouseHelper":
                return new PatchMacMouse(basicClass).apply();
            case "net.minecraft.world.WorldEntitySpawner":
                return new PatchMinecraftSpawnChunkSpawning(basicClass).apply();
            case "com.TominoCZ.FBP.gui.FBPGuiBlacklist":
            case "com.TominoCZ.FBP.handler.FBPKeyInputHandler":
                return new PatchMacMouseFBP(basicClass).apply();
            case "com.tmtravlr.jaff.JAFFMod":
                return new PatchJaffFishLiveInWater(basicClass).apply();
            case "com.tmtravlr.jaff.JAFFEventHandler":
                return new PatchJaffSpawnRemover(basicClass).apply();
            case "com.tmtravlr.jaff.entities.EntityFish":
                return new PatchJaffFishAreFish(basicClass).apply();
            case "hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectBootes":
                return new PatchAstralBootesCheat(basicClass).apply();
            case "hellfirepvp.astralsorcery.common.enchantment.amulet.EnchantmentUpgradeHelper":
                return new PatchAstralAmulet(basicClass).apply();
            case "hellfirepvp.astralsorcery.common.item.tool.ItemCrystalAxe":
            case "hellfirepvp.astralsorcery.common.item.tool.ItemCrystalPickaxe":
                return new PatchAstralTools(basicClass).apply();
            case "mcjty.incontrol.ForgeEventHandlers":
                return new PatchInControlHandlerPriority(basicClass).apply();
            case "nmd.primal.core.common.entities.living.EntityHammerHead":
                return new PatchPrimalNicerHammerHeads(basicClass).apply();
            case "nmd.primal.core.common.entities.living.EntityCanisCampestris":
                return new PatchPrimalScaredyCat(basicClass).apply();
            case "realdrops.handlers.EventHandler":
                return new PatchRidHandlerDeregister(basicClass).apply();
            case "tehnut.harvest.ReplantHandlers":
                return new PatchHarvestOOO(basicClass).apply();
            case "nmd.primal.core.api.interfaces.ISpreadBlock":
                return new PatchPrimalSpreading(basicClass).apply();
            case "nmd.primal.core.common.blocks.saxum.MudDrying":
                return new PatchPrimalDrying(basicClass).apply();
            case "micdoodle8.mods.galacticraft.core.tile.TileEntityInventory":
                return new PatchGalacticraftInventories(basicClass).apply();
            case "micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling":
                return new PatchGalacticraftSlimeling(basicClass).apply();
            case "net.darkhax.infoaccessories.info.InfoType":
                return new PatchInfoAccCompass(basicClass).apply();
            case "net.darkhax.jmapstages.JMapPermissionHandler":
                return new PatchJMapStagesStutter(basicClass).apply();
            default:
                return basicClass;
        }
    }
}
