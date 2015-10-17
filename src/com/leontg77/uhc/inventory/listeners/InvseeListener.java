package com.leontg77.uhc.inventory.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.leontg77.uhc.Main;

/**
 * Invsee inventory listener class.
 * <p> 
 * Contains all eventhandlers for invsee inventory releated events.
 * 
 * @author LeonTG77
 */
public class InvseeListener implements Listener {
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();

		if (!Main.invsee.containsKey(inv)) {
			return;
		}
		
		Main.invsee.get(inv).cancel();
		Main.invsee.remove(inv);
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().endsWith("'s Inventory")) {
			return;
		}
		
		event.setCancelled(true);
	}
}