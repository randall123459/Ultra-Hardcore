package com.leontg77.uhc.listeners;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.State;
import com.leontg77.uhc.utils.BlockUtils;
import com.leontg77.uhc.utils.EntityUtils;
import com.leontg77.uhc.utils.GameUtils;

/**
 * Block listener class.
 * <p> 
 * Contains all eventhandlers for block releated events.
 * 
 * @author LeonTG77
 */
public class BlockListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
		Block block = event.getBlock();
    	
    	if (State.isState(State.SCATTER)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (!GameUtils.getGameWorlds().contains(block.getWorld()) && !block.getWorld().getName().equals("arena") && !player.hasPermission("uhc.build")) {
    		event.setCancelled(true);
			return;
    	}
    	
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
    	
		Random rand = new Random();
		
		if (block.getType() == Material.GRAVEL) {
			if (rand.nextInt(99) < game.getFlintRates()) {
				Main.toReplace.put(Material.GRAVEL, new ItemStack (Material.FLINT));
			} else {
				Main.toReplace.put(Material.FLINT, new ItemStack (Material.GRAVEL));
			}
			return;
		}
		
		if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
			return;
		}
		
		short damage = block.getState().getData().toItemStack().getDurability();
		
		if (block.getType() == Material.LEAVES) {
			if (rand.nextInt(99) < 5) {
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES, damage));
				item.setVelocity(EntityUtils.randomOffset());
			}
			
			if (damage != 0 && damage != 4 && damage != 8 && damage != 12) {
				return;
			}

			ItemStack hand = player.getItemInHand();

			if (game.shears() && hand != null && hand.getType() == Material.SHEARS) {
				if (rand.nextInt(99) >= game.getShearRates()) {
					return;
				}

				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
				item.setVelocity(EntityUtils.randomOffset());
			} else {
				if (rand.nextInt(99) >= game.getAppleRates()) {
					return;
				}

				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
				item.setVelocity(EntityUtils.randomOffset());
			}
			return;
		}
		
		if (block.getType() == Material.LEAVES_2) {
			if (rand.nextInt(99) < 5) {
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES_2, damage));
				item.setVelocity(EntityUtils.randomOffset());
			}
			
			if (damage != 1 && damage != 5 && damage != 9 && damage != 13) {
				return;
			}

			ItemStack hand = player.getItemInHand();
			
			if (game.shears() && hand != null && hand.getType() == Material.SHEARS) {
				if (rand.nextInt(99) >= game.getShearRates()) {
					return;
				}
				
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
				item.setVelocity(EntityUtils.randomOffset());
			} else {
				if (rand.nextInt(99) >= game.getAppleRates()) {
					return;
				}
				
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
				item.setVelocity(EntityUtils.randomOffset());
			}
		}
    }

	@EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player player = event.getPlayer();
    	
    	if (State.isState(State.SCATTER)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (!GameUtils.getGameWorlds().contains(block.getWorld())) {
    		if (block.getWorld().getName().equals("arena")) {
    			return;
    		}
    		
    		if (player.hasPermission("uhc.build")) {
    			return;
    		}
    		
    		event.setCancelled(true);
    		return;
    	}
    }
	
	@EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
		Block block = event.getBlock();
    	
    	if (block.getWorld().getName().equals("lobby")) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
			return;
		}
    	
    	Location loc = block.getLocation();
    	Material type = block.getType();
    	
		short damage = block.getState().getData().toItemStack().getDurability();
		Random rand = new Random();

    	event.setCancelled(true);
    	block.setType(Material.AIR); 
    	block.getState().update(true);
		
		if (type == Material.LEAVES) {
			if (rand.nextInt(99) < 5) {
				Item item = block.getWorld().dropItem(loc.clone().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES, damage));
				item.setVelocity(EntityUtils.randomOffset());
			}
			
			if (damage != 0 && damage != 4 && damage != 8 && damage != 12) {
				return;
			}

			if (rand.nextInt(99) >= game.getAppleRates()) {
				return;
			}

			Item item = block.getWorld().dropItem(loc.clone().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
			item.setVelocity(EntityUtils.randomOffset());
			return;
		}
		
		if (type == Material.LEAVES_2) {
			if (rand.nextInt(99) < 5) {
				Item item = block.getWorld().dropItem(loc.clone().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES_2, damage));
				item.setVelocity(EntityUtils.randomOffset());
			}
			
			if (damage != 1 && damage != 5 && damage != 9 && damage != 13) {
				return;
			}

			if (rand.nextInt(99) >= game.getAppleRates()) {
				return;
			}
			
			Item item = block.getWorld().dropItem(loc.clone().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
			item.setVelocity(EntityUtils.randomOffset());
		}
    }
}