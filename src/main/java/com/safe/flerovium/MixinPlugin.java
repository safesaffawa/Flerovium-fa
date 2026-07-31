package com.safe.flerovium;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinPlugin implements IMixinConfigPlugin {

   private static final String[] REQUIRED_MODS = {"sodium", "fabricloader", "minecraft"};

   public MixinPlugin() {
   }

   public void onLoad(String mixinPackage) {
      // 强制检查：缺任何必需模组直接崩溃
      for (String modId : REQUIRED_MODS) {
         if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new RuntimeException(
               "[Flerovium] CRITICAL: Required mod '" + modId + "' is NOT loaded! "
               + "Flerovium cannot function without it. Install " + modId + " and restart."
            );
         }
      }
      Flerovium.LOGGER.info("[Flerovium] All dependency checks passed. Full performance mode ENGAGED.");
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      // 不允许任何静默跳过 —— 要么注入，要么崩溃
      Flerovium.LOGGER.info("[Flerovium] FORCE APPLY: {}", mixinClassName);
      return true;
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
