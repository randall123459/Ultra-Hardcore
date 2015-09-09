package com.leontg77.uhc;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * The parkour class.
 * 
 * @author LeonTG77
 */
public class Parkour implements Listener {
	private static Parkour manager = new Parkour();
	public BukkitRunnable task;

	public Location spawn = new Location(Bukkit.getWorld("lobby"), 29.5, 34, 0.5, -90, 0);
	public Location point1 = new Location(Bukkit.getWorld("lobby"), 90.5, 36, 2.5, -90, 0);
	public Location point2 = new Location(Bukkit.getWorld("lobby"), 134.5, 41, 12.5, 0, 0);
	public Location point3 = new Location(Bukkit.getWorld("lobby"), 102.5, 54, 17.5, -180, 0);
	
	public HashSet<Player> parkourPlayers = new HashSet<Player>();
	public HashMap<Player, Integer> checkpoint = new HashMap<Player, Integer>();
	public HashMap<Player, Integer> time = new HashMap<Player, Integer>();

	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static Parkour getManager() {
		return manager;
	}
	
	/**
	 * Set up the parkour class.
	 */
	public void setup() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		task = new BukkitRunnable() {
			public void run() {
				for (Player parkourers : time.keySet()) {
					time.put(parkourers, time.get(parkourers) + 1);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 20, 20);
	}
	
	/**
	 * Set up the parkour class.
	 */
	public void shutdown() {
		HandlerList.unregisterAll(this);
		parkourPlayers.clear();
		checkpoint.clear();
		task.cancel();
		time.clear();
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL) {
			return;
		}
		
		Player player = event.getPlayer();
		Entity point = PlayerUtils.getNearby(player.getLocation(), 2).get(0);
		
		if (point instanceof ArmorStand) {
			if (((ArmorStand) point).getCustomName() == null) {
				return;
			}
			
			if (((ArmorStand) point).getCustomName().contains("Start")) {
				if (parkourPlayers.contains(player)) {
					player.sendMessage(Main.prefix() + "The timer has been reset to §a0s§7.");
					player.playSound(player.getLocation(), "random.pop", 1, 1);
					time.put(player, 0);
					return;
				}
				
				player.sendMessage(Main.prefix() + "Parkour started.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourPlayers.add(player);
				checkpoint.put(player, 0);
				time.put(player, 0);
			}
			
			if (!parkourPlayers.contains(player)) {
				return;
			}
			
			if (((ArmorStand) point).getCustomName().contains("#1")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 1) {
					return;
				}
				
				player.sendMessage(Main.prefix() + "You reached checkpoint 1.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourPlayers.add(player);
				checkpoint.put(player, 1);
			}
			
			if (((ArmorStand) point).getCustomName().contains("#2")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 2) {
					return;
				}
				
				player.sendMessage(Main.prefix() + "You reached checkpoint 2.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourPlayers.add(player);
				checkpoint.put(player, 2);
			}
			
			if (((ArmorStand) point).getCustomName().contains("#3")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 3) {
					return;
				}
				
				player.sendMessage(Main.prefix() + "You reached checkpoint 3.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourPlayers.add(player);
				checkpoint.put(player, 3);
			}
			
			if (((ArmorStand) point).getCustomName().contains("finish")) {
				player.sendMessage(Main.prefix() + "You finished the parkour, time used: §a" + DateUtils.ticksToString(time.get(player)));
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
				
				parkourPlayers.remove(player);
				checkpoint.remove(player);
				time.remove(player);
			}
		}
	}
}