package safe.flerovium.mixin.Item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemEntityRenderer.class, remap = false)
public abstract class ItemEntityRenderMixin extends EntityRenderer<ItemEntity> {
    protected ItemEntityRenderMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Redirect(
            method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    private void skipSuperRender(EntityRenderer instance, Entity entity, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource buffer, int light) {
        // NeoForge posts a RenderNameTagEvent here. Fabric has no direct equivalent event.
        // The super.render() call is skipped to avoid double rendering.
    }
}
