package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.uhc.Settings;
import com.leontg77.uhc.scenario.Scenario;

/**
 * Permakill scenario class
 * 
 * @author LeonTG77
 */
public class Permakill extends Scenario implements Listener {
	private boolean enabled = false;

	public Permakill() {
		super("Permakill", "Everytime a player dies it toggles between perma day and perma night");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setGameRuleValue("doDaylightCycle", "false");
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setTime(6000);
		} else {
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setGameRuleValue("doDaylightCycle", "true");
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).getTime() == 6000) {
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setTime(18000);
		} else {
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setTime(6000);
		}
	}
}