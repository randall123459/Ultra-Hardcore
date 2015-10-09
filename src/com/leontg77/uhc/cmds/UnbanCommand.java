package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
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

				BanList list = Bukkit.getBanList(Type.NAME);
				String target = args[0];
		    	
				if (list.isBanned(target)) {
					PlayerUtils.broadcast(Main.prefix() + "§6" + target + " §7has been unbanned.");
					list.pardon(target);
				} else {
					sender.sendMessage(Main.prefix() + "§6" + target + " §7is not banned.");
				}
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unban")) {
        	ArrayList<String> returnList = new ArrayList<String>();
        	BanList list = Bukkit.getBanList(Type.NAME);
        	
        	if (args[0].isEmpty()) {
        		for (BanEntry entry : list.getBanEntries()) {
        			String name = entry.getTarget();
        			
        			returnList.add(name);
        		}
        	}
        	else {
        		for (BanEntry entry : list.getBanEntries()) {
        			String name = entry.getTarget();
        			
        			if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
        				returnList.add(name);
        			}
        		}
        	}
        	return returnList;
        }
		return null;
	}
}