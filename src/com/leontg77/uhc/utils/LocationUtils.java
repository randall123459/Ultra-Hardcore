package com.leontg77.uhc.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Location utilities class.
 * <p>
 * Contains location related methods.
 * 
 * @author LeonTG77, with help from ghowden and D4mnX
 */
public class LocationUtils {
	private static BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST};
	
	/**
	 * Check if the given block is nearby the given location.
	 * 
	 * @param material the type of the block
	 * @param location the location.
	 * @return <code>True</code> if blocks nearby has the checked type, <code>false</code> otherwise.
	 * 
	 * @author D4mnX
	 */
    public static boolean hasBlockNearby(Material material, Location location) {
        Block block = location.getBlock();
        
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
	 * 
	 * @author ghowden
	 */
	public static boolean isOutsideOfBorder(Location loc) {
        WorldBorder border = loc.getWorld().getWorldBorder();

        double size = border.getSize();
        double x = loc.getX() - border.getCenter().getX();
        double z = loc.getZ() - border.getCenter().getZ();
        
        return Math.abs(x) < size && Math.abs(z) < size;
    }
	
	/**
     * Finds a safe location inside the border. If the X or Z value is outside of the border they are set to be within
     * the border with the specified buffer. If no safe teleportable Y coordinate was found for the new location then
     * the Y location will be set to -1 and IS NOT SAFE FOR TELEPORTING. If the location is already inside the border
     * then it will be returned the without modification
     *
     * @param loc the location to check
     * @return a location within the border
     * 
	 * @author ghowden
     */
    public static Location findSafeLocationInsideBorder(Location loc, int buffer, TravelAgent travel) {
        WorldBorder border = loc.getWorld().getWorldBorder();
        Location centre = border.getCenter();

        Location pos = loc.subtract(centre);

        double size = border.getSize() / 2;
        double bufferSize = size - buffer;

        double x = pos.getX();
        double z = pos.getZ();
        boolean changed = false;

        if (Math.abs(x) > size) {
            pos.setX(x > 0 ? bufferSize : -bufferSize);
            changed = true;
        }

        if (Math.abs(z) > size) {
            pos.setZ(z > 0 ? bufferSize : -bufferSize);
            changed = true;
        }

        if (!changed) {
        	return loc;
        }

        pos.setY(highestTeleportableYAtLocation(pos.add(centre)));
        
        Location to = travel.findOrCreate(pos);
        
        if (!isOutsideOfBorder(to)) {
        	pos = to;
        }
        
        return pos;
    }
	
	/**
     * Checks for the highest non-air block with 2 air blocks above it.
     *
     * @param location the location whose X,Z coordinates are used
     * @return -1 if no valid location found, otherwise coordinate with non-air Y coord with 2 air blocks above it
	 * 
	 * @author ghowden
     */
    public static int highestTeleportableYAtLocation(Location location) {
        Location startingLocation = location.clone();
        startingLocation.setY(location.getWorld().getMaxHeight());

        boolean above2WasAir = false;
        boolean aboveWasAir = false;
        Block currentBlock = startingLocation.getBlock();

        while (currentBlock.getY() >= 0) {

            if (currentBlock.getType() != Material.AIR) {
                if (above2WasAir && aboveWasAir) {
                    return currentBlock.getY();
                }

                above2WasAir = aboveWasAir;
                aboveWasAir = false;
            } else {
                above2WasAir = aboveWasAir;
                aboveWasAir = true;
            }

            currentBlock = currentBlock.getRelative(BlockFace.DOWN);
        }

        return -1;
    }
}