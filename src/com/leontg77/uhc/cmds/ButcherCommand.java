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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.EntityUtils;
import com.leontg77.uhc.utils.NameUtils;

/**
 * Butcher command class.
 * 
 * @author LeonTG77
 */
public class ButcherCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.butcher")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		int amount = 0;
		
		if (args.length == 0) {
			for (World world : Bukkit.getWorlds()) {
	    		for (Entity mob : world.getEntities()) {
					if (EntityUtils.isButcherable(mob.getType())) {
						mob.remove();
						amount++;
					}
	    		}
	       	}
	    	
			sender.sendMessage(Main.PREFIX + "Killed §6" + amount + " §7entities.");
			return true;
		}
		
		EntityType type;
		
		try {
			type = EntityType.valueOf(args[0].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild entity type.");
			return true;
		}
		
		for (World world : Bukkit.getWorlds()) {
    		for (Entity mob : world.getEntities()) {
				if (mob.getType().equals(type)) {
					mob.remove();
					amount++;
				}
    		}
       	}
    	
		sender.sendMessage(Main.PREFIX + "Killed §6" + amount + " §7" + NameUtils.getMobName(type).toLowerCase() + "s.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.butcher")) {
			return null;
		}

    	ArrayList<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
        	if (args[0].equals("")) {
        		for (EntityType type : EntityType.values()) {
        			toReturn.add(type.name().toLowerCase());
        		}
        	} else {
        		for (EntityType type : EntityType.values()) {
        			if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(type.name().toLowerCase());
        			}
        		}
        	}
        }
    	
    	return toReturn;
	}
}