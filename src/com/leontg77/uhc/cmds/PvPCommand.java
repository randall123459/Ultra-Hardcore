package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;

public class PvPCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pvp")) {
			if (sender.hasPermission("uhc.pvp")) {
				if (Bukkit.getWorlds().get(0).getPVP()) {
					for (World w : Bukkit.getWorlds()) {
						w.setPVP(false);
					}
					sender.sendMessage(Main.prefix() + "PvP has been disabled.");
				} else {
					for (World w : Bukkit.getWorlds()) {
						w.setPVP(true);
					}
					sender.sendMessage(Main.prefix() + "PvP has been enabled.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}