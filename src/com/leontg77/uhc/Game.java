package com.leontg77.uhc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main.BorderShrink;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Game management class.
 * <p>
 * This class contains all setters and getters for all togglable features.
 * 
 * @author LeonTG77
 */
public class Game {
	private static Game instance = new Game();
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Game getInstance() {
		return instance;
	}

	private Settings settings = Settings.getInstance();
	
	// ############################ BASIC ############################
	// TODO: <-- just for the marker.
	
	/**
	 * Change the FFA mode.
	 * 
	 * @param ffa True to enable, false to disable.
	 */
	public void setFFA(boolean ffa) {
		settings.getConfig().set("ffa", ffa);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
	}
	
	/**
	 * Check if the game is in FFA mode
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isFFA() {
		return settings.getConfig().getBoolean("ffa", true);
	}
	
	/**
	 * Set the team size of the game.
	 * 
	 * @param teamSize the new teamsize.
	 */
	public void setTeamSize(int teamSize) {
		settings.getConfig().set("teamsize", teamSize);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
	}
	
	/**
	 * Get the game teamsize.
	 * 
	 * @return The teamsize.
	 */
	public int getTeamSize() {
		return settings.getConfig().getInt("teamsize", 0);
	}
	
	/**
	 * Set the scenarios of the game.
	 * 
	 * @param scenarios The new scenarios.
	 */
	public void setScenarios(String scenarios) {
		settings.getConfig().set("scenarios", scenarios);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
	}

	public String getScenarios() {
		return settings.getConfig().getString("scenarios", "games scheduled");
	}
	
	/**
	 * Set the host of the game.
	 * 
	 * @param host The new host.
	 */
	public void setHost(String host) {
		settings.getConfig().set("host", host);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
	}

	/**
	 * Get the host of the game.
	 * 
	 * @return The host.
	 */
	public String getHost() {
		return settings.getConfig().getString("host", "None");
	}

	/**
	 * Set the max players that will be able to join.
	 * 
	 * @param maxplayers The max player limit.
	 */
	public void setMaxPlayers(int maxplayers) {
		settings.getConfig().set("maxplayers", maxplayers);
		settings.saveConfig();
	}
	
	/**
	 * Get the max players that can join the server.
	 * 
	 * @return The max player limit.
	 */
	public int getMaxPlayers() {
		return settings.getConfig().getInt("maxplayers", 150);
	}

	/**
	 * Set the matchpost for the game.
	 * 
	 * @param matchpost The new matchpost.
	 */
	public void setMatchPost(String matchpost) {
		settings.getConfig().set("matchpost", matchpost);
		settings.saveConfig();
	}

	/**
	 * Get the matchpost for the game.
	 * 
	 * @return The game's matchpost.
	 */
	public String getMatchPost() {
		return settings.getConfig().getString("matchpost", "No_Post_Set");
	}
	
	/**
	 * Set the world to be used for the game.
	 * <p> 
	 * This will automaticly use all the same worlds 
	 * that has the world name with _end or _nether at the end.
	 * 
	 * @param name The new world name.
	 */
	public void setWorld(String name) {
		settings.getConfig().set("world", name);
		settings.saveConfig();
	}

	/**
	 * Get the world to be used for the game.
	 * 
	 * @return The game world
	 */
	public World getWorld() {
		return Bukkit.getWorld(settings.getConfig().getString("world", "leon"));
	}

	/**
	 * Set the time of the pvp for the game.
	 * 
	 * @param meetup The time in minutes.
	 */
	public void setPvP(int pvp) {
		settings.getConfig().set("time.pvp", pvp);
		settings.saveConfig();
	}

	/**
	 * Get the time when pvp will be enabled after the start.
	 * 
	 * @return The time in minutes.
	 */
	public int getPvP() {
		return settings.getConfig().getInt("time.pvp", 15);
	}

	/**
	 * Set the time of the meetup of the game.
	 * 
	 * @param meetup The time in minutes.
	 */
	public void setMeetup(int meetup) {
		settings.getConfig().set("time.meetup", meetup);
		settings.saveConfig();
	}

