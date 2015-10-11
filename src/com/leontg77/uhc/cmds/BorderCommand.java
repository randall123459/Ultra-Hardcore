package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class BorderCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			World world = player.getWorld();
			WorldBorder border = world.getWorldBorder();
			
			if (args.length == 0) {
				int size = (int) border.getSize();
				
				player.sendMessage(Main.prefix() + "The borders are currently: §a" + size + "x" + size);
				return true;
			}
			
			if (!player.hasPermission("uhc.border")) {
				player.sendMessage(Main.NO_PERMISSION_MESSAGE);
				return true;
			}
			
			int radius;
			
			try {
				radius = Integer.parseInt(args[0]);
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + args[0] + " is not an vaild radius.");
				return true;
			}

			border.setSize(radius);
			border.setWarningDistance(0);
			border.setWarningTime(60);
			border.setDamageAmount(0.1);
			border.setDamageBuffer(0);
			
			if (world.getEnvironment() == Environment.NETHER) {
				border.setCenter(0.5, 0.5);
			} else {
				border.setCenter(0.0, 0.0);
			}
			
			PlayerUtils.broadcast(Main.prefix() + "Borders in world '§a" + world.getName() + "§7' has been setup with radius §6" + radius + "x" + radius + "§7.");
		} else {
			if (args.length == 0) {
				sender.sendMessage(Main.prefix() + "Usage: /border <world> [size]");
				return true;
			}
			
			World world = Bukkit.getWorld(args[0]);
			
			if (world == null) {
				sender.sendMessage(ChatColor.RED + "There are no worlds with the name " + args[0]);
				return true;
			}

			WorldBorder border = world.getWorldBorder();
			
			if (args.length == 1) {
				int size = (int) border.getSize();
				
				sender.sendMessage(Main.prefix() + "The borders in '§6" + world.getName() + "§7' are currently: §a" + size + "x" + size);
				return true;
			}
			
			int radius;
			
			try {
				radius = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not an vaild radius.");
				return true;
			}

			border.setSize(radius);
			border.setWarningDistance(0);
			border.setWarningTime(60);
			border.setDamageAmount(0.1);
			border.setDamageBuffer(0);
			
			if (world.getEnvironment() == Environment.NETHER) {
				border.setCenter(0.5, 0.5);
			} else {
				border.setCenter(0.0, 0.0);
			}
			
			PlayerUtils.broadcast(Main.prefix() + "Borders in world '§6" + world.getName() + "§7' has been setup with radius §a" + radius + "x" + radius + "§7.");
		}
		return true;
	}
}