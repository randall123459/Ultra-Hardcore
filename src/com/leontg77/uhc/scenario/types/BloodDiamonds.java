package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.uhc.scenario.Scenario;

public class BloodDiamonds extends Scenario implements Listener {
	private boolean enabled = false;
	
	public BloodDiamonds() {
		super("BloodDiamonds", "Every time you mine a diamond you take half a heart.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
    	Player player = event.getPlayer();
		Block block = event.getBlock();
    	
    	if (block.getType() == Material.DIAMOND_ORE) {
    		player.damage(1.0);
    	}
    }
}