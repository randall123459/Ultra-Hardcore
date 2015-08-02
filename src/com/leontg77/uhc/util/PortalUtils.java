package com.leontg77.uhc.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Nether utilities class
 * @author LeonTG77
 */
public class PortalUtils {
	
	/**
	 * Find a possible portal location for an entity.
	 * @param entity the entity.
	 * @param from the from portal.
	 * @param travelAgent the portal travel agent.
	 * @return The possible portal location, null if not found.
	 */
    public static Location getPossiblePortalLocation(Entity entity, Location from, TravelAgent travelAgent) {
        String fromWorldName = from.getWorld().getName();

        String targetWorldName;
        if (from.getWorld().getEnvironment() == World.Environment.NETHER) {
            if (!fromWorldName.endsWith("_nether")) {
                worldError(entity);
                return null;
            }

            targetWorldName = fromWorldName.substring(0, fromWorldName.length() - 7);
        } else if (from.getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (!isNetherPortal(from)) {
                return null;
            }

            targetWorldName = fromWorldName + "_nether";
        } else {
            return null;
        }

        World targetWorld = Bukkit.getWorld(targetWorldName);
        if (targetWorld == null) {
            worldError(entity);
            return null;
        }

        Location to = new Location(targetWorld, from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch());
        to = travelAgent.findOrCreate(to);
        return to;
    }
	
    /**
     * Check if an location has a portal near itself.
     * @param location the location.
     * @return True if portal was found, false otherwise.
     */
    private static boolean isNetherPortal(Location location) {
        Block block = location.getBlock();
        for (BlockFace face : new BlockFace[] {
                BlockFace.SELF,
                BlockFace.EAST,
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.WEST,
                BlockFace.NORTH_EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH_WEST,
                BlockFace.NORTH_WEST
        }) {
            if (block.getRelative(face).getType() == Material.PORTAL) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Sends an error to an entity if the portal didn't work.
     * @param entity the entity.
     */
    private static void worldError(Entity entity) {
        if (entity.getWorld().getEnvironment() == World.Environment.NORMAL) {
            sendMessage(entity,
                    "No world is linked to this portal. Nether is most likely off.",
                    "Please check the match post. If nether is stated",
                    "as \"enabled\" there, please inform an operator."
            );
        } else {
            sendMessage(entity,
                    "No world is linked to this portal. How did this happen?",
                    "You are already in the nether, this is unexpected.",
                    "Please immediately inform an operator."
            );
        }
    }

    /**
     * Sends messages to an entity.
     * @param entity the entity.
     * @param messages messages to send.
     */
    private static void sendMessage(Entity entity, String... messages) {
        if (entity.getType() == EntityType.PLAYER) {
            for (String message : messages) {
                ((Player) entity).sendMessage(message);
            }
        }
    }
}