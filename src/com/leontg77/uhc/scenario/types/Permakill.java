package com.leontg77.uhc.scenario.types;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.scenario.Scenario;

/**
 * Permakill scenario class
 * 
 * @author LeonTG77
 */
public class Permakill extends Scenario implements Listener {
	private Game game = Game.getInstance();
	private boolean enabled = false;

	public Permakill() {
		super("Permakill", "Everytime a player dies it toggles between perma day and perma night");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			game.getWorld().setGameRuleValue("doDaylightCycle", "false");
			game.getWorld().setTime(6000);
		} else {
			game.getWorld().setGameRuleValue("doDaylightCycle", "true");
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (game.getWorld().getTime() == 6000) {
			game.getWorld().setTime(18000);
		} else {
			game.getWorld().setTime(6000);
		}
	}
}