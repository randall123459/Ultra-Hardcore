package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;

/**
 * Diamondless scenario class
 * 
 * @author LeonTG77
 */
public class Diamondless extends Scenario implements Listener {
	private boolean enabled = false;

	public Diamondless() {
		super("Diamondless", "You can't obtain diamonds");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
	    	
		if (block.getType() != Material.DIAMOND_ORE) {
			return;
		}
    	
		boolean cutclean = ScenarioManager.getInstance().getScenario("CutClean").isEnabled();
		ItemStack replace = new ItemStack(cutclean ? Material.IRON_INGOT : Material.IRON_ORE);
		
		Main.toReplace.put(Material.DIAMOND, replace);
    }
}