package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class UnbanCommand implements CommandExecutor, TabCompleter {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unban")) {
			if (sender.hasPermission("uhc.unban")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /unban <player>");
					return true;
				}
		    	
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GOLD + args[0] + " §7has been unbanned.", "uhc.unban");
		   		Bukkit.getServer().getBanList(Type.NAME).pardon(args[0]);
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unban")) {
			if (sender.hasPermission("uhc.unban")) {
				if (args.length == 1) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	
		        	if (!args[0].equals("")) {
		        		for (OfflinePlayer banned : Bukkit.getServer().getBannedPlayers()) {
		        			if (banned.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
		        				arg.add(banned.getName());
		        			}
		        		}
		        	}
		        	else {
		        		for (OfflinePlayer banned : Bukkit.getServer().getBannedPlayers()) {
		        			arg.add(banned.getName());
		        		}
		        	}
		        	return arg;
		        }
			}
		}
		return null;
	}
}