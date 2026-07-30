package safe.flerovium.mixin.Block;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 优化方块破坏裂纹渲染。
 * 
 * 26.2 注意：
 * - "CrumblingRenderer" 不是独立类，裂纹渲染在 LevelRenderer 中
 * - 方法名可能是 "renderHitOutline" 或 "renderDestroyProgress"
 * - 用 IDE 搜索 LevelRenderer 中包含 "destroy" 或 "crack" 的方法
 */
@Mixin(value = LevelRenderer.class, remap = false)
public abstract class CrumblingRendererMixin {

    /**
     * 跳过远处方块的破坏裂纹渲染。
     */
    @Inject(
        method = "renderLevel",  // 或具体的裂纹渲染方法
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void skipFarCrumbling(CallbackInfo ci) {
        // 距离检测逻辑
        // 如果玩家距离破坏方块太远，直接 cancel
    }
}