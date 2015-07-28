package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.DamageUtil;

public class RewardingLongshots extends Scenario implements Listener {
	private boolean enabled = false;
	
	public RewardingLongshots() {
		super("RewardingLongshots", "When shooting and hitting people with a bow from a variable distance, you will be rewarded with various different items.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
			return;
		}
	
		Player player = (Player) event.getEntity();
		Arrow damager = (Arrow) event.getDamager();
		
		if (!(damager.getShooter() instanceof Player)) {
			return;
		}
		
		if (!enabled) {
			return;
		}
		
		Player killer = (Player) damager.getShooter();
		
		double distance = killer.getLocation().distance(player.getLocation());
		
		if (distance >= 30 && distance <= 49) {
			killer.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
			Bukkit.broadcastMessage("§9[RewardingLongshots] §f" + killer.getName() + " got a longshot of " + DamageUtil.convertHealth(distance) + " blocks.");
		} else if (distance >= 50 && distance <= 99) {
			killer.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
			killer.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
			Bukkit.broadcastMessage("§9[RewardingLongshots] §f" + killer.getName() + " got a longshot of " + DamageUtil.convertHealth(distance) + " blocks.");
		} else if (distance >= 100 && distance <= 199) {
			killer.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
			killer.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
			Bukkit.broadcastMessage("§9[RewardingLongshots] §f" + killer.getName() + " got a longshot of " + DamageUtil.convertHealth(distance) + " blocks.");
			killer.getInventory().addItem(new ItemStack(Material.DIAMOND));
		} else if (distance >= 200) {
			killer.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 2));
			killer.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 3));
			killer.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
			Bukkit.broadcastMessage("§9[RewardingLongshots] §f" + killer.getName() + " got a longshot of " + DamageUtil.convertHealth(distance) + " blocks.");
		}
	}
}