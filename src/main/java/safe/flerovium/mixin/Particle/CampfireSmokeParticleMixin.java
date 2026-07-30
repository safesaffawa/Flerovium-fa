package safe.flerovium.mixin.Particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CampfireSmokeParticle.class, remap = false)
public abstract class CampfireSmokeParticleMixin extends Particle {

    protected CampfireSmokeParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void beforeTick(CallbackInfo ci) {
        if (age <= 2 || !this.hasPhysics) {
            BlockPos bottom = BlockPos.containing(this.x, this.y - 0.1, this.z);
            this.hasPhysics = !this.level.canSeeSky(bottom);
        }
    }
}