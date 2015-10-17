package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.worlds.Pregen;

public class PregenCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.pregen")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		Pregen pregen = Pregen.getInstance();
		
		if (args.length < 2) {
			if (args.length > 0 && args[0].equalsIgnoreCase("cancel")) {
				pregen.stop();
				return true;
			}
			
			sender.sendMessage(Main.PREFIX + "Usage: /pregen <world> <radius>");
			return true;
		}
		
		World world = Bukkit.getServer().getWorld(args[0]);
		
		if (world == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not an world.");
			return true;
		}
		
		int radius;
		
		try {
			radius = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild radius.");
			return true;
		}
		
		pregen.start(world, radius);
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setup")) {
			if (sender.hasPermission("uhc.setup")) { 
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