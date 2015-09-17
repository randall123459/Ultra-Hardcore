package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class HealCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("heal")) {
			if (sender.hasPermission("uhc.heal")) {
				if (args.length == 0) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setHealth(player.getMaxHealth());
						player.setFireTicks(0);
						player.sendMessage(Main.prefix() + "You have been healed.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can heal themselves.");
					}
					return true;
				}
				
				if (sender.hasPermission("uhc.heal.other")) {
					if (args[0].equals("*")) {
						for (Player online : PlayerUtils.getPlayers()) {
							online.setHealth(online.getMaxHealth());
							online.setFireTicks(0);
						}
						PlayerUtils.broadcast(Main.prefix() + "All players have been healed.");
					} 
					else {
						Player target = Bukkit.getServer().getPlayer(args[0]);
						
						if (target == null) {
							sender.sendMessage(ChatColor.RED + "That player is not online.");
						}

						target.setHealth(target.getMaxHealth());
						target.setFireTicks(0);
						target.sendMessage(Main.prefix() + "You have been healed.");
						sender.sendMessage(Main.prefix() + "You healed " + target.getName() + ".");
					}
				} 
				else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setHealth(player.getMaxHealth());
						player.setFireTicks(0);
						player.sendMessage(Main.prefix() + "You have been healed.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can heal themselves.");
					}
				}
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
}