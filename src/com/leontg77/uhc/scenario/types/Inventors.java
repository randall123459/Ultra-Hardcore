package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class Inventors extends Scenario implements Listener {
	private ArrayList<Material> crafted = new ArrayList<Material>();
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
		if (!isEnabled()) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getRecipe().getResult();
		
		if (!crafted.contains(item.getType())) {
			crafted.add(item.getType());
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN).replaceAll("UHC", "Inventors") + player.getName() + " §7crafted " + item.getType().name().toLowerCase().replaceAll("_", " ") + " first.");
		}
	}
}