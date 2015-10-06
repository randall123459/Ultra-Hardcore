package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Unban command class
 * 
 * @author LeonTG77
 */
public class UnbanCommand implements CommandExecutor, TabCompleter {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unban")) {
			if (sender.hasPermission("uhc.unban")) {
				if (args.length == 0) {
					sender.sendMessage(Main.prefix() + "Usage: /unban <player>");
					return true;
				}
		    	
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GOLD + args[0] + " §7has been unbanned.");
		   		Bukkit.getBanList(Type.NAME).pardon(args[0]);
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unban")) {
			if (sender.hasPermission("uhc.unban")) {
				if (args.length == 1) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	
		        	if (!args[0].equals("")) {
		        		for (BanEntry banned : Bukkit.getBanList(Type.NAME).getBanEntries()) {
		        			if (banned.getTarget().toLowerCase().startsWith(args[0].toLowerCase())) {
		        				arg.add(banned.getTarget());
		        			}
		        		}
		        	}
		        	else {
		        		for (BanEntry banned : Bukkit.getBanList(Type.NAME).getBanEntries()) {
		        			arg.add(banned.getTarget());
		        		}
		        	}
		        	return arg;
		        }
			}
		}
		return null;
	}
}