package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.utils.GameUtils;

public class HOFCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can view the hall of fame.");
			return true;
		}
		
		Player player = (Player) sender;
		String host = GameUtils.getCurrentHost();
		
		if (cmd.getName().equalsIgnoreCase("hof")) {
			if (args.length > 0) {
				host = GameUtils.getHost(args[0]);
			}
			
			if (Settings.getInstance().getHOF().getConfigurationSection(host) == null) {
				sender.sendMessage(ChatColor.RED + "That player has not hosted any games here.");
				return true;
			}
			
			InvGUI.getManager().openHOF(player, host);
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("hof")) {
			if (args.length == 1) {
	        	ArrayList<String> arg = new ArrayList<String>();
	        	
	        	if (!args[0].equals("")) {
	        		for (String type : Settings.getInstance().getHOF().getKeys(false)) {
	        			if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
	        				arg.add(type);
	        			}
	        		}
	        	}
	        	else {
	        		for (String type : Settings.getInstance().getHOF().getKeys(false)) {
        				arg.add(type);
	        		}
	        	}
	        	return arg;
	        }
		}
		return null;
	}
}