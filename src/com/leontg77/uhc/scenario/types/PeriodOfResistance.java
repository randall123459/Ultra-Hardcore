package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

/**
 * PeriodOfResistance scenario class
 * 
 * @author LeonTG77
 */
public class PeriodOfResistance extends Scenario implements Listener {
	private boolean enabled = false;

	public PeriodOfResistance() {
		super("PeriodOfResistance", "Every 5 minutes the resistance type changes, during the next 5 minutes you cannot take damage from what the type was.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}