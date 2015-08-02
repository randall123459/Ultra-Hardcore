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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.util.BlockUtils;
import com.leontg77.uhc.util.PortalUtils;

public class BlockListener implements Listener {

	@EventHandler(ignoreCancelled = true)
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
			if ((new Random().nextInt(99) + 1) <= Main.flintrate) {
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
		
		if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
			try {
				Tree tree = (Tree) event.getBlock().getState().getData();
				if (tree.getSpecies() == TreeSpecies.GENERIC || tree.getSpecies() == TreeSpecies.DARK_OAK) {
					Random r = new Random();
					
					if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.SHEARS) {
						if ((r.nextInt(99) + 1) <= Main.shearrate) {
							event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.APPLE, 1));
						}
					}
				}
			} catch (ClassCastException localClassCastException) {
				Bukkit.getLogger().warning(ChatColor.RED + "Could not shear leave for apple @ " + block.getLocation().toString());
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
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		if (State.isState(State.SCATTER)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location to = PortalUtils.getPossiblePortalLocation(event.getPlayer(), event.getFrom(), event.getPortalTravelAgent());
        if (to != null) {
            event.setTo(to);
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        Location to = PortalUtils.getPossiblePortalLocation(event.getEntity(), event.getFrom(), event.getPortalTravelAgent());
        if (to != null) {
            event.setTo(to);
        }
    }
}