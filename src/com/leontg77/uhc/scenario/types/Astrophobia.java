package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class Astrophobia extends Scenario implements Listener {
	private boolean enabled = false;

	public Astrophobia() {
		super("Astrophobia", "The sun is gone, and the world is in eternal night. Deadly meteors impact the surface at random, leaving craters and flames, and sometimes ores. Aliens arrive from the sky wearing protective armor and shooting powerful weapons. Their tracking bombs (charged creepers) are fired onto the world to target any players that come near them. After defeating one, players can spawn a charged creeper of their own.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}