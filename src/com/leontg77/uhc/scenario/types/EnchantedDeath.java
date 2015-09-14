package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;

/**
 * EnchantedDeath scenario class
 * 
 * @author LeonTG77
 */
public class EnchantedDeath extends Scenario implements Listener {
	private boolean enabled = false;
	
	public EnchantedDeath() {
		super("EnchantedDeath", "You cannot craft an enchantment table, only way of getting it is killing a player.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		if (player.getKiller() != null) {
			event.getDrops().add(new ItemStack (Material.ENCHANTMENT_TABLE));
		}
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		ItemStack item = event.getRecipe().getResult();
		
		if (item.getType() == Material.ENCHANTMENT_TABLE) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
}