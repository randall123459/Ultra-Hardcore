package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Random command class.
 * 
 * @author LeonTG77
 */
public class RandomCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.random")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /random <size> [playersnotplaying...]");
			return true;
		}
		
		int size = 1;
		
		try {
			size = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild teamsize.");
			return true;
		}
		
		ArrayList<Player> list = new ArrayList<Player>();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.getScoreboard().getEntryTeam(online.getName()) == null) {
				list.add(online);
			}
		}
		
		Collections.shuffle(list);
		
		if (args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				Player target = Bukkit.getServer().getPlayer(args[i]);
				
				if (target != null) {
					if (list.contains(target)) {
						list.remove(target);
					}
				}
			}
		}

		Teams teams = Teams.getInstance();
		Team team = teams.findAvailableTeam();
		
		if (team == null) {
			sender.sendMessage(ChatColor.RED + "No more available teams.");
			return true;
		}
		
		try {
			for (int i = 0; i < size; i++) {
				if (list.size() < i) {
					sender.sendMessage(ChatColor.RED + "Could not add a player to team " + team.getName() + ".");
					continue;
				}
				
				Player p = list.get(i);
				team.addEntry(p.getName());
				p.sendMessage(Main.PREFIX + "You were added to team " + team.getName());
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Not enough players for this team.");
		}

		if (team.getSize() > 0) {
			sender.sendMessage(Main.PREFIX + "Created a team of " + size + " using team " + team.getName() + ".");
			
			teams.sendMessage(team, Main.PREFIX + "You were added to §6" + team.getName() + "§7.");
			teams.sendMessage(team, Main.PREFIX + "Your teammates:");
			
			for (String entry : team.getEntries()) {
				teams.sendMessage(team, Main.PREFIX + "§a" + entry);
			}
		}
		return true;
	}
}