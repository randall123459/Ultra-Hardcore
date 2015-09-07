package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import com.leontg77.uhc.scenario.Scenario;

public class Krenzinator extends Scenario implements Listener {
    private final ShapelessRecipe diamond = new ShapelessRecipe(new ItemStack(Material.DIAMOND)).addIngredient(9, Material.REDSTONE_BLOCK);
	private boolean enabled = false;

	public Krenzinator() {
		super("Krenzinator", "Play UHC like Krenzinator does");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			Bukkit.addRecipe(diamond);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		if (isEnabled()) {
			return;
		}
		
		ItemStack item = event.getRecipe().getResult();
		
		if (item.getType() == Material.DIAMOND) {
			if (event.getRecipe() instanceof ShapelessRecipe) {
				if (((ShapelessRecipe) event.getRecipe()).getIngredientList().contains(new ItemStack(Material.REDSTONE_BLOCK))) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}

		Player victim = event.getEntity();

		if (victim.getUniqueId().toString().equals("f6eb67da-99f1-4352-b5c5-c0440be575f1") || victim.getUniqueId().toString().equals("42d908a4-c270-4059-b796-53d217f9429f")) {
			ItemStack diamonds = new ItemStack(Material.DIAMOND, 10);

			event.getDrops().add(diamonds);
		}
	}
	
	@EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
		if (!isEnabled()) {
			return;
		}

        if (((event.getVehicle() instanceof Horse)) && ((event.getEntered() instanceof Player))) {
            
            Horse horse = (Horse) event.getVehicle();
            
            if (horse.getVariant().equals(Horse.Variant.DONKEY)) {
                return;
            }

            ((Player)event.getEntered()).sendMessage(ChatColor.RED + "You can't mount horses in this gamemode!");
            event.setCancelled(true);
        }
    }
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.getDamager().getType().equals(EntityType.EGG)) {
			event.setDamage(1.0);
		}  
	}
	
	@EventHandler
    public void onCraft(CraftItemEvent event)  {
        if (!isEnabled() || event.isCancelled()) {
            return;
        }
        
        if (event.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
            event.getWhoClicked().damage(2.0);
        }
    }
}