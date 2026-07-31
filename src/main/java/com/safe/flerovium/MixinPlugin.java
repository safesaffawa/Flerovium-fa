package com.safe.flerovium;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	private boolean isSodiumVersionAllowed(String targetVersion) {
		var container = FabricLoader.getInstance().getModContainer("sodium");
		if (container.isEmpty()) {
			return false;
		}
		try {
			Version version = container.get().getMetadata().getVersion();
			if (version instanceof SemanticVersion semVersion) {
				return semVersion.compareTo(SemanticVersion.parse(targetVersion)) >= 0;
			}
		} catch (Exception e) {
			// If the version cannot be parsed, assume it is compatible - sodium is
			// already enforced as a required dependency via fabric.mod.json.
			return true;
		}
		return true;
	}

	private boolean doModExist(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return switch (mixinClassName) {
			case "com.safe.flerovium.mixins.Entity.EntityRendererMixin",
					"com.safe.flerovium.mixins.Entity.ModelCuboidAccessor" ->
					isSodiumVersionAllowed("0.7.0");
			case "com.safe.flerovium.mixins.Particle.ReduceTerrainParticlesMixin" ->
					Flerovium.config.reduceTerrainParticles && !doModExist("simulated");
			case "com.safe.flerovium.mixins.Particle.SingleQuadParticleMixin" ->
					!doModExist("asyncparticles");
			case "com.safe.flerovium.mixins.Sound.ClientLevelMixin" ->
					!doModExist("simulated");
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
