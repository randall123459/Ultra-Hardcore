package com.leontg77.uhc.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.State;
import com.leontg77.uhc.utils.BlockUtils;

/**
 * Block listener class.
 * <p> 
 * Contains all eventhandlers for block releated events.
 * 
 * @author LeonTG77
 */
public class BlockListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
		Block block = event.getBlock();
    	
    	if (State.isState(State.SCATTER)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (event.getBlock().getWorld().getName().equals("lobby") && !player.hasPermission("uhc.build")) {
    		event.setCancelled(true);
    		return;
    	}
		
		if (block.getType() == Material.GRAVEL) {
			if ((new Random().nextInt(99) + 1) <= game.getFlintRates()) {
				event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), 13);
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.FLINT));
				item.setVelocity(new Vector(0, 0.2, 0));
			} else {
				event.setCancelled(true);
				BlockUtils.blockCrack(player, block.getLocation(), 13);
				block.setType(Material.AIR);
				block.getState().update();
				Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GRAVEL));
				item.setVelocity(new Vector(0, 0.2, 0));
			}
		}
		
		if (game.shears()) {
			if (block.getType() == Material.LEAVES) {
				try {
					Tree tree = (Tree) event.getBlock().getState().getData();
					if (tree.getSpecies() == TreeSpecies.GENERIC) {
						Random r = new Random();
						
						if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.SHEARS) {
							if ((r.nextInt(99) + 1) <= game.getShearRates()) {
								event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.APPLE, 1));
							}
						}
					}
				} catch (ClassCastException localClassCastException) {
					Bukkit.getLogger().warning(ChatColor.RED + "Could not shear leaf for apple @ " + block.getLocation().toString());
				}
			}
		}
    }

	@EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
    	
    	if (State.isState(State.SCATTER)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (event.getBlock().getWorld().getName().equals("lobby") && !player.hasPermission("uhc.build")) {
    		event.setCancelled(true);
    		return;
    	}
    }

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (!Arena.getInstance().isEnabled()) {
			return;
		}
		
		Location loc = event.getBlock().getLocation();

		for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
			for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
				for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
					if (loc.getWorld().getBlockAt(x, y, z).getType() == Material.FIRE) {
						loc.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
						loc.getWorld().getBlockAt(x, y, z).getState().update();
					}
				}
			}
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
		Block block = event.getBlock();
    	
    	if (block.getWorld().getName().equals("lobby")) {
    		event.setCancelled(true);
    		return;
    	}
    }
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		if (State.isState(State.SCATTER)) {
			event.setCancelled(true);
		}
	}
}