package de.scribble.lp.fishrigging;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * 
 * @author Scribble
 *
 */
@Mod(modid = "fishrigging", name = "FishRigging", version = "1.5")
public class FishRigging {
	
	@EventHandler
	public void init(FMLInitializationEvent ev) {
		MinecraftForge.EVENT_BUS.register(new FishManipEvents());
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent ev) {
		ev.registerServerCommand(new FishriggingCommand());
	}
}
