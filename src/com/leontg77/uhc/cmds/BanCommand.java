package com.leontg77.uhc.cmds;

import java.util.ArrayList;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Ban command class
 * 
 * @author LeonTG77
 */
public class BanCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("ban")) {
			if (sender.hasPermission("uhc.ban")) {
				if (args.length < 2) {
					sender.sendMessage(Main.prefix() + "Usage: /ban <player> <reason>");
					return true;
				}
		    	
		    	final Player target = Bukkit.getServer().getPlayer(args[0]);
				StringBuilder reason = new StringBuilder("");
					
				for (int i = 1; i < args.length; i++) {
					reason.append(args[i]).append(" ");
				}
						
				final String msg = reason.toString().trim();

		    	if (target == null) {
					PlayerUtils.broadcast(Main.prefix() + "§6" + args[0] + " §7has been banned for §a" + msg);
		    		Bukkit.getBanList(Type.NAME).addBan(args[0], msg, null, sender.getName());
					Scoreboards.getManager().resetScore(args[0]);
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
						PlayerUtils.broadcast(Main.prefix() + "§6" + args[0] + " §7has been banned for §a" + msg);
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
				    	}
				    	
			    		BanEntry ban = Bukkit.getBanList(Type.NAME).addBan(target.getName(), msg, null, sender.getName());
				    	target.setWhitelisted(false);
				    	
						Scoreboards.getManager().resetScore(args[0]);
				    	Scoreboards.getManager().resetScore(target.getName());
				    	
				    	PlayerDeathEvent event = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
						Bukkit.getServer().getPluginManager().callEvent(event);
						
				    	target.kickPlayer(
				    	"§8» §7You have been §4banned §7from §6Arctic UHC §8«" +
				    	"\n" + 
				    	"\n§cReason §8» §7" + ban.getReason() +
				    	"\n§cBanned by §8» §7" + ban.getSource() +
			 			"\n" +
				   		"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				    	);
					}
				}, 60);
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
}