package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class AssaultAndBattery extends Scenario implements Listener {
	private boolean enabled = false;

	public AssaultAndBattery() {
		super("AssaultAndBattery", "To2 Where one person can only do meelee damage to players, while the other one can only do ranged attacks. If a teammate dies, you can do both meelee and ranged attacks.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}