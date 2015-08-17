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
 * <p>
 * This class contains methods for enabling/disabling spec mode, toggling spec mode and managing vanishing.
 * 
 * @author LeonTG77
 */
public class Spectator {
	private static Spectator manager = new Spectator();
	
	/**
	 * Gets the instance of the class.
	 * @return the instance.
	 */
	public static Spectator getManager() {
		return manager;
	}
	
	/**
	 * Enable or disable the spectator mode for the given player.
	 * 
	 * @param player the player setting for.
	 * @param enable True to enable spec mode, false to disable.
	 * @param command True if the mode was set by a command.
	 */
	public void set(Player player, boolean enable, boolean command) {
		if (enable) {
			if (command) {
				if (Main.spectating.contains(player.getName())) {
					player.sendMessage(Main.prefix() + "You are already spectating.");
					return;
				}

				player.sendMessage(Main.prefix() + "You are now spectating, Don't spoil ANYTHING.");
			} else {
				player.sendMessage(Main.prefix() + "Spectator mode enabled.");
			}
			
			ItemStack compass = new ItemStack (Material.COMPASS);
			ItemMeta compassMeta = compass.getItemMeta();
			compassMeta.setDisplayName(ChatColor.GREEN + "Teleporter");
			compassMeta.setLore(Arrays.asList(ChatColor.GRAY + "Left click to teleport to a random player.", ChatColor.GRAY + "Right click to open a player teleporter."));
			compass.setItemMeta(compassMeta);
			
			ItemStack vision = new ItemStack (Material.INK_SACK, 1, (short) 12);
			ItemMeta visionMeta = vision.getItemMeta();
			visionMeta.setDisplayName(ChatColor.GREEN + "Toggle Night Vision");
			visionMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to toggle the night vision effect."));
			vision.setItemMeta(visionMeta);
			
			ItemStack nether = new ItemStack (Material.INK_SACK, 1, (short) 12);
			ItemMeta netherMeta = nether.getItemMeta();
			netherMeta.setDisplayName(ChatColor.GREEN + "Players in the nether");
			netherMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to toggle get a list of players in the nether."));
			nether.setItemMeta(netherMeta);
			
			ItemStack tp = new ItemStack (Material.FEATHER);
			ItemMeta tpMeta = tp.getItemMeta();
			tpMeta.setDisplayName(ChatColor.GREEN + "Teleport to 0,0");
			tpMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to teleport to 0,0."));
			tp.setItemMeta(tpMeta);
			
			player.getInventory().remove(compass);
			player.getInventory().remove(vision);
			player.getInventory().remove(nether);
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
			
			if (player.getExp() > 0) {
				ExperienceOrb exp = player.getWorld().spawn(player.getLocation().getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
				exp.setExperience((int) player.getExp());
				exp.setVelocity(new Vector(0, 0.2, 0));
			}
			
			player.setGameMode(GameMode.SPECTATOR);
			player.setWalkSpeed((float) 0.2);
			player.setFlySpeed((float) 0.1);
			player.setAllowFlight(true);
			player.setFlying(true);
			
			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			
			Teams.getManager().joinTeam("spec", player);
			
			if (!Main.spectating.contains(player.getName())) {
				Main.spectating.add(player.getName());
			}
			
			player.getInventory().setItem(1, tp);
			player.getInventory().setItem(3, compass);
			player.getInventory().setItem(5, nether);
			player.getInventory().setItem(7, vision);
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (!Main.spectating.contains(online.getName())) {
					online.hidePlayer(player);
				}
				player.showPlayer(online);
			}
		} else {
			if (command) {
				if (!Main.spectating.contains(player.getName())) {
					player.sendMessage(Main.prefix() + "You are not spectating.");
					return;
				}

				player.sendMessage(Main.prefix() + "You are no longer spectating.");
			} else {
				player.sendMessage(Main.prefix() + "Spectator mode disabled.");
			}
			
			player.setGameMode(GameMode.SURVIVAL);
			player.setWalkSpeed((float) 0.2);
			player.setFlySpeed((float) 0.1);
			player.setAllowFlight(false);
			player.setFlying(false);

			Teams.getManager().leaveTeam(player);
			
			if (Main.spectating.contains(player.getName())) {
				Main.spectating.remove(player.getName());
			}
			
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			
			for (Player online : PlayerUtils.getPlayers()) {
				if (Main.spectating.contains(online.getName())) {
					player.hidePlayer(online);
				} else {
					player.showPlayer(online);
				}
				online.showPlayer(player);
				manageVanish(online);
			}
		}
	}
	
	/**
	 * Toggles the given player's spectator mode.
	 * 
	 * @param player the player toggling for.
	 */
	public void toggle(Player player, boolean command) {
		if (Main.spectating.contains(player.getName())) {
			set(player, false, command);
		} else {
			set(player, true, command);
		}
	}
	
	/**
	 * Hides all spectators from non spectators.
	 * 
	 * @param player The player not spectating.
	 */
	public void manageVanish(Player player) {
		for (Player online : PlayerUtils.getPlayers()) {
			if (Main.spectating.contains(online.getName()) && !Main.spectating.contains(player.getName())) {
				player.hidePlayer(online);
			}
		}
	}
}