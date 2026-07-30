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

    @Unique
    long flerovium$lastTick = -1;

    @Unique
    int flerovium$cachedLight = 0;

    protected SingleQuadParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    /**
     * 缓存光照计算结果，同一 tick 内不重复计算。
     * 
     * ⚠️ 26.2 注意：
     * - SingleQuadParticle 类是否还存在（可能改名或合并）
     * - getLightColor(float) 方法签名确认
     * - Minecraft.getInstance().clientTickCount 字段名确认
     *   （可能叫 tickCount / clientTick / renderTick 等）
     */
    @Override
    protected int getLightColor(float partialTick) {
        long tickCount = Minecraft.getInstance().clientTickCount;
        if (tickCount == flerovium$lastTick) {
            return flerovium$cachedLight;
        }
        flerovium$lastTick = tickCount;
        flerovium$cachedLight = super.getLightColor(partialTick);
        return flerovium$cachedLight;
    }
}