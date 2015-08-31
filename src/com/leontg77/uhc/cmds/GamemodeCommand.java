package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

public class GamemodeCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gamemode")) {
			if (sender.hasPermission("uhc.gamemode")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /gamemode <mode> [player]");
					return true;
				}
				
				GameMode mode = null;
				
				for (GameMode modes : GameMode.values()) {
					if (modes.name().startsWith(args[0].toUpperCase())) {
						mode = modes;
					}
				}
				
				if (mode == null) {
					if (args[0].equals("0")) {
						mode = GameMode.SURVIVAL;
					} else if (args[0].equals("1")) {
						mode = GameMode.CREATIVE;
					} else if (args[0].equals("2")) {
						mode = GameMode.ADVENTURE;
					} else if (args[0].equals("3")) {
						mode = GameMode.SPECTATOR;
					} else {
						sender.sendMessage(ChatColor.RED + "That is not an vaild gamemode.");
						return true;
					}
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setGameMode(mode);
						player.sendMessage(Main.prefix() + "You have changed your gamemode to §6" + mode.name().toLowerCase() + " §7mode.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can change their own gamemode."); 
					}
					return true;
				}
				
				if (!sender.hasPermission("uhc.gamemode.other")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.setGameMode(mode);
						player.sendMessage(Main.prefix() + "You have changed your gamemode to §6" + mode.name().toLowerCase() + " §7mode.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can change their own gamemode."); 
					}
					return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[1]);
				
				if (target == null) {
					sender.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				target.setGameMode(mode);
				target.sendMessage(Main.prefix() + ChatColor.GOLD + sender.getName() + " §7has changed your gamemode to §6" + mode.name().toLowerCase() + " §7mode.");
				sender.sendMessage(Main.prefix() + "You have changed §6" + target.getName() + "'s §7gamemode to §6" + mode.name().toLowerCase() + " §7mode.");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}