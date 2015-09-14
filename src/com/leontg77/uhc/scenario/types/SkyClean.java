package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.BlockUtils;

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
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 12);
			block.setType(Material.AIR);
			block.getState().update();
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GLASS));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
		
		if (block.getType() == Material.SNOW_BLOCK) {
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 80);
			block.setType(Material.AIR);
			block.getState().update();
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.SNOW_BLOCK));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
	}
}