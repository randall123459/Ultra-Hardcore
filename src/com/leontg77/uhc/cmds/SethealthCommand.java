package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class SethealthCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("sethealth")) {
			if (sender.hasPermission("uhc.sethealth")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /sethealth <health> [player|*]");
					return true;
				}
				
				double health;
				
				try {
					health = Double.parseDouble(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild health.");
					return true;
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						Damageable d = player;
						double n = health;
						if (n > d.getMaxHealth()) {
							n = d.getMaxHealth();
						}
						player.setHealth(n);
						player.sendMessage(Main.prefix() + "You set your own health to §6" + n);
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can change their health.");
					}
					return true;
				}
				
				if (args[1].equals("*")) {
					for (Player online : PlayerUtils.getPlayers()) {
						Damageable d = online;
						double n = health;
						if (n > d.getMaxHealth()) {
							n = d.getMaxHealth();
						}
						online.setHealth(n);
						online.sendMessage(Main.prefix() + "Your health was set to §6" + n);
					}
					sender.sendMessage(Main.prefix() + "You set everyones health to §6" + health);
				} else {
					Player target = Bukkit.getServer().getPlayer(args[1]);
					Damageable d = target;
					double n = health;
					if (n > d.getMaxHealth()) {
						n = d.getMaxHealth();
					}
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}
					
					target.setHealth(n);
					target.sendMessage(Main.prefix() + "Your health was set to §6" + n);
					sender.sendMessage(Main.prefix() + "You set §6" + target.getName() + "'s §7health to §6" + n);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}