package com.leontg77.uhc;
 
import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.leontg77.uhc.util.PlayerUtils;

/**
 * Player data class.
 * @author LeonTG77
 */
public class Data {
	private FileConfiguration config;
	private File file;
	
	private boolean creating = false;
	private Player player;
	
	/**
	 * Gets the data of a player.
	 * @param player the player.
	 * @return the data class.
	 */
	public static Data getData(Player player) {
		return new Data(player, player.getUniqueId().toString());
	}

	/**
	 * Gets the data of a player.
	 * @param player the player.
	 * @return the data class.
	 */
	public static Data getData(OfflinePlayer player) {
		return new Data(player.getPlayer(), player.getUniqueId().toString());
	}
	
	/**
	 * Constuctor for playerdata.
	 * @param uuid the uuid of the player.
	 */
	private Data(Player player, String uuid) {
        if (!plugin.getDataFolder().exists()) {
        	plugin.getDataFolder().mkdir();
        }
        
        File f = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
        
        if (!f.exists()) {
        	f.mkdir(); 
        }
        
        file = new File(f, uuid + ".yml");
        
        if (!file.exists()) {
        	try {
        		file.createNewFile();
        		creating = true;
        	} catch (IOException e) {
        		Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create " + uuid + ".yml!");
        	}
        }
               
        config = YamlConfiguration.loadConfiguration(file);
        this.player = player;
        
        if (creating) {
        	config.set("firstjoined", System.currentTimeMillis());
        	config.set("rank", Rank.USER.name());
        	config.set("muted", false);
        	config.set("stats.gamesplayed", 0);
        	config.set("stats.wins", 0);
        	config.set("stats.kills", 0);
        	config.set("stats.deaths", 0);
        	config.set("stats.arenakills", 0);
        	config.set("stats.arenadeaths", 0);
        	saveFile();
        }
	}
	
	/**
	 * Check if the user just joined for the first time.
	 * @return True if it's the first time, false otherwise
	 */
	public boolean isNew() {
		return creating;
	}
	
	/**
	 * Get the player class for the data owner.
	 * @return the player class.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Get the configuration file for the player.
	 * @return the configuratiob file.
	 */
	public FileConfiguration getFile() {
		return config;
	}
	
	/**
	 * Save the config file.
	 */
	public void saveFile() {
		try {
			config.save(file);
		} catch (IOException e) {
    		Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save " + file.getName() + "!");
		}
	}
	
	/**
	 * Reload the config file.
	 */
	public void reloadFile() {
        config = YamlConfiguration.loadConfiguration(file);
	}
	
	public void setRank(Rank rank) {
		config.set("rank", rank.name());
		saveFile();
		
		PlayerUtils.handlePermissions(player);
	}

	/**
	 * Get the players rank.
	 * @return the rank.
	 */
	public Rank getRank() {
		return Rank.valueOf(config.getString("rank"));
	}
	
	/**
	 * Sets if the player is muted or not
	 * @param mute mute the player.
	 */
	public void setMuted(boolean mute) {
		config.set("muted", mute);
		saveFile();
	}
	
	public boolean isMuted() {
		return config.getBoolean("muted");
	}
	
	/**
	 * Adds an amount from the stats.
	 * @param stat stat type.
	 * @param newValue how much to add.
	 */
	public void increaseStat(String stat) {
		config.set("stats." + stat, config.getInt("stats." + stat) + 1);
		saveFile();
	}
	
	/**
	 * Removes an amount from the stats.
	 * @param stat stat type.
	 * @param newValue how much to remove.
	 */
	public void decreaseStat(String stat) {
		config.set("stats." + stat, config.getInt("stats." + stat) - 1);
		saveFile();
	}
	
	/**
	 * Gets the amount from a stat.
	 * @param stat the stat.
	 * @return the amount in a int.
	 */
	public int getStat(String stat) {
		return config.getInt("stats." + stat);
	}
	
	public enum Rank {
		USER, VIP, STAFF, HOST;
	}
}