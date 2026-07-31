package com.safe.flerovium.mixin.Particle;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Particle.class})
public interface ParticleAccessor {
   @Accessor("stoppedByCollision")
   boolean isStoppedByCollision();
}
