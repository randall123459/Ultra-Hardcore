package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class ArenaCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("arena")) {
			final Arena a = Arena.getManager();
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (a.isEnabled()) {
						if (a.hasPlayer(player)) {
							player.sendMessage(Main.prefix() + "You are already in the arena.");
							return true;
						}
						a.addPlayer(player);
						player.sendMessage(Main.prefix() + "You joined the arena.");
					} else {
						player.sendMessage(ChatColor.RED + "The arena is currently disabled.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can join arenas.");
				}
				return true;
			}
			
			if (sender.hasPermission("uhc.arenaadmin")) {
				if (args[0].equalsIgnoreCase("enable")) {
					if (a.isEnabled()) {
						sender.sendMessage(Main.prefix() + "Arena is already enabled.");
						return true;
					}
					a.setEnabled(true);
					PlayerUtils.broadcast(Main.prefix() + "The arena has been enabled.");
				} else if (args[0].equalsIgnoreCase("disable")) {
					if (!a.isEnabled()) {
						sender.sendMessage(Main.prefix() + "Arena is not enabled.");
						return true;
					}
					a.setEnabled(false);
					PlayerUtils.broadcast(Main.prefix() + "The arena has been disabled.");
				} else if (args[0].equalsIgnoreCase("reset")) {
					PlayerUtils.broadcast(Main.prefix() + "The arena is resetting, lag incoming.");
					for (int x = -14; x <= 14; x++) {
						for (int z = -14; z <= 14; z++) {
							Bukkit.getWorld("arena").regenerateChunk(x, z);
						}
					}
					PlayerUtils.broadcast(Main.prefix() + "Arena reset complete.");
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;

						if (args[0].equalsIgnoreCase("leave")) {
							if (a.isEnabled()) {
								if (!a.hasPlayer(player)) {
									player.sendMessage(Main.prefix() + "You are not in the arena.");
									return true;
								}
								a.removePlayer(player, false);
								player.sendMessage(Main.prefix() + "You left the arena.");
							} else {
								player.sendMessage(ChatColor.RED + "The arena is currently disabled.");
							}
							return true;
						}
						
						if (a.isEnabled()) {
							if (a.hasPlayer(player)) {
								player.sendMessage(Main.prefix() + "You are already in the arena.");
								return true;
							}
							a.addPlayer(player);
							player.sendMessage(Main.prefix() + "You joined the arena.");
						} else {
							player.sendMessage(ChatColor.RED + "The arena is currently disabled.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can join arenas.");
					}
					return true;
				}
			} else {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					
					if (args[0].equalsIgnoreCase("leave")) {
						if (a.isEnabled()) {
							if (!a.hasPlayer(player)) {
								player.sendMessage(Main.prefix() + "You are not in the arena.");
								return true;
							}
							a.removePlayer(player, false);
							player.sendMessage(Main.prefix() + "You left the arena.");
						} else {
							player.sendMessage(ChatColor.RED + "The arena is currently disabled.");
						}
						return true;
					}
					
					if (a.isEnabled()) {
						if (a.hasPlayer(player)) {
							player.sendMessage(Main.prefix() + "You are already in the arena.");
							return true;
						}
						a.addPlayer(player);
						player.sendMessage(Main.prefix() + "You joined the arena.");
					} else {
						player.sendMessage(ChatColor.RED + "The arena is currently disabled.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can join arenas.");
				}
				return true;
			}
		}
		return true;
	}
}