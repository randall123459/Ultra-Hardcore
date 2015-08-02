package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.util.DateUtils;
import com.leontg77.uhc.util.NumberUtils;
import com.leontg77.uhc.util.PlayerUtils;

/**
 * The inventory managing class.
 * @author LeonTG77
 */
public class InvGUI {
	private InvGUI() {}
	private static InvGUI manager = new InvGUI();
	public static InvGUI getManager() {
		return manager;
	}
	
	/**
	 * Opens an inventory of all the online players that is playing.
	 * @param player the player opening for.
	 */
	public void openSelector(Player player) {
		Inventory inv = Bukkit.createInventory(null, PlayerUtils.playerInvSize(), "Player Selector");
	
		for (Player online : PlayerUtils.getPlayers()) {
			if (!Main.spectating.contains(online.getName())) {
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName(ChatColor.GREEN + online.getName());
				meta.setOwner(online.getName());
				meta.setLore(Arrays.asList(ChatColor.GRAY + "Click to teleport to " + online.getName() + "."));
				item.setItemMeta(meta);
				inv.addItem(item);
			}	
		}
		player.openInventory(inv);
	}
	
	/**
	 * Opens the inventory of a target player.
	 * @param player player to open for.
	 * @param target the players inv to use.
	 */
	public void openInv(Player player, final Player target) {
		final Inventory inv = Bukkit.getServer().createInventory(target, 45, "Player Inventory");
	
		Main.invsee.put(inv, new BukkitRunnable() {
			public void run() {
				if (inv.getItem(0) != target.getInventory().getHelmet()) {
					inv.setItem(0, target.getInventory().getHelmet());
				}

				if (inv.getItem(1) != target.getInventory().getChestplate()) {
					inv.setItem(1, target.getInventory().getChestplate());
				}

				if (inv.getItem(2) != target.getInventory().getLeggings()) {
					inv.setItem(2, target.getInventory().getLeggings());
				}

				if (inv.getItem(3) != target.getInventory().getBoots()) {
					inv.setItem(3, target.getInventory().getBoots());
				}
				
				ItemStack info = new ItemStack (Material.BOOK);
				ItemMeta infoMeta = info.getItemMeta();
				infoMeta.setDisplayName("§4Player information");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§aName: §7" + target.getName());
				lore.add(" ");
				int health = (int) target.getHealth();
				lore.add("§aHearts: §7" + (((double) health) / 2) + "§4♥");
				lore.add("§a% Health: §7" + NumberUtils.makePercent(target.getHealth()) + "%");
				lore.add("§aHunger: §7" + (target.getFoodLevel() / 2));
				lore.add("§aXp level: §7" + target.getLevel());
				lore.add("§aLocation: §7" + target.getWorld().getEnvironment().name().replaceAll("_", "").toLowerCase().replaceAll("normal", "overworld") + ", x:" + target.getLocation().getBlockX() + ", y:" + target.getLocation().getBlockY() + ", z:" + target.getLocation().getBlockZ());
				lore.add(" ");
				lore.add("§cPotion effects:");
				if (target.getActivePotionEffects().size() == 0) {
					lore.add(ChatColor.GRAY + "None");
				}
				for (PotionEffect l : target.getActivePotionEffects()) {
					lore.add(ChatColor.GRAY + l.getType().getName().substring(0, 1).toUpperCase() + l.getType().getName().substring(1).toLowerCase().replaceAll("_", "") + " §atier: §7" + (l.getAmplifier() + 1) + " §aLength: §7" + DateUtils.ticksToString(l.getDuration() / 20));
				}
				infoMeta.setLore(lore);
				info.setItemMeta(infoMeta);
				inv.setItem(8, info);
				lore.clear();
				
				int i = 9;
				for (ItemStack item : target.getInventory().getContents()) {
					if (inv.getItem(i) != item) {
						inv.setItem(i, item);
					}
					i++;
				}
			}
		});
		Main.invsee.get(inv).runTaskTimer(Main.plugin, 1, 1);
		
		player.openInventory(inv);
	}

	public void openRules(Player player) {
		
	}
}