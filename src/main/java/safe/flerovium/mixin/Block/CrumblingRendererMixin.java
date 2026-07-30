package safe.flerovium.mixin.Block;

import safe.flerovium.functions.BlockBreaking.BlockBreakingDecalGenerator;
import safe.flerovium.functions.BlockBreaking.BlockBreakingRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.block.model.data.ModelData;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.SortedSet;

@Mixin(value = LevelRenderer.class, remap = false)
public abstract class CrumblingRendererMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable private ClientLevel level;
    @Shadow @Final private RenderBuffers renderBuffers;
    @Shadow public abstract Frustum getFrustum();

    @Redirect(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;long2ObjectEntrySet()Lit/unimi/dsi/fastutil/objects/ObjectSet;"
        )
    )
    private ObjectSet>> redirectBlockDestruction(
        Long2ObjectMap> instance,
        PoseStack poseStack,
        float tickDelta,
        long limitTime,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix
    ) {
        Vec3 camPos = camera.getPosition();
        ObjectSet>> newSet = new ObjectOpenHashSet<>();
        Frustum frustum = getFrustum();

        for (var entry : instance.long2ObjectEntrySet()) {
            BlockPos pos = BlockPos.of(entry.getLongKey());
            float relX = (float)(pos.getX() - camPos.x());
            float relY = (float)(pos.getY() - camPos.y());
            float relZ = (float)(pos.getZ() - camPos.z());

            if (Math.abs(relX) + Math.abs(relZ) > 65536) {
                newSet.add(entry);
                continue;
            }

            if (!frustum.isVisible(pos)) continue;

            SortedSet progressSet = entry.getValue();
            if (progressSet.isEmpty()) continue;
            
            BlockDestructionProgress progress = progressSet.last();
            int stage = progress.getProgress();
            if (stage < 0 || stage >= ModelBakery.DESTROY_TYPES.size()) continue;

            VertexConsumer consumer = renderBuffers.crumblingBufferSource()
                .getBuffer(ModelBakery.DESTROY_TYPES.get(stage));
            
            PoseStack.Pose last = poseStack.last();
            VertexConsumer decal = new BlockBreakingDecalGenerator(consumer, relX, relY, relZ);
            ModelData modelData = level.getModelData(pos);

            BlockBreakingRenderer.renderBreakingTexture(
                minecraft.getBlockRenderer(),
                camPos,
                level.getBlockState(pos),
                pos,
                level,
                poseStack,
                decal,
                modelData
            );
        }
        return newSet;
    }
}