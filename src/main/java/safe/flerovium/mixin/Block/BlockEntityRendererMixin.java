package safe.flerovium.mixin.Block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds distance-based culling to block entity rendering.
 * In MC 26.2, block entities are rendered via BlockEntityRenderDispatcher.tryExtractRenderState()
 * which converts BlockEntity → BlockEntityRenderState for later rendering.
 * We intercept here to skip creating render states for distant block entities.
 */
@Mixin(value = BlockEntityRenderDispatcher.class, remap = false)
public abstract class BlockEntityRendererMixin {

    @Inject(method = "tryExtractRenderState", at = @At("HEAD"), cancellable = true, require = 0)
    private void skipFarBlockEntities(BlockEntity blockEntity, float tickDelta,
                                       ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
                                       boolean global, CallbackInfoReturnable<BlockEntityRenderState> cir) {
        var cam = Minecraft.getInstance().gameRenderer.mainCamera().position();
        var pos = blockEntity.getBlockPos();
        double dx = cam.x - (pos.getX() + 0.5);
        double dy = cam.y - (pos.getY() + 0.5);
        double dz = cam.z - (pos.getZ() + 0.5);
        if (dx * dx + dy * dy + dz * dz > 64.0 * 64.0) {
            cir.setReturnValue(null);
        }
    }
}

