package safe.flerovium.mixin.Sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMixin {

    @Inject(method = "play", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SoundInstance;resolve(Lnet/minecraft/client/sounds/SoundManager;)Lnet/minecraft/client/sounds/WeighedSoundEvents;",
            shift = At.Shift.AFTER), cancellable = true, require = 0)
    private void onPlaySound(SoundInstance sound, CallbackInfo ci) {
        var s = sound.getSound();
        if (s == null) return;
        if (s.shouldStream()) return;
    }
}