package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Team management class.
 * <p>
 * Contains methods for adding players to a team, removing players from a team, getting the team for a player and setting up teams.
 * 
 * @author LeonTG77
 */
public class Teams {
	private Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
	private List<Team> teams = new ArrayList<Team>();
	
	private static Teams manager = new Teams();
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Teams getManager() {
		return manager;
	}
	
	/**
	 * Set up all the teams.
	 */
	public void setup() {
		teams.clear();
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

		Collections.shuffle(list);
		
		ArrayList<String> tempList = new ArrayList<String>();
		
		for (String li : list) {
			tempList.add(li + ChatColor.ITALIC);
		}
		
		for (String li : list) {
			tempList.add(li + ChatColor.BOLD);
		}
		
		for (String li : list) {
			tempList.add(li + ChatColor.UNDERLINE);
		}
		
		for (String li : list) {
			tempList.add(li + ChatColor.STRIKETHROUGH);
		}
		
		tempList.remove(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString());
		list.remove(ChatColor.WHITE.toString());

		list.addAll(tempList);
		
		Team spec = (sb.getTeam("spec") == null ? sb.registerNewTeam("spec") : sb.getTeam("spec"));
		spec.setDisplayName("spec");
		spec.setPrefix("§7§o");
		spec.setSuffix("§f");
		
		spec.setAllowFriendlyFire(false);
		spec.setCanSeeFriendlyInvisibles(true);	
		spec.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		
		for (int i = 0; i < list.size(); i++) {
			String teamName = "UHC" + (i + 1);
			
			Team team = (sb.getTeam(teamName) == null ? sb.registerNewTeam(teamName) : sb.getTeam(teamName));
			team.setDisplayName(teamName);
			team.setPrefix(list.get(i));
			team.setSuffix("§f");
			
			team.setAllowFriendlyFire(true);
			team.setCanSeeFriendlyInvisibles(true);
			team.setNameTagVisibility(NameTagVisibility.ALWAYS);
			
			teams.add(team);
		}

		Bukkit.getLogger().info("[UHC] Setup " + (teams.size() + 1) + " teams.");
	}
	
	/**
	 * Find the first available team.
	 * 
	 * @return The available team, null if not found.
	 */
	public Team findAvailableTeam() {
		for (Team team : getTeams()) {
			if (team.getSize() == 0) {
				return team;
			}
		}
		return null;
	}

	/**
	 * Joins the specified team for the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param team the team joining.
	 * @param player the player joining.
	 */
	public void joinTeam(Team team, OfflinePlayer player) {	
		if (team != null) {
			team.addEntry(player.getName());
		}
	}

	/**
	 * Joins the specified team for the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param name the name of the team joining.
	 * @param player the player joining.
	 */
	public void joinTeam(String name, OfflinePlayer player) {	
		Team team = sb.getTeam(name);
		
		if (team != null) {
			team.addEntry(player.getName());
		}
	}
	
	/**
	 * Leaves the current team of the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param player the player thats leaving the team.
	 */
	public void leaveTeam(OfflinePlayer player) {
		Team team = getTeam(player);
		
		if (team != null) {
			team.removeEntry(player.getName());
		}
	}
	
	/**
	 * Sends a message to everyone on the given team.
	 * 
	 * @param team the team.
	 * @param message the message to send.
	 */
	public void sendMessage(Team team, String message) {
		for (String entry : team.getEntries()) {
			Player teammate = Bukkit.getServer().getPlayer(entry);
			
			if (teammate != null) {
				teammate.sendMessage(message);
			}
		}
	}

	/**
	 * Gets the team of the given player player.
	 * 
	 * @param player the player in the team.
	 * @return The team, null if the player isn't on a team.
	 */
	public Team getTeam(OfflinePlayer player) {
		return sb.getEntryTeam(player.getName());
	}

	/**
	 * Gets the team by a name.
	 * 
	 * @param name the name.
	 * @return The team.
	 */
	public Team getTeam(String name) {
		return sb.getTeam(name);
	}
	
	/**
	 * Get a list of all teams.
	 * 
	 * @return A list of all teams.
	 */
	public List<Team> getTeams() {
		return teams;
	}
	
	/**
	 * Get a list of all teams that has players on it.
	 * 
	 * @return A list of teams with players.
	 */
	public List<Team> getTeamsWithPlayers() {
		ArrayList<Team> list = new ArrayList<Team>();
		
		for (Team team : teams) {
			if (team.getSize() > 0) {
				list.add(team);
			}
		}
		
		return list;
	}
}