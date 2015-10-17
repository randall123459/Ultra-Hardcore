package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.utils.GameUtils;

/**
 * Matchpost command class.
 * 
 * @author LeonTG77
 */
public class MatchpostCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Settings settings = Settings.getInstance();
		String teamSize = GameUtils.getTeamSize();
		
		if (teamSize.startsWith("No") || teamSize.startsWith("Open")) {
			sender.sendMessage(Main.PREFIX + "There are no matches running.");
			return true;
		}
		
		sender.sendMessage(Main.PREFIX + "Match post: §a" + settings.getConfig().getString("matchpost"));
		return true;
	}
}