package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class BroadcastCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("broadcast")) {
			if (sender.hasPermission("uhc.broadcast")) {
				StringBuilder message = new StringBuilder("");
				
				for (int i = 0; i < args.length; i++) {
					message.append(args[i]).append(" ");
				}
				
				String msg = ChatColor.translateAlternateColorCodes('&', message.toString().trim()).trim();
				
				PlayerUtils.broadcast(Main.prefix() + "§6§l" + msg);
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}