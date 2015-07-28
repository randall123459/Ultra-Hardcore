package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;

public class GoldRush extends Scenario implements Listener {
	private boolean enabled = false;

	public GoldRush() {
		super("GoldRush", "You cannot craft leather or iron armor.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		ItemStack item = event.getRecipe().getResult();
		
		if (item.getType() == Material.IRON_HELMET) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.IRON_CHESTPLATE) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.IRON_LEGGINGS) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.IRON_BOOTS) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.LEATHER_HELMET) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.LEATHER_CHESTPLATE) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.LEATHER_LEGGINGS) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.LEATHER_BOOTS) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
}