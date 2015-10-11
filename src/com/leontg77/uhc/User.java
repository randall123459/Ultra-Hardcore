package com.leontg77.uhc;
 
import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.leontg77.uhc.utils.PermsUtils;

/**
 * User class.
 * <p>
 * This class contains methods for setting and getting stats, ranks, mute status and getting/saving/reloading the data file etc.
 * 
 * @author LeonTG77
 */
public class User {
	private Player player;
	private String uuid;
	
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
	public static User get(Player player) {
		return new User(player, player.getUniqueId().toString());
	}

	/**
	 * Gets the data of the given OFFLINE player.
	 * <p>
	 * If the data doesn't exist it will create a new data file and threat the player as a newly joined one.
	 * 
	 * @param offline the offline player.
	 * @return the data instance for the player.
	 */
	public static User get(OfflinePlayer offline) {
		return new User(offline.getPlayer(), offline.getUniqueId().toString());
	}
	
	/**
	 * Constuctor for player data.
	 * <p>
	 * This will set up the data for the player and create missing data.
	 * 
	 * @param uuid the player.
	 * @param uuid the uuid of the player.
	 */
	private User(Player player, String uuid) {
        if (!plugin.getDataFolder().exists()) {
        	plugin.getDataFolder().mkdir();
        }
        
        boolean creating = false;
        
        File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
        
        if (!folder.exists()) {
        	folder.mkdir(); 
        }
        
        file = new File(folder, uuid + ".yml");
        
        if (!file.exists()) {
        	try {
        		file.createNewFile();
        		creating = true;
        	} catch (Exception e) {
        		plugin.getLogger().severe(ChatColor.RED + "Could not create " + uuid + ".yml!");
        	}
        }
               
        config = YamlConfiguration.loadConfiguration(file);
        
        this.player = player;
        this.uuid = uuid;
        
        if (creating) {
        	if (player != null) {
            	config.set("username", player.getName());
            	config.set("uuid", player.getUniqueId().toString());
            	config.set("ip", player.getAddress().getAddress().getHostAddress());
        	}

        	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        	
        	config.set("firstjoined", new Date().getTime());
        	config.set("lastlogin", new Date().getTime());
        	config.set("lastlogoff", -1);
        	config.set("rank", Rank.USER.name());
        	
        	config.set("new.unique", folder.listFiles());
        	config.set("new.hasbeenwelcomed", false);
        	
			config.set("muted.status", false);
			config.set("muted.reason", "NOT_MUTED");
			config.set("muted.time", -1);
			
			for (Stat stats : Stat.values()) {
	        	config.set("stats." + stats.name().toLowerCase(), 0);
			}
			
        	saveFile();
        }
	}
	
	/**
	 * Check if the user hasn't been welcomed to the server.
	 * 
	 * @return True if he hasn't, false otherwise.
	 */
	public boolean hasntBeenWelcomed() {
		boolean joined = config.getBoolean("new.hasbeenwelcomed", true);
		
		if (joined) {
            return false;
        } else {
        	config.set("new.hasbeenwelcomed", true);
        	saveFile();
			return true;
        }
	}
	
	/**
	 * Get the player class for the user.
	 * 
	 * @return The player class.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Get the uuid for the user.
	 * 
	 * @return The uuid.
	 */
	public String getUUID() {
		return uuid;
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
    		plugin.getLogger().severe(ChatColor.RED + "Could not save " + file.getName() + "!");
		}
	}
	
	/**
	 * Reload the config file.
	 */
	public void reloadFile() {
        config = YamlConfiguration.loadConfiguration(file);
	}
	
	/**
	 * Set the rank for the player.
	 * 
	 * @param rank The new rank.
	 */
	public void setRank(Rank rank) {
		config.set("rank", rank.name());
		saveFile();
		
		if (player != null) {
			PermsUtils.removePermissions(player);
			PermsUtils.addPermissions(player);
		}
	}

	/**
	 * Get the rank of the player.
	 * 
	 * @return the rank.
	 */
	public Rank getRank() {
		return Rank.valueOf(config.getString("rank", "USER"));
	}
	
	/**
	 * Mute the user.
	 * 
	 * @param reason The reason of the mute.
	 * @param unmute The date of unmute, null if permanent.
	 */
	public void mute(String reason, Date unmute) {
		config.set("muted.status", true);
		config.set("muted.reason", reason);
		
		if (unmute == null) {
			config.set("muted.time", -1);
		} else {
			config.set("muted.time", unmute.getTime());
		}
		
		saveFile();
	}
	
	/**
	 * Unmute the user.
	 */
	public void unmute() {
		config.set("muted.status", false);
		config.set("muted.reason", "NOT_MUTED");
		config.set("muted.time", -1);
		saveFile();
	}
	
	/**
	 * Check if the player is muted.
	 * 
	 * @return <code>true</code> if the player is muted, <code>false</code> otherwise.
	 */
	public boolean isMuted() {
		return config.getBoolean("muted.status", false);
	}
	
	/**
	 * Get the reason the player is muted.
	 * 
	 * @return The reason of the mute, null if not muted.
	 */
	public String getMutedReason() {
		if (!isMuted()) {
			return "NOT_MUTED";
		}
		
		return config.getString("muted.reason", "NOT_MUTED");
	}
	
	/**
	 * Get the time in milliseconds for the unmute.
	 * 
	 * @return The unmute time.
	 */
	public long getUnmuteTime() {
		if (!isMuted()) {
			return -1;
		}
	
		return config.getLong("muted.time", -1);
	}
	
	/**
	 * Increase the given stat by 1.
	 * 
	 * @param stat the stat increasing.
	 */
	public void increaseStat(Stat stat) {
		Game game = Game.getInstance();
		
		if (game.isRecordedRound()) {
			return;
		}
		
		String statName = stat.name().toLowerCase();
		int current = config.getInt("stats." + statName, 0);
		
		config.set("stats." + statName, current + 1);
		saveFile();
	}
	
	/**
	 * Get the amount from the given stat.
	 * 
	 * @param stat the stat getting.
	 * @return The amount in a int form.
	 */
	public int getStat(Stat stat) {
		return config.getInt("stats." + stat.name().toLowerCase(), 0);
	}
	
	/**
	 * The ranking enum class.
	 * 
	 * @author LeonTG77
	 */
	public enum Rank {
		USER, VIP, STAFF, TRIAL, HOST;
	}
	
	/**
	 * The stats enum class.
	 * 
	 * @author LeonTG77
	 */
	public enum Stat {
		DEATHS, KILLS, WINS, GAMESPLAYED, ARENAKILLS, ARENADEATHS, GOLDENAPPLESEATEN, GOLDENHEADSEATEN, HORSESTAMED, NETHER, END, DIAMONDS, GOLD, HOSTILEMOBKILLS, ANIMALKILLS, KILLSTREAK, DAMAGETAKEN;
	}
}