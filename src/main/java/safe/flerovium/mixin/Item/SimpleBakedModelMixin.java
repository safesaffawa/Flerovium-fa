package safe.flerovium.mixin.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.block.dispatch.BlockStateModelPart", remap = false)
public abstract class SimpleBakedModelMixin {

    @Inject(method = "getQuads", at = @At("TAIL"), require = 0)
    private void onGetQuads(CallbackInfo ci) {
    }
}