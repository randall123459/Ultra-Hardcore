package com.leontg77.uhc;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * The class for managing scoreboards.
 * @author LeonTG77
 */
public class Scoreboards {
	private static Scoreboards manager = new Scoreboards();
	private Scoreboards() {}
	
	public Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
	public Objective kills = sb.getObjective("PK");
	public Objective ab = sb.getObjective("AB");
	public Objective heal = sb.getObjective("HP");
	public Objective heal2 = sb.getObjective("HP2");
	
	/**
	 * Gets the instance of the class.
	 * @return the instance.
	 */
	public static Scoreboards getManager() {
		return manager;
	}
	
	/**
	 * Sets the score of a player.
	 * @param player the player setting for.
	 * @param score the new score.
	 * @param game True if its the ingame kills scoreboard, false if its the arena board.
	 */
	public void setScore(String player, int score, boolean game) {
		Score scores;
		
		if (game) {
			scores = kills.getScore(player);
		} else {
			scores = ab.getScore(player);
		}
		
		scores.setScore(score);
	}

	/**
	 * Gets a score for a player.
	 * @param player the player getting for
	 * @return the score
	 */
	public int getScore(String player, boolean game) {
		if (game) {
			return kills.getScore(player).getScore();
		} else {
			return ab.getScore(player).getScore();
		}
	}

	/**
	 * Reset the score of a player.
	 * @param player the player.
	 */
	public void resetScore(String player) {
		sb.resetScores(player);
	}
	
	/**
	 * Sets up the scoreboards.
	 */
	public void setup() {
		if (sb.getObjective("PK") == null) {
			kills = sb.registerNewObjective("PK", "dummy");
			Bukkit.getLogger().info("§a[UHC] Setup player kill scoreboard.");
		}
		if (sb.getObjective("AB") == null) {
			ab = sb.registerNewObjective("AB", "dummy");
			Bukkit.getLogger().info("§a[UHC] Setup arena player kill scoreboard.");
		}
		if (sb.getObjective("HP") == null) {
			heal = sb.registerNewObjective("HP", "dummy");
			Bukkit.getLogger().info("§a[UHC] Setup tab list health scoreboard.");
		}
		if (sb.getObjective("HP2") == null) {
			heal2 = sb.registerNewObjective("HP2", "dummy");
			Bukkit.getLogger().info("§a[UHC] Setup bellow name health scoreboard.");
		}
		kills.setDisplayName("§4UHC §8- §7" + Settings.getInstance().getConfig().getString("game.host"));
		kills.setDisplaySlot(DisplaySlot.SIDEBAR);
		ab.setDisplayName("§4Open PvP §8- §7Join with /a");
		heal.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		heal2.setDisplaySlot(DisplaySlot.BELOW_NAME);
		heal2.setDisplayName("§4♥");
		Bukkit.getLogger().info("§a[UHC] Scoreboards has been setup.");
	}
}