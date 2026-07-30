package safe.flerovium.mixin.Block;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, remap = false)
public abstract class CrumblingRendererMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"), require = 0)
    private void skipFarCrumbling(CallbackInfo ci) {
    }
}