package safe.flerovium.Iris;

import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.List;

import static safe.flerovium.functions.FastSimpleBakedModelRenderer.*;
import static safe.flerovium.functions.MathUtil.packSafe;

public class IrisSimpleBakedItemRenderer {
    private static final MemoryStack STACK = MemoryStack.create();
    private static final long SCRATCH_BUFFER = MemoryUtil.nmemAlignedAlloc(64, BUFFER_VERTEX_COUNT * IrisEntityVertex.STRIDE);
    private static long BUFFER_PTR = SCRATCH_BUFFER;
    private static int BUFFED_VERTEX = 0;
    private static final Vector2f uv0 = new Vector2f(), uv1 = new Vector2f(), uv2 = new Vector2f(), uv3 = new Vector2f();

    private static void flush(VertexBufferWriter writer) {
        if (BUFFED_VERTEX == 0) return;
        STACK.push();
        writer.push(STACK, SCRATCH_BUFFER, BUFFED_VERTEX, IrisEntityVertex.FORMAT);
        STACK.pop();
        BUFFER_PTR = SCRATCH_BUFFER; BUFFED_VERTEX = 0;
    }

    private static boolean isBufferMax() { return BUFFED_VERTEX >= BUFFER_VERTEX_COUNT; }

    private static void putBulkData(VertexBufferWriter writer, PoseStack.Pose pose, BakedQuad bakedQuad, int light,
                                    int overlay, int color, int faces) {
        Object raw = bakedQuad;
        Matrix4f pm = pose.pose();
        int pn = ((net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView) raw).getFaceNormal();
        float nx = MatrixHelper.transformNormalX(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        float ny = MatrixHelper.transformNormalY(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        float nz = MatrixHelper.transformNormalZ(pose.normal(), NormI8.unpackX(pn), NormI8.unpackY(pn), NormI8.unpackZ(pn));
        int n = packSafe(nx, ny, nz);

        float p0x = MatrixHelper.transformPositionX(pm, qx(raw,0), qy(raw,0), qz(raw,0));
        float p0y = MatrixHelper.transformPositionY(pm, qx(raw,0), qy(raw,0), qz(raw,0));
        float p0z = MatrixHelper.transformPositionZ(pm, qx(raw,0), qy(raw,0), qz(raw,0));
        float p2x = MatrixHelper.transformPositionX(pm, qx(raw,2), qy(raw,2), qz(raw,2));
        float p2y = MatrixHelper.transformPositionY(pm, qx(raw,2), qy(raw,2), qz(raw,2));
        float p2z = MatrixHelper.transformPositionZ(pm, qx(raw,2), qy(raw,2), qz(raw,2));
        if ((faces & 0b1000000) != 0 && (p0x+p2x)*nx + (p0y+p2y)*ny + (p0z+p2z)*nz > 0) return;

        float p1x = MatrixHelper.transformPositionX(pm, qx(raw,1), qy(raw,1), qz(raw,1));
        float p1y = MatrixHelper.transformPositionY(pm, qx(raw,1), qy(raw,1), qz(raw,1));
        float p1z = MatrixHelper.transformPositionZ(pm, qx(raw,1), qy(raw,1), qz(raw,1));
        float p3x = MatrixHelper.transformPositionX(pm, qx(raw,3), qy(raw,3), qz(raw,3));
        float p3y = MatrixHelper.transformPositionY(pm, qx(raw,3), qy(raw,3), qz(raw,3));
        float p3z = MatrixHelper.transformPositionZ(pm, qx(raw,3), qy(raw,3), qz(raw,3));

        uv0.set(qtu(raw,0), qtv(raw,0)); uv1.set(qtu(raw,1), qtv(raw,1)); uv2.set(qtu(raw,2), qtv(raw,2)); uv3.set(qtu(raw,3), qtv(raw,3));
        float mu = (uv0.x+uv1.x+uv2.x+uv3.x)/4, mv = (uv0.y+uv1.y+uv2.y+uv3.y)/4;
        int l = Math.max(((qu(raw,0) & 0xffff) << 16) | (qu(raw,0) >> 16), light);
        long P = BUFFER_PTR;
        IrisEntityVertex.write(P, p0x, p0y, p0z, -1, uv0.x, uv0.y, mu, mv, overlay, l, n, -1); P += IrisEntityVertex.STRIDE;
        IrisEntityVertex.write(P, p1x, p1y, p1z, -1, uv1.x, uv1.y, mu, mv, overlay, l, n, -1); P += IrisEntityVertex.STRIDE;
        IrisEntityVertex.write(P, p2x, p2y, p2z, -1, uv2.x, uv2.y, mu, mv, overlay, l, n, -1); P += IrisEntityVertex.STRIDE;
        IrisEntityVertex.write(P, p3x, p3y, p3z, -1, uv3.x, uv3.y, mu, mv, overlay, l, n, -1); P += IrisEntityVertex.STRIDE;
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
        for (BakedQuad q : quads) putBulkData(writer, pose, q, light, overlay, packedColor, faces);
    }

    public static void render(List<BakedQuad> quads, int faces, int packedLight, int packedOverlay,
                              PoseStack poseStack, VertexBufferWriter writer) {
        renderQuadList(poseStack.last(), writer, faces, quads, packedLight, packedOverlay, 0xFFFFFFFF);
        flush(writer);
    }
}
