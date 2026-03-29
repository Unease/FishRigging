package com.minecrafttas.fishrigging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FishRigging implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger("FishRigging");
	public static boolean isTASModLoaded = FabricLoader.getInstance().isModLoaded("tasmod");
	
	@Override
	public void onInitialize() {

		LOGGER.info("Initalizing...");
		
		if (isTASModLoaded) {
			LOGGER.info("TASmod detected!");
		} else {
			LOGGER.info("TASmod not detected!");
		}
	}
}