	/**
	 * Get the time when meetup will occur after the start.
	 * 
	 * @return The time in minutes.
	 */
	public int getMeetup() {
		return settings.getConfig().getInt("time.meetup", 90);
	}

	// ############################ MISC ############################
	// TODO: <-- just for the marker.

	/**
	 * Enable or disable team management.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setTeamManagement(boolean enable) {
		settings.getConfig().set("misc.team", enable);
		settings.saveConfig();
	}
	
	/**
	 * Get if team management is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean teamManagement() {
		return settings.getConfig().getBoolean("misc.team", false);
	}

	/**
	 * Enable or disable the pregame board.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setPregameBoard(boolean enable) {
		settings.getConfig().set("misc.board.pregame", enable);
		settings.saveConfig();
	}

	/**
	 * Get if the pregame board is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean pregameBoard() {
		return settings.getConfig().getBoolean("misc.board.pregame", false);
	}

	/**
	 * Enable or disable the arena board.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setArenaBoard(boolean enable) {
		settings.getConfig().set("misc.board.arena", enable);
		settings.saveConfig();
	}

	/**
	 * Get if the arena board is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean arenaBoard() {
		return settings.getData().getBoolean("misc.board.arena", false);
	}

	/**
	 * Enable or disable recordedround mode.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setRecordedRound(boolean enable) {
		settings.getConfig().set("recordedround.enabled", enable);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
	}

	/**
	 * Get if recordedround mode is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isRecordedRound() {
		return settings.getConfig().getBoolean("recordedround.enabled", false);
	}
	
	/**
	 * Set the name of the recorded round.
	 * <p>
	 * Note: If recordedround mode is disabled, this will have no effect.
	 * 
	 * @param name The new name of the RR.
	 */
	public void setRRName(String name) {
		settings.getConfig().set("recordedround.name", name);
		settings.saveConfig();
	}

	/**
	 * Get the recorded round name.
	 * @return
	 */
	public String getRRName() {
		return settings.getConfig().getString("recordedround.name", "ANAMEHERE");
	}

	/**
	 * Enable or disable stats.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setStats(boolean enable) {
		settings.getConfig().set("misc.stats", enable);
		settings.saveConfig();
	}

	/**
	 * Get if stats are enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean stats() {
		return settings.getConfig().getBoolean("misc.stats", true);
	}

	/**
	 * Mute or unmute the chat
	 * 
	 * @param mute True to mute, false to unmute.
	 */
	public void setMuted(boolean mute) {
		settings.getData().set("muted", mute);
		settings.saveData();
	}

