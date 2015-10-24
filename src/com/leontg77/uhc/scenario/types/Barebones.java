package com.leontg77.uhc.scenario.types;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;

/**
 * Barebones scenario class
 * 
 * @author LeonTG77
 */
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

	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		boolean cutclean = ScenarioManager.getInstance().getScenario("CutClean").isEnabled();
		ItemStack replaced = new ItemStack (cutclean ? Material.IRON_INGOT : Material.IRON_ORE);
    	
		if (block.getType() == Material.EMERALD_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.EMERALD, replaced);
			return;
    	}
		
		if (block.getType() == Material.REDSTONE_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.REDSTONE, replaced);
			return;
    	}
    	
		if (block.getType() == Material.LAPIS_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.INK_SACK, replaced);
			return;
    	}
    	
		if (block.getType() == Material.GOLD_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.GOLD_ORE, replaced);
			return;
    	}
    	
		if (block.getType() == Material.DIAMOND_ORE) {
			if (block.getDrops(player.getItemInHand()).isEmpty()) {
				return;
			}

			Main.toReplace.put(Material.DIAMOND, replaced);
    	}
    }
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		List<ItemStack> drops = event.getDrops();
		
		drops.add(new ItemStack (Material.STRING, 2));
		drops.add(new ItemStack (Material.DIAMOND, 1));
		drops.add(new ItemStack (Material.GOLDEN_APPLE, 1));
		drops.add(new ItemStack (Material.ARROW, 32));
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		ItemStack item = event.getRecipe().getResult();
		CraftingInventory inv = event.getInventory();
		
		if (item.getType() != Material.ANVIL && item.getType() != Material.GOLDEN_APPLE && item.getType() != Material.ENCHANTMENT_TABLE) {
			return;
		}
		
		inv.setResult(new ItemStack(Material.AIR));
	}
}