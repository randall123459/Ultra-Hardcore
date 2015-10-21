package com.leontg77.uhc.listeners.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.InvGUI;

/**
 * HOF inventory listener class.
 * <p> 
 * Contains all eventhandlers for HOF inventory releated events.
 * 
 * @author LeonTG77
 */
public class HOFListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		InvGUI manager = InvGUI.getInstance();
		
		Inventory inv = event.getInventory();
		ItemStack item = event.getCurrentItem();
		
		if (!inv.getTitle().contains("'s HoF, Page")) {
			return;
		}
			
		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}
		
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aNext page")) {
			manager.currentPage.put(player, manager.currentPage.get(player) + 1);
			player.openInventory(manager.pagesForPlayer.get(player).get(manager.currentPage.get(player)));
		}
		
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aPrevious page")) {
			manager.currentPage.put(player, manager.currentPage.get(player) - 1); 
			player.openInventory(manager.pagesForPlayer.get(player).get(manager.currentPage.get(player)));
		}
		
		event.setCancelled(true);
	}
}