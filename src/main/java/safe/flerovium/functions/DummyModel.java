package safe.flerovium.functions;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DummyModel {

    public static final DummyModel INSTANCE = new DummyModel();

    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return Collections.emptyList();
    }

    public boolean useAmbientOcclusion() { return false; }
    public boolean isGui3d() { return false; }
    public boolean usesBlockLight() { return false; }
    public boolean isCustomRenderer() { return false; }
    public TextureAtlasSprite getParticleIcon() { return null; }
}