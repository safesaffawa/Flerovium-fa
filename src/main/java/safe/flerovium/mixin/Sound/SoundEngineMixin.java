package safe.flerovium.mixin.Sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMixin {

    /**
     * 限制同时播放的音效数量 / 跳过远处音效。
     * 
     * 26.2: SoundEngine 类名不变。
     * play() 方法签名: play(SoundInstance)
     */
    @Inject(
        method = "play",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void limitSounds(SoundInstance sound, CallbackInfo ci) {
        // 例如：如果当前播放的音效数超过阈值，跳过新音效
        // 或者：如果音效来源距离玩家太远，跳过
    }

    /**
     * 降低远处音效的音量。
     */
    @Inject(
        method = "play",
        at = @At("TAIL"),
        require = 0
    )
    private void attenuateDistantSounds(SoundInstance sound, CallbackInfo ci) {
        // 根据距离衰减音量
    }
}