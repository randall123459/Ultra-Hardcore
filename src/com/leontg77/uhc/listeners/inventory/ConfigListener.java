package com.leontg77.uhc.listeners.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Config inventory listener class.
 * <p> 
 * Contains all eventhandlers for config inventory releated events.
 * 
 * @author LeonTG77
 */
public class ConfigListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
        Scoreboards board = Scoreboards.getInstance();
		Game game = Game.getInstance();
		
		Inventory inv = event.getInventory();
		ItemStack item = event.getCurrentItem();
		
		if (!inv.getTitle().equals("» §7Game config")) {
			return;
		}
		
		event.setCancelled(true);
		
		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}
		
		String name = item.getItemMeta().getDisplayName().substring(2);
		
		if (name.equalsIgnoreCase("Absorption")) {
			if (game.absorption()) {
				PlayerUtils.broadcast(Main.PREFIX + "Absorption has been disabled.");
				game.setAbsorption(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Absorption has been enabled.");
				game.setAbsorption(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Golden Heads")) {
			if (game.goldenHeads()) {
				PlayerUtils.broadcast(Main.PREFIX + "Golden Heads has been disabled.");
				game.setGoldenHeads(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Golden Heads has been enabled.");
				game.setGoldenHeads(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Pearl Damage")) {
			if (game.pearlDamage()) {
				PlayerUtils.broadcast(Main.PREFIX + "Pearl Damage has been disabled.");
				game.setPearlDamage(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Pearl Damage has been enabled.");
				game.setPearlDamage(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Notch Apples")) {
			if (game.notchApples()) {
				PlayerUtils.broadcast(Main.PREFIX + "Notch Apples has been disabled.");
				game.setNotchApples(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Notch Apples has been enabled.");
				game.setNotchApples(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Hearts on tab")) {
			if (game.heartsOnTab()) {
				PlayerUtils.broadcast(Main.PREFIX + "Tab will now show % health.");
				game.setHeartsOnTab(false);
				
				board.tabHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Tab will now show hearts.");
				game.setHeartsOnTab(true);
				
				board.hearts.setDisplaySlot(DisplaySlot.PLAYER_LIST);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Hardcore Hearts")) {
			if (game.hardcoreHearts()) {
				PlayerUtils.broadcast(Main.PREFIX + "Hardcore Hearts has been disabled.");
				game.setHardcoreHearts(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Hardcore Hearts has been enabled.");
				game.setHardcoreHearts(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Tab health color")) {
			if (game.tabShowsHealthColor()) {
				PlayerUtils.broadcast(Main.PREFIX + "The tab list will no longer have the color of your health.");
				game.setTabShowsHealthColor(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "The tab list will now have the color of your health.");
				game.setTabShowsHealthColor(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Recorded Round")) {
			if (game.isRecordedRound()) {
				PlayerUtils.broadcast(Main.PREFIX + "The server is no longer in recorded round mode.");
				game.setRecordedRound(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "The server is now in recorded round mode.");
				game.setRecordedRound(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Nether")) {
			if (game.nether()) {
				PlayerUtils.broadcast(Main.PREFIX + "Nether has been disabled.");
				game.setNether(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Nether has been enabled.");
				game.setNether(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("The End")) {
			if (game.theEnd()) {
				PlayerUtils.broadcast(Main.PREFIX + "The End has been disabled.");
				game.setTheEnd(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "The End has been enabled.");
				game.setTheEnd(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Anti Stripmine")) {
			if (game.antiStripmine()) {
				PlayerUtils.broadcast(Main.PREFIX + "Stripmining is no longer allowed.");
				game.setAntiStripmine(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Stripmining is now allowed.");
				game.setAntiStripmine(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Death Lightning")) {
			if (game.deathLightning()) {
				PlayerUtils.broadcast(Main.PREFIX + "Death Lightning has been disabled.");
				game.setDeathLightning(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Death Lightning has been enabled.");
				game.setDeathLightning(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Horses")) {
			if (game.horses()) {
				PlayerUtils.broadcast(Main.PREFIX + "Horses has been disabled.");
				game.setHorses(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Horses has been enabled.");
				game.setHorses(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Horse Armor")) {
			if (game.horseArmor()) {
				PlayerUtils.broadcast(Main.PREFIX + "Horse Armor has been disabled.");
				game.setHorseArmor(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Horse Armor has been enabled.");
				game.setHorseArmor(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Horse Healing")) {
			if (game.horseHealing()) {
				PlayerUtils.broadcast(Main.PREFIX + "Horse Healing has been disabled.");
				game.setHorseHealing(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Horse Healing has been enabled.");
				game.setHorseHealing(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Shears")) {
			if (game.shears()) {
				PlayerUtils.broadcast(Main.PREFIX + "Shears has been disabled.");
				game.setShears(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Shears has been enabled.");
				game.setShears(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Ghast drop gold")) {
			if (game.ghastDropGold()) {
				PlayerUtils.broadcast(Main.PREFIX + "Ghasts will now drop ghast tears.");
				game.setGhastDropGold(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Ghasts will now drop gold ingots.");
				game.setGhastDropGold(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Golden Melon needs ingots")) {
			if (game.goldenMelonNeedsIngots()) {
				PlayerUtils.broadcast(Main.PREFIX + "Golden Melons now require nuggets.");
				game.setGoldenMelonNeedsIngots(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Golden Melons now require ingots.");
				game.setGoldenMelonNeedsIngots(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Tier 2")) {
			if (game.tier2()) {
				PlayerUtils.broadcast(Main.PREFIX + "Tier 2 has been disabled.");
				game.setTier2(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Tier 2 has been enabled.");
				game.setTier2(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Splash")) {
			if (game.splash()) {
				PlayerUtils.broadcast(Main.PREFIX + "Splash has been disabled.");
				game.setSplash(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Splash has been enabled.");
				game.setSplash(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Strength")) {
			if (game.strength()) {
				PlayerUtils.broadcast(Main.PREFIX + "Strength has been disabled.");
				game.setStrength(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Strength has been enabled.");
				game.setStrength(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}
		
		if (name.equalsIgnoreCase("Nerfed Strength")) {
			if (game.nerfedStrength()) {
				PlayerUtils.broadcast(Main.PREFIX + "Strength is no longer nerfed.");
				game.setNerfedStrength(false);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Strength is now nerfed.");
				game.setNerfedStrength(true);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
		}
	}
}