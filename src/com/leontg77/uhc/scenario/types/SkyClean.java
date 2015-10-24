package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

/**
 * SkyClean scenario class
 * 
 * @author LeonTG77
 */
public class SkyClean extends Scenario implements Listener {
	private boolean enabled = false;
	
	public SkyClean() {
		super("SkyClean", "Sand drops glass and snow blocks drop snowblocks rather than snowballs.");
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
		
		if (block.getType() == Material.SAND) {
			Main.toReplace.put(Material.SAND, new ItemStack(Material.GLASS));
			return;
		}
		
		if (block.getType() == Material.SNOW_BLOCK) {
			Main.toReplace.put(Material.SNOW_BALL, new ItemStack(Material.SNOW_BLOCK));
		}
	}
}