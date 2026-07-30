package safe.flerovium.mixin.Particle;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;

/**
 * This mixin is excluded when particle_core mod is present.
 * It ensures particle systems work correctly with Flerovium's optimizations.
 */
@Mixin(value = Particle.class, remap = false)
public abstract class ParticleMixin {
    // This mixin exists to provide compatibility with the MixinPlugin's
    // shouldApplyMixin logic for particle_core detection.
    // The actual mixin logic is handled by other particle mixins.
}
