package safe.flerovium.mixin.Particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = CampfireSmokeParticle.class, remap = false)
public abstract class CampfireSmokeParticleMixin extends Particle {

    protected CampfireSmokeParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    /**
     * 在粒子移动前检测是否被碰撞停止，
     * 并动态设置 hasPhysics（基于上方是否有天空）。
     * 
     * ⚠️ 26.2 注意：
     * - CampfireSmokeParticle 可能不再是独立类（可能合并到通用烟雾粒子）
     * - move(DDD)V 方法签名应该不变
     * - hasPhysics 字段名应该不变（官方名）
     * - level.canSeeSky() 应该不变
     */
    @ModifyArgs(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/CampfireSmokeParticle;move(DDD)V"),
        require = 0
    )
    void beforeMove(Args args) {
        if (((ParticleAccessor) this).isStoppedByCollision()) {
            return;
        }
        if (age <= 2 || !this.hasPhysics) {
            BlockPos bottom = BlockPos.containing(this.x, this.y - 0.1, this.z);
            this.hasPhysics = !this.level.canSeeSky(bottom);
        }
    }
}