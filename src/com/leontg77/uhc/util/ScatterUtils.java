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
 * @author LeonTG77
 */
public class ScatterUtils {
	private static Material[] nospawn = { Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA, Material.CACTUS };
	
	/**
	 * Get a list of available scatter locations
	 * @param world the world to scatter in.
	 * @param radius the maximum radius to scatter.
	 * @param count the amount of scatter locations needed.
	 * @return List of scatter locations.
	 */
	public static List<Location> getScatterLocations(World world, int radius, int count) {
		double minDisSq = mindis(radius, count);

		List<Location> locs = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			double min = minDisSq;
			
			for (int j = 0; j < 2002; j++) {
				if (j == 2001) {
					for (int l = 0; l < 2002; l++) {
						if (l == 2001) {
							PlayerUtils.broadcast(ChatColor.RED + "Could not scatter " + i, "uhc.admin");
							break;
						}
						
						Random rand = new Random();
						int x = rand.nextInt(radius * 2) - radius;
						int z = rand.nextInt(radius * 2) - radius;

						Location r = new Location(world, x + 0.5, 0, z + 0.5);

						boolean close = false;
						for (Location lo : locs) {
							if (lo.distanceSquared(r) < min) {
								close = true;
							}
						}
						
						if (!close && isValidSpawnLocation(r.clone())) {
							locs.add(r);
							break;
						} else {
							min -= 1;
						}
					}
					break;
				}
				
				Random rand = new Random();
				int x = rand.nextInt(radius * 2) - radius;
				int z = rand.nextInt(radius * 2) - radius;

				Location r = new Location(world, x + 0.5, 0, z + 0.5);

				boolean close = false;
				for (Location l : locs) {
					if (l.distanceSquared(r) < min) {
						close = true;
					}
				}
				
				if (!close && isValidSpawnLocation(r.clone())) {
					locs.add(r);
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
	 * @param l the location .
	 * @return True if its vaild, false otherwise.
	 */
	private static boolean isValidSpawnLocation(Location l) {
		l.setY(l.getWorld().getHighestBlockYAt(l));
		Material m = l.add(0, -1, 0).getBlock().getType();
		boolean noHazard = true;
		if (l.getBlockY() < 60) {
			noHazard = false;
		}	
		for (Material no : nospawn) {
			if (m == no) {
				noHazard = false;
			}
		}
		return noHazard;
	}
	
	/**
	 * Find the minumun distance between players.
	 * @param radius the max radius.
	 * @param count amount of scatter locations.
	 * @return The minimum distance.
	 */
	private static double mindis(int radius, int count) {
		double totalColumns = (radius * 2) * (radius * 2);
		double minDisSq = (totalColumns / (double) count);

		return minDisSq;
	}
}