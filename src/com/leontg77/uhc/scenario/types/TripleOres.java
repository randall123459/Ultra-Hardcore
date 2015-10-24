package com.leontg77.uhc.scenario.types;

import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;

/**
 * TripleOres scenario class
 * 
 * @author LeonTG77
 */
public class TripleOres extends Scenario implements Listener {
	private HashSet<Location> locs = new HashSet<Location>();
	private boolean enabled = false;
	
	public TripleOres() {
		super("TripleOres", "When mining an ore it drops 3 of the dropped item.");
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
		if (block.getType() == Material.IRON_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			if (scen.getScenario("CutClean").isEnabled()) {
				Main.toReplace.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT, 3));
				
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
			} else {
				if (locs.contains(block.getLocation())) {
					return;
				}

				Main.toReplace.put(Material.IRON_ORE, new ItemStack(Material.IRON_ORE, 3));
			}
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			if (scen.getScenario("Barebones").isEnabled()) {
				return;
			}
			
			if (scen.getScenario("Goldless").isEnabled()) {
				return;
			}

			if (scen.getScenario("CutClean").isEnabled()) {
				Main.toReplace.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT, 3));
				
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
			} else {
				if (locs.contains(block.getLocation())) {
					return;
				}

				Main.toReplace.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_ORE, 3));
			}
		
		}
		
		if (block.getType() == Material.COAL_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.COAL, new ItemStack(Material.COAL, 3));
			
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(3);
		}
		
		if (block.getType() == Material.EMERALD_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.EMERALD, new ItemStack(Material.EMERALD, 3));
			
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(8);
		}
		
		if (block.getType() == Material.DIAMOND_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.DIAMOND, new ItemStack(Material.DIAMOND, 3));
			
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(12);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (ScenarioManager.getInstance().getScenario("CutClean").isEnabled()) {
			return;
		}
		
		Block block = event.getBlockPlaced();
		
		if (block.getType() == Material.IRON_ORE || block.getType() == Material.GOLD_ORE) {
			if (!locs.contains(block.getLocation())) {
				locs.add(block.getLocation());
			}
		}
	}
}