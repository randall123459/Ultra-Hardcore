package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

public class BlockRush extends Scenario implements Listener {
	private boolean enabled = false;

	public BlockRush() {
		super("BlockRush", "Mining a specific block type for the first time gives you a reward, usually 1 gold ingot.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}