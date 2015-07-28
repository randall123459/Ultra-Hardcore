package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

public class RulesCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rules")) {
			sender.sendMessage(Main.prefix() + "You can find our rules at the match post:");
			sender.sendMessage("§7- §f" + Settings.getInstance().getData().getString("match.post"));
		}
		return true;
	}
}