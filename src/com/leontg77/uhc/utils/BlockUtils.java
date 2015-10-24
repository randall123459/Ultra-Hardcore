package com.leontg77.uhc.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Block utilities class.
 * <p>
 * Contains block related methods.
 * 
 * @author LeonTG77
 */
public class BlockUtils {

	/**
	 * Get the sapling that should be dropped from the leaf.
	 * 
	 * @param type The leaf type.
	 * @param damage The leaf durability.
	 * @return The sapling that should drop.
	 */
	public static ItemStack getSaplingFor(Material type, short damage) {
		if (type == Material.LEAVES_2) {
			switch (damage) {
			case 0:
			case 4:
			case 8:
			case 12:
				return new ItemStack(Material.SAPLING, 1, (short) 4);
			case 1:
			case 5:
			case 9:
			case 13:
				return new ItemStack(Material.SAPLING, 1, (short) 5);
			}
		} else {
			switch (damage) {
			case 0:
			case 4:
			case 8:
			case 12:
				return new ItemStack(Material.SAPLING);
			case 1:
			case 5:
			case 9:
			case 13:
				return new ItemStack(Material.SAPLING, 1, (short) 1);
			case 2:
			case 6:
			case 10:
			case 14:
				return new ItemStack(Material.SAPLING, 1, (short) 2);
			case 3:
			case 7:
			case 11:
			case 15:
				return new ItemStack(Material.SAPLING, 1, (short) 3);
			}
		}
		return null;
	}
	
	/**
	 * Get the block face direction bases on the given locations yaw.
	 * 
	 * @author ghowden
	 * 
	 * @param loc the location.
	 * @return the block face.
	 */
	public static BlockFace getBlockDirection(Location loc) {
        double rotation = (loc.getYaw()+180) % 360;
        
        if (rotation < 0) {
            rotation += 360.0;
        }
        
        if (0 <= rotation && rotation < 11.25) {
            return BlockFace.NORTH;
        } 
        else if (11.25 <= rotation && rotation < 33.75) {
            return BlockFace.NORTH_NORTH_EAST;
        } 
        else if (33.75 <= rotation && rotation < 56.25) {
            return BlockFace.NORTH_EAST;
        } 
        else if (56.25 <= rotation && rotation < 78.75) {
            return BlockFace.EAST_NORTH_EAST;
        } 
        else if (78.75 <= rotation && rotation < 101.25) {
            return BlockFace.EAST;
        } 
        else if (101.25 <= rotation && rotation < 123.75) {
            return BlockFace.EAST_SOUTH_EAST;
        } 
        else if (123.75 <= rotation && rotation < 146.25) {
            return BlockFace.SOUTH_EAST;
        } 
        else if (146.25 <= rotation && rotation < 168.75) {
            return BlockFace.SOUTH_SOUTH_EAST;
        } 
        else if (168.75 <= rotation && rotation < 191.25) {
            return BlockFace.SOUTH;
        } 
        else if (191.25 <= rotation && rotation < 213.75) {
            return BlockFace.SOUTH_SOUTH_WEST;
        } 
        else if (213.75 <= rotation && rotation < 236.25) {
            return BlockFace.SOUTH_WEST;
        } 
        else if (236.25 <= rotation && rotation < 258.75) {
            return BlockFace.WEST_SOUTH_WEST;
        } 
        else if (258.75 <= rotation && rotation < 281.25) {
            return BlockFace.WEST;
        } 
        else if (281.25 <= rotation && rotation < 303.75) {
            return BlockFace.WEST_NORTH_WEST;
        } 
        else if (303.75 <= rotation && rotation < 326.25) {
            return BlockFace.NORTH_WEST;
        } 
        else if (326.25 <= rotation && rotation < 348.75) {
            return BlockFace.NORTH_NORTH_WEST;
        } 
        else if (348.75 <= rotation && rotation < 360.0) {
            return BlockFace.NORTH;
        } 
        else {
            return null;
        }
    }
}