package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class Timebomb extends Scenario implements Listener {
	private boolean enabled = false;

	public Timebomb() {
		super("Timebomb", "After killing a player all of their items will appear in a double chest rather than dropping on the ground. You then have 30 seconds to loot what you want and get the hell away from it. This is because the chest explodes after the time is up.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		final Player player = event.getEntity().getPlayer();
		
		event.setKeepInventory(true);
		final Location loc = player.getLocation().add(0, -1, 0);
		
		loc.getBlock().setType(Material.CHEST);
		loc.getBlock().getState().update(true);
		Chest chest = (Chest) loc.getBlock().getState();
		
		Location lo = loc.clone().add(0, 0, -1);
		lo.getBlock().setType(Material.CHEST);
		lo.getBlock().getState().update(true);
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) {
				continue;
			}
			chest.getInventory().addItem(item);
		}
		for (ItemStack item : player.getInventory().getArmorContents()) {
			if (item == null) {
				continue;
			}
			chest.getInventory().addItem(item);
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				PlayerUtils.broadcast(Main.prefix().replaceAll("UHC", "Timebomb") + ChatColor.GREEN + player.getName() + "'s §7corpse has exploded!");
				loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 10, false, true);
				// Using actual lightning to kill the items.
				loc.getWorld().strikeLightning(loc);
			}
		}, 600);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				loc.getWorld().strikeLightning(loc);
			}
		}, 620);
	}	
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
	}
}