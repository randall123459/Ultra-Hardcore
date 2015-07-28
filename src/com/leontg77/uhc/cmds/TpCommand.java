package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

public class TpCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("tp")) {
			if (sender.hasPermission("uhc.tp") || Main.spectating.contains(sender.getName())) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /tp <player>");
					return true;
				}
				
				Player targetOne = Bukkit.getServer().getPlayer(args[0]);
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (targetOne == null) {
							player.sendMessage(ChatColor.RED + "That player is not online.");
							return true;
						} 
						
						player.sendMessage(Main.prefix() + "You teleported to §6" + targetOne.getName() + "§7.");
						player.teleport(targetOne);
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can teleport to others.");
					}
					return true;
				}
				
				Player targetTwo = Bukkit.getServer().getPlayer(args[1]);
				
				if (targetOne == null || targetTwo == null) {
					sender.sendMessage(ChatColor.RED + "One of the players are not online.");
					return true;
				} 
				
				sender.sendMessage(Main.prefix(ChatColor.GOLD) + targetOne.getName() + "§7 was teleported to §6" + targetTwo.getName() + "§7.");
				targetOne.teleport(targetTwo);
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}