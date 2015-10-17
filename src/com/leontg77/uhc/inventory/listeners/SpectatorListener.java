package com.leontg77.uhc.inventory.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.inventory.InvGUI;
import com.leontg77.uhc.utils.LocationUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Spectator inventory listener class.
 * <p> 
 * Contains all eventhandlers for spectator inventory releated events.
 * 
 * @author LeonTG77
 */
public class SpectatorListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		
		Spectator spec = Spectator.getInstance();
		ItemStack item = event.getCurrentItem();
		
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (item.getType() == Material.INK_SACK) {
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Short.MAX_VALUE, 0));
			}
			
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.FEATHER) {
			Location loc = new Location(player.getWorld(), 0.5, 0, 0.5);
			loc.setY(LocationUtils.highestTeleportableYAtLocation(loc));
			
			player.teleport(loc);
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.COMPASS) {
			if (event.isRightClick()) {
				InvGUI.getInstance().openSelector(player);
				event.setCancelled(true);
				return;
			}
			
			ArrayList<Player> players = new ArrayList<Player>();
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (!Spectator.getInstance().isSpectating(online)) {
					players.add(online);
				}
			}
			
			if (players.size() > 0) {
				Player target = players.get(new Random().nextInt(players.size()));
				player.teleport(target.getLocation());
				player.sendMessage(Main.PREFIX + "You teleported to §a" + target.getName() + "§7.");
			} else {
				player.sendMessage(Main.PREFIX + "No players to teleport to.");
			}
			
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.LAVA_BUCKET) {
			Game game = Game.getInstance();

			if (!game.nether()) {
				player.sendMessage(Main.PREFIX + "Nether is disabled.");
				event.setCancelled(true);
				return;
			}

			ArrayList<String> netherL = new ArrayList<String>();
			StringBuilder nether = new StringBuilder();
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (online.getWorld().getEnvironment() == Environment.NETHER) {
					if (Spectator.getInstance().isSpectating(online)) {
						continue;
					}
					
					netherL.add(online.getName());
				}
			}
			
			if (netherL.size() == 0) {
				player.sendMessage(Main.PREFIX + "No players are in the nether.");
				event.setCancelled(true);
				return;
			}
			
			int i = 1;
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (online.getWorld().getEnvironment() == Environment.NETHER) {
					if (Spectator.getInstance().isSpectating(online)) {
						continue;
					}
					
					if (nether.length() > 0) {
						if (netherL.size() == i) {
							nether.append(" §7and §a");
						} else {
							nether.append("§7, §a");
						}
					}

					nether.append("§a" + online.getName());
					i++;
				}
			}

			player.sendMessage(Main.PREFIX + "Players in the nether:");
			player.sendMessage("§8» §7" + nether.toString().trim());
			
			event.setCancelled(true);
		}
	}
}