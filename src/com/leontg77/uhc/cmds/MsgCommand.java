package com.leontg77.uhc.cmds;

import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.User;
import com.leontg77.uhc.utils.DateUtils;

public class MsgCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("msg")) {
	    	if (args.length < 2) {
	    		sender.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
	        	return true;
	        }
			
			if (sender instanceof Player) {
				User user = User.get((Player) sender);
		    	
				if (user.isMuted()) {
					TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
					Date date = new Date();
					
					if (user.getUnmuteTime() == -1 || user.getUnmuteTime() > date.getTime()) {
						sender.sendMessage(Main.prefix() + "You have been muted for: §a" + user.getMutedReason());
						
						if (user.getUnmuteTime() < 0) {
							sender.sendMessage(Main.prefix() + "Your mute is permanent.");
						} else {
							sender.sendMessage(Main.prefix() + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getUnmuteTime()));
						}
						return true;
					} 
					else {
						user.setMuted(false, "N", null, "N");
					}
				}
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
	        
	        if (Spectator.getManager().isSpectating(sender.getName()) && !sender.hasPermission("uhc.seemsg") && !Spectator.getManager().isSpectating(target.getName())) {
	        	if (!target.hasPermission("uhc.seemsg")) {
	        		sender.sendMessage(ChatColor.RED + "You cannot message players that's not spectating.");
		        	return true;
	        	}
	        }
	        
	        String msg = message.toString().trim();
	               
	        sender.sendMessage("§6me §8-> §6" + target.getName() + " §8» §f" + msg);
	    	target.sendMessage("§6" + sender.getName() + " §8-> §6me §8» §f" + msg);
	    	Main.msg.put(target, sender);
			Main.msg.put(sender, target);
	    }
		return true;
	}
}