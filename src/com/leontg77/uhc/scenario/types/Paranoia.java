package com.leontg77.uhc.scenario.types;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

public class Paranoia extends Scenario implements Listener {
	private boolean enabled = false;
	
	public Paranoia() {
		super("Paranoia", "Your coordinates are broadcasted when you mine diamonds/gold, craft or eat an golden apple, you craft an anvil or enchantment table or you die");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public String prefix() {
		return "§c§lParanoia §8§l>> §f";
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		Block block = event.getBlock();
		
		if (block.getType() == Material.DIAMOND_ORE) {
			PlayerUtils.broadcast(prefix() + ChatColor.GREEN + player.getName() + "§7 mined §bdiamond ore §7at " + location(loc));
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			PlayerUtils.broadcast(prefix() + ChatColor.GREEN + player.getName() + "§7 mined §6gold ore §7at " + location(loc));
		}
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE) {
			PlayerUtils.broadcast(prefix() + ChatColor.GREEN + player.getName() + "§7 ate a §eGolden Apple §7at " + location(loc));
		}
	}
	
	@EventHandler
	public void onPrepareItemCraftEvent(CraftItemEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		HumanEntity player = event.getWhoClicked();
		Location loc = player.getLocation();
		
		if (event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE) {
			PlayerUtils.broadcast(prefix() + ChatColor.GREEN + player.getName() + "§7 crafted a §eGolden Apple §7at " + location(loc));
		}
		
		if (event.getRecipe().getResult().getType() == Material.ANVIL) {
			PlayerUtils.broadcast(prefix() + ChatColor.GREEN + player.getName() + "§7 crafted an §dAnvil §7at " + location(loc));
		}
		
		if (event.getRecipe().getResult().getType() == Material.ENCHANTMENT_TABLE) {
			PlayerUtils.broadcast(prefix() + ChatColor.GREEN + player.getName() + "§7 crafted an §5Enchantment Table §7at " + location(loc));
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getEntity();
		Location loc = player.getLocation();

		PlayerUtils.broadcast(prefix() + ChatColor.GREEN + player.getName() + "§7 died at " + location(loc));
	}

	private String location(Location loc) {
		return "x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ();
	}
}