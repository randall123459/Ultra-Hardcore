package com.leontg77.uhc;

import java.util.ArrayList;

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
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main.Border;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.PlayerUtils;

/**
 * The runnable class for all the runnables
 * <p>
 * This class contains methods for starting countdowns, timers, RR timer and RR countdown.
 * 
 * @author LeonTG77
 */
public class Runnables {
	public static int task, heal, pvp, meetup;
	
	/**
	 * Start the countdown for the game.
	 */
	public static void start() {
		for (Player online : PlayerUtils.getPlayers()) {
			PlayerUtils.sendTitle(online, "§c3", "", 1, 20, 1);
			online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PlayerUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}, 20);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PlayerUtils.sendTitle(online, "§a1", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}, 40);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
					if (Spectator.getManager().isSpectating(online)) {
						for (Achievement a : Achievement.values()) {
							if (online.hasAchievement(a)) {
								online.removeAchievement(a);
							}
						}
						
						for (PotionEffect effect : online.getActivePotionEffects()) {
							online.removePotionEffect(effect.getType());	
						}
						
						online.awardAchievement(Achievement.OPEN_INVENTORY);
						online.setHealth(online.getMaxHealth());
						online.setSaturation(20);
						online.setFoodLevel(20);
						online.setFireTicks(0);
						online.setLevel(0);
						online.setExp(0);
						PlayerUtils.sendTitle(online, "§aGo!", "§7Good luck have fun!", 1, 20, 1);
						continue;
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
					online.setHealth(online.getMaxHealth());
					online.getInventory().clear();
					online.setAllowFlight(false);
					online.setSaturation(20);
					online.setFoodLevel(20);
					online.setFlying(false);
					online.setFireTicks(0);
					online.setLevel(0);
					online.setExp(0);
					
					Main.kills.put(online.getName(), 0);
					
					online.sendMessage(Main.prefix() + "Remember to read the match post: " + Settings.getInstance().getConfig().getString("matchpost"));
					online.sendMessage(Main.prefix() + "If you have any questions, use /helpop.");
					PlayerUtils.sendTitle(online, "§aGo!", "§7Good luck have fun!", 1, 20, 1);
					
					Data data = Data.getFor(online);
					data.increaseStat("gamesplayed");
				}
				
				for (Team team : Teams.getManager().getTeamsWithPlayers()) {
					Main.teamKills.put(team.getName(), 0);
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
				}
				
				timer();
				State.setState(State.INGAME);
				Scoreboards.getManager().setScore("§8» §a§lPvE", 1);
				Scoreboards.getManager().setScore("§8» §a§lPvE", 0);
				heal = 1;
				pvp = Settings.getInstance().getConfig().getInt("time.pvp");
				meetup = Settings.getInstance().getConfig().getInt("time.meetup");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 60 &aFinal heal in:&7");
				
				for (World world : Bukkit.getWorlds()) {
					if (world.getName().equals("lobby") || world.getName().equals("arena")) {
						continue;
					}
					
					world.setTime(0);
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					
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
		}, 60);
	}
	
	/**
	 * Start the timers.
	 */
	public static void timer() {
		task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				heal--;
				pvp--;
				meetup--;
				
				if (heal == 0) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lFinal heal has been given, do not ask for another one.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						PlayerUtils.sendTitle(online, "§6Final heal!", "§aDo not ask for another one.", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.NOTE_PIANO, 1, 0);
						online.setHealth(online.getMaxHealth());
						online.setSaturation(20);
						online.setFoodLevel(20);
						online.setFireTicks(0);
					}

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (pvp * 60) + " &aPvP in:&7");
				}
				
				if (heal == -1) {
					PlayerUtils.broadcast(Main.prefix() + "The chat has been enabled.");
					Main.muted = false;
				}
				
				if (heal == -2) {
					for (World world : Bukkit.getWorlds()) {
						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}
						
						world.setGameRuleValue("doMobSpawning", "true");
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Mobs can now spawn.");
				}

				if (pvp == 0) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lPvP has been enabled.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						PlayerUtils.sendTitle(online, "", "§4PvP has been enabled!", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.WITHER_SHOOT, 1, 0);
					}
					
					for (World world : Bukkit.getWorlds()) {
						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}
						
						world.setPVP(true);
						
						if (Main.border == Border.PVP) {
							world.getWorldBorder().setSize(299, meetup * 60);
						}
					}
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (meetup * 60) + " &aMeetup in:&7");
				}
				
				if (meetup == 0) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 &6Meetup is now!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.sendMessage(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");											  
						online.sendMessage(ChatColor.RED + " ");													  
						online.sendMessage(ChatColor.RED + " Meetup has started!");			
						if (Main.border == Border.MEETUP) {
							online.sendMessage(ChatColor.RED + " Border will start shrinking in 2 minutes.");	
						}								  
						online.sendMessage(ChatColor.RED + " ");
						online.sendMessage(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");	
						online.playSound(online.getLocation(), Sound.WITHER_DEATH, 1, 1);
					}

					for (World world : Bukkit.getWorlds()) {
						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}
						
						world.setThundering(false);
						world.setStorm(false);

						if (ScenarioManager.getInstance().getScenario("Astrophobia").isEnabled()) {
							continue;
						}
						
						world.setGameRuleValue("doDaylightCycle", "false");
						world.setTime(6000);
					}
				}
				
				if (meetup == -2 && Main.border == Border.MEETUP) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lBorder will now shrink to 300x300 over 10 minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}

					for (World world : Bukkit.getWorlds()) {
						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}

						world.getWorldBorder().setSize(299, 600);
					}
				}
				
				if (meetup == -12 && Main.border == Border.MEETUP) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lBorder has stopped shrinking.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}

					for (World world : Bukkit.getWorlds()) {
						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}

						world.getWorldBorder().setDamageBuffer(0);
					}
				}
				
				if (pvp == 45) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l45 minutes to pvp.");
					return;
				}
				
				if (pvp == 30) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l30 minutes to pvp.");
					return;
				}
				
				if (pvp == 15) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l15 minutes to pvp.");
					return;
				}
				
				if (pvp == 10) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l10 minutes to pvp.");
					return;
				}
				
				if (pvp == 5) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l5 minutes to pvp.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (pvp == 1) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l1 minute to pvp.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 120) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l2 hours to meetup.");
					return;
				}
				
				if (meetup == 105) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour and 45 minutes to meetup.");
					return;
				}
				
				if (meetup == 90) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour and 30 minutes to meetup.");
					return;
				}
				
				if (meetup == 75) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour and 15 minutes to meetup.");
					return;
				}
				
				if (meetup == 60) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l1 hour to meetup.");
					return;
				}
				
				if (meetup == 45) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l45 minutes to meetup.");
					return;
				}
				
				if (meetup == 30) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l30 minutes to meetup.");
					return;
				}
				
				if (meetup == 15) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l15 minutes to meetup.");
					return;
				}
				
				if (meetup == 10) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l10 minutes to meetup.");
					return;
				}
				
				if (meetup == 5) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l5 minutes to meetup.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 1) {
					PlayerUtils.broadcast(Main.prefix() + "§6§l1 minute to meetup, get ready to head to 0,0.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
			}
		}, 1200, 1200);
	}
	
	/**
	 * Start the countdown for the Recorded Round.
	 *
	 * @deprecated Not in use yet.
	 */
	@Deprecated
	public static void startRR() {
		for (Player online : PlayerUtils.getPlayers()) {
			PlayerUtils.sendTitle(online, "§c3", "", 1, 20, 1);
			online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PlayerUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}, 20);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PlayerUtils.sendTitle(online, "§a1", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}, 40);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
					if (Spectator.getManager().isSpectating(online)) {
						for (Achievement a : Achievement.values()) {
							if (online.hasAchievement(a)) {
								online.removeAchievement(a);
							}
						}
						
						for (PotionEffect effect : online.getActivePotionEffects()) {
							online.removePotionEffect(effect.getType());	
						}
						
						online.awardAchievement(Achievement.OPEN_INVENTORY);
						online.setHealth(online.getMaxHealth());
						online.setSaturation(20);
						online.setFoodLevel(20);
						online.setFireTicks(0);
						online.setLevel(0);
						online.setExp(0);
						
						PlayerUtils.sendTitle(online, "§aGo!", "§7Good luck have fun!", 1, 20, 1);
						continue;
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
					online.setHealth(online.getMaxHealth());
					online.getInventory().clear();
					online.setAllowFlight(false);
					online.setSaturation(20);
					online.setFoodLevel(20);
					online.setFlying(false);
					online.setFireTicks(0);
					online.setLevel(0);
					online.setExp(0);
					
					Main.kills.put(online.getName(), 0);
					PlayerUtils.sendTitle(online, "§aGo!", "§7Good luck have fun!", 1, 20, 1);
				}
				
				for (Team team : Teams.getManager().getTeamsWithPlayers()) {
					Main.teamKills.put(team.getName(), 0);
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
				}
				
				rrTimer();
				State.setState(State.INGAME);
				pvp = Settings.getInstance().getConfig().getInt("time.pvp");
				meetup = 0;
				
				for (World world : Bukkit.getWorlds()) {
					if (world.getName().equals("lobby") || world.getName().equals("arena")) {
						continue;
					}
					
					world.setTime(0);
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					
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
		}, 60);
	}
	
	/**
	 * Start the Recorded Round timers.
	 *
	 * @deprecated Not in use yet.
	 */
	@Deprecated
	public static void rrTimer() {
		task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				pvp--;
				meetup++;

				if (pvp == 0) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lPvP has been enabled.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 20) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 1.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 40) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 2.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 60) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 3.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 80) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 4.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 100) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 5.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 120) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 6.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 140) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 7.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 160) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 8.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 180) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 9.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (meetup == 200) {
					PlayerUtils.broadcast(Main.prefix() + "§6§lEnd of episode 10.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
			}
		}, 1200, 1200);
	}
}