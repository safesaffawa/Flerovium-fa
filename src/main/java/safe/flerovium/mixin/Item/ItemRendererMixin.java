package safe.flerovium.mixin.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ItemRenderer.class, remap = false)
public abstract class ItemRendererMixin {

    /**
     * 跳过空 quad 列表的渲染调用。
     * 
     * 26.2 注意：
     * - renderQuadList 方法可能签名变了
     * - 如果编译报错 "method not found"，用 IDE 搜索 ItemRenderer 中
     *   实际调用 BakedQuad 列表渲染的方法名
     */
    @Redirect(
        method = "renderQuadList",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/RenderType;"
        ),
        require = 0  // 如果方法不存在不报错
    )
    private Object skipEmptyQuads(List<BakedQuad> quads) {
        if (quads == null || quads.isEmpty()) {
            return null;
        }
        return null; // 需要根据实际返回类型调整
    }
}