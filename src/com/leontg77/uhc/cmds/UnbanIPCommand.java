package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanList.Type;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * UnbanIP command class
 * 
 * @author LeonTG77
 */
public class UnbanIPCommand implements CommandExecutor, TabCompleter {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unbanip")) {
			if (sender.hasPermission("uhc.unban")) {
				if (args.length == 0) {
					sender.sendMessage(Main.prefix() + "Usage: /unbanip <ip>");
					return true;
				}
		    	
				PlayerUtils.broadcast(Main.prefix() + "An IP has been unbanned.");
		   		Bukkit.getBanList(Type.IP).pardon(args[0]);
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unbanip")) {
			if (sender.hasPermission("uhc.unban")) {
				if (args.length == 1) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	
		        	if (!args[0].equals("")) {
		        		for (BanEntry banned : Bukkit.getServer().getBanList(Type.IP).getBanEntries()) {
		        			if (banned.getTarget().toLowerCase().startsWith(args[0].toLowerCase())) {
		        				arg.add(banned.getTarget());
		        			}
		        		}
		        	}
		        	else {
		        		for (BanEntry banned : Bukkit.getServer().getBanList(Type.IP).getBanEntries()) {
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