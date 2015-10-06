package com.leontg77.uhc.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Location utilities class.
 * <p>
 * Contains location related methods.
 * 
 * @author LeonTG77
 */
public class LocationUtils {
	
	/**
	 * Check if the given block is nearby the given location.
	 * 
	 * @param material the type of the block
	 * @param location the location.
	 * @return <code>True</code> if blocks nearby has the checked type, <code>false</code> otherwise.
	 */
    public static boolean hasBlockNearby(Material material, Location location) {
        Block block = location.getBlock();
        BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST};
        
        for (BlockFace face : faces) {
            if (block.getRelative(face).getType() == material) {
                return true;
            }
        }

        return false;
    }

	/**
	 * Get the highest block at the given location.
	 * 
	 * @param loc the location.
	 * @return The highest block.
	 */
	public static Location getHighestBlock(Location loc) {
		for (int i = 255; i >= 0; i--) {
			if (loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).getType() != Material.AIR) {
				return loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).getLocation();
			}
		}
		return loc;
	}
	
	/**
	 * Check if the given location is outside the border.
	 * 
	 * @param loc the location.
	 * @return True if it is, false otherwise.
	 */
	public static boolean isOutsideOfBorder(Location loc) {
        WorldBorder border = loc.getWorld().getWorldBorder();

        double size = border.getSize();
        double x = loc.getX();
        double z = loc.getZ();
        
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }
	
	
	@SuppressWarnings("unused")
	public static Location findLocationInsideBorder(Location loc) {
        WorldBorder border = loc.getWorld().getWorldBorder();
        
        double size = border.getSize();
        double x = loc.getX();
        double z = loc.getZ();

        if (isOutsideOfBorder(loc)) {
        	Location newLoc = loc;
        	return newLoc;
        } else {
            return loc;
        }
    }
}