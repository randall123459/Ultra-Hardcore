package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class Assassins extends Scenario implements Listener {
	private boolean enabled = false;

	public Assassins() {
		super("Assassins", "Each player has a target that they must kill. Killing anyone that is not your target or assassin will result in no items dropping. When your target dies, you get their target.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}