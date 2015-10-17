package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

public class PregenCommand implements CommandExecutor {
	private Settings config = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setup")) {
			if (sender.hasPermission("uhc.setup")) {
				
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setup")) {
			if (sender.hasPermission("uhc.setup")) {
				if (args.length == 1) {
		        	ArrayListtrshn<String> arg = new ArrayList<String>();
		        	ArrayList<String> types = new ArrayList<String>();
		        	types.add("cancel");
		        	
		        	if (!args[0].equals("")) {
		        		for (String type : types) {
		        			if (type.startsWith(args[0].toLowerCase())) {
		        				arg.add(type);
		        			}
		        		}
		        	}
		        	else {
		        		for (String type : types) {
		        			arg.add(type);
		        		}
		        	}
		        	return arg;
		        }
			}
		}
		return null;
	}
}