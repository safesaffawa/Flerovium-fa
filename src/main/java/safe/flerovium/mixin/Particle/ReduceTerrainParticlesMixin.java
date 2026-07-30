package safe.flerovium.mixin.Particle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ParticleEngine.class, remap = false)
public abstract class ReduceTerrainParticlesMixin {

    /**
     * 跳过远处或视野外的方块破坏粒子。
     * 距离 > 8格 直接跳过；
     * 距离 2~8格 根据视角角度渐进跳过。
     * 
     * ⚠️ 26.2 注意：
     * - destroy 方法签名：确认参数是 (BlockPos, BlockState)
     * - Minecraft.getInstance() 应该不变
     * - gameRenderer.getMainCamera() 应该不变
     * - Camera.getLookVector() 返回类型可能是 Vector3f（JOML）
     */
    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    void skipFarDestroy(BlockPos pos, BlockState state, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        Camera cam = client.gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();
        Vec3 blockPos = pos.getCenter();

        double dx = blockPos.x - camPos.x;
        double dy = blockPos.y - camPos.y;
        double dz = blockPos.z - camPos.z;
        double distSq = dx * dx + dy * dy + dz * dz;

        // 超过 8 格直接跳过
        if (distSq > 64.0) {
            ci.cancel();
            return;
        }

        // 视野角度检测
        Vector3f look = cam.getLookVector();
        double len = Math.sqrt(distSq);
        float dot = (float) ((dx * look.x + dy * look.y + dz * look.z) / len);

        // 距离越远，要求越正对才生成粒子
        float threshold = Mth.lerp(
            Mth.clamp((float) len - 2, 0, 6) / 6.0f,
            0.5f,   // 2格处 ≈ 60°
            0.98f   // 8格处 ≈ 11°
        );

        if (dot < threshold) {
            ci.cancel();
        }
    }
}