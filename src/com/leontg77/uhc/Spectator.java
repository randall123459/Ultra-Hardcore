package com.leontg77.uhc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * The spectator class to manage spectating.
 * <p>
 * This class contains methods for enabling/disabling spec mode, toggling spec mode and managing vanishing.
 * 
 * @author LeonTG77
 */
public class Spectator {
	private static Spectator manager = new Spectator();
	private Settings settings = Settings.getInstance();
	
	public HashSet<String> spectators = new HashSet<String>();
	public HashSet<String> specinfo = new HashSet<String>();
	public HashSet<String> cmdspy = new HashSet<String>();
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Spectator getManager() {
		return manager;
	}
	
	/**
	 * Enable spectator mode for the given player.
	 * 
	 * @param player the player enabling for.
	 * @param force force the enabling.
	 */
	public void enableSpecmode(Player player, boolean force) {
		if (force) {
			player.sendMessage(Main.prefix() + "You are now in spectator mode, do not spoil.");
		} else {
			if (isSpectating(player)) {
				player.sendMessage(Main.prefix() + "Your spectator mode is already enabled.");
				return;
			}

			player.sendMessage(Main.prefix() + "You are now in spectator mode, do not spoil.");
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
		
		ItemStack nether = new ItemStack (Material.LAVA_BUCKET, 1);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName(ChatColor.GREEN + "Players in the nether");
		netherMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to get a list of players in the nether."));
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
		
		if (player.getTotalExperience() > 0) {
			ExperienceOrb exp = player.getWorld().spawn(player.getLocation().getBlock().getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
			exp.setExperience(player.getTotalExperience());
			exp.setVelocity(new Vector(0, 0.2, 0));
		}
		
		player.getInventory().setArmorContents(null);
		player.getInventory().clear();
		
		player.setGameMode(GameMode.SPECTATOR);
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		
		Teams.getManager().joinTeam("spec", player);
		
		if (!spectators.contains(player.getName())) {
			spectators.add(player.getName());
		}
		
		player.getInventory().setItem(1, tp);
		player.getInventory().setItem(3, compass);
		player.getInventory().setItem(5, nether);
		player.getInventory().setItem(7, vision);
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (!isSpectating(online)) {
				online.hidePlayer(player);
			}
			
			player.showPlayer(online);
		}
	}
	
	/**
	 * Disable spectator mode for the given player.
	 * 
	 * @param player the player disabling for.
	 * @param force force the disabling.
	 */
	public void disableSpecmode(Player player, boolean force) {
		if (force) {
			player.sendMessage(Main.prefix() + "You are no longer in spectator mode.");
		} else {
			if (!isSpectating(player)) {
				player.sendMessage(Main.prefix() + "Your spectator mode is not enabled.");
				return;
			}

			player.sendMessage(Main.prefix() + "You are no longer in spectator mode.");
		}
		
		player.setGameMode(GameMode.SURVIVAL);
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);

		Teams.getManager().leaveTeam(player);
		
		if (spectators.contains(player.getName())) {
			spectators.remove(player.getName());
		}
		
		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		player.getInventory().setArmorContents(null);
		player.getInventory().clear();
		
		World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
		double x = settings.getData().getDouble("spawn.x");
		double y = settings.getData().getDouble("spawn.y");
		double z = settings.getData().getDouble("spawn.z");
		float yaw = (float) settings.getData().getDouble("spawn.yaw");
		float pitch = (float) settings.getData().getDouble("spawn.pitch");
		
		Location loc = new Location(w, x, y, z, yaw, pitch);
		player.teleport(loc);
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (isSpectating(online)) {
				player.hidePlayer(online);
			} else {
				player.showPlayer(online);
			}
			
			online.showPlayer(player);
		}
	}
	
	/**
	 * Toggles the given player's spectator mode.
	 * 
	 * @param player the player toggling for.
	 */
	public void toggle(Player player, boolean force) {
		if (isSpectating(player)) {
			enableSpecmode(player, force);
		} else {
			disableSpecmode(player, force);
		}
	}
	
	/**
	 * Check whether the given player is spectating or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player is speccing, <code>false</code> otherwise.
	 */
	public boolean isSpectating(Player player) {
		return spectators.contains(player.getName());
	}
	
	/**
	 * Check whether the given string is in the spectator list.
	 * 
	 * @param entry the string cheking.
	 * @return <code>true</code> if the string is in the spectator list, <code>false</code> otherwise.
	 */
	public boolean isSpectating(String entry) {
		if (entry.equals("CONSOLE")) {
			return true;
		}
		
		return spectators.contains(entry);
	}

	/**
	 * Hides all the spectators for the given player.
	 * 
	 * @param player the player.
	 */
	public void hideSpectators(Player player) {
		for (Player online : PlayerUtils.getPlayers()) {
			if (isSpectating(online)) {
				player.hidePlayer(online);
			} else {
				player.showPlayer(online);
			}
			
			online.showPlayer(player);
		}
	}
	
	/**
	 * Check whether the given player has specinfo or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player has specinfo, <code>false</code> otherwise.
	 */
	public boolean hasSpecInfo(Player player) {
		if (!spectators.contains(player.getName())) {
			return false;
		}
		
		if (specinfo.contains(player.getName())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Check whether the given player has cmdspy or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player has cmdspy, <code>false</code> otherwise.
	 */
	public boolean hasCommandSpy(Player player) {
		if (cmdspy.contains(player.getName())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * SpecInfo class for all the specinfo broadcasting.
	 * <p>
	 * Contains EventHandlers and Listeners for all info SpecInfo needs.
	 * 
	 * @author LeonTG77
	 */
	public static class SpecInfo implements Listener {
		public static HashMap<String, Integer> totalDiamonds = new HashMap<String, Integer>();
		public static HashMap<String, Integer> totalGold = new HashMap<String, Integer>();
		public static HashSet<Location> locs = new HashSet<Location>();
		
		public static String prefix() {
			return "§8[§9S§8] ";
		}

		@EventHandler
		public void onBlockBreak(BlockBreakEvent event) {
			Block block = event.getBlock();
			
			if (block.getType() == Material.GOLD_ORE) {
				if (locs.contains(block.getLocation())) {
					return;
				}

				Location loc = block.getLocation();
				Player player = event.getPlayer();
				int amount = 0;
				
				for (int x = loc.getBlockX() - 2; x <= loc.getBlockX() + 2; x++) {
					for (int y = loc.getBlockY() - 2; y <= loc.getBlockY() + 2; y++) {
						for (int z = loc.getBlockZ() - 2; z <= loc.getBlockZ() + 2; z++) {
							if (loc.getWorld().getBlockAt(x, y, z).getType() == Material.GOLD_ORE) {
								locs.add(loc.getWorld().getBlockAt(x, y, z).getLocation());
								amount++;
							}
						}
					}
				}
				
				if (totalGold.containsKey(player.getName())) {
					totalGold.put(player.getName(), totalGold.get(player.getName()) + amount);
				} else {
					totalGold.put(player.getName(), amount);
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§7" + player.getName() + "§f:§6GOLD §f[V:§6" + amount + "§f] [T:§6" + totalGold.get(player.getName()) + "§f]");
					}
				}
				return;
			}
			
			if (block.getType() == Material.DIAMOND_ORE) {
				if (locs.contains(block.getLocation())) {
					return;
				}

				Location loc = block.getLocation();
				Player player = event.getPlayer();
				int amount = 0;
				
				for (int x = loc.getBlockX() - 2; x <= loc.getBlockX() + 2; x++) {
					for (int y = loc.getBlockY() - 2; y <= loc.getBlockY() + 2; y++) {
						for (int z = loc.getBlockZ() - 2; z <= loc.getBlockZ() + 2; z++) {
							if (loc.getWorld().getBlockAt(x, y, z).getType() == Material.DIAMOND_ORE) {
								locs.add(loc.getWorld().getBlockAt(x, y, z).getLocation());
								amount++;
							}
						}
					}
				}
				
				if (totalDiamonds.containsKey(player.getName())) {
					totalDiamonds.put(player.getName(), totalDiamonds.get(player.getName()) + amount);
				} else {
					totalDiamonds.put(player.getName(), amount);
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§7" + player.getName() + "§f:§3DIAMOND §f[V:§3" + amount + "§f] [T:§3" + totalDiamonds.get(player.getName()) + "§f]");
					}
				}
			}
		}

		@EventHandler
		public void onBlockPlace(BlockPlaceEvent event) {
			Block block = event.getBlockPlaced();
			
			if (block.getType() == Material.GOLD_ORE) {
				locs.add(block.getLocation());
				return;
			}
			
			if (block.getType() == Material.DIAMOND_ORE) {
				locs.add(block.getLocation());
			}
		}

		@EventHandler
		public void onPlayerTeleport(PlayerTeleportEvent event) {
			if (event.getCause() == TeleportCause.ENDER_PEARL) {
				Player player = event.getPlayer();

				if (Spectator.getManager().isSpectating(player)) {
					return;
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§5Pearl: §a" + player.getName() + " §f<-> D:§d" + NumberUtils.convertDouble(event.getFrom().distance(event.getTo())) + "m.");
					}
				}
			}
		}

		@EventHandler
		public void onPlayerPortal(PlayerPortalEvent event) {
			Player player = event.getPlayer();

			if (Spectator.getManager().isSpectating(player)) {
				return;
			}

			for (Player online : PlayerUtils.getPlayers()) {
				if (Spectator.getManager().hasSpecInfo(online)) {
					online.sendMessage(prefix() + "§dPortal:§6" + player.getName() + "§f from §a" + NameUtils.fixString(event.getFrom().getWorld().getEnvironment().name(), true).replaceAll("Normal", "overworld").toLowerCase() + "§f to §c" + NameUtils.fixString(event.getTo().getWorld().getEnvironment().name(), true).replaceAll("Normal", "overworld").toLowerCase());
				}
			}
		}

		@EventHandler
		public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
			Player player = event.getPlayer();
			ItemStack item = event.getItem();
			
			if (Spectator.getManager().isSpectating(player)) {
				return;
			}
			
			if (event.getItem().getType() == Material.GOLDEN_APPLE) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§aHeal: §6" + player.getName() + "§f<->§6" + (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Golden Head") ? "§5Golden Head" : "Golden Apple"));
					}
				}
				return;
			}
			
			if (event.getItem().getType() == Material.POTION) {
				Potion pot;

				if (item.getDurability() == 8261) {
					pot = new Potion(PotionType.INSTANT_HEAL, 1);
				} else if (item.getDurability() == 16453) {
					pot = new Potion(PotionType.INSTANT_HEAL, 1);
				} else {
					try {
						pot = Potion.fromItemStack(item);
					} catch (Exception e) {
						return;
					}
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						for (PotionEffect e : pot.getEffects()) {
							online.sendMessage(prefix() + "§5Potion: §a" + player.getName() + "§f <-> P:§d" + NameUtils.getPotionName(e.getType()) + " §fT:§d" + pot.getLevel() + ((e.getDuration() / 20) > 0 ? " §fD:§d" + DateUtils.ticksToString(e.getDuration() / 20) : "") + " §fV:§dNormal");
						}
					}
				}
			}
		}
		
		@EventHandler
		public void onPotionSplash(PotionSplashEvent event) {
			if (event.getPotion().getShooter() instanceof Player) {
				Player player = (Player) event.getPotion().getShooter();
				ItemStack item = event.getPotion().getItem();
				Potion pot;

				if (item.getDurability() == 16453) {
					pot = new Potion(PotionType.INSTANT_HEAL, 1);
				} else if (item.getDurability() == 16421) {
					pot = new Potion(PotionType.INSTANT_HEAL, 2);
				} else {
					try {
						pot = Potion.fromItemStack(item);
					} catch (Exception e) {
						return;
					}
				}
				
				if (Spectator.getManager().isSpectating(player.getName())) {
					return;
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						for (PotionEffect e : pot.getEffects()) {
							online.sendMessage(prefix() + "§5Potion: §a" + player.getName() + "§f <-> P:§d" + NameUtils.getPotionName(e.getType()) + " §fT:§d" + pot.getLevel() + ((e.getDuration() / 20) > 0 ? " §fD:§d" + DateUtils.ticksToString(e.getDuration() / 20) : "") + " §fV:§dSplash");
						}
					}
				}
			}
		}

		@EventHandler
		public void onCraftItem(CraftItemEvent event) {
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getRecipe().getResult();

			if (Spectator.getManager().isSpectating(player.getName())) {
				return;
			}
			
			if (item.getType() == Material.GOLDEN_APPLE) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§6" + (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Golden Head") ? "§5Golden Head" : "Golden Apple"));
					}
				}
				return;
			}
			
			if (item.getType() == Material.DIAMOND_HELMET) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§bDia. Helmet");
					}
				}
				return;
			}
			
			if (item.getType() == Material.DIAMOND_CHESTPLATE) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§bDia. Chest");
					}
				}
				return;
			}
			
			if (item.getType() == Material.DIAMOND_LEGGINGS) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§bDia. Leggings");
					}
				}
				return;
			}
			
			if (item.getType() == Material.DIAMOND_BOOTS) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§bDia. Boots");
					}
				}
				return;
			}
			
			if (item.getType() == Material.DIAMOND_SWORD) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§bDia. Sword");
					}
				}
				return;
			}
			
			if (item.getType() == Material.BOW) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§dBow");
					}
				}
				return;
			}
			
			if (item.getType() == Material.ANVIL) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§dAnvil");
					}
				}
				return;
			}
			
			if (item.getType() == Material.ENCHANTMENT_TABLE) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§dEnchant. Table");
					}
				}
				return;
			}
			
			if (item.getType() == Material.BREWING_STAND_ITEM) {
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().hasSpecInfo(online)) {
						online.sendMessage(prefix() + "§2Craft§f: §a" + player.getName() + "§f<->§dBrewing Stand");
					}
				}
			}
		}
		
		@EventHandler(ignoreCancelled = true)
		public void onDamage(final EntityDamageEvent event) {
			if (event.isCancelled()) {
				return;
			}
			
			if (!(event.getEntity() instanceof Player)) {
				return;
			}

			final Player player = (Player) event.getEntity();
			
			if (Spectator.getManager().isSpectating(player.getName())) {
				return;
			}
			
			if (event instanceof EntityDamageByEntityEvent) {
				onDamageByOther(player, (EntityDamageByEntityEvent) event);
				return;
			}
			
			final DamageCause cause = event.getCause();
			final double olddamage = player.getHealth();

			new BukkitRunnable() {
				public void run() {
					double damage = olddamage - player.getHealth();
					
					if (damage <= 0) {
						return;
					}
					
					if (cause == DamageCause.LAVA) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dLava §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dFire §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					}  
					else if (cause == DamageCause.CONTACT) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dCactus §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.DROWNING) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dDrowning §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.FALL) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dFall §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.LIGHTNING) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dLightning §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.MAGIC) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dMagic §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.POISON) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dPoison §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.STARVATION) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dStarving §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.SUFFOCATION) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dSuffocation §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.VOID) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dVoid §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
					} 
					else if (cause == DamageCause.WITHER) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dWither §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (cause == DamageCause.BLOCK_EXPLOSION) {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§dTNT §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§d??? §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
					}
				}
			}.runTaskLater(Main.plugin, 1);
		}

		private void onDamageByOther(final Player player, final EntityDamageByEntityEvent event) {
			if (Spectator.getManager().isSpectating(player.getName())) {
				return;
			}
			
			final double olddamage = player.getHealth();
			
			new BukkitRunnable() {
				public void run() {
					double damage = olddamage - player.getHealth();
					
					if (damage <= 0) {
						return;
					}
					
					if (event.getDamager() instanceof Player) {
						Player killer = (Player) event.getDamager();
						
						if (Spectator.getManager().isSpectating(killer)) {
							return;
						}
						
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§4PvP§f:§a" + killer.getName() + "§f-M>§c" + player.getName() + " §f[§a" + NumberUtils.convertDouble((killer.getHealth() / 2)) + "§f:§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					} 
					else if (event.getDamager() instanceof Projectile) {
						Projectile p = (Projectile) event.getDamager();
						
						if (p.getShooter() instanceof Player) {
							Player shooter = (Player) p.getShooter();
							
							if (p instanceof Arrow) {
								for (Player online : PlayerUtils.getPlayers()) {
									if (Spectator.getManager().hasSpecInfo(online)) {
										online.sendMessage(prefix() + "§4PvP§f:§a" + shooter.getName() + "§f-B>§c" + player.getName() + " §f[§a" + NumberUtils.convertDouble((shooter.getHealth() / 2)) + "§f:§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
									}
								}
								return;
							}
							else {
								for (Player online : PlayerUtils.getPlayers()) {
									if (Spectator.getManager().hasSpecInfo(online)) {
										online.sendMessage(prefix() + "§4PvP§f:§a" + shooter.getName() + "§f-?P>§c" + player.getName() + " §f[§a" + NumberUtils.convertDouble((shooter.getHealth() / 2)) + "§f:§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
									}
								}
							}
							return;
						} 
						else {
							if (p.getShooter() instanceof Entity) {
								Entity entity = (Entity) p.getShooter();
								
								for (Player online : PlayerUtils.getPlayers()) {
									if (Spectator.getManager().hasSpecInfo(online)) {
										online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§d" + NameUtils.getMobName(entity) + " §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
									}
								}
								return;
							}
							else {
								for (Player online : PlayerUtils.getPlayers()) {
									if (Spectator.getManager().hasSpecInfo(online)) {
										online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§d??? §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
									}
								}
							}
						}
						return;
					} 
					else if (event.getDamager() instanceof LivingEntity) {
						Entity e = event.getDamager();
						
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§d" + NameUtils.getMobName(e) + " §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					}
					else if (event.getDamager() instanceof TNTPrimed) {
						Entity e = event.getDamager();
						
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§d" + NameUtils.getMobName(e) + " §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
						return;
					}
					else {
						for (Player online : PlayerUtils.getPlayers()) {
							if (Spectator.getManager().hasSpecInfo(online)) {
								online.sendMessage(prefix() + "§5PvE§f:§c" + player.getName() + "§f<-§d??? §f[§c" + NumberUtils.convertDouble((player.getHealth() / 2)) + "§f] [§6" + NumberUtils.convertDouble((damage / 2)) + "§f]");
							}
						}
					}
				}
			}.runTaskLater(Main.plugin, 1);
		}
	}
}