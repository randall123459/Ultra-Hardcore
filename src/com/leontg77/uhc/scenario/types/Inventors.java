package com.leontg77.uhc.scenario.types;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Inventors scenario class
 * 
 * @author LeonTG77
 */
public class Inventors extends Scenario implements Listener {
	private HashSet<String> crafted = new HashSet<String>();
	private boolean enabled = false;

	public Inventors() {
		super("Inventors", "The first person to craft any item will be broadcasted in chat.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getRecipe().getResult();
		
		if (!crafted.contains(item.getType().name() + item.getDurability())) {
			crafted.add(item.getType().name() + item.getDurability());
			PlayerUtils.broadcast(Main.prefix().replaceAll("UHC", "Inventors") + ChatColor.GREEN + player.getName() + " §7crafted §6" + item.getType().name().toLowerCase().replaceAll("_", " ") + (item.getDurability() > 0 ? ":" + item.getDurability() : "") + " §7first.");
		}
	}
}