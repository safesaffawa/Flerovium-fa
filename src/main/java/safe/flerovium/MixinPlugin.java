package safe.flerovium;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    private boolean doModExist(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    private boolean isVersionAllowed(String modId, String range) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(ModContainer::getMetadata)
                .map(meta -> {
                    try {
                        Version version = meta.getVersion();
                        VersionPredicate predicate = VersionPredicate.parse(range);
                        return predicate.test(version);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .orElse(true);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return switch (mixinClassName) {
            case "safe.flerovium.mixin.Entity.EntityRendererMixin",
                 "safe.flerovium.mixin.Entity.ModelCuboidAccessor",
                 "safe.flerovium.mixin.Item.BufferBuilderEntityFastMixin" ->
                    isVersionAllowed("sodium", ">=0.7.0");
            case "safe.flerovium.mixin.Particle.SingleQuadParticleMixin" -> !doModExist("asyncparticles");
            case "safe.flerovium.mixin.Sound.ClientLevelMixin" -> !doModExist("simulated");
            case "safe.flerovium.mixin.Block.CrumblingRendererMixin" -> Flerovium.config.fastBreakingTexture;
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
