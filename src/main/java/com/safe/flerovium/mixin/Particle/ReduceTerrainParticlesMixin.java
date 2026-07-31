package com.safe.flerovium.mixin.Particle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ClientLevel.class}
)
public abstract class ReduceTerrainParticlesMixin {
   public ReduceTerrainParticlesMixin() {
   }

   @Inject(
      method = {"addDestroyBlockEffect"},
      at = {@At("HEAD")},
      cancellable = true
   )
   void skipFarDestroy(BlockPos pos, BlockState state, CallbackInfo ci) {
      Minecraft client = Minecraft.getInstance();
      Camera cam = client.gameRenderer.getCamera();
      Vec3 camPos = cam.position();
      Vec3 blockPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      double dx = blockPos.x - camPos.x;
      double dy = blockPos.y - camPos.y;
      double dz = blockPos.z - camPos.z;
      double distSq = dx * dx + dy * dy + dz * dz;
      if (distSq > (double)64.0F) {
         ci.cancel();
      } else {
         Vector3fc look = cam.forwardVector();
         double len = Math.sqrt(distSq);
         float dot = (float)((dx * (double)look.x() + dy * (double)look.y() + dz * (double)look.z()) / len);
         float threshold = Mth.lerp(Mth.clamp((float)len - 2.0F, 0.0F, 6.0F) / 6.0F, 0.5F, 0.98F);
         if (dot < threshold) {
            ci.cancel();
         }

      }
   }
}
