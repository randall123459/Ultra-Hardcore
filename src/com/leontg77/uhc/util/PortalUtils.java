package com.leontg77.uhc.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Portal utilities class
 * @author D4mnX
 */
public class PortalUtils {
	
    /**
     * Check if an location has a portal near itself.
     * @param material Material of the portal type.
     * @param location the location.
     * @return True if portal was found, false otherwise.
     */
    public static boolean isPortal(Material material, Location location) {
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
            if (block.getRelative(face).getType() == material) {
                return true;
            }
        }

        return false;
    }
}