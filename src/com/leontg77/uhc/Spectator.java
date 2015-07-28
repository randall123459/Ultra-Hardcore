package com.leontg77.uhc;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.leontg77.uhc.util.PlayerUtils;

/**
 * The spectator class to manage spectating.
 * @author LeonTG77
 */
public class Spectator {
	private static Spectator manager = new Spectator();
	private Spectator() {}
	
	/**
	 * Gets the instance of the class.
	 * @return the instance.
	 */
	public static Spectator getManager() {
		return manager;
	}
	
	/**
	 * Manage the players spectator mode.
	 * @param player the player you're setting for.
	 * @param enable true or false if what you want for spectating.
	 */
	public void set(Player player, boolean enable) {
		if (enable) {
			if (Main.spectating.contains(player.getName())) {
				player.sendMessage(Main.prefix() + "You are already spectating.");
				return;
			}

			player.sendMessage(Main.prefix() + "You are now spectating, Don't spoil ANYTHING.");
			
			ItemStack compass = new ItemStack (Material.COMPASS);
			ItemMeta compassMeta = compass.getItemMeta();
			compassMeta.setDisplayName(ChatColor.GREEN + "Teleporter");
			compassMeta.setLore(Arrays.asList(ChatColor.GRAY + "Left click to teleport to a random player.", ChatColor.GRAY + "Right click to open a player teleporter."));
			compass.setItemMeta(compassMeta);
			
			ItemStack night = new ItemStack (Material.INK_SACK, 1, (short) 12);
			ItemMeta nightMeta = night.getItemMeta();
			nightMeta.setDisplayName(ChatColor.GREEN + "Toggle Night Vision");
			nightMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right click to toggle the night vision effect."));
			night.setItemMeta(nightMeta);
			
			ItemStack tp = new ItemStack (Material.FEATHER);
			ItemMeta tpMeta = tp.getItemMeta();
			tpMeta.setDisplayName(ChatColor.GREEN + "Teleport to 0,0");
			tpMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right click to telepor to 0,0."));
			tp.setItemMeta(tpMeta);
			
			player.getInventory().remove(compass);
			player.getInventory().remove(night);
			player.getInventory().remove(tp);
			
			for (ItemStack content : player.getInventory().getContents()) {
				if (content != null) {
					Item item = player.getWorld().dropItem(player.getLocation().getBlock().getLocation().add(0.5, 0.7, 0.5), content);
					item.setVelocity(new Vector(0, 0.2, 0));
				}
			}

			for (ItemStack armorContent : player.getInventory().getArmorContents()) {
				if (armorContent != null && armorContent.getType() != Material.AIR) {
					Item item = player.getWorld().dropItem(player.getLocation().getBlock().getLocation().add(0.5, 0.7, 0.5), armorContent);
					item.setVelocity(new Vector(0, 0.2, 0));
				}
			}
			
			ExperienceOrb exp = player.getWorld().spawn(player.getLocation().getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
			exp.setExperience((int) player.getExp());
			exp.setVelocity(new Vector(0, 0.2, 0));
			
			player.setGameMode(GameMode.SPECTATOR);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setFlySpeed((float) 0.1);
			
			Main.spectating.add(player.getName());
			
			Teams.getManager().joinTeam("spec", player);
			
			player.getInventory().setItem(1, tp);
			player.getInventory().setItem(4, compass);
			player.getInventory().setItem(7, night);
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (!Main.spectating.contains(online.getName())) {
					online.hidePlayer(player);
				}
				player.showPlayer(online);
			}
		} else {
			if (!Main.spectating.contains(player.getName())) {
				player.sendMessage(Main.prefix() + "You are not spectating.");
				return;
			}
			
			player.sendMessage(Main.prefix() + "You are no longer spectating.");
			player.setGameMode(GameMode.SURVIVAL);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setFlySpeed((float) 0.1);
			
			if (Teams.getManager().getTeam(player) != null) {
				Teams.getManager().leaveTeam(player);
			}
			
			Main.spectating.remove(player.getName());
			
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			player.getInventory().clear();
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (!Main.spectating.contains(online.getName())) {
					player.showPlayer(online);
				} else {
					player.hidePlayer(online);
				}
				online.showPlayer(player);
				hideAll(online);
			}
		}
	}
	
	/**
	 * Toggles the players spectator mode.
	 * @param player the player toggling.
	 */
	public void toggle(Player player) {
		if (Main.spectating.contains(player.getName())) {
			this.set(player, false);
		} else {
			this.set(player, true);
		}
	}
	
	/**
	 * Hides all spectators from non spectators.
	 * @param player The players not spectating.
	 */
	public void hideAll(Player player) {
		for (Player online : PlayerUtils.getPlayers()) {
			if (Main.spectating.contains(online.getName()) && !Main.spectating.contains(player.getName())) {
				player.hidePlayer(online);
			}
		}
	}
}