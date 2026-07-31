package com.safe.flerovium;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinPlugin implements IMixinConfigPlugin {
   public MixinPlugin() {
   }

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   private boolean isVersionAllowed(String modId, String minVersion) {
      if (!FabricLoader.getInstance().isModLoaded(modId)) {
         return false;
      }
      try {
         Version modVersion = FabricLoader.getInstance().getModContainer(modId)
            .get().getMetadata().getVersion();
         Version required = Version.parse(minVersion);
         return modVersion.compareTo(required) >= 0;
      } catch (VersionParsingException e) {
         return false;
      }
   }

   private boolean doModExist(String modId) {
      return FabricLoader.getInstance().isModLoaded(modId);
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      boolean var10000;
      switch (mixinClassName) {
         case "com.safe.flerovium.mixin.Entity.EntityRendererMixin":
         case "com.safe.flerovium.mixin.Entity.ModelCuboidAccessor":
            var10000 = this.isVersionAllowed("sodium", "0.7.0");
            break;
         case "com.safe.flerovium.mixin.Particle.ReduceTerrainParticlesMixin":
            var10000 = Flerovium.config.reduceTerrainParticles && !this.doModExist("simulated");
            break;
         case "com.safe.flerovium.mixin.Particle.SingleQuadParticleMixin":
            var10000 = !this.doModExist("asyncparticles");
            break;
         case "com.safe.flerovium.mixin.Sound.ClientLevelMixin":
            var10000 = !this.doModExist("simulated");
            break;
         default:
            var10000 = true;
      }

      return var10000;
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
