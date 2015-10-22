package com.leontg77.uhc.listeners.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Stats inventory listener class.
 * <p> 
 * Contains all eventhandlers for stats inventory releated events.
 * 
 * @author LeonTG77
 */
public class StatsListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().endsWith("'s Stats")) {
			return;
		}
		
		event.setCancelled(true);
	}
}