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
import com.leontg77.uhc.managers.Fireworks;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class RankCommand implements CommandExecutor, TabCompleter {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rank")) {
			if (sender.hasPermission("uhc.rank")) {
				if (args.length < 2) {
					sender.sendMessage(Main.prefix() + "Usage: /rank <player> <newrank>");
					return true;
				}
				
				Rank rank;
				
				try {
					rank = Rank.valueOf(args[1].toUpperCase());
				} catch (Exception e) {
					sender.sendMessage(Main.prefix() + "Invaild rank.");
					return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[0]);
				OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[0]);
				
				if (target == null) {
					User.get(offline).setRank(rank);
					PlayerUtils.broadcast(Main.prefix() + "§6" + offline.getName() + " §7has been given §a" + (rank == Rank.VIP ? "VIP" : NameUtils.fixString(rank.name(), false)) + " §7rank.");
					return true;
				}
				
				User.get(target).setRank(rank);
				Fireworks.getInstance().launchRandomFirework(target.getLocation());
				PlayerUtils.broadcast(Main.prefix() + "§6" + target.getName() + " §7has been given §a" + (rank == Rank.VIP ? "VIP" : NameUtils.fixString(rank.name(), false)) + " §7rank.");
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rank")) {
			if (sender.hasPermission("uhc.rank")) {
				ArrayList<String> arg = new ArrayList<String>();
				
				if (args.length == 1) {
					if (!args[0].equals("")) {
		        		for (Player online : PlayerUtils.getPlayers()) {
		        			if (online.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
		        				arg.add(online.getName());
		        			}
		        		}
		        	} else {
		        		for (Player online : PlayerUtils.getPlayers()) {
	        				arg.add(online.getName());
		        		}
		        	}
		        }
				
				if (args.length == 2) {
					if (!args[1].equals("")) {
		        		for (Rank rank : Rank.values()) {
		        			if (rank.name().toLowerCase().startsWith(args[1].toLowerCase())) {
		        				arg.add(rank.name().toLowerCase());
		        			}
		        		}
		        	} else {
		        		for (Rank rank : Rank.values()) {
	        				arg.add(rank.name().toLowerCase());
		        		}
		        	}
		        }
				return arg;
			}
		}
		return null;
	}
}