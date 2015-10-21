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

/**
 * Hall of fame command class.
 * 
 * @author LeonTG77
 */
public class HOFCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can view the hall of fame.");
			return true;
		}
		
		Player player = (Player) sender;
		String host = GameUtils.getCurrentHost();
		
		Settings settings = Settings.getInstance();
		InvGUI inv = InvGUI.getInstance();
		
		if (args.length > 0) {
			host = GameUtils.getHost(args[0]);
		}
		
		if (settings.getHOF().getConfigurationSection(host) == null) {
			sender.sendMessage(ChatColor.RED + "That player has not hosted any games here.");
			return true;
		}
		
		inv.openHOF(player, host);
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		
    	ArrayList<String> toReturn = new ArrayList<String>();
		Settings settings = Settings.getInstance();
    	
		if (args.length == 1) {
        	if (args[0].equals("")) {
        		for (String type : settings.getHOF().getKeys(false)) {
    				toReturn.add(type);
        		}
        	} else {
        		for (String type : settings.getHOF().getKeys(false)) {
        			if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
        }
		
    	return toReturn;
	}
}