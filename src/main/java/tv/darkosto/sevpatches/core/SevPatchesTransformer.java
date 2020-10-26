package tv.darkosto.sevpatches.core;

import net.minecraft.launchwrapper.IClassTransformer;
import tv.darkosto.sevpatches.core.patches.PatchAstralAmulet;
import tv.darkosto.sevpatches.core.patches.PatchAstralBootesCheat;
import tv.darkosto.sevpatches.core.patches.PatchHarvestOOO;
import tv.darkosto.sevpatches.core.patches.PatchInControlHandlerPriority;
import tv.darkosto.sevpatches.core.patches.PatchJaffFishAreFish;
import tv.darkosto.sevpatches.core.patches.PatchJaffFishLiveInWater;
import tv.darkosto.sevpatches.core.patches.PatchJaffSpawnRemover;
import tv.darkosto.sevpatches.core.patches.PatchMinecraftSpawnChunkSpawning;
import tv.darkosto.sevpatches.core.patches.PatchPrimalDrying;
import tv.darkosto.sevpatches.core.patches.PatchPrimalNicerHammerHeads;
import tv.darkosto.sevpatches.core.patches.PatchPrimalScaredyCat;
import tv.darkosto.sevpatches.core.patches.PatchPrimalSpreading;
import tv.darkosto.sevpatches.core.patches.PatchRidHandlerDeregister;

public class SevPatchesTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            case "net.minecraft.world.WorldEntitySpawner":
                return new PatchMinecraftSpawnChunkSpawning(basicClass).apply();
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
            default:
                return basicClass;
        }
    }
}
