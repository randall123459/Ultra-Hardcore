package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;

/**
 * CutClean scenario class
 * 
 * @author LeonTG77
 */
public class CutClean extends Scenario implements Listener {
	private boolean enabled = false;
	
	public CutClean() {
		super("CutClean", "No furnaces required, items requiring cooking drop their cooked variety.");
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Cow) {
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 3));
			event.getDrops().add(new ItemStack(Material.LEATHER));
		} 
		else if (entity instanceof Chicken) {
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, 3));
			event.getDrops().add(new ItemStack(Material.FEATHER, 2));
		}
		else if (entity instanceof Pig) {
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.GRILLED_PORK, 3));
		}
		else if (entity instanceof Sheep) {
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_MUTTON, 2));
		}
		else if (entity instanceof Rabbit) {
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.COOKED_RABBIT, 1));
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (block.getType() == Material.IRON_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			if (scen.getScenario("TripleOres").isEnabled()) {
				return;
			}
			
			Main.toReplace.put(Material.IRON_ORE, new ItemStack (Material.IRON_INGOT));
			
			ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(3);
			return;
		}
		
		if (block.getType() == Material.POTATO) {
			Main.toReplace.put(Material.POTATO_ITEM, new ItemStack(Material.BAKED_POTATO, 1 + new Random().nextInt(2)));
			return;
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
			
			if (scen.getScenario("TripleOres").isEnabled()) {
				return;
			}
			
			Main.toReplace.put(Material.GOLD_ORE, new ItemStack (Material.GOLD_INGOT));
			
			ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(7);
		}
	}
}