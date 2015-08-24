package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class FeedCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("feed")) {
			if (sender.hasPermission("uhc.feed")) {
				if (args.length == 0) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setFoodLevel(20);
						player.setSaturation(20.0f);
						player.sendMessage(Main.prefix() + "You have been fed.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can feed themselves.");
					}
					return true;
				}
				
				if (sender.hasPermission("uhc.feed.other")) {
					if (args[0].equals("*")) {
						for (Player online : PlayerUtils.getPlayers()) {
							online.setFoodLevel(20);
							online.setSaturation(20.0f);
							online.sendMessage(Main.prefix() + "You have been fed.");
						}
						sender.sendMessage(Main.prefix() + "You fed everyone.");
					} else {
						Player target = Bukkit.getServer().getPlayer(args[0]);
						
						if (target == null) {
							sender.sendMessage(ChatColor.RED + "That player is not online.");
						}

						target.setFoodLevel(20);
						target.setSaturation(20.0f);
						target.sendMessage(Main.prefix() + "You have been fed.");
						sender.sendMessage(Main.prefix() + "You fed " + target.getName() + ".");
					}
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setFoodLevel(20);
						player.setSaturation(20.0f);
						player.sendMessage(Main.prefix() + "You have been fed.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can feed themselves.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}