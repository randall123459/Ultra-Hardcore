package com.leontg77.uhc.listeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;

/**
 * Inventory listener class.
 * <p> 
 * Contains all eventhandlers for inventory releated events.
 * 
 * @author ghowden
 */
public class InventoryListener implements Listener {
    protected static final Set<Material> DISABLED = ImmutableSet.of(Material.IRON_BARDING, Material.GOLD_BARDING, Material.DIAMOND_BARDING);
	private Game game = Game.getInstance();
	
    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
    	InventoryView view = event.getView();
    	
        if (view.getTopInventory() instanceof HorseInventory) {
        	if (game.horseArmor()) {
            	return;
            }
        	
            if (!DISABLED.contains(event.getOldCursor().getType())) {
            	return;
            }

            event.getWhoClicked().sendMessage(Main.PREFIX + "Horse armor is disabled.");
            event.setCancelled(true);
        }
        
        if (event.getInventory().getType() != InventoryType.BREWING) {
            if (event.getRawSlots().contains(3)) {
            	if (!game.tier2() && event.getOldCursor().getType() == Material.GLOWSTONE_DUST) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Tier 2 potions are disabled.");
                    event.setCancelled(true);
                }
                
                if (!game.splash() && event.getOldCursor().getType() == Material.SULPHUR) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Splash potions are disabled.");
                    event.setCancelled(true);
                }
                
                if (game.ghastDropGold() && event.getOldCursor().getType() == Material.GHAST_TEAR) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Regen potions are disabled.");
                    event.setCursor(new ItemStack(Material.GOLD_INGOT, event.getOldCursor().getAmount()));
                    event.setCancelled(true);
                }
                
                if (!game.strength() && event.getOldCursor().getType() == Material.BLAZE_POWDER) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Strength is disabled.");
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (event.getDestination().getType() != InventoryType.BREWING) {
        	return;
        }

        if (!game.tier2() && event.getItem().getType() == Material.GLOWSTONE_DUST) {
            event.setCancelled(true);
        }
        
        if (!game.splash() && event.getItem().getType() == Material.SULPHUR) {
            event.setCancelled(true);
        }
        
        if (game.ghastDropGold() && event.getItem().getType() == Material.GHAST_TEAR) {
            event.setCancelled(true);
        }
        
        if (!game.strength() && event.getItem().getType() == Material.BLAZE_POWDER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
    	InventoryView view = event.getView();
    	
        if (view.getTopInventory() instanceof HorseInventory) {
        	if (game.horseArmor()) {
        		return;
        	}
        	
        	if (event.getClickedInventory() == null) {
            	return;
            }

            InventoryType clicked = event.getClickedInventory().getType();

            Optional<ItemStack> relevant = Optional.absent();
            switch (event.getAction()) {
                case MOVE_TO_OTHER_INVENTORY:
                    if (clicked == InventoryType.PLAYER) {
                        relevant = Optional.fromNullable(event.getCurrentItem());
                    }
                    break;
                case PLACE_ALL:
                case PLACE_SOME:
                case PLACE_ONE:
                case SWAP_WITH_CURSOR:
                    if (clicked != InventoryType.PLAYER) {
                        relevant = Optional.fromNullable(event.getCursor());
                    }
                    break;
                case HOTBAR_SWAP:
                    if (clicked != InventoryType.PLAYER) {
                        relevant = Optional.fromNullable(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
                    }
                    break;
                default:
                	break;
            }

            if (relevant.isPresent() && DISABLED.contains(relevant.get().getType())) {
                event.getWhoClicked().sendMessage(Main.PREFIX + "Horse armor is disabled.");
                event.setCancelled(true);
            }
        }
        
        if (event.getInventory().getType() == InventoryType.BREWING) {
            if (event.getClickedInventory() == null) {
            	return;
            }

            InventoryType clicked = event.getClickedInventory().getType();

            Optional<ItemStack> relevant = Optional.absent();
            switch (event.getAction()) {
                case MOVE_TO_OTHER_INVENTORY:
                    if (clicked == InventoryType.PLAYER) {
                        relevant = Optional.fromNullable(event.getCurrentItem());
                    }
                    break;
                case PLACE_ALL:
                case PLACE_SOME:
                case PLACE_ONE:
                case SWAP_WITH_CURSOR:
                    if (clicked == InventoryType.BREWING) {
                        relevant = Optional.fromNullable(event.getCursor());
                    }
                    break;
                case HOTBAR_SWAP:
                    if (clicked == InventoryType.BREWING) {
                        relevant = Optional.fromNullable(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
                    }
                    break;
                default:
                    break;
            }

            if (relevant.isPresent()) {
                if (!game.tier2() && relevant.get().getType() == Material.GLOWSTONE_DUST) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Tier 2 potions are disabled.");
                    event.setCancelled(true);
                }
                
                if (!game.splash() && relevant.get().getType() == Material.SULPHUR) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Splash potions are disabled.");
                    event.setCancelled(true);
                }
                
                if (game.ghastDropGold() && relevant.get().getType() == Material.GHAST_TEAR) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Regen potions are disabled.");
                    event.setCurrentItem(new ItemStack(Material.GOLD_INGOT, relevant.get().getAmount()));
                    event.setCancelled(true);
                }
                
                if (!game.strength() && relevant.get().getType() == Material.BLAZE_POWDER) {
                    event.getWhoClicked().sendMessage(Main.PREFIX + "Strength is disabled.");
                    event.setCancelled(true);
                }
            }
        }
    }
}