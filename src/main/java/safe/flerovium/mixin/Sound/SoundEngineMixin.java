package safe.flerovium.mixin.Sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMixin {
    @Unique
    private static int flerovium$soundPlayCount = 0;
    @Unique
    private static long flerovium$lastTickReset = 0;
    @Unique
    private static final int MAX_SOUNDS_PER_TICK = 64;

    @Inject(method = "play", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SoundInstance;resolve(Lnet/minecraft/client/sounds/SoundManager;)Lnet/minecraft/client/sounds/WeighedSoundEvents;",
            shift = At.Shift.AFTER), cancellable = true, require = 0)
    private void onPlaySound(SoundInstance sound, CallbackInfoReturnable<Void> cir) {
        var s = sound.getSound();
        if (s == null) return;
        if (s.shouldStream()) return;

        long tick = Minecraft.getInstance().clientTickCount;
        if (tick != flerovium$lastTickReset) {
            flerovium$lastTickReset = tick;
            flerovium$soundPlayCount = 0;
        }
        if (flerovium$soundPlayCount++ >= MAX_SOUNDS_PER_TICK) {
            cir.cancel();
        }
    }
}