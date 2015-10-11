package com.leontg77.uhc.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;

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
	 * Get the inv size of # players online.
	 * 
	 * @return the inv size.
	 */
	public static int playerInvSize() {
		int length = 0;
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.getWorld().getName().equals("lobby")) {
				continue;
			}
			
			if (Spectator.getManager().isSpectating(online)) {
				continue;
			}
			
			length++;
		}
		
		if (length <= 9) {
			return 9;
		} else if (length > 9 && length <= 18) {
			return 18;
		} else if (length > 18 && length <= 27) {
			return 27;
		} else if (length > 27 && length <= 36) {
			return 36;
		} else if (length > 36 && length <= 45) {
			return 45;
		} else if (length > 45 && length <= 54) {
			return 54;
		}
		
		return 54;
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
	 * @param item the item giving.
	 */
	public static void giveItem(Player player, ItemStack item) {
		if (player != null) {
			if (hasSpaceFor(player, item)) {
				player.getInventory().addItem(item);
			} else {
				player.sendMessage(Main.prefix() + "A item was dropped on the ground since your inventory is full!");
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
				
				Item i = player.getWorld().dropItem(player.getLocation().add(0, 0.7, 0), item);
				i.setVelocity(new Vector(0, 0.2, 0));
			}
		}
	}
	
	public static boolean hasSpaceFor(Player player, ItemStack item) {
		if (player.getInventory().firstEmpty() == -1) {
			for (int i = 0; i < 35; i++) {
				if (player.getInventory().getItem(i).equals(item)) {
					if ((player.getInventory().getItem(i).getAmount() + item.getAmount()) <= item.getMaxStackSize()) {
						return true;
					}
				}
				
				if (i == 34) {
					return false;
				}
			}
		} else {
			return true;
		}
		return false;
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
		
		if (total < entered) {
			return false;
		} else {
			return true;
		}
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
}