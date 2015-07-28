package com.leontg77.uhc;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.cmds.ConfigCommand.Border;
import com.leontg77.uhc.util.PlayerUtils;

/**
 * The runnable class for all the runnables
 * @author LeonTG77
 */
public class Runnables extends BukkitRunnable {
	public static int timeToStart, task, pvp, meetup, finalheal;
	
	public void run() {
		if (timeToStart == 3) {
			for (Player online : PlayerUtils.getPlayers()) {
				PlayerUtils.sendTitle(online, "§c3", "", 1, 20, 1);
				online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
			}
		}
		if (timeToStart == 2) {
			for (Player online : PlayerUtils.getPlayers()) {
				PlayerUtils.sendTitle(online, "§e2", "", 1, 20, 1);
				online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
			}
		}
		
		if (timeToStart == 1) {
			Main.stopCountdown();
			for (Player online : PlayerUtils.getPlayers()) {
				PlayerUtils.sendTitle(online, "§a1", "", 1, 20, 1);
				online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
			}
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
						if (Main.spectating.contains(online.getName())) {
							Spectator.getManager().set(online, false);
						}
						
						if (!Main.spectating.contains(online.getName())) {
							online.showPlayer(online);
						}
						
						for (Achievement a : Achievement.values()) {
							if (online.hasAchievement(a)) {
								online.removeAchievement(a);
							}
						}
						
						if (online.getGameMode() != GameMode.SURVIVAL) {
							online.setGameMode(GameMode.SURVIVAL);
						}
						
						for (PotionEffect effect : online.getActivePotionEffects()) {
							online.removePotionEffect(effect.getType());	
						}

						online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 100));
						online.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 6000, 100));
						online.setItemOnCursor(new ItemStack (Material.AIR));
						online.awardAchievement(Achievement.OPEN_INVENTORY);
						online.getInventory().setArmorContents(null);
						online.getInventory().clear();
						online.setAllowFlight(false);
						online.setSaturation(20);
						online.setFlying(false);
						online.setFoodLevel(20);
						online.setHealth(20.0);
						online.setFireTicks(0);
						online.setLevel(0);
						online.setExp(0);
						
						online.sendMessage(Main.prefix() + "Remember to read the match post: " + Settings.getInstance().getData().getString("match.post"));
						online.sendMessage(Main.prefix() + "If you have any questions, use /helpop.");
						PlayerUtils.sendTitle(online, "§aGo!", "§7Good luck have fun!", 1, 20, 1);
					}
					
					GameState.setState(GameState.INGAME);
					Scoreboards.getManager().setScore("§a§lPvE", 1);
					Scoreboards.getManager().setScore("§a§lPvE", 0);
					finalheal = 1;
					pvp = Settings.getInstance().getData().getInt("game.pvp");
					meetup = Settings.getInstance().getData().getInt("game.meetup");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 60 &aFinal heal in:&7");
					
					for (World world : Bukkit.getWorlds()) {
						world.setTime(0);
						world.setDifficulty(Difficulty.HARD);
						world.setPVP(false);

						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}
						
						world.setGameRuleValue("doMobSpawning", "false");
						world.setGameRuleValue("doDaylightCycle", "true");
						
						for (Entity mobs : world.getEntities()) {
							if (mobs.getType() == EntityType.BLAZE ||
									mobs.getType() == EntityType.CAVE_SPIDER ||
									mobs.getType() == EntityType.CREEPER ||
									mobs.getType() == EntityType.ENDERMAN ||
									mobs.getType() == EntityType.ZOMBIE ||
									mobs.getType() == EntityType.WITCH ||
									mobs.getType() == EntityType.WITHER ||
									mobs.getType() == EntityType.DROPPED_ITEM ||
									mobs.getType() == EntityType.GHAST ||
									mobs.getType() == EntityType.GIANT ||
									mobs.getType() == EntityType.MAGMA_CUBE ||
									mobs.getType() == EntityType.DROPPED_ITEM ||
									mobs.getType() == EntityType.SKELETON ||
									mobs.getType() == EntityType.SPIDER ||
									mobs.getType() == EntityType.SLIME ||
									mobs.getType() == EntityType.SILVERFISH ||
									mobs.getType() == EntityType.SKELETON || 
									mobs.getType() == EntityType.EXPERIENCE_ORB) {
								
								mobs.remove();
							}
						}
						
						if (Main.border == Border.START) {
							world.getWorldBorder().setSize(299, meetup * 60);
							world.setThundering(false);
							world.setStorm(false);
						}
					}
				}
			}, 20);
			
			task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
				public void run() {
					finalheal--;
					pvp--;
					meetup--;
					
					if (finalheal == 0) {
						PlayerUtils.broadcast(Main.prefix() + "§6§lFinal heal has been given, do not ask for another one.");
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
							online.setHealth(20.0);
							online.setFireTicks(0);
							online.setFoodLevel(20);
							online.setSaturation(20);
						}

						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (pvp * 60) + " &aPvP in:&7");
					}
					
					if (finalheal == -2) {
						for (World world : Bukkit.getWorlds()) {
							if (world.getName().equals("lobby") || world.getName().equals("arena")) {
								continue;
							}
							
							world.setGameRuleValue("doMobSpawning", "true");
						}
					}

					if (pvp == 0) {
						PlayerUtils.broadcast(Main.prefix() + "§6§lPvP has been enabled.");
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
						for (World world : Bukkit.getWorlds()) {
							world.setPVP(true);
							world.setPVP(true);
							world.setPVP(true);
							world.setPVP(true);
							world.setPVP(true);
							world.setPVP(true);
							world.setPVP(true);
							world.setPVP(true);
						}
						if (Main.border == Border.PVP) {
							for (World world : Bukkit.getWorlds()) {
								if (world.getName().equals("lobby") || world.getName().equals("arena")) {
									continue;
								}
								
								world.getWorldBorder().setSize(299, meetup * 60);
							}
						}
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (meetup * 60) + " &aMeetup in:&7");
					}
					
					if (meetup == 0) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 &6Meetup is now! Head to 0,0 and only stop for a fight!");
						for (Player online : PlayerUtils.getPlayers()) {
							online.sendMessage(ChatColor.DARK_GRAY + "==============================================");													  
							online.sendMessage(ChatColor.GREEN + " Meetup has started, start headding to 0,0.");											  
							online.sendMessage(ChatColor.GREEN + " Only stop for a fight, nothing else.");
							online.sendMessage(ChatColor.DARK_GRAY + "==============================================");
							online.playSound(online.getLocation(), Sound.WITHER_DEATH, 1, 0);
						}

						for (World world : Bukkit.getWorlds()) {
							if (world.getName().equals("lobby") || world.getName().equals("arena")) {
								continue;
							}
							
							if (Main.border == Border.MEETUP) {
								world.getWorldBorder().setSize(299, 600);
							}
							
							world.setThundering(false);
							world.setStorm(false);

							world.setGameRuleValue("doDaylightCycle", "false");
							world.setTime(6000);
						}
					}
					
					if (pvp == 45) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l45 minutes to pvp.");
					}
					
					if (pvp == 30) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l30 minutes to pvp.");
					}
					
					if (pvp == 15) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l15 minutes to pvp.");
					}
					
					if (pvp == 10) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l10 minutes to pvp.");
					}
					
					if (pvp == 5) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l5 minutes to pvp.");
					}
					
					if (pvp == 1) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l1 minute to pvp.");
					}
					
					if (meetup == 120) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l2 hours to meetup.");
					}
					
					if (meetup == 105) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour and 45 minutes to meetup.");
					}
					
					if (meetup == 90) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour and 30 minutes to meetup.");
					}
					
					if (meetup == 75) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour and 15 minutes to meetup.");
					}
					
					if (meetup == 60) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour to meetup.");
					}
					
					if (meetup == 45) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l45 minutes to meetup.");
					}
					
					if (meetup == 30) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l30 minutes to meetup.");
					}
					
					if (meetup == 15) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l15 minutes to meetup.");
					}
					
					if (meetup == 10) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l10 minutes to meetup.");
					}
					
					if (meetup == 5) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l5 minutes to meetup.");
					}
					
					if (meetup == 1) {
						PlayerUtils.broadcast(Main.prefix() + "§6§l1 minute to meetup, get ready to head to 0,0.");
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
					}
				}
			}, 1220, 1200);
		}
		timeToStart--;
	}
}