package com.safe.flerovium;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Flerovium implements ClientModInitializer {
	public static final String MODID = "flerovium";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	// Config is loaded eagerly so that the mixin plugin can read it while mixins are being applied.
	public static final Config config = ConfigParser.getConfig();

	@Override
	public void onInitializeClient() {
		LOGGER.info("Flerovium loaded (entityBackFaceCulling={}, itemBackFaceCulling={}, reduceTerrainParticles={}, skipEntityTangentCompute={})",
				config.entityBackFaceCulling, config.itemBackFaceCulling,
				config.reduceTerrainParticles, config.skipEntityTangentCompute);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}
