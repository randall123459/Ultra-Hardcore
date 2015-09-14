package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * InfinieEnchanter scenario class
 * 
 * @author LeonTG77
 */
public class InfiniteEnchanter extends Scenario {
	private boolean enabled = false;
	
	public InfiniteEnchanter() {
		super("InfiniteEnchanter", "Everyone is given 128 enchantment tables, anvils and bookshelves.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.getInventory().addItem(new ItemStack (Material.ENCHANTMENT_TABLE, 128));
				online.getInventory().addItem(new ItemStack (Material.BOOKSHELF, 128));
				online.getInventory().addItem(new ItemStack (Material.ANVIL, 128));
				online.setLevel(23000);
			}
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.getInventory().remove(Material.ENCHANTMENT_TABLE);
				online.getInventory().remove(Material.BOOKSHELF);
				online.getInventory().remove(Material.ANVIL);
				online.setLevel(0);
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}