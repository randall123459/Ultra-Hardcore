package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Chat command class.
 * 
 * @author LeonTG77
 */
public class ChatCommand implements CommandExecutor, TabCompleter {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.chat")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /chat <clear|mute>");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("mute")) {
			Game game = Game.getInstance();
			
			if (game.isMuted()) {
				game.setMuted(false);
				PlayerUtils.broadcast(Main.PREFIX + "The chat has been enabled.");
				return true;
			} 
			
			PlayerUtils.broadcast(Main.PREFIX + "The chat has been disabled.");
			game.setMuted(true);
		} else if (args[0].equalsIgnoreCase("clear")) {
			for (int i = 0; i < 150; i++) {
				for (Player online : PlayerUtils.getPlayers()) {
					online.sendMessage("§0");
				}
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "The chat has been cleared.");
		} else {
			sender.sendMessage(Main.PREFIX + "Usage: /chat <clear|mute>");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.chat")) {
			return null;
		}

    	ArrayList<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
	    	ArrayList<String> types = new ArrayList<String>();
	    	types.add("clear");
	    	types.add("mute");
	    	
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
    	
    	return toReturn;
	}
}