package safe.flerovium.mixin.Sound;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientLevel.class, remap = false)
public abstract class ClientLevelMixin {

    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void cachedSkyColor(net.minecraft.world.phys.Vec3 cameraPos, float partialTick, CallbackInfoReturnable<net.minecraft.world.phys.Vec3> cir) {
    }

    @Inject(method = "tickEntities", at = @At("HEAD"), require = 0)
    private void onTickEntities(CallbackInfo ci) {
    }
}