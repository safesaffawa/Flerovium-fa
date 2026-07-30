package safe.flerovium.mixin.Item;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

/**
 * Caches render types for SimpleBakedModel to avoid recomputing them every frame.
 * Based on the NeoForge implementation by MoePus.
 */
@Mixin(value = SimpleBakedModel.class, remap = false)
public abstract class SimpleBakedModelMixin {
    // Per-model caches keyed by ItemStack (for item-specific render types)
    private Map<ItemStack, List<RenderType>> flerovium$cachedItemRenderTypes = Object2ObjectMaps.emptyMap();
    private Map<ItemStack, List<RenderType>> flerovium$cachedFabulousItemRenderTypes = Object2ObjectMaps.emptyMap();

    @Inject(method = "getRenderTypes", at = @At("HEAD"), cancellable = true)
    public void getRenderTypes(ItemStack itemStack, boolean fabulous, CallbackInfoReturnable<List<RenderType>> cir) {
        Map<ItemStack, List<RenderType>> cache = fabulous
                ? flerovium$cachedFabulousItemRenderTypes
                : flerovium$cachedItemRenderTypes;

        // Check if the cache is our empty sentinel
        if (cache != Object2ObjectMaps.emptyMap()) {
            List<RenderType> cached = cache.get(itemStack);
            if (cached != null) {
                cir.setReturnValue(cached);
                return;
            }
        }

        // Lazy init
        if (cache == Object2ObjectMaps.emptyMap()) {
            cache = new Object2ObjectOpenHashMap<>();
            if (fabulous) {
                flerovium$cachedFabulousItemRenderTypes = cache;
            } else {
                flerovium$cachedItemRenderTypes = cache;
            }
        }

        // Compute the default render types by calling the BakedModel interface default method
        List<RenderType> result = BakedModel.super.getRenderTypes(itemStack, fabulous);

        // Cache and return
        cache.put(itemStack, result);
        cir.setReturnValue(result);
    }
}
