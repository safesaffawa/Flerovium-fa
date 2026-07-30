package safe.flerovium.mixin.Particle;

import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ParticleEngine.class, remap = false)
public abstract class ParticleEngineMixin {
}