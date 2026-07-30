package safe.flerovium.mixin.Item;

import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemDisplayContext.class, remap = false)
public abstract class ItemTransformMixin {

    @Shadow(remap = false)
    public Vector3f rotation;

    @Shadow(remap = false)
    public Vector3f translation;

    @Shadow(remap = false)
    public Vector3f scale;

    @Inject(method = "apply", at = @At("HEAD"), require = 0)
    private void onApply(boolean leftHand, com.mojang.blaze3d.vertex.PoseStack poseStack, CallbackInfo ci) {
    }
}