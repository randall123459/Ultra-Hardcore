package com.leontg77.uhc.scenario.types;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.SpecInfo;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.BlockUtils;
import com.leontg77.uhc.util.PlayerUtils;

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
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Block block = event.getBlock();
		
		if (block.getType() == Material.IRON_ORE) {
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 15);
			block.setType(Material.AIR);
			block.getState().update();

			if (ScenarioManager.getInstance().getScenario("CutClean").isEnabled()) {
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT, 3));
				item.setVelocity(new Vector(0, 0.2, 0));
				
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
			} else {
				if (locs.contains(block.getLocation())) {
					Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE, 1));
					item.setVelocity(new Vector(0, 0.2, 0));
					locs.remove(block.getLocation());
					return;
				}
				
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE, 3));
				item.setVelocity(new Vector(0, 0.2, 0));
			}
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			if (ScenarioManager.getInstance().getScenario("Barebones").isEnabled()) {
				return;
			}
			
			if (ScenarioManager.getInstance().getScenario("Goldless").isEnabled()) {
				return;
			}
			
			if (!SpecInfo.locs.contains(event.getBlock().getLocation())) {
				Player player = event.getPlayer();
				int amount = 0;
				Location loc = event.getBlock().getLocation();
				
				for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
					for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
						for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
							if (loc.getWorld().getBlockAt(x, y, z).getType() == Material.GOLD_ORE) {
								amount++;
								SpecInfo.locs.add(loc.getWorld().getBlockAt(x, y, z).getLocation());
							}
						}
					}
				}
				
				if (SpecInfo.totalG.containsKey(player.getName())) {
					SpecInfo.totalG.put(player.getName(), SpecInfo.totalG.get(player.getName()) + amount);
				} else {
					SpecInfo.totalG.put(player.getName(), amount);
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Main.spectating.contains(online.getName())) {
						online.sendMessage("[§4S§f] §7" + player.getName() + "§f:§6GOLD §f[V:§6" + amount + "§f] [T:§6" + SpecInfo.totalG.get(player.getName()) + "§f]");
					}
				}
				amount = 0;
			}
			
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 14);
			block.setType(Material.AIR);
			block.getState().update();

			if (ScenarioManager.getInstance().getScenario("CutClean").isEnabled()) {
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GOLD_INGOT, 3));
				item.setVelocity(new Vector(0, 0.2, 0));
				
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
				exp.setExperience(7);
			} else {
				if (locs.contains(block.getLocation())) {
					Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GOLD_ORE, 1));
					item.setVelocity(new Vector(0, 0.2, 0));
					locs.remove(block.getLocation());
					return;
				}
				
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GOLD_ORE, 3));
				item.setVelocity(new Vector(0, 0.2, 0));
			}
		}
		
		if (block.getType() == Material.COAL_ORE) {
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 16);
			block.setType(Material.AIR);
			block.getState().update();

			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.COAL, 3));
			item.setVelocity(new Vector(0, 0.2, 0));
			
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(9);
		}
		
		if (block.getType() == Material.EMERALD_ORE) {
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 129);
			block.setType(Material.AIR);
			block.getState().update();

			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.EMERALD, 3));
			item.setVelocity(new Vector(0, 0.2, 0));
			
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(9);
		}
		
		if (block.getType() == Material.DIAMOND_ORE) {
			if (!SpecInfo.locs.contains(event.getBlock().getLocation())) {
				Location loc = event.getBlock().getLocation();
				Player player = event.getPlayer();
				int amount = 0;
				
				for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
					for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
						for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
							if (loc.getWorld().getBlockAt(x, y, z).getType() == Material.DIAMOND_ORE) {
								amount++;
								locs.add(loc.getWorld().getBlockAt(x, y, z).getLocation());
							}
						}
					}
				}
				
				if (SpecInfo.totalD.containsKey(player.getName())) {
					SpecInfo.totalD.put(player.getName(), SpecInfo.totalD.get(player.getName()) + amount);
				} else {
					SpecInfo.totalD.put(player.getName(), amount);
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Main.spectating.contains(online.getName())) {
						online.sendMessage("[§4S§f] §7" + player.getName() + "§f:§3DIAMOND §f[V:§3" + amount + "§f] [T:§3" + SpecInfo.totalD.get(player.getName()) + "§f]");
					}
				}
				amount = 0;
			}
			
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 56);
			block.setType(Material.AIR);
			block.getState().update();

			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.DIAMOND, 3));
			item.setVelocity(new Vector(0, 0.2, 0));
			
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(11);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!isEnabled()) {
			return;
		}

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