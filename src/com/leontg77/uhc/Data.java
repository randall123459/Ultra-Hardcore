package com.leontg77.uhc;
 
import static com.leontg77.uhc.Main.plugin;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Player data class.
 * <p>
 * This class contains methods for setting and getting stats, ranks, mute status and getting/saving/reloading the data file.
 * 
 * @author LeonTG77
 */
public class Data {
	private Player player;
	private boolean creating = false;
	
	private FileConfiguration config;
	private File file;
	
	/**
	 * Gets the data of the given player.
	 * <p>
	 * If the data doesn't exist it will create a new data file and threat the player as a newly joined one.
	 * 
	 * @param player the player.
	 * @return the data instance for the player.
	 */
	public static Data getFor(Player player) {
		return new Data(player, player.getUniqueId().toString());
	}

	/**
	 * Gets the data of the given OFFLINE player.
	 * <p>
	 * If the data doesn't exist it will create a new data file and threat the player as a newly joined one.
	 * 
	 * @param offline the offline player.
	 * @return the data instance for the player.
	 */
	public static Data getFor(OfflinePlayer offline) {
		return new Data(offline.getPlayer(), offline.getUniqueId().toString());
	}
	
	/**
	 * Constuctor for player data.
	 * <p>
	 * This will set up the data for the player and create missing data.
	 * 
	 * @param uuid the player.
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
        	} catch (Exception e) {
        		Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create " + uuid + ".yml!");
        	}
        }
               
        config = YamlConfiguration.loadConfiguration(file);
        this.player = player;
        
        if (creating) {
        	if (player != null) {
            	config.set("username", player.getName());
        	}
        	
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
	 * 
	 * @return <code>True</code> if it's the first time, <code>false</code> otherwise
	 */
	public boolean isNew() {
		return creating;
	}
	
	/**
	 * Get the player class for the data owner.
	 * 
	 * @return The player class.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Get the configuration file for the player.
	 * 
	 * @return The configuration file.
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
		} catch (Exception e) {
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
	 * 
	 * @return the rank.
	 */
	public Rank getRank() {
		return Rank.valueOf(config.getString("rank"));
	}
	
	/**
	 * Sets if the player is muted or not.
	 * 
	 * @param mute <code>true</code> to mute the player, <code>false</code> to unmute.
	 */
	public void setMuted(boolean mute) {
		config.set("muted", mute);
		saveFile();
	}
	
	/**
	 * Check if the player is muted.
	 * 
	 * @return <code>true</code> if the player is muted, <code>false</code> otherwise.
	 */
	public boolean isMuted() {
		return config.getBoolean("muted");
	}
	
	/**
	 * Adds an amount from the stats.
	 * 
	 * @param stat the stat name.
	 */
	public void increaseStat(String stat) {
		config.set("stats." + stat, config.getInt("stats." + stat) + 1);
		saveFile();
	}
	
	/**
	 * Removes an amount from the stats.
	 * 
	 * @param stat the stat name.
	 */
	public void decreaseStat(String stat) {
		config.set("stats." + stat, config.getInt("stats." + stat) - 1);
		saveFile();
	}
	
	/**
	 * Gets the amount from a stat.
	 * 
	 * @param stat the stat name.
	 * @return The amount in an integer.
	 */
	public int getStat(String stat) {
		return config.getInt("stats." + stat);
	}
	
	/**
	 * The ranking enum class.
	 * 
	 * @author LeonTG77
	 */
	public enum Rank {
		USER, VIP, STAFF, TRIAL, HOST;
	}
}