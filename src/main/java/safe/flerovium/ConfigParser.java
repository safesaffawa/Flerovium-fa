package safe.flerovium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

public class ConfigParser {
    private static Config config;
    static Supplier<String> configPath = () -> String.valueOf(FabricLoader.getInstance()
            .getConfigDir().resolve("flerovium.json"));

    public static void loadConfig() {
        Gson gson = new Gson();
        File configFile = new File(configPath.get());

        if (!configFile.exists()) {
            config = new Config();
            saveConfig();
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                config = gson.fromJson(reader, Config.class);
                saveConfig();
            } catch (JsonIOException | JsonSyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(configPath.get())) {
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
