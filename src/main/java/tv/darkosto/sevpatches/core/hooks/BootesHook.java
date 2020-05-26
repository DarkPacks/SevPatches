package tv.darkosto.sevpatches.core.hooks;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectBootes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BootesHook {
    public static boolean handleBootesDrops(List<ItemStack> stacks, World world, EntityLivingBase entity, boolean did, Random rand) {
        ArrayList<EntityItem> drops = Lists.newArrayList();
        for (ItemStack stack : stacks) {
            EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY, entity.posZ, stack);
            entityItem.setDefaultPickupDelay();
            drops.add(entityItem);
        }
        if (!ForgeHooks.onLivingDrops(entity, CommonProxy.dmgSourceStellar, drops, 0, false)) {
            for (EntityItem item : drops) {
                if (rand.nextFloat() < CEffectBootes.dropChance) {
                    world.spawnEntity(item);
                    did = true;
                }
            }
        }

        return did;
    }
}
