package safe.flerovium.mixin.Item;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.BakedQuadView;
import net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces Sodium's slow entity-format putBakedQuad path (per-vertex VertexConsumer)
 * with the fast BakedModelEncoder.writeQuadVertices path (4 vertices at once).
 *
 * Sodium's BufferBuilderMixin only uses the fast path for block format.
 * This mixin extends the fast path to entity format (items in hand, on ground, in frames).
 */
@Mixin(value = BufferBuilder.class, priority = 1500)
public abstract class BufferBuilderEntityFastMixin implements VertexConsumer {
    @Shadow
    @Final
    private boolean entityFormat;

    @Shadow
    @Final
    private boolean blockFormat;

    @Inject(method = "putBakedQuad", at = @At("HEAD"), cancellable = true, require = 0)
    private void fastEntityPutBakedQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance, CallbackInfo ci) {
        // Only optimize entity format (items, entities) - let Sodium handle block format
        if (!this.entityFormat) return;

        // Iris uses extended vertex format, skip fast path (let Iris handle it)
        if (this.getClass().getName().contains("Iris")) return;

        VertexBufferWriter writer = VertexBufferWriter.of(this);
        if (writer == null) return;

        BakedQuadView quadX = (BakedQuadView) (Object) quad;
        BakedModelEncoder.writeQuadVertices(writer, pose, quadX, instance);

        // Track sprite for texture animation (same as Sodium does)
        if (quad.materialInfo().sprite() != null) {
            SpriteUtil.INSTANCE.markSpriteActive(quad.materialInfo().sprite());
        }

        ci.cancel();
    }
}
