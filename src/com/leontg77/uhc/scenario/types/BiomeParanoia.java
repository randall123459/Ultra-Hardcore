package com.leontg77.uhc.scenario.types;

import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.NameUtils;

/**
 * BiomeParanoia scenario class
 * 
 * @author LeonTG77
 */
public class BiomeParanoia extends Scenario implements Listener, CommandExecutor {
	private boolean enabled = false;

	public BiomeParanoia() {
		super("BiomeParanoia", "Your tab name color is the color of the biome you are in, /bl for biome colors.");
		Main main = Main.plugin;
		
		main.getCommand("bl").setExecutor(this);
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Biome biome = player.getLocation().getBlock().getBiome();
		
		player.setPlayerListName(biomeColor(biome) + player.getName());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(Main.PREFIX + "\"BiomeParanoia\" is not enabled.");
			return true;
		}
		
		sender.sendMessage(Main.PREFIX + "All biome colors:");
		
		StringBuilder biomes = new StringBuilder();
		
		for (Biome b : Biome.values()) {
			if (!isSendable(b)) {
				continue;
			}
			
			if (biomes.length() > 0) {
				biomes.append("§f, ");
			}
			
			biomes.append(biomeColor(b) + NameUtils.fixString(b.name(), true));
		}
		
		sender.sendMessage(biomes.toString().trim());
		return true;
	}

	/**
	 * Get the color of the given biome.
	 * 
	 * @param biome the given biome.
	 * @return The biome color in string format.
	 */
	private String biomeColor(Biome biome) {
		switch (biome) {
		case BEACH:
			return ChatColor.YELLOW.toString() + ChatColor.ITALIC;
		case BIRCH_FOREST:
			return ChatColor.YELLOW.toString() + ChatColor.BOLD;
		case BIRCH_FOREST_HILLS:
			return ChatColor.YELLOW.toString() + ChatColor.BOLD;
		case BIRCH_FOREST_HILLS_MOUNTAINS:
			return ChatColor.YELLOW.toString() + ChatColor.BOLD;
		case BIRCH_FOREST_MOUNTAINS:
			return ChatColor.YELLOW.toString() + ChatColor.BOLD;
		case COLD_BEACH:
			return ChatColor.YELLOW.toString() + ChatColor.ITALIC;
		case COLD_TAIGA:
			return ChatColor.BLUE.toString();
		case COLD_TAIGA_HILLS:
			return ChatColor.BLUE.toString();
		case COLD_TAIGA_MOUNTAINS:
			return ChatColor.BLUE.toString();
		case DEEP_OCEAN:
			return ChatColor.DARK_BLUE.toString();
		case DESERT:
			return ChatColor.YELLOW.toString();
		case DESERT_HILLS:
			return ChatColor.YELLOW.toString();
		case DESERT_MOUNTAINS:
			return ChatColor.YELLOW.toString();
		case EXTREME_HILLS:
			return ChatColor.GRAY.toString();
		case EXTREME_HILLS_MOUNTAINS:
			return ChatColor.GRAY.toString();
		case EXTREME_HILLS_PLUS:
			return ChatColor.GRAY.toString();
		case EXTREME_HILLS_PLUS_MOUNTAINS:
			return ChatColor.GRAY.toString();
		case FLOWER_FOREST:
			return ChatColor.DARK_GREEN.toString();
		case FOREST:
			return ChatColor.DARK_GREEN.toString();
		case FOREST_HILLS:
			return ChatColor.DARK_GREEN.toString();
		case FROZEN_OCEAN:
			return ChatColor.DARK_BLUE.toString();
		case FROZEN_RIVER:
			return ChatColor.AQUA.toString();
		case HELL:
			return ChatColor.RED.toString();
		case ICE_MOUNTAINS:
			return ChatColor.WHITE.toString();
		case ICE_PLAINS:
			return ChatColor.WHITE.toString();
		case ICE_PLAINS_SPIKES:
			return ChatColor.WHITE.toString() + ChatColor.ITALIC;
		case JUNGLE:
			return ChatColor.GREEN.toString() + ChatColor.BOLD;
		case JUNGLE_EDGE:
			return ChatColor.GREEN.toString() + ChatColor.BOLD;
		case JUNGLE_EDGE_MOUNTAINS:
			return ChatColor.GREEN.toString() + ChatColor.BOLD;
		case JUNGLE_HILLS:
			return ChatColor.GREEN.toString() + ChatColor.BOLD;
		case JUNGLE_MOUNTAINS:
			return ChatColor.GREEN.toString() + ChatColor.BOLD;
		case MEGA_SPRUCE_TAIGA:
			return ChatColor.BLUE.toString() + ChatColor.BOLD;
		case MEGA_SPRUCE_TAIGA_HILLS:
			return ChatColor.BLUE.toString() + ChatColor.BOLD;
		case MEGA_TAIGA:
			return ChatColor.BLUE.toString() + ChatColor.BOLD;
		case MEGA_TAIGA_HILLS:
			return ChatColor.BLUE.toString() + ChatColor.BOLD;
		case MESA:
			return ChatColor.DARK_RED.toString();
		case MESA_BRYCE:
			return ChatColor.DARK_RED.toString();
		case MESA_PLATEAU:
			return ChatColor.DARK_RED.toString();
		case MESA_PLATEAU_FOREST:
			return ChatColor.DARK_RED.toString();
		case MESA_PLATEAU_FOREST_MOUNTAINS:
			return ChatColor.DARK_RED.toString();
		case MESA_PLATEAU_MOUNTAINS:
			return ChatColor.DARK_RED.toString();
		case MUSHROOM_ISLAND:
			return ChatColor.GRAY.toString() + ChatColor.ITALIC;
		case MUSHROOM_SHORE:
			return ChatColor.GRAY.toString() + ChatColor.ITALIC;
		case OCEAN:
			return ChatColor.DARK_BLUE.toString();
		case PLAINS:
			return ChatColor.GREEN.toString();
		case RIVER:
			return ChatColor.AQUA.toString();
		case ROOFED_FOREST:
			return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD;
		case ROOFED_FOREST_MOUNTAINS:
			return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD;
		case SAVANNA:
			return ChatColor.GOLD.toString();
		case SAVANNA_MOUNTAINS:
			return ChatColor.GOLD.toString();
		case SAVANNA_PLATEAU:
			return ChatColor.GOLD.toString();
		case SAVANNA_PLATEAU_MOUNTAINS:
			return ChatColor.GOLD.toString();
		case SKY:
			return ChatColor.BLACK.toString();
		case SMALL_MOUNTAINS:
			return ChatColor.GRAY.toString();
		case STONE_BEACH:
			return ChatColor.GRAY.toString();
		case SUNFLOWER_PLAINS:
			return ChatColor.GREEN.toString();
		case SWAMPLAND:
			return ChatColor.DARK_GRAY.toString();
		case SWAMPLAND_MOUNTAINS:
			return ChatColor.DARK_GRAY.toString();
		case TAIGA:
			return ChatColor.DARK_AQUA.toString();
		case TAIGA_HILLS:
			return ChatColor.DARK_AQUA.toString();
		case TAIGA_MOUNTAINS:
			return ChatColor.DARK_AQUA.toString();
		default:
			return null;
		}
	}

	/**
	 * Check if the given biome can be sent in a message.
	 * 
	 * @param biome the biome checking.
	 * @return <code>True</code> if the biome is sendable, <code>false</code> otherwise.
	 */
	private boolean isSendable(Biome biome) {
		switch (biome) {
		case BEACH:
			return true;
		case BIRCH_FOREST:
			return true;
		case COLD_TAIGA:
			return true;
		case DESERT:
			return true;
		case EXTREME_HILLS:
			return true;
		case FLOWER_FOREST:
			return true;
		case FOREST:
			return true;
		case HELL:
			return true;
		case ICE_PLAINS:
			return true;
		case ICE_PLAINS_SPIKES:
			return true;
		case JUNGLE:
			return true;
		case MEGA_TAIGA:
			return true;
		case MESA:
			return true;
		case MUSHROOM_ISLAND:
			return true;
		case OCEAN:
			return true;
		case PLAINS:
			return true;
		case RIVER:
			return true;
		case ROOFED_FOREST:
			return true;
		case SAVANNA:
			return true;
		case SKY:
			return true;
		case SUNFLOWER_PLAINS:
			return true;
		case SWAMPLAND:
			return true;
		case TAIGA:
			return true;
		default:
			return false;
		}
	}
}