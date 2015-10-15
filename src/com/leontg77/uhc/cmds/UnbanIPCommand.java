package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanList.Type;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
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
		if (!sender.hasPermission("uhc.unbanip")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /unbanip <player>");
			return true;
		}

		BanList list = Bukkit.getBanList(Type.IP);
		String ip = args[0];
    	
		if (list.isBanned(ip)) {
			PlayerUtils.broadcast(Main.PREFIX + "An IP has been unbanned.");
			list.pardon(ip);
		} else {
			sender.sendMessage(Main.PREFIX + "That IP is not banned.");
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.unbanip")) {
			return null;
		}
		
    	ArrayList<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	BanList list = Bukkit.getBanList(Type.IP);
        	
        	if (args[0].isEmpty()) {
        		for (BanEntry entry : list.getBanEntries()) {
        			String ip = entry.getTarget();
        			
        			toReturn.add(ip);
        		}
        	}
        	else {
        		for (BanEntry entry : list.getBanEntries()) {
        			String ip = entry.getTarget();
        			
        			if (ip.toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(ip);
        			}
        		}
        	}
        }
		return toReturn;
	}
}