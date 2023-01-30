package tv.darkosto.sevpatches.core.mixins;

import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tv.darkosto.sevpatches.SevPatches;

@Mixin(value = TileEntityBeacon.class, priority = 1500)
public class TileEntityBeaconMixin {
    @Redirect(method = "addEffectsToPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;expand(DDD)Lnet/minecraft/util/math/AxisAlignedBB;", ordinal = 0))
    public AxisAlignedBB expandProxy(AxisAlignedBB box, double x, double y, double z) {
        return box.grow(x, y, z);
    }
}
