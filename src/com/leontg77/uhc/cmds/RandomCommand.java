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

public class RandomCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("random")) {
			if (sender.hasPermission("uhc.random")) {
				if (args.length == 0) {
					sender.sendMessage(Main.prefix() + "Usage: /random <size> [playersnotplaying...]");
					return true;
				}
				
				int size = 1;
				
				try {
					size = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild team.");
					return true;
				}
				
				if (args.length == 1) {
					ArrayList<Player> a = new ArrayList<Player>();
					
					for (Player online : PlayerUtils.getPlayers()) {
						if (online.getScoreboard().getEntryTeam(online.getName()) == null) {
							a.add(online);
						}
					}
					
					Collections.shuffle(a);

					Team t = Teams.getManager().findAvailableTeam();
					
					if (t == null) {
						sender.sendMessage(ChatColor.RED + "No more available teams.");
						return true;
					}
					
					try {
						for (int i = 0; i < size; i++) {
							if (a.size() < i) {
								sender.sendMessage(ChatColor.RED + "Could not add a player to team " + t.getName() + ".");
								continue;
							}
							
							Player p = a.get(i);
							t.addEntry(p.getName());
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Not enough players for this team.");
					}

					if (t.getSize() > 0) {
						sender.sendMessage(Main.prefix() + "Created a rTo" + size + " using team " + t.getName() + ".");
						Teams.getManager().sendMessage(t, Main.prefix() + "You were added to §a" + t.getName() + "§7.");
						Teams.getManager().sendMessage(t, Main.prefix() + "Your teammates:");
						
						for (String entry : t.getEntries()) {
							Teams.getManager().sendMessage(t, Main.prefix() + "§a" + entry);
						}
					}
					return true;
				}
				
				ArrayList<Player> a = new ArrayList<Player>();
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (online.getScoreboard().getEntryTeam(online.getName()) == null) {
						a.add(online);
					}
				}
				
				Collections.shuffle(a);
				
				for (int i = 1; i < args.length; i++) {
					Player target = Bukkit.getServer().getPlayer(args[i]);
					
					if (target != null) {
						if (a.contains(target)) {
							a.remove(target);
						}
					}
				}

				Team t = Teams.getManager().findAvailableTeam();
				
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "No more available teams.");
					return true;
				}
				
				try {
					for (int i = 0; i < size; i++) {
						if (a.size() < i) {
							sender.sendMessage(ChatColor.RED + "Could not add a player to team " + t.getName() + ".");
							continue;
						}
						
						Player p = a.get(i);
						t.addEntry(p.getName());
						p.sendMessage(Main.prefix() + "You were added to team " + t.getName());
					}
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Not enough players for this team.");
				}

				if (t.getSize() > 0) {
					sender.sendMessage(Main.prefix() + "Created a rTo" + size + " using team " + t.getName() + ".");
					Teams.getManager().sendMessage(t, Main.prefix() + "You were added to §a" + t.getName() + "§7.");
					Teams.getManager().sendMessage(t, Main.prefix() + "Your teammates:");
					for (String entry : t.getEntries()) {
						Teams.getManager().sendMessage(t, Main.prefix() + "§a" + entry);
					}
				}
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
}