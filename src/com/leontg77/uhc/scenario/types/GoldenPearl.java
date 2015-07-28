package com.leontg77.uhc.scenario.types;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

public class GoldenPearl extends Scenario implements Listener {
	private FixedMetadataValue m = new FixedMetadataValue(Main.plugin, "GP");
	private boolean enabled = false;

	public GoldenPearl() {
		super("GoldenPearl", "You can craft a goldenpearl with gold ingots around a pearl, the golden one deals no damage but the normal one does.");
		addRecipe();
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	private void addRecipe() {
        ItemStack head = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD  + "Golden Pearl");
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "This is a magic pearl that won't hurt you."));
        head.setItemMeta(meta); 
        
        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', Material.ENDER_PEARL);
        Bukkit.getServer().addRecipe(goldenhead);
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		if (isEnabled()) {
			return;
		}
		
		ItemStack item = event.getRecipe().getResult();
		
		if (item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName() == "§6Golden Pearl") {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.getEntity() instanceof EnderPearl) {
			EnderPearl pearl = (EnderPearl) event.getEntity();
			
			if (pearl.getShooter() instanceof Player) {
				Player shooter = (Player) pearl.getShooter();
				
				if (shooter.getItemInHand().hasItemMeta() && shooter.getItemInHand().getItemMeta().hasDisplayName() && shooter.getItemInHand().getItemMeta().getDisplayName().equals("§6Golden Pearl")) {
					pearl.setMetadata("Label", m);
				}
			}
			
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.getDamager() instanceof EnderPearl) {
			EnderPearl pearl = (EnderPearl) event.getDamager();
			
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				
				if (pearl.getShooter() instanceof Player && player == pearl.getShooter()) {
					if (pearl.hasMetadata("Label")) {
					    if (pearl.getMetadata("Label").contains(m) || pearl.getMetadata("Label").equals(m)) {
					    	event.setCancelled(true);
						}
					}
				}
			}
		}
	}
}