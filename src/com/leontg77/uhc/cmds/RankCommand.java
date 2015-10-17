package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Rank;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Rank command class.
 * 
 * @author LeonTG77
 */
public class RankCommand implements CommandExecutor, TabCompleter {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.rank")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Main.PREFIX + "Usage: /rank <player> <newrank>");
			return true;
		}
		
		Rank rank;
		
		try {
			rank = Rank.valueOf(args[1].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(Main.PREFIX + args[1] + " is not a vaild rank.");
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[0]);
		
		if (target == null) {
			PlayerUtils.broadcast(Main.PREFIX + "§6" + offline.getName() + " §7has been given §a" + NameUtils.fixString(rank.name(), false) + " §7rank.");
			User.get(offline).setRank(rank);
			return true;
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been given §a" + NameUtils.fixString(rank.name(), false) + " §7rank.");
		User.get(target).setRank(rank);
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.rank")) {
			return null;
		}
		
		ArrayList<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			if (args[0].equals("")) {
        		for (Player online : PlayerUtils.getPlayers()) {
    				toReturn.add(online.getName());
        		}
        	} else {
        		for (Player online : PlayerUtils.getPlayers()) {
        			if (online.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(online.getName());
        			}
        		}
        	}
        }
		
		if (args.length == 2) {
			if (args[1].equals("")) {
        		for (Rank rank : Rank.values()) {
    				toReturn.add(rank.name().toLowerCase());
        		}
        	} else {
        		for (Rank rank : Rank.values()) {
        			if (rank.name().toLowerCase().startsWith(args[1].toLowerCase())) {
        				toReturn.add(rank.name().toLowerCase());
        			}
        		}
        	}
        }
		
		return toReturn;
	}
}