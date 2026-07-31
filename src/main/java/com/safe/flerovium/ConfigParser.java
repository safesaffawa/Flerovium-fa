package com.safe.flerovium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigParser {
   private static Config config;
   static Supplier<String> config_path = () -> {
      Path configDir = FabricLoader.getInstance().getConfigDir();
      return configDir.resolve("flerovium.json").toString();
   };

   public ConfigParser() {
   }

   public static void loadConfig() {
      Gson gson = new Gson();
      File configFile = new File((String)config_path.get());
      if (!configFile.exists()) {
         config = new Config();
         saveConfig();
      } else {
         try {
            FileReader reader = new FileReader(configFile);

            try {
               config = (Config)gson.fromJson(reader, Config.class);
               if (config == null) {
                  config = new Config();
               }

               saveConfig();
            } catch (Throwable var6) {
               try {
                  reader.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            reader.close();
         } catch (JsonSyntaxException | IOException | JsonIOException e) {
            Flerovium.LOGGER.warn("Failed to load flerovium.json, using defaults", e);
            config = new Config();
            saveConfig();
         }
      }

   }

   public static void saveConfig() {
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

      try {
         FileWriter writer = new FileWriter((String)config_path.get());

         try {
            gson.toJson(config, writer);
         } catch (Throwable var5) {
            try {
               writer.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         writer.close();
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
