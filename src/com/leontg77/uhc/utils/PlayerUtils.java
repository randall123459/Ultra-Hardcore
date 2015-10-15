package com.leontg77.uhc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.leontg77.uhc.Main;

/**
 * Player utilities class.
 * <p>
 * Contains player related methods.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class PlayerUtils {
	
	/**
	 * Get a list of players online.
	 * 
	 * @return A list of online players.
	 */
	public static List<Player> getPlayers() {
		List<Player> list = new ArrayList<Player>();
		
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			list.add(online);
		}
		
		return list;
	}
	
	/**
	 * Get the given player's ping.
	 * 
	 * @param player the player
	 * @return the players ping
	 */
	public static int getPing(Player player) {
		CraftPlayer craft = (CraftPlayer) player;
		return craft.getHandle().ping;
	} 
	
	/**
	 * Gets an offline player by a name.
	 * <p>
	 * This is just because of the deprecation on <code>Bukkit.getOfflinePlayer(String)</code> 
	 * 
	 * @param name The name.
	 * @return the offline player.
	 */
	public static OfflinePlayer getOfflinePlayer(String name) {
		return Bukkit.getServer().getOfflinePlayer(name);
	}
	
	/**
	 * Broadcasts a message to everyone online.
	 * 
	 * @param message the message.
	 */
	public static void broadcast(String message) {
		for (Player online : getPlayers()) {
			online.sendMessage(message);
		}
		
		Bukkit.getLogger().info(message.replaceAll("§l", "").replaceAll("§o", "").replaceAll("§r", "").replaceAll("§m", "").replaceAll("§n", ""));
	}
	
	/**
	 * Broadcasts a message to everyone online with a specific permission.
	 * 
	 * @param message the message.
	 * @param permission the permission.
	 */
	public static void broadcast(String message, String permission) {
		for (Player online : getPlayers()) {
			if (online.hasPermission(permission)) {
				online.sendMessage(message);
			}
		}
		
		Bukkit.getLogger().info(message.replaceAll("§l", "").replaceAll("§o", "").replaceAll("§r", "").replaceAll("§m", "").replaceAll("§n", ""));
	}
	
	/**
	 * Get a list of entites within a distance of a location.
	 * 
	 * @param loc the location.
	 * @param distance the distance.
	 * @return A list of entites nearby.
	 */
	public static List<Entity> getNearby(Location loc, int distance) {
		List<Entity> list = new ArrayList<Entity>();
		
		for (Entity e : loc.getWorld().getEntities()) {
			if (e instanceof Player) {
				continue;
			}
			
			if (!e.getType().isAlive()) {
				continue;
			}
			
			if (loc.distance(e.getLocation()) <= distance) {
				list.add(e);
			}
		}
		
		for (Player online : getPlayers()) {
			if (online.getWorld() == loc.getWorld()) {
				if (loc.distance(online.getLocation()) <= distance) {
					list.add(online);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Give the given item to the given player.
	 * <p>
	 * Method is made so if the inventory is full it drops the item to the ground.
	 * 
	 * @param player the player giving to.
	 * @param stack the item giving.
	 */
	public static void giveItem(Player player, ItemStack stack) {
		PlayerInventory inv = player.getInventory();
		
		HashMap<Integer, ItemStack> leftOvers = inv.addItem(stack);
		
		if (leftOvers.isEmpty()) {
			return;
		}
		
		player.sendMessage(Main.PREFIX + "Your inventory was full, item was dropped on the ground.");
		
		Location loc = player.getLocation();
		World world = player.getWorld();
		
		for (ItemStack leftOver : leftOvers.values()) {
			Item item = world.dropItem(loc, leftOver);
			item.setVelocity(EntityUtils.randomOffset());
		}
	}

	/**
	 * Check if the given player has enough of the given number of the given material.
	 * 
	 * @param player the player.
	 * @param material the material.
	 * @param entered the number.
	 * 
	 * @return <code>True</code> if the player has the given number of the material, <code>false</code> otherwise
	 */
	public static boolean hasEnough(Player player, Material material, int entered) {
		int total = 0;
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) {
				continue;
			}
			
			if (item.getType() == material) {
				total = total + item.getAmount();
			}
		}
		
		return total >= entered;
	}
}