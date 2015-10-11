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

/**
 * Msg command class
 * 
 * @author LeonTG77
 */
public class MsgCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (args.length < 2) {
    		sender.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
        	return true;
        }
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = User.get(player);
	    	
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
				} else {
					user.unmute();
				}
			}
		}
    	   
    	Player target = Bukkit.getServer().getPlayer(args[0]);
        Spectator spec = Spectator.getManager();
               
        if (target == null) {
        	sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
        	return true;
        }
        
        if (spec.isSpectating(sender.getName()) && !spec.isSpectating(target.getName()) && !sender.hasPermission("uhc.seemsg") && !target.hasPermission("uhc.seemsg")) {
    		sender.sendMessage(Main.prefix() + "");
        	return true;
    	}
               
        StringBuilder message = new StringBuilder();
               
        for (int i = 1; i < args.length; i++) {
        	message.append(args[i]).append(" ");
        }
        
        String msg = message.toString().trim();
               
        sender.sendMessage("§6me §8-> §6" + target.getName() + " §8» §f" + msg);
    	target.sendMessage("§6" + sender.getName() + " §8-> §6me §8» §f" + msg);
    	
    	Main.msg.put(target, sender);
		Main.msg.put(sender, target);
		return true;
    }
}