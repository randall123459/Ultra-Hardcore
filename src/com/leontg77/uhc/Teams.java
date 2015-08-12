package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Team class for uhc.
 * @author LeonTG77
 */
public class Teams {
	private Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
	private ArrayList<Team> teams = new ArrayList<Team>();
	private static Teams manager = new Teams();
	private Teams() {}
	
	/**
	 * Gets the instance of the class.
	 * @return the instance.
	 */
	public static Teams getManager() {
		return manager;
	}

	/**
	 * Joins a team.
	 * @param teamName the team joining.
	 * @param player the player joining.
	 */
	public void joinTeam(String teamName, Player player) {	
		Team team = sb.getTeam(teamName);
		team.addEntry(player.getName());
	}
	
	/**
	 * Leaves the current team of the player.
	 * @param player the player leaving.
	 */
	public void leaveTeam(Player player) {	
		if (this.getTeam(player) != null) {
			this.getTeam(player).removeEntry(player.getName());
		}
	}

	/**
	 * Gets the team of a player.
	 * @param player the player wanting.
	 * @return The team.
	 */
	public Team getTeam(Player player) {
		return player.getScoreboard().getEntryTeam(player.getName());
	}
	
	/**
	 * Gets a list of all teams.
	 * @return the list of teams.
	 */
	public List<Team> getTeams() {
		return teams;
	}
	
	/**
	 * Gets a list of all teams.
	 * @return the list of teams.
	 */
	public List<Team> getTeamsWithPlayers() {
		List<Team> list = new ArrayList<Team>();
		for (Team team : teams) {
			if (team.getSize() > 0) {
				list.add(team);
			}
		}
		return list;
	}
	
	/**
	 * Sets up all the teams.
	 */
	public void setupTeams() {
		ArrayList<String> list = new ArrayList<String>();
		
		list.add(ChatColor.BLACK.toString());
		list.add(ChatColor.DARK_BLUE.toString());
		list.add(ChatColor.DARK_GREEN.toString());
		list.add(ChatColor.DARK_AQUA.toString());
		list.add(ChatColor.DARK_RED.toString());
		list.add(ChatColor.DARK_PURPLE.toString());
		list.add(ChatColor.GOLD.toString());
		list.add(ChatColor.GRAY.toString());
		list.add(ChatColor.DARK_GRAY.toString());
		list.add(ChatColor.BLUE.toString());
		list.add(ChatColor.GREEN.toString());
		list.add(ChatColor.AQUA.toString());
		list.add(ChatColor.RED.toString());
		list.add(ChatColor.LIGHT_PURPLE.toString());
		list.add(ChatColor.YELLOW.toString());
		list.add(ChatColor.WHITE.toString());
		
		ArrayList<String> list2 = new ArrayList<String>();
		
		for (String li : list) {
			list2.add(li + ChatColor.BOLD);
		}
		
		for (String li : list) {
			list2.add(li + ChatColor.ITALIC);
		}
		
		for (String li : list) {
			list2.add(li + ChatColor.UNDERLINE);
		}
		
		for (String li : list) {
			list2.add(li + ChatColor.STRIKETHROUGH);
		}
		
		for (String li : list) {
			list2.add(li + ChatColor.BOLD + ChatColor.ITALIC);
			list2.add(li + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.UNDERLINE);
			list2.add(li + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.STRIKETHROUGH);
			list2.add(li + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.STRIKETHROUGH + ChatColor.UNDERLINE);
			list2.add(li + ChatColor.BOLD + ChatColor.STRIKETHROUGH + ChatColor.UNDERLINE);
			list2.add(li + ChatColor.BOLD + ChatColor.UNDERLINE);
			list2.add(li + ChatColor.BOLD + ChatColor.STRIKETHROUGH);
			list2.add(li + ChatColor.ITALIC + ChatColor.UNDERLINE);
			list2.add(li + ChatColor.ITALIC + ChatColor.STRIKETHROUGH);
			list2.add(li + ChatColor.ITALIC + ChatColor.STRIKETHROUGH + ChatColor.UNDERLINE);
			list2.add(li + ChatColor.UNDERLINE + ChatColor.STRIKETHROUGH);
		}
		
		list.remove(ChatColor.WHITE.toString());
		list.remove(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString());

		list.addAll(list2);
		
		for (int i = 0; i < list.size(); i++) {
			Team team = (sb.getTeam("UHC" + (i + 1)) == null ? sb.registerNewTeam("UHC" + (i + 1)) : sb.getTeam("UHC" + (i + 1)));
			
			team.setDisplayName("UHC" + (i + 1));
			team.setPrefix(list.get(i));
			team.setSuffix("§r");
			team.setAllowFriendlyFire(true);
			team.setCanSeeFriendlyInvisibles(true);
			teams.add(team);
		}

		Bukkit.getLogger().info("§a[UHC] Setup " + teams.size() + " teams.");
	}
}