package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class GoneFishin extends Scenario implements Listener {
	private boolean enabled = false;
	
	public GoneFishin() {
		super("GoneFishin", "Everyone is given an op fishing rod and a stack of anvils, enchantment tables cannot be crafted.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setLevel(20000);
				online.getInventory().addItem(new ItemStack (Material.ANVIL, 64));
				ItemStack rod = new ItemStack (Material.FISHING_ROD);
				ItemMeta meta = rod.getItemMeta();
				meta.addEnchant(Enchantment.DURABILITY, 250, true);
				meta.addEnchant(Enchantment.LUCK, 250, true);
				meta.addEnchant(Enchantment.LURE, 3, true);
				rod.setItemMeta(meta);
				online.getInventory().addItem(rod);
			}
		}
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
		
		if (item.getType() == Material.ENCHANTMENT_TABLE) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
}