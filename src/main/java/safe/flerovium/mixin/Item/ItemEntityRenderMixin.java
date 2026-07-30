package safe.flerovium.mixin.render;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemEntityRenderer.class, remap = false)
public abstract class ItemEntityRenderMixin {

    /**
     * 减少远处掉落物的渲染复杂度。
     * 
     * 26.2: 
     * - 类名: ItemEntityRenderer（官方名）
     * - render 方法签名: render(ItemEntity, float, float, PoseStack, MultiBufferSource, int)
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void skipFarItems(
        ItemEntity entity,
        float entityYaw,
        float partialTick,
        com.mojang.blaze3d.vertex.PoseStack poseStack,
        net.minecraft.client.renderer.MultiBufferSource bufferSource,
        int packedLight,
        CallbackInfo ci
    ) {
        // 如果掉落物距离摄像机太远，跳过渲染
        // 或者：减少远处掉落物的渲染层数（只渲染1层而非堆叠）
        var camera = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera();
        double distSq = entity.distanceToSqr(camera.getPosition());
        
        if (distSq > 4096.0) { // 64格以外
            ci.cancel();
        }
    }
}