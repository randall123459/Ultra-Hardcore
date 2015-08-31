package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class ClearChatCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("clearchat")) {
			if (sender.hasPermission("uhc.staff")) {
				for (int i = 0; i < 150; i++) {
					for (Player online : PlayerUtils.getPlayers()) {
						online.sendMessage("§b");
					}
				}
				
				PlayerUtils.broadcast(Main.prefix() + "The chat has been cleared.");
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		return true;
	}
}