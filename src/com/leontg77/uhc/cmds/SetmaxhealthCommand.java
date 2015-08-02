package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class SetmaxhealthCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setmaxhealth")) {
			if (sender.hasPermission("uhc.setmaxhealth")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /setmaxhealth <health> [player|*]");
					return true;
				}
				
				double health;
				
				try {
					health = Double.parseDouble(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild health.");
					return true;
				}
				
				if (health > 200) {
					sender.sendMessage(ChatColor.RED + "You cannot set more than 200 health.");
					return true;
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setMaxHealth(health);
						player.sendMessage(Main.prefix() + "You set your own maxhealth to §6" + health);
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can change their maxhealth.");
					}
					return true;
				}
				
				if (args[1].equals("*")) {
					for (Player online : PlayerUtils.getPlayers()) {
						online.setMaxHealth(health);
						online.sendMessage(Main.prefix() + "Your maxhealth was set to §6" + health);
					}
					sender.sendMessage(Main.prefix() + "You set everyones maxhealth to §6" + health);
				} else {
					Player target = Bukkit.getServer().getPlayer(args[1]);
					
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}
					
					target.setMaxHealth(health);
					target.sendMessage(Main.prefix() + "Your maxhealth was set to §6" + health);
					sender.sendMessage(Main.prefix() + "You set §6" + target.getName() + "'s §7maxhealth to §6" + health);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}