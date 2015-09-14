package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;

/**
 * BetaZombies scenario class
 * 
 * @author LeonTG77
 */
public class BetaZombies extends Scenario implements Listener {
	private boolean enabled = false;
	
	public BetaZombies() {
		super("BetaZombies", "Zombies drop feathers.");
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Zombie) {
			for (ItemStack drops : event.getDrops()) {
				if (drops.getType() == Material.ROTTEN_FLESH) {
					drops.setType(Material.FEATHER);
				}
			}
		}
	}
}