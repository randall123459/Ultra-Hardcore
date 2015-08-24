package com.leontg77.uhc.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.cmds.HOFCommand;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Inventory listener class.
 * <p> 
 * Contains all eventhandlers for inventory releated events.
 * 
 * @author LeonTG77
 */
public class InventoryListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		if (event.getInventory().getTitle().equals("Player Inventory")) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getInventory().getTitle().endsWith("Fame")) {
			event.setCancelled(true);
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§aNext page")) {
				HOFCommand.page.put(player, HOFCommand.page.get(player) + 1);
				player.openInventory(HOFCommand.pages.get(player).get(HOFCommand.page.get(player)));
				return;
			}
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§aPrevious page")) {
				HOFCommand.page.put(player, HOFCommand.page.get(player) - 1); 
				player.openInventory(HOFCommand.pages.get(player).get(HOFCommand.page.get(player)));
			}
			return;
		}
		
		if (event.getInventory().getTitle().equals("Arctic UHC Rules")) {
			event.setCancelled(true);
			return;
		}
		
		if (!Spectator.getManager().isSpectating(player)) {
			return;
		}
        
		if (event.getInventory().getTitle().equals("Player Selector")) {
			Player target = Bukkit.getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().substring(2, event.getCurrentItem().getItemMeta().getDisplayName().length()));
			
			if (target == null) {
				player.sendMessage(Main.prefix() + "Target is not online.");
			} else {
				player.teleport(target);
			}
			
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.COMPASS) {
			if (event.isLeftClick()) {
				ArrayList<Player> players = new ArrayList<Player>();
				for (Player online : PlayerUtils.getPlayers()) {
					if (!Spectator.getManager().isSpectating(online)) {
						players.add(online);
					}
				}
				
				if (players.size() > 0) {
					Player target = players.get(new Random().nextInt(players.size()));
					player.teleport(target.getLocation());
					player.sendMessage(Main.prefix() + "You teleported to §a" + target.getName() + "§7.");
				} else {
					player.sendMessage(Main.prefix() + "No players to teleport to.");
				}
				event.setCancelled(true);
				return;
			}
			
			InvGUI.getManager().openSelector(player);
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.INK_SACK) {
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 0));
			}
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.LAVA_BUCKET) {
			StringBuilder nether = new StringBuilder();
			int i = 1;
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (online.getWorld().getEnvironment() == Environment.NETHER) {
					if (nether.length() > 0) {
						if (i == PlayerUtils.getPlayers().size()) {
							nether.append(" §7and §a");
						} else {
							nether.append("§7, §a");
						}
					}

					nether.append(ChatColor.GREEN + online.getName());
				}
			}
			
			if (nether.length() == 0) {
				player.sendMessage(Main.prefix() + "No players are in the nether.");
				event.setCancelled(true);
				return;
			}

			player.sendMessage(Main.prefix() + "Players in the nether:");
			player.sendMessage("§8» §7" + nether.toString().trim());
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() == Material.FEATHER) {
			player.teleport(new Location(player.getWorld(), 0, 100, 0));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();
		
		if (Main.invsee.containsKey(inv)) {
			Main.invsee.get(inv).cancel();
			Main.invsee.remove(inv);
		}
	}
}