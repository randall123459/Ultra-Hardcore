package com.leontg77.uhc;

import static com.leontg77.uhc.Main.plugin;

import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R3.BiomeBase;

/**
 * BiomeSwap class.
 * <p>
 * This class contains methods for swapping biomes
 * 
 * @author regaw_leinad, modified by LeonTG77
 */
public class BiomeSwap {
	private static BiomeSwap manager = new BiomeSwap();
	private BiomeBase[] origBiomes;
	private Random random;

	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static BiomeSwap getInstance() {
		return manager;
	}

	public void setup() {
		this.origBiomes = getMcBiomesCopy();
		this.random = new Random();

		update();
	}

	private void update() {
		doSwap(Settings.getInstance().getSwap().getStringList("swap"));
	}

	private void doSwap(List<String> swaps) {
		for (String s : swaps) {
			String[] biomes = s.split(";");
			if (biomes.length == 2) {
				if (biomes[0].equalsIgnoreCase("all")) {
					SwappableBiome newBiome = fromString(biomes[1]);
					if (newBiome != null) {
						plugin.getLogger().info("Swapping all biomes with " + newBiome.toString().toLowerCase().replaceAll("_", ""));
						swapBiome(newBiome);
					} else {
						plugin.getLogger().warning("Invalid biome swap config value in: " + s);
					}
				} 
				else if (biomes[1].equalsIgnoreCase("random")) {
					SwappableBiome newBiome = getRandomBiome();
					SwappableBiome oldBiome = fromString(biomes[0]);
					
					if (oldBiome != null) {
						plugin.getLogger().info("Swapping " + oldBiome.toString().toLowerCase().replaceAll("_", "") + " with " + newBiome.toString().toLowerCase().replaceAll("_", ""));
						swapBiome(oldBiome, newBiome);
					} else {
						plugin.getLogger().warning("Invalid biome swap config value: " + biomes[0]);
					}
				} 
				else {
					SwappableBiome oldBiome = fromString(biomes[0]);
					SwappableBiome newBiome = fromString(biomes[1]);
					
					if ((oldBiome != null) && (newBiome != null)) {
						plugin.getLogger().info("Swapping " + oldBiome.toString().toLowerCase().replaceAll("_", "") + " with " + newBiome.toString().toLowerCase().replaceAll("_", ""));
						swapBiome(oldBiome, newBiome);
					} else {
						plugin.getLogger().warning("Invalid biome swap config value in: " + s);
					}
				}
			} else {
				plugin.getLogger().warning("Invalid biome swap config string: " + s);
			}
		}
	}

	public void swapBiome(SwappableBiome oldBiome, SwappableBiome newBiome) {
		if (oldBiome.getId() != SwappableBiome.SKY.getId()) {
			BiomeBase[] biomes = getMcBiomes();

			biomes[oldBiome.getId()] = getOrigBiome(newBiome.getId());
		} else {
			plugin.getLogger().warning("Cannot swap SKY biome!");
		}
	}

	public void swapBiome(SwappableBiome newBiome) {
		BiomeBase[] biomes = getMcBiomes();
		BiomeBase newB = getOrigBiome(newBiome.getId());
		
		for (int i = 0; i < SwappableBiome.values().length; i++) {
			if ((i != newBiome.getId()) && (i != SwappableBiome.SKY.getId())) {
				biomes[i] = newB;
			}
		}
	}

	public SwappableBiome getRandomBiome() {
		return SwappableBiome.values()[random.nextInt(SwappableBiome.values().length)];
	}

	public SwappableBiome fromString(String biome) {
		for (SwappableBiome b : SwappableBiome.values()) {
			if (b.toString().equalsIgnoreCase(biome)) {
				return b;
			}
		}
		return null;
	}

	public void resetBiomes() {
		BiomeBase[] biomes = getMcBiomes();
		for (SwappableBiome b : SwappableBiome.values()) {
			biomes[b.getId()] = getOrigBiome(b.getId());
		}
	}

	/**
	 * Gets a copy of the mc biome bases.
	 * 
	 * @return Copy of mc biome bases.
	 */
	private BiomeBase[] getMcBiomesCopy() {
		return BiomeBase.getBiomes().clone();
	}

	/**
	 * Get an array of all MC biome bases.
	 * 
	 * @return Array of MC biomebases.
	 */
	private BiomeBase[] getMcBiomes() {
		return BiomeBase.getBiomes();
	}

	/**
	 * Get an original biome by the given id.
	 * 
	 * @param value the id "value"
	 * @return The biome base of the original biome.
	 */
	private BiomeBase getOrigBiome(int value) {
		return origBiomes[value];
	}

	/**
	 * List of swapppable biomes
	 * 
	 * @author regaw_leinad
	 */
	public enum SwappableBiome {
		OCEAN(0), PLAINS(1), DESERT(2), EXTREME_HILLS(3), FOREST(4), TAIGA(5), SWAMPLAND(
		6), RIVER(7), HELL(8), SKY(9), FROZEN_OCEAN(10), FROZEN_RIVER(
		11), ICE_PLAINS(12), ICE_MOUNTAINS(13), MUSHROOM_ISLAND(14), MUSHROOM_SHORE(
		15), BEACH(16), DESERT_HILLS(17), FOREST_HILLS(18), TAIGA_HILLS(
		19), SMALL_MOUNTAINS(20), JUNGLE(21), JUNGLE_HILLS(22), JUNGLE_EDGE(
		23), DEEP_OCEAN(24), STONE_BEACH(25), COLD_BEACH(26), BIRCH_FOREST(
		27), BIRCH_FOREST_HILLS(28), ROOFED_FOREST(29), COLD_TAIGA(30), COLD_TAIGA_HILLS(
		31), MEGA_TAIGA(32), MEGA_TAIGA_HILLS(33), EXTREME_HILLS_PLUS(
		34), SAVANNA(35), SAVANNA_PLATEAU(36), MESA(37), MESA_PLATEAU_F(
		38), MESA_PLATEAU(39);

		private int id;

		/**
		 * Constructor for SwappableBiome
		 * 
		 * @param id the id of the biome.
		 */
		private SwappableBiome(int id) {
			this.id = id;
		}

		/**
		 * Get the id of the biome.
		 * 
		 * @return The id.
		 */
		public int getId() {
			return id;
		}
	}
}