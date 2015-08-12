package com.leontg77.uhc.scenario.types;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;

public class Backpacks extends Scenario implements Listener {
	private boolean enabled = false;

	public Backpacks() {
		super("Backpacks", "Players can type /bp to open up a backpack inventory.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/bp")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "\"Backbacks\" is not enabled.");
				return;
			}
			
			player.openInventory(player.getEnderChest());
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getEntity();
		Block block = player.getLocation().add(0, -1, 0).getBlock();
		
		block.setType(Material.CHEST);
		block.getState().update();
		
		Chest chest = (Chest) block.getState();
		for (ItemStack item : player.getEnderChest().getContents()) {
			chest.getInventory().addItem(item);
		}
		player.getEnderChest().clear();
	}
}