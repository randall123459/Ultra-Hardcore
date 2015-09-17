package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;

public class ReplyCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("reply")) {
			if (args.length == 0) {
	    		sender.sendMessage(ChatColor.RED + "Usage: /reply <message>");
	        	return true;
	        }   
	    	
	    	if (!Main.msg.containsKey(sender)) {
				sender.sendMessage(ChatColor.RED + "You have no one to reply to.");
				return true;
			}
	        
	        CommandSender target = Main.msg.get(sender);
					
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "You have no one to reply to.");
				return true;
			}
			
	        StringBuilder message = new StringBuilder();
	               
	        for (int i = 0; i < args.length; i++) {
	        	message.append(args[i]).append(" ");
	        }
            
	        String msg = message.toString().trim();			
	        
	        if (Spectator.getManager().isSpectating(sender.getName()) && !sender.hasPermission("uhc.seemsg") && !Spectator.getManager().isSpectating(target.getName())) {
	        	if (!target.hasPermission("uhc.seemsg")) {
	        		sender.sendMessage(ChatColor.RED + "You cannot message players that's not spectating.");
		        	return true;
	        	}
	        }
	 
	    	sender.sendMessage("§6me §8-> §6" + target.getName() + " §8» §f" + msg);
	    	target.sendMessage("§6" + sender.getName() + " §8-> §6me §8» §f" + msg);
	    	Main.msg.put(target, sender);
	    	Main.msg.put(sender, target);
	    }
		return true;
	}
}