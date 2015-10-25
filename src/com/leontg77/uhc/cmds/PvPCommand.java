package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.leontg77.uhc.Main;

public class PvPCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.pvp")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Main.PREFIX + "Usage: /pvp <world|global> <on|off>");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("global")) {
			if (args[1].equalsIgnoreCase("on")) {
				for (World world : Bukkit.getWorlds()) {
					world.setPVP(true);
					world.setSpawnFlags(true, true);
				}
				sender.sendMessage(Main.PREFIX + "Global pvp has been enabled.");
			} else if (args[1].equalsIgnoreCase("off")) {
				for (World world : Bukkit.getWorlds()) {
					world.setPVP(false);
				}
				sender.sendMessage(Main.PREFIX + "Global pvp has been disabled.");
			} else {
				sender.sendMessage(Main.PREFIX + "Usage: /pvp <world|global> <on|off>");
			}
			return true;
		}
		
		World world = Bukkit.getServer().getWorld(args[0]);
		
		if (world == null) {
			sender.sendMessage(ChatColor.RED + "That world does not exist.");
			return true;
		}
		
		if (args[1].equalsIgnoreCase("on")) {
			sender.sendMessage(Main.PREFIX + "PvP in §a" + world.getName() + " §7has been enabled.");
			world.setPVP(true);
		} else if (args[1].equalsIgnoreCase("off")) {
			sender.sendMessage(Main.PREFIX + "PvP in §a" + world.getName() + " §7has been disabled.");
			world.setPVP(false);
		} else {
			sender.sendMessage(Main.PREFIX + "Usage: /pvp <world|global> <on|off>");
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.pvp")) {
			return null;
		}

    	ArrayList<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("global");
        	
        	for (World world : Bukkit.getServer().getWorlds()) {
        		types.add(world.getName());
        	}
        	
        	if (args[0].equals("")) {
        		for (String type : types) {
    				toReturn.add(type);
        		}
        	} else {
        		for (String type : types) {
        			if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
        }
		
		if (args.length == 2) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("on");
        	types.add("off");
        	
        	if (args[0].equals("")) {
        		for (String type : types) {
        			toReturn.add(type);
        		}
        	} else {
        		for (String type : types) {
        			if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
        }
		return toReturn;
	}
}