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

public class TempbanCommand implements CommandExecutor {	

	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("tempban")) {
			if (sender.hasPermission("uhc.tempban")) {
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <time> <reason>");
					return true;
				}
		    	
		    	final Player target = Bukkit.getServer().getPlayer(args[0]);
							
				StringBuilder reason = new StringBuilder("");
					
				for (int i = 2; i < args.length; i++) {
					reason.append(args[i]).append(" ");
				}
				
				long time = DateUtils.parseDateDiff(args[1], true);
				final Date date = new Date(time);
						
				final String msg = reason.toString().trim().trim();

		    	if (target == null) {
					PlayerUtils.broadcast(Main.prefix() + "§6" + args[0] + " §7has been temp-banned for §6" + msg);
					Scoreboards.getManager().resetScore(args[0]);
		    		Bukkit.getServer().getBanList(Type.NAME).addBan(args[0], msg, date, sender.getName());
		            return true;
				}

				PlayerUtils.broadcast(Main.prefix() + "Incoming DQ in §63§7.");
		    	for (Player online : PlayerUtils.getPlayers()) {
		    		online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1, 1);
		    	}
	    		
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						PlayerUtils.broadcast(Main.prefix() + "Incoming DQ in §62§7.");
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1, 1);
				    	}
					}
				}, 20);
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						PlayerUtils.broadcast(Main.prefix() + "Incoming DQ in §61§7.");
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1, 1);
				    	}
					}
				}, 40);
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						PlayerUtils.broadcast(Main.prefix() + "§6" + target.getName() + " §7has been temp-banned for §6" + msg);
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
				    	}
			    		Bukkit.getServer().getBanList(Type.NAME).addBan(target.getName(), msg, date, sender.getName());
				    	target.kickPlayer("§7" + msg + " §8- §cLasts: §7" + args[1]);
				    	target.setWhitelisted(false);
						Scoreboards.getManager().resetScore(args[0]);
				    	Scoreboards.getManager().resetScore(target.getName());
					}
				}, 60);
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}