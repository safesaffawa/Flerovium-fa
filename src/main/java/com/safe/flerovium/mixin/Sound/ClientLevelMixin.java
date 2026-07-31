package com.safe.flerovium.mixin.Sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ClientLevel.class}
)
public abstract class ClientLevelMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   public ClientLevelMixin() {
   }

   @Inject(
      method = {"playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPlaySoundDistanceCull(double x, double y, double z, SoundEvent soundEvent, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed, CallbackInfo ci) {
      if (!distanceDelay) {
         double d = this.minecraft.gameRenderer.getCamera().position().distanceToSqr(x, y, z);
         double r = (double)soundEvent.getRange(volume);
         if (d > r * r + (double)1.0F) {
            ci.cancel();
         }

      }
   }
}
