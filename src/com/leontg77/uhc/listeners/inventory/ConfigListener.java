package com.leontg77.uhc.listeners.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Game;

/**
 * Inventory listener class.
 * <p> 
 * Contains all eventhandlers for inventory releated events.
 * 
 * @author LeonTG77
 */
public class ConfigListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		Game game = Game.getInstance();
		
		Inventory inv = event.getInventory();
		ItemStack item = event.getCurrentItem();
		
		if (!inv.getTitle().equals("§8» §cGame config §8«")) {
			event.setCancelled(true);
			return;
		}
		
		game.absorption();
		game.antiStripmine();
		game.deathLightning();
		game.ghastDropGold();
		game.goldenHeads();
		game.hardcoreHearts();
		game.heartsOnTab();
		game.horseArmor();
		game.horseHealing();
		game.horses();
		game.goldenMelonNeedsIngots();
		game.nerfedStrength();
		game.nether();
		game.strength();
		game.notchApples();
		game.shears();
		game.splash();
		game.tabShowsHealthColor();
		game.theEnd();
		game.tier2();
		game.pearlDamage();
	}
}