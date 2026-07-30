package safe.flerovium.functions.BlockBreaking;

import safe.flerovium.Iris.IrisTerrainVertex;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class BlockBreakingDecalGenerator implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float relX;
    private final float relY;
    private final float relZ;
    private float x;
    private float y;
    private float z;

    public BlockBreakingDecalGenerator(VertexConsumer delegate, float relX, float blockY, float blockZ) {
        this.delegate = delegate;
        this.relX = relX;
        this.relY = blockY;
        this.relZ = blockZ;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        this.delegate.setColor(-1);
        return this;
    }

    @Override
    public VertexConsumer setColor(int color) {
        this.delegate.setColor(-1);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer setOverlay(int overlay) {
        this.delegate.setOverlay(overlay);
        return this;
    }

    @Override
    public VertexConsumer setLight(int light) {
        this.delegate.setLight(light);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        this.delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        this.delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float width) {
        return this.delegate.setLineWidth(width);
    }

    private static Vector3f calcUV(float normalX, float normalY, float normalZ, float dx, float dy, float dz) {
        Direction direction = Direction.getNearest((int) normalX, (int) normalY, (int) normalZ, null);
        float u, v;
        switch (direction) {
            case DOWN -> {
                u = dx;
                v = -dz;
            }
            case UP -> {
                u = dx;
                v = dz;
            }
            case NORTH -> {
                u = -dx;
                v = -dy;
            }
            case SOUTH -> {
                u = dx;
                v = -dy;
            }
            case WEST -> {
                u = dz;
                v = -dy;
            }
            case EAST -> {
                u = -dz;
                v = -dy;
            }
            default -> {
                u = 0;
                v = 0;
            }
        }
        return new Vector3f(u, v, 0);
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        this.delegate.setNormal(normalX, normalY, normalZ);
        float dx = this.x - this.relX;
        float dy = this.y - this.relY;
        float dz = this.z - this.relZ;
        Vector3f uv = calcUV(normalX, normalY, normalZ, dx, dy, dz);

        delegate.setUv(uv.x, uv.y);
        return this;
    }

    void putBulkDataSodium(
            VertexBufferWriter writer,
            PoseStack.Pose pose,
            BakedQuad bakedQuad,
            int[] lightmap
    ) {}

    void putBulkDataIris(
            VertexBufferWriter writer,
            PoseStack.Pose pose,
            BakedQuad bakedQuad,
            int[] lightmap
    ) {}

    public void putBulkData(
            PoseStack.Pose pose,
            BakedQuad bakedQuad,
            float[] brightness,
            float red, float green, float blue, float alpha,
            int[] lightmap,
            int packedOverlay,
            boolean readAlpha
    ) {
        VertexBufferWriter writer = VertexBufferWriter.tryOf(delegate);
        if (writer == null) return;
        if (!(delegate instanceof BufferBuilder)) return;
    }
}