	/**
	 * Check if the chat is muted.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isMuted() {
		return settings.getData().getBoolean("muted", false);
	}

	// ############################ RATES ############################
	// TODO: <-- just for the marker.

	public void setAppleRates(int shearRate) {
		settings.getConfig().set("rates.apple.rate", shearRate);
		settings.saveConfig();
	}
	
	public int getAppleRates() {
		return settings.getConfig().getInt("rates.apple.rate", 1);
	}
	
	public void setFlintRates(int flintRate) {
		settings.getConfig().set("rates.flint.rate", flintRate);
		settings.saveConfig();
	}
	
	public int getFlintRates() {
		return settings.getConfig().getInt("rates.flint.rate", 35);
	}

	/**
	 * Enable or disable team management.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setShears(boolean enable) {
		settings.getConfig().set("rates.shears.enabled", enable);
		settings.saveConfig();
	}
	
	/**
	 * Get if team management is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean shears() {
		return settings.getConfig().getBoolean("rates.shears.enabled", true);
	}
	
	public void setShearRates(int shearRate) {
		settings.getConfig().set("rates.shears.rate", shearRate);
		settings.saveConfig();
	}
	
	public int getShearRates() {
		if (!shears()) {
			return 0;
		}
		
		return settings.getConfig().getInt("rates.shears.rate", 5);
	}
	
	// ############################ FEATURES ############################
	// TODO: <-- just for the marker.

	public boolean antiStripmine() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setBorderShrink(BorderShrink border) {
		settings.getConfig().set("feature.border.shrinkAt", border.name());
		settings.saveConfig();
	}
	
	public BorderShrink getBorderShrink() {
		return BorderShrink.valueOf(settings.getConfig().getString("feature.border.shrinkAt", BorderShrink.MEETUP.name()));
	}

	public boolean absorption() {
		return settings.getConfig().getBoolean("feature.absorption.enabled", false);
	}
	
	public void setAbsorption(boolean enable) {
		settings.getConfig().set("feature.absorption.enabled", enable);
		settings.saveConfig();
	}

	public boolean goldenHeads() {
		return settings.getConfig().getBoolean("feature.goldenheads.enabled", true);
	}
	
	public void setGoldenHeads(boolean enable) {
		settings.getConfig().set("feature.goldenheads.enabled", enable);
		settings.saveConfig();
	}
	
	public int goldenHeadsHeal() {
		return settings.getConfig().getInt("feature.goldenheads.heal", 4);
	}
	
	public void setGoldenHeadsHeal(int heal) {
		settings.getConfig().set("feature.goldenheads.heal", heal);
		settings.saveConfig();
	}

	public boolean pearlDamage() {
		return settings.getConfig().getBoolean("feature.pearldamage.enabled", false);
	}
	
	public void setPearlDamage(boolean enable) {
		settings.getConfig().set("feature.pearldamage.enabled", enable);
		settings.saveConfig();
	}

	public boolean notchApples() {
		return settings.getConfig().getBoolean("feature.notchapples.enabled", true);
	}
	
	public void setNotchApples(boolean enable) {
		settings.getConfig().set("feature.notchapples.enabled", enable);
		settings.saveConfig();
	}

	public boolean deathLightning() {
		return settings.getConfig().getBoolean("feature.deathlightning.enabled", true);
	}
	
	public void setDeathLightning(boolean enable) {
		settings.getConfig().set("feature.deathlightning.enabled", enable);
		settings.saveConfig();
	}

	public boolean nether() {
		return settings.getConfig().getBoolean("feature.nether.enabled", false);
	}
	
	public void setNether(boolean enable) {
		settings.getConfig().set("feature.nether.enabled", enable);
		settings.saveConfig();
	}

	public boolean theEnd() {
		return settings.getConfig().getBoolean("feature.theend.enabled", false);
	}
	
	public void setTheEnd(boolean enable) {
		settings.getConfig().set("feature.theend.enabled", enable);
		settings.saveConfig();
	}

	public boolean ghastDropGold() {
		return settings.getConfig().getBoolean("feature.ghastdrops.enabled", true);
	}
	
	public void setGhastDropGold(boolean enable) {
		settings.getConfig().set("feature.ghastdrops.enabled", enable);
		settings.saveConfig();
	}

	public boolean nerfedStrength() {
		return settings.getConfig().getBoolean("feature.nerfedStrength.enabled", true);
	}
	
	public void setNerfedStrength(boolean enable) {
		settings.getConfig().set("feature.nerfedStrength.enabled", enable);
		settings.saveConfig();
	}

	public boolean tabShowsHealthColor() {
		return settings.getConfig().getBoolean("feature.tabShowsHealthColor.enabled", false);
	}
	
	public void setTabShowsHealthColor(boolean enable) {
		settings.getConfig().set("feature.tabShowsHealthColor.enabled", enable);
		settings.saveConfig();
	}

	public boolean goldenMelonNeedsIngots() {
		return settings.getConfig().getBoolean("feature.goldenMelonNeedsIngots.enabled", true);
	}
	
	public void setGoldenMelonNeedsIngots(boolean enable) {
		settings.getConfig().set("feature.goldenMelonNeedsIngots.enabled", enable);
		settings.saveConfig();
	}

	public boolean tier2() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean splash() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean horses() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean horseHealing() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean horseArmor() {
		// TODO Auto-generated method stub
		return false;
	}
}