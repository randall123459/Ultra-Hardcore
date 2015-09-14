package com.leontg77.uhc.scenario.types;

import org.bukkit.entity.Player;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * PotentialHearts scenario class
 * 
 * @author LeonTG77
 */
public class PotentialHearts extends Scenario {
	private boolean enabled = false;

	public PotentialHearts() {
		super("PotentialHearts", "Everyone starts off with 10 hearts and 10 unhealed potential hearts you need to heal by yourself.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setMaxHealth(40);
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