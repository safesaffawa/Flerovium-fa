package safe.flerovium.mixin.Sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMixin {

    @Inject(method = "play", at = @At("HEAD"), cancellable = true, require = 0)
    private void limitSounds(SoundInstance sound, CallbackInfoReturnable<Void> cir) {
    }

    @Inject(method = "play", at = @At("TAIL"), require = 0)
    private void attenuateDistantSounds(SoundInstance sound, CallbackInfoReturnable<Void> cir) {
    }
}