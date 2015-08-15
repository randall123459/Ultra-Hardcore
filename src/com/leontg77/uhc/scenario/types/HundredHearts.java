package com.leontg77.uhc.scenario.types;

import org.bukkit.entity.Player;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class HundredHearts extends Scenario {
	private boolean enabled = false;

	public HundredHearts() {
		super("HundredHearts", "Everyone has 100 hearts.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setMaxHealth(200);
			}
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setMaxHealth(20);
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}