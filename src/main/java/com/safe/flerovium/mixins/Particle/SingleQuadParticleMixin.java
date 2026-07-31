package com.safe.flerovium.mixins.Particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = SingleQuadParticle.class, priority = 100)
public abstract class SingleQuadParticleMixin extends Particle {
	@Unique
	long flerovium$lastTick = -1L;
	@Unique
	int flerovium$cachedLight = 0;

	protected SingleQuadParticleMixin(ClientLevel p_107234_, double p_107235_, double p_107236_, double p_107237_) {
		super(p_107234_, p_107235_, p_107236_, p_107237_);
	}

	protected int getLightCoords(float pt) {
		long tickCount = Minecraft.getInstance().clientTickCount;
		if (tickCount == this.flerovium$lastTick) {
			return this.flerovium$cachedLight;
		} else {
			this.flerovium$lastTick = tickCount;
			this.flerovium$cachedLight = super.getLightCoords(pt);
			return this.flerovium$cachedLight;
		}
	}
}
