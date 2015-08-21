package com.leontg77.uhc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Scatter utilities class.
 * <p>
 * Contains scatter related methods.
 * 
 * @author LeonTG77
 */
public class ScatterUtils {
	private static Material[] nospawn = { Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA, Material.CACTUS };
	
	/**
	 * Get a list of available scatter locations.
	 * 
	 * @param world the world to scatter in.
	 * @param radius the maximum radius to scatter.
	 * @param count the amount of scatter locations needed.
	 * @return A list of scatter locations.
	 */
	public static List<Location> getScatterLocations(World world, int radius, int count) {
		List<Location> locs = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			double min = 150;
			
			for (int j = 0; j < 4004; j++) {
				if (j == 4003) {
					PlayerUtils.broadcast(ChatColor.RED + "Could not scatter " + i, "uhc.admin");
					break;
				}
				
				Random rand = new Random();
				int x = rand.nextInt(radius * 2) - radius;
				int z = rand.nextInt(radius * 2) - radius;

				Location loc = new Location(world, x + 0.5, 0, z + 0.5);

				boolean close = false;
				for (Location l : locs) {
					if (l.distanceSquared(loc) < min) {
						close = true;
					}
				}
				
				if (!close && isVaildLocation(loc.clone())) {
					locs.add(loc);
					break;
				} else {
					min -= 1;
				}
			}
		}

		for (Location loc : locs) {
			loc.getChunk().load(true);
			double y = BlockUtils.highestBlock(loc).getY();
			loc.setY(y + 2);
		}
		
		return locs;
	}

	/**
	 * Check if a location is a vaild scatter point.
	 * 
	 * @param l the location .
	 * @return True if its vaild, false otherwise.
	 */
	private static boolean isVaildLocation(Location l) {
		l.setY(l.getWorld().getHighestBlockYAt(l));
		
		Material m = l.add(0, -1, 0).getBlock().getType();
		boolean vaild = true;
		
		if (l.getBlockY() < 60) {
			vaild = false;
		}	
		
		for (Material no : nospawn) {
			if (m == no) {
				vaild = false;
			}
		}
		
		return vaild;
	}
}