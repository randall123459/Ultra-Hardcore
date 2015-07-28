package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class Inventors extends Scenario implements Listener {
	private boolean enabled = false;

	public Inventors() {
		super("Inventors", "The first person to craft any item will be broadcasted in chat.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}