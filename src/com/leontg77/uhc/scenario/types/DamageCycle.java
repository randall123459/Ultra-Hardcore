package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

/**
 * DamageCycle scenario class
 * 
 * @author LeonTG77
 */
public class DamageCycle extends Scenario implements Listener {
	private boolean enabled = false;
	private BukkitRunnable task;

	public DamageCycle() {
		super("DamageCycle", "Every 5 min a type of damage is selected to randomly OHKO you.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			this.task = new BukkitRunnable() {
				public void run() {
					
				}
			};
			
			task.runTaskTimer(Main.plugin, 6000, 6000);
		} else {
			task.cancel();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}