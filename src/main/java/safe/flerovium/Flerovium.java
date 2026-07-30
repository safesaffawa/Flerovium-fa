package safe.flerovium;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Flerovium implements ModInitializer {
	public static final String MOD_ID = "flerovium";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Config config = ConfigParser.getConfig();

	@Override
	public void onInitialize() {
		// Flerovium is a client-side rendering optimization mod.
		// All logic is driven by mixins; nothing to register here.
	}
}
