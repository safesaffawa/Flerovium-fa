package safe.flerovium.mixin.Block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

@Mixin(value = BlockEntityRenderer.class, remap = false)
public interface BlockEntityRendererMixin<T extends BlockEntity> {

    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    default void skipFarBlockEntities(T blockEntity, float partialTick, PoseStack poseStack,
                                       MultiBufferSource bufferSource, int packedLight, int packedOverlay,
                                       CallbackInfo ci) {
        var cam = Minecraft.getInstance().gameRenderer.mainCamera().position();
        double dx = cam.x - (blockEntity.getBlockPos().getX() + 0.5);
        double dy = cam.y - (blockEntity.getBlockPos().getY() + 0.5);
        double dz = cam.z - (blockEntity.getBlockPos().getZ() + 0.5);
        if (dx * dx + dy * dy + dz * dz > 64.0 * 64.0) {
            ci.cancel();
        }
    }
}
