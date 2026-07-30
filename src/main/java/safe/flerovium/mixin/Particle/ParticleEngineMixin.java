package safe.flerovium.mixin.Particle;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ParticleEngine.class, remap = false)
public abstract class ParticleEngineMixin {

    /**
     * 用球体测试替代 AABB 测试，加速粒子视锥剔除。
     * 
     * ⚠️ 26.2 注意：
     * - render 方法签名可能变了（参数顺序/类型）
     * - Frustum 内部字段 camX/camY/camZ 可能被封装
     * - intersection 字段可能改名
     * 
     * 如果编译通过但运行时 crash，用 IDE 检查 Frustum 源码确认字段名。
     */
    @Redirect(
        method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/culling/Frustum;isVisible(Lnet/minecraft/world/phys/AABB;)Z"
        )
    )
    boolean fastFrustumCheck(Frustum instance, AABB aabb, @Local Particle particle) {
        // 边界粒子（无有效包围盒）直接通过
        if (particle.getBoundingBox().minX == Double.NEGATIVE_INFINITY) {
            return true;
        }

        // 计算粒子中心相对于摄像机的偏移
        float x = (float) (particle.x - instance.camX);
        float y = (float) (particle.y - instance.camY);
        float z = (float) (particle.z - instance.camZ);

        // 用包围球近似 AABB
        float width = (float) Math.max(aabb.maxX - aabb.minX, aabb.maxZ - aabb.minZ);
        float height = (float) (aabb.maxY - aabb.minY);
        float max = Math.max(width, height);
        float min = Math.min(width, height);
        float radius = (max + 0.4142f * min) * 0.5f;

        return instance.intersection.testSphere(x, y, z, radius);
    }
}