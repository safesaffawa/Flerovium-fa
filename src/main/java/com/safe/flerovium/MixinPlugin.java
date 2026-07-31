package com.safe.flerovium;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinPlugin implements IMixinConfigPlugin {

   private static final String[] REQUIRED_MODS = {"sodium", "fabricloader", "minecraft"};

   // 所有 Mixin 目标类 —— 缺任何一个直接炸，绝不留情
   private static final String[] CRITICAL_TARGETS = {
      // Sodium 实体渲染（核心优化）
      "net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer",
      "net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid",
      // Minecraft 粒子系统
      "net.minecraft.client.particle.CampfireSmokeParticle",
      "net.minecraft.client.particle.Particle",
      "net.minecraft.client.particle.SingleQuadParticle",
      // Minecraft 客户端 & 音效
      "net.minecraft.client.multiplayer.ClientLevel",
      "net.minecraft.client.sounds.SoundEngine",
      "com.mojang.blaze3d.audio.Library",
      // Flerovium 自身配置
      "com.safe.flerovium.Config"
   };

   public MixinPlugin() {
   }

   public void onLoad(String mixinPackage) {
      // 1. 依赖检查
      for (String modId : REQUIRED_MODS) {
         if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new RuntimeException(
               "[Flerovium] FATAL: Required mod '" + modId + "' is MISSING! "
               + "Install it and restart."
            );
         }
      }

      // 2. 目标类存在性检查 —— ClassNotFoundException 直接崩
      for (String className : CRITICAL_TARGETS) {
         try {
            Class.forName(className);
            Flerovium.LOGGER.info("[Flerovium] Target verified: {}", className);
         } catch (ClassNotFoundException e) {
            throw new RuntimeException(
               "[Flerovium] FATAL: Target class NOT FOUND: " + className
               + "\nThis means the mod environment is incompatible with Flerovium."
               + "\nCheck that Sodium and Minecraft versions match.",
               e
            );
         }
      }

      Flerovium.LOGGER.info("[Flerovium] ALL {} targets verified. FULL PERFORMANCE MODE.", CRITICAL_TARGETS.length);
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
