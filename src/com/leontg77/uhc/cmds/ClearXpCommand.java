package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class ClearXpCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("clearxp")) {
			if (sender.hasPermission("uhc.clearxp")) {
				if (args.length == 0) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setExp(0);
						player.setLevel(0);
						player.sendMessage(Main.prefix() + "You cleared your own xp.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can clear their own xp.");
					}
					return true;
				}
				
				if (sender.hasPermission("uhc.clearxp.other")) {
					if (args[0].equals("*")) {
						for (Player online : PlayerUtils.getPlayers()) {
							online.setExp(0);
							online.setLevel(0);
							online.sendMessage(Main.prefix() + "Your xp was cleared.");
						}
						sender.sendMessage(Main.prefix() + "You cleared everyones xp.");
					} else {
						Player target = Bukkit.getServer().getPlayer(args[0]);
						
						if (target == null) {
							sender.sendMessage(ChatColor.RED + "That player is not online.");
						}

						target.setExp(0);
						target.setLevel(0);
						target.sendMessage(Main.prefix() + "Your xp was cleared.");
						sender.sendMessage(Main.prefix() + "You cleared " + target.getName() + "'s xp.");
					}
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setExp(0);
						player.setLevel(0);
						player.sendMessage(Main.prefix() + "You cleared your own xp.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can clear their own xp.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}