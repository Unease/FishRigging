package de.scribble.lp.fishrigging;

import java.awt.Desktop;
import java.io.IOException;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FishriggingCommand extends CommandBase{

	@Override
	public String getName() {
		return "fishrigging";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/fishrigging";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if(!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() && sender instanceof EntityPlayer) {
			try {
				Desktop.getDesktop().edit(FishManipEvents.fishrigger.getFileLocation());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
