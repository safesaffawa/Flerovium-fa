package com.safe.flerovium;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Flerovium implements ModInitializer {
	public static final String MOD_ID = "flerovium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Config config = ConfigParser.getConfig();

	@Override
	public void onInitialize() {
		LOGGER.info("Flerovium initialized! Making rendering faster...");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}
