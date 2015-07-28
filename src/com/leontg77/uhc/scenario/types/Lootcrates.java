package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class Lootcrates extends Scenario implements Listener {
	private boolean enabled = false;

	public Lootcrates() {
		super("Lootcrates", "Every 10 minutes, players will be given a \"loot crate\" filled with goodies. There are two tiers, an Ender Chest being tier 2 and a normal chest tier 1.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}