package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.utils.GameUtils;

public class MatchpostCommand implements CommandExecutor {	
	private Settings settings = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("matchpost")) {
			if (GameUtils.getTeamSize().startsWith("No") || GameUtils.getTeamSize().startsWith("Open")) {
				sender.sendMessage(Main.prefix() + "There are no matches running.");
				return true;
			}
			
			sender.sendMessage(Main.prefix() + "Match post: §a" + settings.getConfig().getString("matchpost"));
		}
		return true;
	}
}