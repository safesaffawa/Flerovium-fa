package safe.flerovium.functions.BlockBreaking;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockBreakingRenderer {
    static private final RandomSource random = RandomSource.create();

    static public int calcVisibleFaces(Vec3 camPos, int blockX, int blockY, int blockZ) {
        int faces = 0;
        double cx = camPos.x - (blockX + 0.5);
        double cy = camPos.y - (blockY + 0.5);
        double cz = camPos.z - (blockZ + 0.5);

        for (Direction dir : Direction.values()) {
            double dot = dir.getStepX() * cx + dir.getStepY() * cy + dir.getStepZ() * cz;
            if (dot > 0) {
                faces |= 1 << dir.ordinal();
            }
        }
        return faces;
    }

    static public void renderBreakingTexture(
            Object blockModelResolver, Vec3 camPos, BlockState state, BlockPos pos,
            BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer) {
        if (state.getRenderShape() != RenderShape.MODEL) return;
    }
}
