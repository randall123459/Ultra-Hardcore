package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Spectator;

public class SpectateCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("spectate")) {
			if (sender.hasPermission("uhc.spectate")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle> [player]");
		    		return true;
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (args[0].equalsIgnoreCase("toggle")) {
							Spectator.getManager().toggle(player);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("on")) {
							Spectator.getManager().set(player, true);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("off")) {
							Spectator.getManager().set(player, false);
							return true;
						}
						player.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle> [player]");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can spectate.");
					}
		    		return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[1]);
				
				if (sender.hasPermission("uhc.spectateother")) {
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					} 
					
					if (args[0].equalsIgnoreCase("toggle")) {
						Spectator.getManager().toggle(target);
						return true;
					}
					
					if (args[0].equalsIgnoreCase("on")) {
						Spectator.getManager().set(target, true);
						return true;
					}
					
					if (args[0].equalsIgnoreCase("off")) {
						Spectator.getManager().set(target, false);
			    		return true;
					}
					sender.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle> [player]");
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (args[0].equalsIgnoreCase("toggle")) {
							Spectator.getManager().toggle(player);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("on")) {
							Spectator.getManager().set(player, true);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("off")) {
							Spectator.getManager().set(player, false);
							return true;
						}
						player.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle> [player]");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can spectate.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}