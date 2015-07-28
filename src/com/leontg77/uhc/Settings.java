package com.leontg77.uhc;
 
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
 
/**
 * A class to manage all the config files
 * @author LeonTG77
 */
public class Settings {
	private static Settings instance = new Settings(); 
	private Settings() {}   
	
	private FileConfiguration data;
	private File dfile;

	/**
	 * Gets the instance of the class.
	 * @return the instance.
	 */
	public static Settings getInstance() {
		return instance;
	}
       
	/**
	 * Sets the settings manager up and creates missing files.
	 * @param p the main class.
	 */
	public void setup(Plugin p) {      
		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
               
		dfile = new File(p.getDataFolder(), "data.yml");
               
		if (!dfile.exists()) {
			Bukkit.getLogger().info("§a[UHC] Could not find config file, creating...");
			try {
				dfile.createNewFile();
				Bukkit.getLogger().info("§a[UHC] File created.");
			} catch (IOException ex) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
			}
		}
               
		data = YamlConfiguration.loadConfiguration(dfile);
		Bukkit.getLogger().info("§a[UHC] Configs has been setup.");
	}
    
	/**
	 * Gets the data config.
	 * @return the config.
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
		} catch (IOException ex) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
		}
	}
    
	/**
	 * Reloads the data file.
	 */
	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
		Bukkit.getLogger().info("§a[UHC] Config has been reloaded.");
	}
}