package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

public class MsgCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("msg")) {
	    	if (args.length < 2) {
	    		sender.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
	        	return true;
	        }
	    	   
	    	Player target = Bukkit.getServer().getPlayer(args[0]);
	               
	        if (target == null) {
	        	sender.sendMessage(ChatColor.RED + "That player is not online.");
	        	return true;
	        }
	               
	        StringBuilder message = new StringBuilder();
	               
	        for (int i = 1; i < args.length; i++) {
	        	message.append(args[i]).append(" ");
	        }
	        
	        if (Main.spectating.contains(sender.getName()) && !sender.hasPermission("uhc.seemsg") && !Main.spectating.contains(target.getName())) {
	        	if (!target.hasPermission("uhc.seemsg")) {
	        		sender.sendMessage(ChatColor.RED + "You cannot message players that's not spectating.");
		        	return true;
	        	}
	        }
	        
	        String msg = message.toString().trim();
	               
	        sender.sendMessage("§6me §8-> §6" + target.getName() + " §8§l» §f" + msg);
	    	target.sendMessage("§6" + sender.getName() + " §8-> §6me §8§l» §f" + msg);
	    	Main.msg.put(target, sender);
			Main.msg.put(sender, target);
	    }
		return true;
	}
}