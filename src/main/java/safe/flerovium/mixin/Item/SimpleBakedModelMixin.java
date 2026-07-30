package safe.flerovium.mixin.render;

import net.minecraft.client.resources.model.SimpleBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleBakedModel.class, remap = false)
public abstract class SimpleBakedModelMixin {

    /**
     * 在 SimpleBakedModel 构建完成后进行优化处理。
     * 
     * 26.2 注意：
     * - 构造器签名可能变了（Builder 模式）
     * - 如果是 Builder 模式，target 改为 Builder 的 build() 方法
     */
    @Inject(method = "<init>", at = @At("TAIL"), require = 0)
    private void onInit(CallbackInfo ci) {
        // 优化逻辑：例如缓存 quad 列表、预计算包围盒等
    }
}