package safe.flerovium.mixin.Particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SingleQuadParticle.class, priority = 100)
public abstract class SingleQuadParticleMixin extends Particle {
    @Unique long flerovium$lastTick = -1;
    @Unique int flerovium$cachedLight = 0;

    protected SingleQuadParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(method = "getLightColor", at = @At("HEAD"), cancellable = true, require = 0)
    private void cacheLightColor(float partialTick, CallbackInfoReturnable<Integer> cir) {
        long tick = Minecraft.getInstance().clientTickCount;
        if (tick == flerovium$lastTick) {
            cir.setReturnValue(flerovium$cachedLight);
        }
    }

    @Inject(method = "getLightColor", at = @At("RETURN"), require = 0)
    private void cacheLightColorReturn(float partialTick, CallbackInfoReturnable<Integer> cir) {
        flerovium$lastTick = Minecraft.getInstance().clientTickCount;
        flerovium$cachedLight = cir.getReturnValue();
    }
}