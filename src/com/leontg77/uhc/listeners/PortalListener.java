package com.leontg77.uhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.LocationUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class PortalListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
		TravelAgent travel = event.getPortalTravelAgent();
		Player player = event.getPlayer();
		Location from = event.getFrom();
		
		if (game.nether() && LocationUtils.hasBlockNearby(Material.PORTAL, event.getFrom())) {
			String fromName = event.getFrom().getWorld().getName();
	        String targetName;
	        
	        if (event.getFrom().getWorld().getEnvironment().equals(Environment.NETHER)) {
	            if (!fromName.endsWith("_nether")) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the overworld, contact the staff now.");
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 7);
	        } else if (event.getFrom().getWorld().getEnvironment().equals(Environment.NORMAL)) {
	            if (!LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the nether, contact the staff now.");
	                return;
	            }

	            targetName = fromName + "_nether";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.prefix() + "The nether has not been created.");
	            return;
	        }

	        Location to = (world.getName().endsWith("_nether") ? new Location(world, (from.getX() / 8), (from.getY() / 8), (from.getZ() / 8), from.getYaw(), from.getPitch()) : new Location(world, (from.getX() * 8), (from.getY() * 8), (from.getZ() * 8), from.getYaw(), from.getPitch()));
	        
	        if (LocationUtils.isOutsideOfBorder(to)) {
	        	
	        }
	        
	        to = travel.findOrCreate(to);

	        if (to != null) {
	            event.setTo(to);
	        } else {
            	player.sendMessage(Main.prefix() + "Could not teleport you, contact the staff now.");
	        }
		} else {
            if (LocationUtils.hasBlockNearby(Material.PORTAL, event.getFrom())) {
            	player.sendMessage(Main.prefix() + "The nether is disabled.");
            }
		}
		
		if (game.theEnd() && LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
			String fromName = event.getFrom().getWorld().getName();
	        String targetName;
	        
	        if (event.getFrom().getWorld().getEnvironment().equals(Environment.THE_END)) {
	        	event.setTo(Main.getSpawn());
	            return;
	        } else if (event.getFrom().getWorld().getEnvironment().equals(Environment.NORMAL)) {
	            if (!LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the end, contact the staff now.");
	                return;
	            }
	            
	            targetName = fromName + "_end";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.prefix() + "The end has not been created.");
	            return;
	        }

	        Location to = new Location(world, 100.0, 49, 0, 90f, 0f);

			for (int y = to.getBlockY() - 1; y <= to.getBlockY() + 2; y++) {
				for (int x = to.getBlockX() - 2; x <= to.getBlockX() + 2; x++) {
					for (int z = to.getBlockZ() - 2; z <= to.getBlockZ() + 2; z++) {
						if (y == 48) {
							to.getWorld().getBlockAt(x, y, z).setType(Material.OBSIDIAN);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						} else {
							to.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						}
					}
				}
			}
			
			event.setTo(to);
		} else {
            if (LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
            	player.sendMessage(Main.prefix() + "The end is disabled.");
            }
		}
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
		TravelAgent travel = event.getPortalTravelAgent();
		Location from = event.getFrom();
		
		if (game.nether()) {
			String fromName = event.getFrom().getWorld().getName();
	        String targetName;
	        
	        if (event.getFrom().getWorld().getEnvironment().equals(Environment.NETHER)) {
	            if (!fromName.endsWith("_nether")) {
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 7);
	        } else if (event.getFrom().getWorld().getEnvironment().equals(Environment.NORMAL)) {
	            if (!LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
	                return;
	            }

	            targetName = fromName + "_nether";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
	            return;
	        }

	        Location to = (world.getName().endsWith("_nether") ? new Location(world, (from.getX() / 8), (from.getY() / 8), (from.getZ() / 8), from.getYaw(), from.getPitch()) : new Location(world, (from.getX() * 8), (from.getY() * 8), (from.getZ() * 8), from.getYaw(), from.getPitch()));
	        to = travel.findOrCreate(to);

	        if (to != null) {
	            event.setTo(to);
	        }
		}
		
		if (game.theEnd()) {
			String fromName = event.getFrom().getWorld().getName();
	        String targetName;
	        
	        if (event.getFrom().getWorld().getEnvironment().equals(Environment.THE_END)) {
	        	event.setTo(Main.getSpawn());
	            return;
	        } else if (event.getFrom().getWorld().getEnvironment().equals(Environment.NORMAL)) {
	            if (!LocationUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
	                return;
	            }
	            
	            targetName = fromName + "_end";
	        } else {
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
						if (y == 48) {
							to.getWorld().getBlockAt(x, y, z).setType(Material.OBSIDIAN);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						} else {
							to.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						}
					}
				}
			}
			
			event.setTo(to);
		}
    }
}