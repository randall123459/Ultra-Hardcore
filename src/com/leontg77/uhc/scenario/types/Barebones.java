package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.BlockUtils;

public class Barebones extends Scenario implements Listener {
	private boolean enabled = false;
	
	public Barebones() {
		super("Barebones", "The Nether is disabled, and iron is the highest tier you can obtain through gearing up. When a player dies, they will drop 1 diamond, 1 golden apple, 32 arrows, and 2 string. You cannot craft an enchantment table, anvil, or golden apple. Mining any ore except coal or iron will drop an iron ingot.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
    	Player player = event.getPlayer();
		Block block = event.getBlock();
    	
		if (ScenarioManager.getManager().getScenario("CutClean") != null && ScenarioManager.getManager().getScenario("CutClean").isEnabled()) {
	    	if (block.getType() == Material.EMERALD_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.REDSTONE_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.LAPIS_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.GOLD_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.DIAMOND_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
				exp.setExperience(3);
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
		} else {
	    	if (block.getType() == Material.EMERALD_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.REDSTONE_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.LAPIS_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.GOLD_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
	    	
	    	if (block.getType() == Material.DIAMOND_ORE) {
	    		event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), block.getType());
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_ORE));
				item.setVelocity(new Vector(0, 0.2, 0));
	    	}
		}
    }
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		event.getDrops().add(new ItemStack (Material.STRING, 2));
		event.getDrops().add(new ItemStack (Material.DIAMOND, 1));
		event.getDrops().add(new ItemStack (Material.GOLDEN_APPLE, 1));
		event.getDrops().add(new ItemStack (Material.ARROW, 32));
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		ItemStack item = event.getRecipe().getResult();
		
		if (item.getType() == Material.ANVIL) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.GOLDEN_APPLE) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
		
		if (item.getType() == Material.ENCHANTMENT_TABLE) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
}