package com.leontg77.uhc.utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Block utilities class.
 * <p>
 * Contains block related methods.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class BlockUtils {
	
	/**
	 * Display the breaking particles and sound.
	 * 
	 * @param player the player mining the block.
	 * @param loc the location of the block.
	 * @param type the type of the block.
	 */
	public static void blockCrack(Player player, Location loc, Material type) {
		for (Player online : PlayerUtils.getPlayers()) {
			if (online == player) {
				continue;
			}
				
			online.playEffect(loc, Effect.STEP_SOUND, type.getId());
		}
	}
	
	/**
	 * Display the breaking particles and sound.
	 * 
	 * @param player the player mining the block.
	 * @param loc the location of the block.
	 * @param id the id of the block.
	 */
	public static void blockCrack(Player player, Location loc, int id) {
		for (Player online : PlayerUtils.getPlayers()) {
			if (online == player) {
				continue;
			}
				
			online.playEffect(loc, Effect.STEP_SOUND, id);
		}
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