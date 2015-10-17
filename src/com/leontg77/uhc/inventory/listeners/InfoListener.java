package com.leontg77.uhc.inventory.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.leontg77.uhc.Main;

/**
 * Gameinfo inventory listener class.
 * <p> 
 * Contains all eventhandlers for gameinfo inventory releated events.
 * 
 * @author LeonTG77
 */
public class InfoListener implements Listener {
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();

		if (!Main.gameInfo.containsKey(inv)) {
			return;
		}
		
		Main.gameInfo.get(inv).cancel();
		Main.gameInfo.remove(inv);
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().equals("§8» §cGame Information §8«")) {
			return;
		}
		
		event.setCancelled(true);
	}
}