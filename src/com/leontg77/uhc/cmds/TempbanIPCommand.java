package com.leontg77.uhc.cmds;

import java.sql.Date;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class TempbanIPCommand implements CommandExecutor {	

	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("tempban")) {
			if (sender.hasPermission("uhc.tempban")) {
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /tempbanip <ip> <time> <reason>");
					return true;
				}
							
				StringBuilder reason = new StringBuilder("");
					
				for (int i = 2; i < args.length; i++) {
					reason.append(args[i]).append(" ");
				}
				
				long time = DateUtils.parseDateDiff(args[1], true);
				final Date date = new Date(time);
						
				final String msg = reason.toString().trim().trim();
				
				PlayerUtils.broadcast(Main.prefix() + "An ip has been temp-banned for §6" + msg);
				
		    	for (Player online : PlayerUtils.getPlayers()) {
		    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
		    		
		    		if (online.getAddress().getAddress().getHostAddress().equals(args[0])) {
		    			online.kickPlayer("§7" + msg + " §8- §cLasts: §7" + args[1]);
		    			online.setWhitelisted(false);
				    	Scoreboards.getManager().resetScore(online.getName());
		    		}
		    	}
		    	
	    		Bukkit.getServer().getBanList(Type.IP).addBan(args[0], msg, date, sender.getName());
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}