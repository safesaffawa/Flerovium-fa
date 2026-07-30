package safe.flerovium.mixin.render;

import net.minecraft.client.renderer.block.model.ItemTransform;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemTransform.class, remap = false)
public abstract class ItemTransformMixin {

    @Shadow
    public Vector3f rotation;

    @Shadow
    public Vector3f translation;

    @Shadow
    public Vector3f scale;

    /**
     * 优化 ItemTransform 的应用。
     * 
     * 26.2: ItemTransform 字段名是官方的 rotation/translation/scale。
     * 类型是 org.joml.Vector3f（不是旧的 com.mojang.math.Vector3f）。
     */
    @Inject(method = "apply", at = @At("HEAD"), require = 0)
    private void onApply(boolean leftHand, com.mojang.blaze3d.vertex.PoseStack poseStack, CallbackInfo ci) {
        // 优化逻辑：例如跳过 identity transform
        // 如果 rotation/translation/scale 都是默认值，可以提前返回
    }
}