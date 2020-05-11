package tv.darkosto.sevpatches.core.mixins;

import hellfirepvp.astralsorcery.common.enchantment.amulet.EnchantmentUpgradeHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(value = EnchantmentHelper.class, priority = 1500)
public class EnchantmentHelperMixin {
    @Inject(method = "getEnchantmentLevel", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private static void getNewEnchantmentLevelEarly(Enchantment enchID, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(EnchantmentUpgradeHelper.getNewEnchantmentLevel(0, enchID, stack));
    }

    @Inject(method = "getEnchantmentLevel", at = @At(value = "RETURN", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void getNewEnchantmentLevel(Enchantment enchID, ItemStack stack, CallbackInfoReturnable<Integer> cir, NBTTagList nbttaglist, int i, NBTTagCompound nbttagcompound, Enchantment enchantment) {
        int lvl = nbttagcompound.getShort("lvl");
        if (enchantment == enchID) {
            cir.setReturnValue(EnchantmentUpgradeHelper.getNewEnchantmentLevel(lvl, enchID, stack));
        }
    }

    @Inject(method = "getEnchantmentLevel", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private static void getNewEnchantmentLevelLate(Enchantment enchID, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(EnchantmentUpgradeHelper.getNewEnchantmentLevel(0, enchID, stack));
    }

    @Inject(method = "getEnchantments", at = @At(value = "RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void applyNewEnchantmentLevels(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir, Map<Enchantment, Integer> map, NBTTagList nbttaglist) {
        cir.setReturnValue(EnchantmentUpgradeHelper.applyNewEnchantmentLevels(map, stack));
    }

    @Redirect(method = "applyEnchantmentModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantmentTagList()Lnet/minecraft/nbt/NBTTagList;"))
    private static NBTTagList getModifiedEnchantmentTagList(ItemStack itemStack) {
        return EnchantmentUpgradeHelper.modifyEnchantmentTags(itemStack.getEnchantmentTagList(), itemStack);
    }
}
