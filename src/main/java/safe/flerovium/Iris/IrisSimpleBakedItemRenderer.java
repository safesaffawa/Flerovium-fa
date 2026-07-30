package safe.flerovium.Iris;

import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.List;

import static safe.flerovium.functions.FastSimpleBakedModelRenderer.*;

public class IrisSimpleBakedItemRenderer {
    private static final MemoryStack STACK = MemoryStack.create();
    private static final long SCRATCH_BUFFER = MemoryUtil.nmemAlignedAlloc(64, BUFFER_VERTEX_COUNT * IrisEntityVertex.STRIDE);
    private static long BUFFER_PTR = SCRATCH_BUFFER;
    private static int BUFFED_VERTEX = 0;
    private static final Vector2f uv0 = new Vector2f();
    private static final Vector2f uv1 = new Vector2f();
    private static final Vector2f uv2 = new Vector2f();
    private static final Vector2f uv3 = new Vector2f();

    private static void putBulkData(VertexBufferWriter writer, PoseStack.Pose pose, BakedQuad bakedQuad, int light,
                                    int overlay, int color, int faces) {
    }

    private static void flush(VertexBufferWriter writer) {
        if (BUFFED_VERTEX == 0) return;
        STACK.push();
        writer.push(STACK, SCRATCH_BUFFER, BUFFED_VERTEX, IrisEntityVertex.FORMAT);
        STACK.pop();
        BUFFER_PTR = SCRATCH_BUFFER;
        BUFFED_VERTEX = 0;
    }

    private static boolean isBufferMax() {
        return BUFFED_VERTEX >= BUFFER_VERTEX_COUNT;
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexBufferWriter writer, int faces, List<BakedQuad> bakedQuads,
                                       int light, int overlay, int packedColor) {
        for (BakedQuad bakedQuad : bakedQuads) {
            putBulkData(writer, pose, bakedQuad, light, overlay, packedColor, faces);
        }
    }

    public static void render(List<BakedQuad> quads, int faces, int packedLight, int packedOverlay,
                              PoseStack poseStack, VertexBufferWriter writer) {
        renderQuadList(poseStack.last(), writer, faces, quads, packedLight, packedOverlay, 0xFFFFFFFF);
        flush(writer);
    }
}
