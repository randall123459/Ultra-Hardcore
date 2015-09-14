package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Lootcrates scenario class
 * 
 * @author LeonTG77
 */
public class Lootcrates extends Scenario implements Listener {
	private boolean enabled = false;
	private BukkitRunnable task;

	public Lootcrates() {
		super("Lootcrates", "Every 10 minutes, players will be given a loot crate filled with goodies. There are two tiers, an Ender Chest being tier 2 and a normal chest tier 1.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			this.task = new BukkitRunnable() {
				public void run() {
					for (Player online : PlayerUtils.getPlayers()) {
						int i = new Random().nextInt(2);
						
						if (i == 1) {
							PlayerUtils.giveItem(online, new ItemStack (Material.ENDER_CHEST));
						} else {
							PlayerUtils.giveItem(online, new ItemStack (Material.CHEST));
						}
					}
					PlayerUtils.broadcast(Main.prefix() + "Lootcrates has been given out.");
				}
			};
			
			task.runTaskTimer(Main.plugin, 12000, 12000);
		} else {
			task.cancel();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (block.getType() == Material.CHEST) {
			player.sendMessage(ChatColor.RED + "You cannot place lootcrates.");
			event.setCancelled(true);
			return;
		}

		if (block.getType() == Material.ENDER_CHEST) {
			player.sendMessage(ChatColor.RED + "You cannot place lootcrates.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getItem() == null) {
			return;
		}

		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (item.getType() == Material.CHEST) {
			int i = new Random().nextInt(5);
			player.sendMessage(Main.prefix() + "You rolled an §a" + i + "§7.");
			event.setCancelled(true);
			
			switch (i) {
			case 0:
				player.setItemInHand(new ItemStack (Material.IRON_PICKAXE));
				break;
			case 1:
				player.setItemInHand(new ItemStack (Material.APPLE, 2));
				break;
			case 2:
				player.setItemInHand(new ItemStack (Material.COOKED_BEEF, 8));
				break;
			case 3:
				player.setItemInHand(new ItemStack (Material.CAKE));
				break;
			case 4:
				player.setItemInHand(new ItemStack (Material.RAW_FISH, 64, (short) 3));
				break;
			case 5:
				player.setItemInHand(new ItemStack (Material.BOW));
				break;
			}
			return;
		}

		if (item.getType() == Material.ENDER_CHEST) {
			int i = new Random().nextInt(10);
			player.sendMessage(Main.prefix() + "You rolled an §a" + i + "§7.");
			event.setCancelled(true);
			
			switch (i) {
			case 0:
				player.setItemInHand(new ItemStack (Material.DIAMOND));
				break;
			case 1:
				player.setItemInHand(new ItemStack (Material.GOLD_INGOT, 3));
				break;
			case 2:
				player.setItemInHand(new ItemStack (Material.IRON_INGOT, 5));
				break;
			case 3:
				player.setItemInHand(new ItemStack (Material.DIRT, 32));
				break;
			case 4:
				player.setItemInHand(new ItemStack (Material.ENCHANTMENT_TABLE));
				break;
			case 5:
				player.setItemInHand(new ItemStack (Material.DIAMOND_SWORD));
				break;
			case 6:
				player.setItemInHand(new ItemStack (Material.DIAMOND_HELMET));
				break;
			case 7:
				player.setItemInHand(new ItemStack (Material.DIAMOND_BOOTS));
				break;
			case 8:
				player.setItemInHand(new ItemStack (Material.ARROW, 32));
				break;
			case 9:
				player.setItemInHand(new ItemStack (Material.TNT));
				break;
			case 10:
				player.setItemInHand(new ItemStack (Material.FLINT_AND_STEEL));
				break;
			}
		}
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		ItemStack item = event.getRecipe().getResult();
		
		if (item.getType() == Material.CHEST) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
			return;
		}
		
		if (item.getType() == Material.ENDER_CHEST) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
}