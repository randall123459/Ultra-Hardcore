package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class Captains extends Scenario implements Listener {
	private ArrayList<String> captains = new ArrayList<String>();
	private boolean enabled = false;
	private String chooser = "none";
	private boolean cycle = false;
	private int current = -1;

	public Captains() {
		super("Captains", "Theres X amount of captains, there will be rounds where one captain will choose a player until it reaches the teamsize.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		ArrayList<String> ar = new ArrayList<String>();
		for (String arg : event.getMessage().split(" ")) {
			ar.add(arg);
		}
		ar.remove(0);
		
		Player player = event.getPlayer();
		String cmd = event.getMessage().split(" ")[0];
		String[] args = ar.toArray(new String[ar.size()]);
		
		if (cmd.equalsIgnoreCase("/addcaptain")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "Captains is not enabled.");
				return;
			}
			
			if (!player.hasPermission("uhc.captains")) {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return;
			}
			
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /addcaptain <player>");
				return;
			}
			
			if (captains.contains(args[0])) {
				player.sendMessage(ChatColor.RED + args[0] + " is already an captain.");
				return;
			}

			Team t = null;
			
			for (Team team : Teams.getManager().getTeams()) {
				if (team.getSize() == 0) {
					t = team;
					break;
				}
			}
			
			if (t == null) {
				player.sendMessage(ChatColor.RED + "No more available teams.");
				return;
			}
			
			t.addEntry(args[0]);
			
			captains.add(args[0]);
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + args[0] + " is now an captain.");
		}
		
		if (cmd.equalsIgnoreCase("/removecaptain")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "Captains is not enabled.");
				return;
			}
			
			if (!player.hasPermission("uhc.captains")) {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return;
			}
			
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /removecaptain <player>");
				return;
			}
			
			if (!captains.contains(args[0])) {
				player.sendMessage(ChatColor.RED + args[0] + " is not an captain.");
				return;
			}
			
			Team t = player.getScoreboard().getEntryTeam(args[0]);
			
			if (t != null) {
				t.removeEntry(args[0]);
			}
			
			captains.remove(args[0]);
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + args[0] + " is no longer an captain.");
		}
		
		if (cmd.equalsIgnoreCase("/randomcaptains")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "Captains is not enabled.");
				return;
			}
			
			if (!player.hasPermission("uhc.captains")) {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return;
			}
			
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /randomcaptains <amount>");
				return;
			}
			
			int amount;
			
			try {
				amount = Integer.parseInt(args[0]);
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "Invaild number.");
				return;
			}
			
			for (int i = 1; i <= amount; i++) {
				ArrayList<String> list = new ArrayList<String>();
				for (Player online : PlayerUtils.getPlayers()) {
					if (captains.contains(online.getName())) {
						continue;
					}
					
					list.add(online.getName());
				}
				
				String s = list.get(new Random().nextInt(list.size()));
				captains.add(s);
				
				Team t = null;
				
				for (Team team : Teams.getManager().getTeams()) {
					if (team.getSize() == 0) {
						t = team;
						break;
					}
				}
				
				if (t == null) {
					return;
				}
				
				t.addEntry(s);
				PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + s + " §7is now an captain.");
			}
		}
		
		if (cmd.equalsIgnoreCase("/cycle")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "Captains is not enabled.");
				return;
			}
			
			if (!player.hasPermission("uhc.captains")) {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return;
			}
			
			if (cycle) {
				cycle = false;
				PlayerUtils.broadcast(Main.prefix() + "Captains can no longer choose players.");
				chooser = "none";
				current = -1;
			} else {
				cycle = true;
				PlayerUtils.broadcast(Main.prefix() + "Captains can now choose players.");
				String cap = captains.get(0);
				PlayerUtils.broadcast(Main.prefix() + "First captain to choose is §a" + cap);
				chooser = cap;
				current = 0;
			}
		}
		
		if (cmd.equalsIgnoreCase("/choose")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "Captains is not enabled.");
				return;
			}
			
			if (!captains.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "You are not an captain");
				return;
			}
			
			if (player.getName() != chooser) {
				player.sendMessage(ChatColor.RED + "You are not the one choosing.");
				return;
			}
			
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /choose <player>");
				return;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				player.sendMessage(ChatColor.RED + "That player is not online.");
				return;
			}
			
			if (target == player) {
				player.sendMessage(ChatColor.RED + "You cannot choose yourselves.");
				return;
			}
			
			if (target.getScoreboard().getEntryTeam(target.getName()) != null) {
				player.sendMessage(ChatColor.RED + "That player is already taken.");
				return;
			}
			
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			
			if (team != null) {
				team.addEntry(target.getName());
			}
			
			current++;
			if (current >= captains.size()) {
				current = 0;
			}
			
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getName() + " §7has picked §a" + target.getName() + "§7, next captain to choose is §a" + captains.get(current));
			chooser = captains.get(current);
		}
	}
}