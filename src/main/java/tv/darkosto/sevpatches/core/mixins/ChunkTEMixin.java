package tv.darkosto.sevpatches.core.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.darkosto.sevpatches.SevPatches;

@Mixin(value = Chunk.class, priority = 1500)
public abstract class ChunkTEMixin {
    private boolean loadingTileEntities = false;

    @Shadow
    public abstract IBlockState getBlockState(BlockPos pos);

    @Inject(method = "createNewTileEntity", at = @At(value = "RETURN"), cancellable = true, require = 1, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void skipTileEntityIfLoading(BlockPos blockPos, CallbackInfoReturnable<TileEntity> cir, IBlockState iblockstate, Block block) {
        if (this.loadingTileEntities && block.hasTileEntity(iblockstate)) {
            SevPatches.LOGGER.warn("#########################################################################");
            SevPatches.LOGGER.warn("A mod has attempted to add a TileEntity to a chunk during chunk loading; this may result in iterator invalidation");
            SevPatches.LOGGER.warn("This attempt has been blocked, which may lead to some weirdness, e.g. pipes not connecting");
            SevPatches.LOGGER.warn("TE at: {} for block {}", blockPos, block.getRegistryName());
            SevPatches.LOGGER.warn("Stack trace:", new Throwable());
            SevPatches.LOGGER.warn("#########################################################################");
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "onLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addTileEntities(Ljava/util/Collection;)V"), require = 1)
    private void setLoadingTileEntitiesOn(CallbackInfo ci) {
        this.loadingTileEntities = true;
    }

    @Inject(method = "onLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addTileEntities(Ljava/util/Collection;)V", shift = At.Shift.AFTER), require = 1)
    private void setLoadingTileEntitiesOff(CallbackInfo ci) {
        this.loadingTileEntities = false;
    }
}
