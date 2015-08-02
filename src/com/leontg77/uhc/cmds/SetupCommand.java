package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setup")) {
			if (sender.hasPermission("uhc.setup")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /setup <radius> [world]");
					return true;
				}
				
				int radius;
				
				try {
					radius = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild radius.");
					return true;
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						radius = radius + 0;
						player.damage(0.0);
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can generate their own world.");
					}
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("timer")) {
			if (sender.hasPermission("uhc.timer")) {
				if (args.length == 1) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	ArrayList<String> types = new ArrayList<String>();
		        	types.add("cancel");
		        	
		        	if (!args[0].equals("")) {
		        		for (String type : types) {
		        			if (type.startsWith(args[0].toLowerCase())) {
		        				arg.add(type);
		        			}
		        		}
		        	}
		        	else {
		        		for (String type : types) {
		        			arg.add(type);
		        		}
		        	}
		        	return arg;
		        }
			}
		}
		return null;
	}
}