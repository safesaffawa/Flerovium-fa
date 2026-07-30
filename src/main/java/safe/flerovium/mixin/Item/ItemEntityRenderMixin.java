package safe.flerovium.mixin.Item;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemEntityRenderer.class, remap = false)
public abstract class ItemEntityRenderMixin {

    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void skipFarItems(
        ItemEntity entity, float yaw, float tick,
        com.mojang.blaze3d.vertex.PoseStack poseStack,
        Object bufferSource, int packedLight, CallbackInfo ci
    ) {
        var camera = net.minecraft.client.Minecraft.getInstance().gameRenderer.mainCamera();
        double distSq = entity.distanceToSqr(camera.position());
        if (distSq > 4096.0) ci.cancel();
    }
}