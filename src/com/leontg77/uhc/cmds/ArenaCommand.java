package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class ArenaCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Arena arena = Arena.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("arena")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (arena.isEnabled()) {
						if (arena.hasPlayer(player)) {
							player.sendMessage(Main.prefix() + "You are already in the arena.");
							return true;
						}
						
						arena.addPlayer(player);
						player.sendMessage(Main.prefix() + "You joined the arena.");
					} 
					else {
						player.sendMessage(Main.prefix() + "The arena is currently disabled.");
					}
				} 
				else {
					sender.sendMessage(ChatColor.RED + "Only players can join arenas.");
				}
				return true;
			}
			
			if (sender.hasPermission("uhc.arenaadmin")) {
				if (args[0].equalsIgnoreCase("enable")) {
					if (arena.isEnabled()) {
						sender.sendMessage(Main.prefix() + "Arena is already enabled.");
						return true;
					}
					arena.enable();
					PlayerUtils.broadcast(Main.prefix() + "The arena has been enabled.");
				} 
				else if (args[0].equalsIgnoreCase("disable")) {
					if (!arena.isEnabled()) {
						sender.sendMessage(Main.prefix() + "Arena is not enabled.");
						return true;
					}
					arena.disable();
					PlayerUtils.broadcast(Main.prefix() + "The arena has been disabled.");
				} 
				else if (args[0].equalsIgnoreCase("reset")) {
					arena.reset();
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;

						if (args[0].equalsIgnoreCase("leave")) {
							if (arena.isEnabled()) {
								if (!arena.hasPlayer(player)) {
									player.sendMessage(Main.prefix() + "You are not in the arena.");
									return true;
								}
								
								arena.removePlayer(player, false);
								player.sendMessage(Main.prefix() + "You left the arena.");
							}
							else {
								player.sendMessage(Main.prefix() + "The arena is currently disabled.");
							}
							return true;
						}
						
						if (arena.isEnabled()) {
							if (arena.hasPlayer(player)) {
								player.sendMessage(Main.prefix() + "You are already in the arena.");
								return true;
							}
							
							arena.addPlayer(player);
							player.sendMessage(Main.prefix() + "You joined the arena.");
						} else {
							player.sendMessage(Main.prefix() + "The arena is currently disabled.");
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
						if (arena.isEnabled()) {
							if (!arena.hasPlayer(player)) {
								player.sendMessage(Main.prefix() + "You are not in the arena.");
								return true;
							}
							
							arena.removePlayer(player, false);
							player.sendMessage(Main.prefix() + "You left the arena.");
						} 
						else {
							player.sendMessage(Main.prefix() + "The arena is currently disabled.");
						}
						return true;
					}
					
					if (arena.isEnabled()) {
						if (arena.hasPlayer(player)) {
							player.sendMessage(Main.prefix() + "You are already in the arena.");
							return true;
						}
						
						arena.addPlayer(player);
						player.sendMessage(Main.prefix() + "You joined the arena.");
					} 
					else {
						player.sendMessage(Main.prefix() + "The arena is currently disabled.");
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