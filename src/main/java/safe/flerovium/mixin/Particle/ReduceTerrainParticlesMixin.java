package safe.flerovium.mixin.Particle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ParticleEngine.class, remap = false)
public abstract class ReduceTerrainParticlesMixin {

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    void skipFarDestroy(BlockPos pos, BlockState state, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        Camera cam = client.gameRenderer.mainCamera();
        Vec3 camPos = cam.position();
        Vec3 blockPos = Vec3.atCenterOf(pos);

        double dx = blockPos.x - camPos.x;
        double dy = blockPos.y - camPos.y;
        double dz = blockPos.z - camPos.z;
        if (dx * dx + dy * dy + dz * dz > 64.0) ci.cancel();
    }
}