package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

public class NoSprint extends Scenario implements Listener {
	private boolean enabled = false;

	public NoSprint() {
		super("NoSprint", "Disables sprinting");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		final Player player = event.getPlayer();
		
		if (event.isSprinting()) {
			final int foodlevel = player.getFoodLevel();
			player.setFoodLevel(5);
			
			Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
				public void run() {
					player.setFoodLevel(foodlevel);
				}
			}, 40);
		}
	}
}