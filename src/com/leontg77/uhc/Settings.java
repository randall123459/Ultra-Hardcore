package com.leontg77.uhc;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
 
/**
 * Settings class to manage all the config files.
 * <p>
 * This class contains methods for saving, getting and reloading the config, hof and data.yml!
 * 
 * @author LeonTG77
 */
public class Settings {
	private static Settings instance = new Settings();
	
	private FileConfiguration config;
	private File cfile;
	
	private FileConfiguration data;
	private File dfile;
	
	private FileConfiguration hof;
	private File hfile;

	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Settings getInstance() {
		return instance;
	}
       
	/**
	 * Sets the settings manager up and creates missing files.
	 */
	public void setup() {      
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
        
		cfile = new File(plugin.getDataFolder(), "config.yml");
	        
		if (!cfile.exists()) {
			try {
				cfile.createNewFile();
			} catch (Exception e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create config.yml!");
			}
		}
	        
		config = YamlConfiguration.loadConfiguration(cfile);
	    
		dfile = new File(plugin.getDataFolder(), "data.yml");
		    
		if (!dfile.exists()) {
			try {
				dfile.createNewFile();
			} catch (Exception e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
			}
		}
		    
		data = YamlConfiguration.loadConfiguration(dfile);
               
		hfile = new File(plugin.getDataFolder(), "hof.yml");
               
		if (!hfile.exists()) {
			try {
				hfile.createNewFile();
			} catch (Exception e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create hof.yml!");
			}
		}
               
		hof = YamlConfiguration.loadConfiguration(hfile);
		Bukkit.getLogger().info("[UHC] Configs has been setup.");
	}
    
	/**
	 * Gets the config file.
	 * 
	 * @return The file.
	 */
	public FileConfiguration getConfig() {
		return config;
	}
    
	/**
	 * Saves the data config.
	 */
	public void saveConfig() {
		try {
			config.save(cfile);
		} catch (Exception e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
		}
	}
    
	/**
	 * Reloads the config file.
	 */
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
		Bukkit.getLogger().info("§a[UHC] Config has been reloaded.");
	}
    
	/**
	 * Gets the data file.
	 * 
	 * @return The file.
	 */
	public FileConfiguration getData() {
		return data;
	}
    
	/**
	 * Saves the data config.
	 */
	public void saveData() {
		try {
			data.save(dfile);
		} catch (Exception e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
		}
	}
    
	/**
	 * Reloads the data file.
	 */
	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
		Bukkit.getLogger().info("§a[UHC] Data has been reloaded.");
	}
    
	/**
	 * Gets the hof file.
	 * 
	 * @return The file.
	 */
	public FileConfiguration getHOF() {
		return hof;
	}
    
	/**
	 * Saves the hof config.
	 */
	public void saveHOF() {
		try {
			hof.save(hfile);
		} catch (Exception e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save hof.yml!");
		}
	}
    
	/**
	 * Reloads the hof file.
	 */
	public void reloadHOF() {
		hof = YamlConfiguration.loadConfiguration(hfile);
		Bukkit.getLogger().info("§a[UHC] HOF has been reloaded.");
	}
}