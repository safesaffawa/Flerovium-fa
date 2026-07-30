package safe.flerovium.mixin.Item;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(targets = "net.minecraft.client.renderer.item.ItemModelResolver", remap = false)
public abstract class ItemRendererMixin {

    @Redirect(
        method = "resolveModel",
        at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"),
        require = 0
    )
    private boolean skipEmptyQuads(List<BakedQuad> quads) {
        return quads == null || quads.isEmpty();
    }
}