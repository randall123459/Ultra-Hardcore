package com.leontg77.uhc.cmds;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Data;
import com.leontg77.uhc.utils.NumberUtils;

public class StatsCommand implements CommandExecutor {
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("stats")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					Data data = Data.getFor(player);
					
					player.sendMessage("§8---------------------------");
					player.sendMessage(" §8» §6Your stats");
					player.sendMessage(" §8» §6First joined: §7" + new Date(data.getFile().getLong("firstjoined")));
					player.sendMessage("§8---------------------------");
					player.sendMessage(" §8» §aWins: §7" + data.getStat("wins"));
					player.sendMessage(" §8» §aKills: §7" + data.getStat("kills"));
					player.sendMessage(" §8» §aDeaths: §7" + data.getStat("deaths"));
					if (data.getStat("deaths") == 0) {
						player.sendMessage(" §8» §aK/D: §7" + data.getStat("kills"));
					} else {
						player.sendMessage(" §8» §aK/D: §7" + NumberUtils.convertDouble((((double) data.getStat("kills")) / ((double) data.getStat("deaths")))));
					}
					player.sendMessage(" §8» §aGames played: §7" + data.getStat("gamesplayed"));
					player.sendMessage(" §8» §aArena kills: §7" + data.getStat("arenakills"));
					player.sendMessage(" §8» §aArena deaths: §7" + data.getStat("arenadeaths"));
					if (data.getStat("arenadeaths") == 0) {
						player.sendMessage(" §8» §aArena K/D: §7" + data.getStat("arenakills"));
					} else {
						player.sendMessage(" §8» §aArena K/D: §7" + NumberUtils.convertDouble((((double) data.getStat("arenakills")) / ((double) data.getStat("arenadeaths")))));
					}
					player.sendMessage("§8---------------------------");
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can view their stats.");
				}
				return true;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "That player is not online.");
				return true;
			}
			
			Data data = Data.getFor(target);
			
			sender.sendMessage("§8---------------------------");
			sender.sendMessage(" §8» §6" + target.getName() + "'s stats");
			sender.sendMessage(" §8» §6First joined: §7" + new Date(data.getFile().getLong("firstjoined")));
			sender.sendMessage("§8---------------------------");
			sender.sendMessage(" §8» §aWins: §7" + data.getStat("wins"));
			sender.sendMessage(" §8» §aKills: §7" + data.getStat("kills"));
			sender.sendMessage(" §8» §aDeaths: §7" + data.getStat("deaths"));
			if (data.getStat("deaths") == 0) {
				sender.sendMessage(" §8» §aK/D: §7" + data.getStat("kills"));
			} else {
				sender.sendMessage(" §8» §aK/D: §7" + NumberUtils.convertDouble((((double) data.getStat("kills")) / ((double) data.getStat("deaths")))));
			}
			sender.sendMessage(" §8» §aGames played: §7" + data.getStat("gamesplayed"));
			sender.sendMessage(" §8» §aArena kills: §7" + data.getStat("arenakills"));
			sender.sendMessage(" §8» §aArena deaths: §7" + data.getStat("arenadeaths"));
			if (data.getStat("arenadeaths") == 0) {
				sender.sendMessage(" §8» §aArena K/D: §7" + data.getStat("arenakills"));
			} else {
				sender.sendMessage(" §8» §aArena K/D: §7" + NumberUtils.convertDouble((((double) data.getStat("arenakills")) / ((double) data.getStat("arenadeaths")))));
			}
			sender.sendMessage("§8---------------------------");
		}
		return true;
	}
}