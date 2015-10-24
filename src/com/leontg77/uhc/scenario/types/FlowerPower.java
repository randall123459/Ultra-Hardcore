package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

/**
 * FlowerPower scenario class
 * 
 * @author LeonTG77
 */
public class FlowerPower extends Scenario implements Listener {
	private boolean enabled = false;
	
	public FlowerPower() {
		super("FlowerPower", "If you break flowers they will drop an random item.");
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
		
		if (block.getType() != Material.RED_ROSE && block.getType() != Material.YELLOW_FLOWER && block.getType() != Material.DOUBLE_PLANT) {
			return;
		}
		
		if (block.getType() == Material.DOUBLE_PLANT) {
			short damage = block.getState().getData().toItemStack().getDurability();
			
			if (damage == 2 || damage == 3) {
				return;
			}
		}
		
		Main.toReplace.put(block.getType(), randomItem());
	}

	/**
	 * Get an random item out of all minecraft items.
	 * 
	 * @return An random item.
	 */
	private ItemStack randomItem() {
		Random r = new Random();
		
		Material m = Material.values()[r.nextInt(Material.values().length)];
		int a = r.nextInt(m.getMaxStackSize());
		
		return new ItemStack(m, a);
	}
}