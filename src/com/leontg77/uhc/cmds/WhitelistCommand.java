package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Whitelist command class.
 * 
 * @author LeonTG77
 */
public class WhitelistCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.whitelist")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}

       	if (args.length == 0) {
    		sender.sendMessage(Main.PREFIX + "Usage: /whitelist <on|off|add|remove|all|clear|list> [player]");
    		return true;
       	}
		
       	if (args.length > 1) {
       		if (args[0].equalsIgnoreCase("add")) {
               	Player target = Bukkit.getServer().getPlayer(args[1]);
               	
           		if (target == null) {
                   	OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);
                   	
           			PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + offline.getName() + " §7has been whitelisted.");
           			offline.setWhitelisted(true);
           			return true;
           		}
           		
       			PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7has been whitelisted.");
       			target.setWhitelisted(true);
    			return true;
           	} 
       		
       		if (args[0].equalsIgnoreCase("remove")) {
               	Player target = Bukkit.getServer().getPlayer(args[1]);
               	
           		if (target == null) {
                   	OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);
                   	
           			PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + offline.getName() + " §7is no longer whitelisted.");
           			offline.setWhitelisted(false);
           			return true;
           		}
           		
       			PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7is no longer whitelisted.");
           		target.setWhitelisted(false);
    			return true;
           	}  
       	}
		
       	if (args[0].equalsIgnoreCase("on")) {
   			PlayerUtils.broadcast(Main.PREFIX + "The whitelist is now on");
   			
   			Bukkit.getServer().setWhitelist(true);
   		} 
   		else if (args[0].equalsIgnoreCase("off")) {
   			PlayerUtils.broadcast(Main.PREFIX + "The whitelist is now off");
   			
   			Bukkit.getServer().setWhitelist(false);
   		} 
   		else if (args[0].equalsIgnoreCase("all")) {
   			for (Player online : PlayerUtils.getPlayers()) {
   				online.setWhitelisted(true);
   			}
   			
   			PlayerUtils.broadcast(Main.PREFIX + "All players has been whitelisted.");
   		} 
   		else if (args[0].equalsIgnoreCase("clear")) {
   			for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
   				whitelisted.setWhitelisted(false);
   			}
   			
   			PlayerUtils.broadcast(Main.PREFIX + "The whitelist has been cleared.");
   		}
   		else if (args[0].equalsIgnoreCase("list")) {
   			if (Bukkit.getWhitelistedPlayers().size() <= 0) {
		    	sender.sendMessage(Main.PREFIX + "There are no whitelisted players.");
				return true;
			}
   			
   			StringBuilder list = new StringBuilder();
	    	int i = 1;
	    		
	    	for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
	    		if (list.length() > 0) {
					if (i == Bukkit.getWhitelistedPlayers().size()) {
						list.append(" and ");
					} else {
						list.append(", ");
					}
				}
				
				list.append(whitelisted.getName());
				i++;
			}
	    			
	    	sender.sendMessage(Main.PREFIX + "There are §6" + (i - 1) + " §7whitelisted players");
	    	sender.sendMessage("§8» §7Wl'd players: §f" + list.toString() + ".");
   		} 
   		else {
       		sender.sendMessage(Main.PREFIX + "Usage: /whitelist <on|off|add|remove|all|clear|list> [player]");
   		}
       	return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.whitelist")) {
			return null;
		}

    	ArrayList<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
	    	ArrayList<String> types = new ArrayList<String>();
	    	
        	types.add("on");
        	types.add("off");
        	types.add("add");
        	types.add("remove");
        	types.add("all");
        	types.add("clear");
        	types.add("list");
        	
        	if (args[0].equals("")) {
        		for (String type : types) {
        			toReturn.add(type);
        		}
        	} else {
        		for (String type : types) {
        			if (type.startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
        }
		
		if (args.length == 2) {
        	if (args[0].equalsIgnoreCase("add")) {
	        	if (args[1].equals("")) {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			if (!online.isWhitelisted()) {
		        			toReturn.add(online.getName());
	        			}
	        		}
	        	} else {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				if (!online.isWhitelisted()) {
		        				toReturn.add(online.getName());
	        				}
	        			}
	        		}
	        	}
        	} else if (args[0].equalsIgnoreCase("remove")) {
	        	if (args[1].equals("")) {
	        		for (OfflinePlayer whitelisted : Bukkit.getServer().getWhitelistedPlayers()) {
	        			toReturn.add(whitelisted.getName());
	        		}
	        	} else {
	        		for (OfflinePlayer whitelisted : Bukkit.getServer().getWhitelistedPlayers()) {
	        			if (whitelisted.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(whitelisted.getName());
	        			}
	        		}
	        	}
        	}
        }
    	
    	return toReturn;
	}
}