package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class VoteCommand implements CommandExecutor, TabCompleter {
	public static boolean vote = false;
	public static int yes = 0;
	public static int no = 0;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("vote")) {
			if (sender.hasPermission("uhc.vote")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /vote <message|end>");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("end")) {
					PlayerUtils.broadcast(Main.prefix() + "The vote has ended, §a" + yes + " yes §7and §c" + no + " no§7.");
					vote = false;
					yes = 0;
					no = 0;
					Main.voted.clear();
					return true;
				}
				
				StringBuilder message = new StringBuilder();
	               
		        for (int i = 0; i < args.length; i++) {
		        	message.append(args[i]).append(" ");
		        }
		        
		        String msg = message.toString().trim();
		        vote = true;
		        Main.voted.clear();
		        
		        PlayerUtils.broadcast(Main.prefix() + "A vote has started for " + msg + ".");
		        PlayerUtils.broadcast("§8§l» §7Say §a'y'§7 or §c'n'§7 in chat to vote.");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("vote")) {
			if (args.length == 1) {
	        	ArrayList<String> arg = new ArrayList<String>();
	        	ArrayList<String> types = new ArrayList<String>();
	        	types.add("end");
	        	
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
		return null;
	}
}