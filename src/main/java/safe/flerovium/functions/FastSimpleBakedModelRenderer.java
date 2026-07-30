package safe.flerovium.functions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.caffeinemc.mods.sodium.api.util.ColorMixer;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.List;

import static safe.flerovium.functions.MathUtil.*;

public class FastSimpleBakedModelRenderer {
    private static final MemoryStack STACK = MemoryStack.create();
    public static final int VERTEX_COUNT = 4;
    public static final int BUFFER_VERTEX_COUNT = 48;
    public static final int STRIDE = 8;
    private static final long SCRATCH_BUFFER = MemoryUtil.nmemAlignedAlloc(64, BUFFER_VERTEX_COUNT * EntityVertex.STRIDE);
    private static long BUFFER_PTR = SCRATCH_BUFFER;
    private static int BUFFED_VERTEX = 0;

    private static void flush(VertexBufferWriter writer) {
        if (BUFFED_VERTEX == 0) return;
        STACK.push();
        writer.push(STACK, SCRATCH_BUFFER, BUFFED_VERTEX, EntityVertex.FORMAT);
        STACK.pop();
        BUFFER_PTR = SCRATCH_BUFFER;
        BUFFED_VERTEX = 0;
    }

    private static boolean isBufferMax() {
        return BUFFED_VERTEX >= BUFFER_VERTEX_COUNT;
    }

    private static void putBulkData(VertexBufferWriter writer, PoseStack.Pose pose, BakedQuad bakedQuad, int light,
                                    int overlay, int color, int faces) {
        ModelQuadView quad = (ModelQuadView) bakedQuad;
        Matrix4f pose_matrix = pose.pose();
        int pn = quad.getFaceNormal();
        float nx = MatrixHelper.transformNormalX(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        float ny = MatrixHelper.transformNormalY(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        float nz = MatrixHelper.transformNormalZ(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        int n = packSafe(nx, ny, nz);

        float p0x = MatrixHelper.transformPositionX(pose_matrix, quad.getX(0), quad.getY(0), quad.getZ(0));
        float p0y = MatrixHelper.transformPositionY(pose_matrix, quad.getX(0), quad.getY(0), quad.getZ(0));
        float p0z = MatrixHelper.transformPositionZ(pose_matrix, quad.getX(0), quad.getY(0), quad.getZ(0));
        float p2x = MatrixHelper.transformPositionX(pose_matrix, quad.getX(2), quad.getY(2), quad.getZ(2));
        float p2y = MatrixHelper.transformPositionY(pose_matrix, quad.getX(2), quad.getY(2), quad.getZ(2));
        float p2z = MatrixHelper.transformPositionZ(pose_matrix, quad.getX(2), quad.getY(2), quad.getZ(2));

        if ((faces & 0b1000000) != 0) {
            if ((p0x + p2x) * nx + (p0y + p2y) * ny + (p0z + p2z) * nz > 0)
                return;
        }
        float p1x = MatrixHelper.transformPositionX(pose_matrix, quad.getX(1), quad.getY(1), quad.getZ(1));
        float p1y = MatrixHelper.transformPositionY(pose_matrix, quad.getX(1), quad.getY(1), quad.getZ(1));
        float p1z = MatrixHelper.transformPositionZ(pose_matrix, quad.getX(1), quad.getY(1), quad.getZ(1));
        float p3x = MatrixHelper.transformPositionX(pose_matrix, quad.getX(3), quad.getY(3), quad.getZ(3));
        float p3y = MatrixHelper.transformPositionY(pose_matrix, quad.getX(3), quad.getY(3), quad.getZ(3));
        float p3z = MatrixHelper.transformPositionZ(pose_matrix, quad.getX(3), quad.getY(3), quad.getZ(3));

        final int c = color != -1 ? ColorMixer.mulComponentWise(color, quad.getColor(0)) : quad.getColor(0);
        final int l = Math.max(((quad.getLight(0) & 0xffff) << 16) | (quad.getLight(0) >> 16), light);
        long P = BUFFER_PTR;
        EntityVertex.write(P, p0x, p0y, p0z, c, Float.floatToIntBits(quad.getTexU(0)), Float.floatToIntBits(quad.getTexV(0)), overlay, l, n); P += EntityVertex.STRIDE;
        EntityVertex.write(P, p1x, p1y, p1z, c, Float.floatToIntBits(quad.getTexU(1)), Float.floatToIntBits(quad.getTexV(1)), overlay, l, n); P += EntityVertex.STRIDE;
        EntityVertex.write(P, p2x, p2y, p2z, c, Float.floatToIntBits(quad.getTexU(2)), Float.floatToIntBits(quad.getTexV(2)), overlay, l, n); P += EntityVertex.STRIDE;
        EntityVertex.write(P, p3x, p3y, p3z, c, Float.floatToIntBits(quad.getTexU(3)), Float.floatToIntBits(quad.getTexV(3)), overlay, l, n); P += EntityVertex.STRIDE;

        BUFFER_PTR = P;
        BUFFED_VERTEX += VERTEX_COUNT;
        if (isBufferMax()) flush(writer);
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexBufferWriter writer, int faces, List<BakedQuad> bakedQuads,
                                       int light, int overlay, int packedColor) {
        for (BakedQuad bakedQuad : bakedQuads) {
            BakedQuadView quad = (BakedQuadView) bakedQuad;
            if ((faces & (1 << bakedQuad.getDirection().ordinal())) == 0) {
                if (quad.getSprite() != null) SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
                continue;
            }
            int color = packedColor;
            if (quad.hasColor()) {
                color = ColorARGB.toABGR(quad.getColor(0));
            }
            putBulkData(writer, pose, bakedQuad, light, overlay, color, faces);
            if (quad.getSprite() != null) SpriteUtil.INSTANCE.markSpriteActive(quad.getSprite());
        }
    }

    public static void render(List<BakedQuad> quads, int faces, int packedLight, int packedOverlay,
                              PoseStack poseStack, VertexBufferWriter writer) {
        PoseStack.Pose pose = poseStack.last();
        renderQuadList(pose, writer, faces, quads, packedLight, packedOverlay, 0xFFFFFFFF);
        flush(writer);
    }
}
