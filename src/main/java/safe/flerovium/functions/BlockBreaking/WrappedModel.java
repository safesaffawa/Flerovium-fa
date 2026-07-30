package safe.flerovium.functions.BlockBreaking;

import safe.flerovium.functions.DummyModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrappedModel {
    DummyModel original;
    int faces;

    public WrappedModel(DummyModel original, int faces) {
        this.original = original;
        this.faces = faces;
    }

    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        if (direction != null) {
            int faceBit = 1 << direction.ordinal();
            if ((faces & faceBit) == 0) return List.of();
        }
        return original.getQuads(state, direction, random);
    }

    public boolean useAmbientOcclusion() { return false; }
    public boolean isGui3d() { return original.isGui3d(); }
    public boolean usesBlockLight() { return original.usesBlockLight(); }
    public boolean isCustomRenderer() { return original.isCustomRenderer(); }
    public TextureAtlasSprite getParticleIcon() { return original.getParticleIcon(); }
}
