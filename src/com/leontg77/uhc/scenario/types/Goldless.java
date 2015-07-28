package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.BlockUtils;

@SuppressWarnings("deprecation")
public class Goldless extends Scenario implements Listener {
	private boolean enabled = false;

	public Goldless() {
		super("Goldless", "You can't obtain gold");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
    	Player player = event.getPlayer();
		Block block = event.getBlock();
    	
		if (ScenarioManager.getManager().getScenario("CutClean") != null && ScenarioManager.getManager().getScenario("CutClean").isEnabled()) {
	    	if (block.getType() == Material.GOLD_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getTypeId());
				block.setType(Material.AIR);
				block.getState().update();
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
		} else {
	    	if (block.getType() == Material.GOLD_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getTypeId());
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
		}
    }
}