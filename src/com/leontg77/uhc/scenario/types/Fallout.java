package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

public class Fallout extends Scenario {
	private boolean enabled = false;
	private BukkitRunnable task;
	
	public Fallout() {
		super("Fallout", "After a certain amount of time, any player above y: 60 will begin to take half a heart of damage every 30 seconds.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			this.task = new BukkitRunnable() {
				public void run() {
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (!online.getWorld().getName().equals("lobby")) {
							if (online.getLocation().getBlockY() > 60) {
								online.damage(1.0);
							}
						}
					}
				}
			};
			
			task.runTaskTimer(Main.plugin, 600, 600);
		} else {
			task.cancel();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}