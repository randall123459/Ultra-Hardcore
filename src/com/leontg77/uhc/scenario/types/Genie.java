package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class Genie extends Scenario implements Listener {
	private boolean enabled = false;

	public Genie() {
		super("Genie", "You have three wishes throughout the whole game, but what you can wish for depends on the amount of kills you have at the time. So basically, you can't wish for something from a lower kill list if you've gotten more kills than that. Ex: If you wanted a golden apple from the 0 kill wishlist, but since you have 1 kill to your name, you can't. You can only wish for things from the 1 kill wishlist.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}