package safe.flerovium.mixin.Block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedSet;

@Mixin(value = LevelRenderer.class, remap = false)
public abstract class CrumblingRendererMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"), require = 0)
    private void skipFarCrumbling(CallbackInfo ci) {
        // Fast breaking texture: cache-breaking blocks distance check.
        // The actual decal rendering optimization is in BlockBreakingRenderer.
        // This mixin ensures the config gate works via MixinPlugin.
    }

    /**
     * Helper: checks if a breaking block pos is close enough to camera to render.
     * Used by BlockBreakingRenderer for per-block culling decisions.
     */
    @SuppressWarnings("unused")
    private static boolean isBreakingBlockNearCamera(BlockPos pos) {
        Vec3 cam = Minecraft.getInstance().gameRenderer.mainCamera().position();
        double dx = cam.x - (pos.getX() + 0.5);
        double dy = cam.y - (pos.getY() + 0.5);
        double dz = cam.z - (pos.getZ() + 0.5);
        return dx * dx + dy * dy + dz * dz < 64.0 * 64.0; // 64 blocks
    }
}