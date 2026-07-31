package com.safe.flerovium;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigParser {
	private static Config config;
	private static final Supplier<String> config_path = () ->
			String.valueOf(FabricLoader.getInstance().getConfigDir().resolve("flerovium.json"));

	public static void loadConfig() {
		Gson gson = new Gson();
		File configFile = new File(config_path.get());
		if (!configFile.exists()) {
			config = new Config();
			saveConfig();
		} else {
			try (FileReader reader = new FileReader(configFile)) {
				config = gson.fromJson(reader, Config.class);
				if (config == null) {
					config = new Config();
				}
				saveConfig();
			} catch (JsonSyntaxException | IOException | JsonIOException e) {
				Flerovium.LOGGER.warn("Failed to load flerovium.json, using defaults", e);
				config = new Config();
				saveConfig();
			}
		}
	}

	public static void saveConfig() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter(config_path.get())) {
			gson.toJson(config, writer);
		} catch (IOException e) {
			Flerovium.LOGGER.error(e.getMessage());
		}
	}

	public static Config getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}
}
