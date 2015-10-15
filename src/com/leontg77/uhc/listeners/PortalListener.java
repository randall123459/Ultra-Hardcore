package com.leontg77.uhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.LocationUtils;

/**
 * Portal listener class.
 * <p> 
 * Contains all eventhandlers for portal releated events.
 * 
 * @author LeonTG77 with huge help from D4mnX and ghowden
 */
public class PortalListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
		TravelAgent travel = event.getPortalTravelAgent();
		Player player = event.getPlayer();
		Location from = event.getFrom();
		
		if (LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
            if (!game.nether()) {
            	player.sendMessage(Main.PREFIX + "The nether is disabled.");
            	return;
            }
            
			String fromName = from.getWorld().getName();
	        String targetName;
	        
	        switch (from.getWorld().getEnvironment()) {
			case NETHER:
	            if (!fromName.endsWith("_nether")) {
	            	player.sendMessage(Main.PREFIX + "Could not teleport you to the overworld, contact the staff now.");
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 7);
				break;
			case NORMAL:
	            targetName = fromName + "_nether";
				break;
			default:
				return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.PREFIX + "The nether has not been created.");
	            return;
	        }

	        double multiplier = from.getWorld().getEnvironment() == Environment.NETHER ? 8D : 0.125D;
	        Location to = new Location(world, from.getX() * multiplier, from.getY(), from.getZ() * multiplier, from.getYaw(), from.getPitch());
	        
	        to = travel.findOrCreate(to);
	        
	        to = LocationUtils.findSafeLocationInsideBorder(to, 10, travel);

	        if (to == null || to.getY() < 0) {
            	player.sendMessage(Main.PREFIX + "Could not teleport you, contact the staff now.");
	        } else {
	            event.setTo(to);
	        }
		}
		
		if (LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, from)) {
            if (!game.theEnd()) {
            	player.sendMessage(Main.PREFIX + "The end is disabled.");
            	return;
            }
            
			String fromName = from.getWorld().getName();
	        String targetName;
	        
	        switch (from.getWorld().getEnvironment()) {
			case THE_END:
	        	event.setTo(Main.getSpawn());
	        	return;
			case NORMAL:
	            targetName = fromName + "_end";
				break;
			default:
				return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.PREFIX + "The end has not been created.");
	            return;
	        }

	        Location to = new Location(world, 100.0, 49, 0, 90f, 0f);

			for (int y = to.getBlockY() - 1; y <= to.getBlockY() + 2; y++) {
				for (int x = to.getBlockX() - 2; x <= to.getBlockX() + 2; x++) {
					for (int z = to.getBlockZ() - 2; z <= to.getBlockZ() + 2; z++) {
						Block block = world.getBlockAt(x, y, z);
						
						if (y == 48) {
							block.setType(Material.OBSIDIAN);
							block.getState().update();
						} else {
							block.setType(Material.AIR);
							block.getState().update();
						}
					}
				}
			}
			
			event.setTo(to);
		}
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
		TravelAgent travel = event.getPortalTravelAgent();
		Location from = event.getFrom();
		
		if (LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
            if (!game.nether()) {
            	return;
            }
            
			String fromName = from.getWorld().getName();
	        String targetName;
	        
	        switch (from.getWorld().getEnvironment()) {
			case NETHER:
	            if (!fromName.endsWith("_nether")) {
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 7);
				break;
			case NORMAL:
	            targetName = fromName + "_nether";
				break;
			default:
				return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
	            return;
	        }

	        double multiplier = from.getWorld().getEnvironment() == Environment.NETHER ? 8D : 0.125D;
	        Location to = new Location(world, from.getX() * multiplier, from.getY(), from.getZ() * multiplier, from.getYaw(), from.getPitch());
	        
	        to = travel.findOrCreate(to);

	        to = LocationUtils.findSafeLocationInsideBorder(to, 10, travel);

	        if (to != null && to.getY() >= 0) {
	            event.setTo(to);
	        }
		}
		
		if (LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, from)) {
            if (!game.theEnd()) {
            	return;
            }
            
			String fromName = from.getWorld().getName();
	        String targetName;
	        
	        switch (from.getWorld().getEnvironment()) {
			case THE_END:
	        	event.getEntity().remove();
	        	return;
			case NORMAL:
	            targetName = fromName + "_end";
				break;
			default:
				return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
	            return;
	        }

	        Location to = new Location(world, 100.0, 49, 0, 90f, 0f);

			for (int y = to.getBlockY() - 1; y <= to.getBlockY() + 2; y++) {
				for (int x = to.getBlockX() - 2; x <= to.getBlockX() + 2; x++) {
					for (int z = to.getBlockZ() - 2; z <= to.getBlockZ() + 2; z++) {
						Block block = world.getBlockAt(x, y, z);
						
						if (y == 48) {
							block.setType(Material.OBSIDIAN);
							block.getState().update();
						} else {
							block.setType(Material.AIR);
							block.getState().update();
						}
					}
				}
			}
			
			event.setTo(to);
		}
    }
}