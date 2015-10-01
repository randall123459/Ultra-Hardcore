package com.leontg77.uhc.cmds;

import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.utils.NumberUtils;

public class StatsCommand implements CommandExecutor {
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("stats")) {
			if (args.equals(args)) {
				sender.sendMessage(Main.prefix() + "Stats are broken, will be fixed shortly.");
				return true;
			}
			
			if (Game.getInstance().isRR()) {
				sender.sendMessage(Main.prefix() + "Stats are disabled in RR's.");
				return true;
			}
			
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					User user = User.get(player);
					
					player.sendMessage("§8---------------------------");
					player.sendMessage(" §8» §6Your stats");
					player.sendMessage(" §8» §6First joined: §7" + new Date(user.getFile().getLong("firstjoined")));
					player.sendMessage("§8---------------------------");
					player.sendMessage(" §8» §aWins: §7" + user.getStat(Stat.WINS));
					player.sendMessage(" §8» §aKills: §7" + user.getStat(Stat.KILLS));
					player.sendMessage(" §8» §aDeaths: §7" + user.getStat(Stat.DEATHS));
					if (user.getStat(Stat.DEATHS) == 0) {
						player.sendMessage(" §8» §aK/D: §7" + user.getStat(Stat.KILLS));
					} else {
						player.sendMessage(" §8» §aK/D: §7" + NumberUtils.convertDouble((((double) user.getStat(Stat.KILLS)) / ((double) user.getStat(Stat.DEATHS)))));
					}
					player.sendMessage(" §8» §aGames played: §7" + user.getStat(Stat.GAMESPLAYED));
					player.sendMessage(" §8» §aArena kills: §7" + user.getStat(Stat.ARENAKILLS));
					player.sendMessage(" §8» §aArena deaths: §7" + user.getStat(Stat.ARENADEATHS));
					if (user.getStat(Stat.ARENADEATHS) == 0) {
						player.sendMessage(" §8» §aArena K/D: §7" + user.getStat(Stat.ARENAKILLS));
					} else {
						player.sendMessage(" §8» §aArena K/D: §7" + NumberUtils.convertDouble((((double) user.getStat(Stat.ARENAKILLS)) / ((double) user.getStat(Stat.ARENADEATHS)))));
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
			
			User user = User.get(target);
			
			sender.sendMessage("§8---------------------------");
			sender.sendMessage(" §8» §6" + target.getName() + "'s stats");
			sender.sendMessage(" §8» §6First joined: §7" + new Date(user.getFile().getLong("firstjoined")));
			sender.sendMessage("§8---------------------------");
			sender.sendMessage(" §8» §aWins: §7" + user.getStat(Stat.WINS));
			sender.sendMessage(" §8» §aKills: §7" + user.getStat(Stat.KILLS));
			sender.sendMessage(" §8» §aDeaths: §7" + user.getStat(Stat.DEATHS));
			if (user.getStat(Stat.DEATHS) == 0) {
				sender.sendMessage(" §8» §aK/D: §7" + user.getStat(Stat.KILLS));
			} else {
				sender.sendMessage(" §8» §aK/D: §7" + NumberUtils.convertDouble((((double) user.getStat(Stat.KILLS)) / ((double) user.getStat(Stat.DEATHS)))));
			}
			sender.sendMessage(" §8» §aGames played: §7" + user.getStat(Stat.GAMESPLAYED));
			sender.sendMessage(" §8» §aArena kills: §7" + user.getStat(Stat.ARENAKILLS));
			sender.sendMessage(" §8» §aArena deaths: §7" + user.getStat(Stat.ARENADEATHS));
			if (user.getStat(Stat.ARENADEATHS) == 0) {
				sender.sendMessage(" §8» §aArena K/D: §7" + user.getStat(Stat.ARENAKILLS));
			} else {
				sender.sendMessage(" §8» §aArena K/D: §7" + NumberUtils.convertDouble((((double) user.getStat(Stat.ARENAKILLS)) / ((double) user.getStat(Stat.ARENADEATHS)))));
			}
			sender.sendMessage("§8---------------------------");
		}
		return true;
	}
}