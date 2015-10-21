package com.leontg77.uhc.listeners.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;

/**
 * Selector inventory listener class.
 * <p> 
 * Contains all eventhandlers for selector inventory releated events.
 * 
 * @author LeonTG77
 */
public class SelectorListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		InvGUI manager = InvGUI.getInstance();
		
		Spectator spec = Spectator.getInstance();
		
		Inventory inv = event.getInventory();
		ItemStack item = event.getCurrentItem();
		
		if (!spec.isSpectating(player)) {
			return;
		}
			
		if (inv.getTitle().equals("§8» §cPlayer Selector §8«")) {
			return;
		}
		
		event.setCancelled(true);
		
		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}
			
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aNext page")) {
			manager.currentPage.put(player, manager.currentPage.get(player) + 1);
			player.openInventory(manager.pagesForPlayer.get(player).get(manager.currentPage.get(player)));
			return;
		}
		
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aPrevious page")) {
			manager.currentPage.put(player, manager.currentPage.get(player) - 1); 
			player.openInventory(manager.pagesForPlayer.get(player).get(manager.currentPage.get(player)));
			return;
		}
		
		Player target = Bukkit.getServer().getPlayer(item.getItemMeta().getDisplayName().substring(2));
		
		if (target == null) {
			player.sendMessage(Main.PREFIX + "The player you clicked is not online.");
		} 
		else {
			player.sendMessage(Main.PREFIX + "You teleported to §a" + target.getName() + "§7.");
			player.teleport(target);
		}
		
		player.closeInventory();
	}
}