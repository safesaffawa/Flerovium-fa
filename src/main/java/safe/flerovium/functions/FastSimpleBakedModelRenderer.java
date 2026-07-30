package safe.flerovium.functions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.List;

import static safe.flerovium.functions.MathUtil.packSafe;

public class FastSimpleBakedModelRenderer {
    public static final int VERTEX_COUNT = 4;
    public static final int BUFFER_VERTEX_COUNT = 48;
    public static final int STRIDE = 8;
    private static final MemoryStack STACK = MemoryStack.create();
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

    private static boolean isBufferMax() { return BUFFED_VERTEX >= BUFFER_VERTEX_COUNT; }

    private static void putBulkData(VertexBufferWriter writer, PoseStack.Pose pose, BakedQuad bakedQuad, int light,
                                    int overlay, int color, int faces) {
        Object raw = bakedQuad; // Sodium BakedQuadMixin makes this work at runtime
        Matrix4f pm = pose.pose();
        int pn = ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) raw).getFaceNormal();
        float nx = MatrixHelper.transformNormalX(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        float ny = MatrixHelper.transformNormalY(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        float nz = MatrixHelper.transformNormalZ(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        int n = packSafe(nx, ny, nz);

        float p0x = MatrixHelper.transformPositionX(pm, qx(raw, 0), qy(raw, 0), qz(raw, 0));
        float p0y = MatrixHelper.transformPositionY(pm, qx(raw, 0), qy(raw, 0), qz(raw, 0));
        float p0z = MatrixHelper.transformPositionZ(pm, qx(raw, 0), qy(raw, 0), qz(raw, 0));
        float p2x = MatrixHelper.transformPositionX(pm, qx(raw, 2), qy(raw, 2), qz(raw, 2));
        float p2y = MatrixHelper.transformPositionY(pm, qx(raw, 2), qy(raw, 2), qz(raw, 2));
        float p2z = MatrixHelper.transformPositionZ(pm, qx(raw, 2), qy(raw, 2), qz(raw, 2));

        if ((faces & 0b1000000) != 0) {
            if ((p0x + p2x) * nx + (p0y + p2y) * ny + (p0z + p2z) * nz > 0) return;
        }
        float p1x = MatrixHelper.transformPositionX(pm, qx(raw, 1), qy(raw, 1), qz(raw, 1));
        float p1y = MatrixHelper.transformPositionY(pm, qx(raw, 1), qy(raw, 1), qz(raw, 1));
        float p1z = MatrixHelper.transformPositionZ(pm, qx(raw, 1), qy(raw, 1), qz(raw, 1));
        float p3x = MatrixHelper.transformPositionX(pm, qx(raw, 3), qy(raw, 3), qz(raw, 3));
        float p3y = MatrixHelper.transformPositionY(pm, qx(raw, 3), qy(raw, 3), qz(raw, 3));
        float p3z = MatrixHelper.transformPositionZ(pm, qx(raw, 3), qy(raw, 3), qz(raw, 3));

        int c = color != -1 ? color : -1;
        int l = Math.max(((qu(raw, 0) & 0xffff) << 16) | (qu(raw, 0) >> 16), light);
        long P = BUFFER_PTR;
        EntityVertex.write(P, p0x, p0y, p0z, c, Float.floatToIntBits(qtu(raw, 0)), Float.floatToIntBits(qtv(raw, 0)), overlay, l, n); P += EntityVertex.STRIDE;
        EntityVertex.write(P, p1x, p1y, p1z, c, Float.floatToIntBits(qtu(raw, 1)), Float.floatToIntBits(qtv(raw, 1)), overlay, l, n); P += EntityVertex.STRIDE;
        EntityVertex.write(P, p2x, p2y, p2z, c, Float.floatToIntBits(qtu(raw, 2)), Float.floatToIntBits(qtv(raw, 2)), overlay, l, n); P += EntityVertex.STRIDE;
        EntityVertex.write(P, p3x, p3y, p3z, c, Float.floatToIntBits(qtu(raw, 3)), Float.floatToIntBits(qtv(raw, 3)), overlay, l, n); P += EntityVertex.STRIDE;

        BUFFER_PTR = P; BUFFED_VERTEX += VERTEX_COUNT;
        if (isBufferMax()) flush(writer);
    }

    private static float qx(Object q, int i) { return ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) q).getX(i); }
    private static float qy(Object q, int i) { return ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) q).getY(i); }
    private static float qz(Object q, int i) { return ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) q).getZ(i); }
    private static float qtu(Object q, int i) { return ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) q).getTexU(i); }
    private static float qtv(Object q, int i) { return ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) q).getTexV(i); }
    private static int qu(Object q, int i) { return ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) q).getLight(i); }

    public static void renderQuadList(PoseStack.Pose pose, VertexBufferWriter writer, int faces, List<BakedQuad> quads,
                                       int light, int overlay, int packedColor) {
        for (BakedQuad q : quads) {
            putBulkData(writer, pose, q, light, overlay, packedColor, faces);
        }
    }

    public static void render(List<BakedQuad> quads, int faces, int packedLight, int packedOverlay,
                              PoseStack poseStack, VertexBufferWriter writer) {
        renderQuadList(poseStack.last(), writer, faces, quads, packedLight, packedOverlay, 0xFFFFFFFF);
        flush(writer);
    }
}
