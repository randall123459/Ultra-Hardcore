package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * EnchantParanoia scenario class
 * 
 * @author LeonTG77
 */
public class EnchantParanoia extends Scenario implements Listener {
	private boolean enabled = false;
	private BukkitRunnable task;
	
	public EnchantParanoia() {
		super("EnchantParanoia", "You cannot see what enchants you have");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			this.task = new BukkitRunnable() {
				public void run() {
					for (Player online : PlayerUtils.getPlayers()) {
						for (ItemStack contents : online.getOpenInventory().getTopInventory().getContents()) {
							if (contents == null) {
								continue;
							}
							
							if (!contents.getEnchantments().isEmpty()) {
								ItemMeta meta = contents.getItemMeta();
							
								ArrayList<String> lore = new ArrayList<String>();
								
								for (int i = 0; i < contents.getEnchantments().size(); i++) {
									lore.add("§7Who knows...?");
								}
								
								meta.setLore(lore);
								meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								contents.setItemMeta(meta);
							}
						}
						
						for (ItemStack contents : online.getOpenInventory().getBottomInventory().getContents()) {
							if (contents == null) {
								continue;
							}
							
							if (!contents.getEnchantments().isEmpty()) {
								ItemMeta meta = contents.getItemMeta();
							
								ArrayList<String> lore = new ArrayList<String>();
								
								for (int i = 0; i < contents.getEnchantments().size(); i++) {
									lore.add("§7Who knows...?");
								}
								
								meta.setLore(lore);
								meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								contents.setItemMeta(meta);
							}
						}
						
						for (ItemStack contents : online.getInventory().getContents()) {
							if (contents == null) {
								continue;
							}
							
							if (!contents.getEnchantments().isEmpty()) {
								ItemMeta meta = contents.getItemMeta();
							
								ArrayList<String> lore = new ArrayList<String>();
								
								for (int i = 0; i < contents.getEnchantments().size(); i++) {
									lore.add("§7Who knows...?");
								}
								
								meta.setLore(lore);
								meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								contents.setItemMeta(meta);
							}
						}
					}
				}
			};
			
			task.runTaskTimer(Main.plugin, 20, 20);
		} else {
			task.cancel();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		ItemStack item = event.getItem();
		ItemMeta meta = item.getItemMeta();
	
		ArrayList<String> lore = new ArrayList<String>();
		
		for (int i = 0; i < event.getEnchantsToAdd().size(); i++) {
			lore.add("§7Who knows...?");
		}
		
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
	}
}