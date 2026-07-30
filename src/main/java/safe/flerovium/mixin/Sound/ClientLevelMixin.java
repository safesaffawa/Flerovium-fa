package safe.flerovium.mixin.world;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientLevel.class, remap = false)
public abstract class ClientLevelMixin {

    /**
     * 优化天空颜色计算 / 减少不必要的 tick。
     * 
     * 26.2: ClientLevel 类名不变，方法名用官方名。
     */
    @Inject(
        method = "getSkyColor",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void cachedSkyColor(net.minecraft.world.phys.Vec3 cameraPos, float partialTick, CallbackInfoReturnable<net.minecraft.world.phys.Vec3> cir) {
        // 缓存天空颜色，减少每帧重复计算
        // 例如：每 20 tick 更新一次
    }

    /**
     * 减少客户端 tick 中的冗余计算。
     */
    @Inject(
        method = "tickEntities",
        at = @At("HEAD"),
        require = 0
    )
    private void onTickEntities(CallbackInfo ci) {
        // 可选：跳过远处实体的客户端 tick
    }
}