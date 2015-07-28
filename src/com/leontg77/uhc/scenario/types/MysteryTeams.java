package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class MysteryTeams extends Scenario implements Listener {
	private boolean enabled = false;

	public MysteryTeams() {
		super("MysteryTeams", "Teams are unknown until meeting and comparing wool.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}