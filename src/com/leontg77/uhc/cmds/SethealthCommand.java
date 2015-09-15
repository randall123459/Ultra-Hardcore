package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class SethealthCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
				
				if (health < 0) {
					health = 0;
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						double hp = health;
						
						if (hp > player.getMaxHealth()) {
							hp = player.getMaxHealth();
						}
						
						player.setHealth(hp);
						player.sendMessage(Main.prefix() + "You set your own health to §6" + hp);
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can change their health.");
					}
					return true;
				}
				
				if (args[1].equals("*")) {
					for (Player online : PlayerUtils.getPlayers()) {
						double hp = health;
						
						if (hp > online.getMaxHealth()) {
							hp = online.getMaxHealth();
						}
						online.setHealth(hp);
					}
					PlayerUtils.broadcast(Main.prefix() + "All players health is now §6" + health);
				} else {
					Player target = Bukkit.getServer().getPlayer(args[1]);
					double hp = health;
					
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}
					
					if (hp > target.getMaxHealth()) {
						hp = target.getMaxHealth();
					}
					
					target.setHealth(hp);
					target.sendMessage(Main.prefix() + "Your health was set to §6" + hp);
					sender.sendMessage(Main.prefix() + "You set §6" + target.getName() + "'s §7health to §6" + hp);
				}
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
}