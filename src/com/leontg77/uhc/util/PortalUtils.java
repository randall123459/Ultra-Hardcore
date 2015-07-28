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
 * @deprecated Not in use yet.
 */
@Deprecated
public class PortalUtils {
	
	/**
	 * @deprecated Not in use yet.
	 */
	@Deprecated
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
	 * @deprecated Not in use yet.
	 */
	@Deprecated
    private static boolean isNetherPortal(Location location) {
        Block block = location.getBlock();
        for (BlockFace face : new BlockFace[]{
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
	 * @deprecated Not in use yet.
	 */
	@Deprecated
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
	 * @deprecated Not in use yet.
	 */
	@Deprecated
    private static void sendMessage(Entity entity, String... messages) {
        if (entity.getType() == EntityType.PLAYER) {
            for (String message : messages) {
                ((Player) entity).sendMessage(message);
            }
        }
    }
}