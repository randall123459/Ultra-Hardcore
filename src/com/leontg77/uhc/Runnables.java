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
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * The runnable class for all the runnables
 * <p>
 * This class contains methods for starting countdowns, timers, RR timer and RR countdown.
 * 
 * @author LeonTG77
 */
public class Runnables {
	private static Settings settings = Settings.getInstance();
	private static Game game = Game.getInstance();
	
	public static int task;
	public static int heal;
	public static int pvp;
	public static int meetup;
	
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
							if (effect.getType().equals(PotionEffectType.NIGHT_VISION)) {
								continue;
							}
							
							online.removePotionEffect(effect.getType());	
						}
						
						online.awardAchievement(Achievement.OPEN_INVENTORY);
						online.setHealth(online.getMaxHealth());
						online.setSaturation(20);
						online.setFoodLevel(20);
						online.setFireTicks(0);
						online.setLevel(0);
						online.setExp(0);
						PlayerUtils.sendTitle(online, "§aGo!", "§7Have fun spectating!", 1, 20, 1);
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
					
					if (ScenarioManager.getInstance().getScenario("Kings").isEnabled()) {
						for (PotionEffect effect : online.getActivePotionEffects()) {
							if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
								continue;
							}
							
							if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
								continue;
							}
							
							if (effect.getType().equals(PotionEffectType.SPEED)) {
								continue;
							}
							
							online.removePotionEffect(effect.getType());	
						}
					} else {
						for (PotionEffect effect : online.getActivePotionEffects()) {
							online.removePotionEffect(effect.getType());	
						}
					}

					online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 100));
					if (!online.getUniqueId().toString().equals("cb996bf0-333f-42c2-85ca-3a9bec231cc6")) {
						online.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 6000, 100));
					}
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

					Bukkit.getServer().setIdleTimeout(10);
					Main.kills.put(online.getName(), 0);
					SpecInfo.totalDiamonds.clear();
					SpecInfo.totalGold.clear();
					Main.relog.clear();
					
					online.sendMessage(Main.prefix() + "Remember to read the match post: " + Settings.getInstance().getConfig().getString("matchpost"));
					online.sendMessage(Main.prefix() + "If you have any questions, use /helpop.");
					PlayerUtils.sendTitle(online, "§aGo!", "§7Good luck, have fun!", 1, 20, 1);
					
					User data = User.get(online);
					data.increaseStat("gamesplayed");
				}
				
				for (Team team : Teams.getManager().getTeamsWithPlayers()) {
					Main.teamKills.put(team.getName(), 0);
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
				}
				
				for (String e : Scoreboards.getManager().kills.getScoreboard().getEntries()) {
					Scoreboards.getManager().resetScore(e);
				}

				game.setPregameBoard(false);
				game.setArenaBoard(false);
				
				timer();
				Bukkit.getServer().getPluginManager().registerEvents(new SpecInfo(), Main.plugin);
				State.setState(State.INGAME);
				Scoreboards.getManager().setScore("§8» §a§lPvE", 1);
				Scoreboards.getManager().setScore("§8» §a§lPvE", 0);
				heal = 1;
				pvp = settings.getConfig().getInt("time.pvp");
				meetup = settings.getConfig().getInt("time.meetup");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 60 &cFinal heal in&8:&7");
				
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
					
					if (game.getBorderShrinkTime() == Border.START) {
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
		if (Bukkit.getScheduler().isQueued(task) || Bukkit.getScheduler().isCurrentlyRunning(task)) {
			Bukkit.getScheduler().cancelTask(task);
		}
		
		task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				heal--;
				pvp--;
				meetup--;
				
				if (heal == 0) {
					PlayerUtils.broadcast(Main.prefix() + "Final heal has been given, do not ask for another one.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						PlayerUtils.sendTitle(online, "§6Final heal!", "§aDo not ask for another one.", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
						online.setHealth(online.getMaxHealth());
						online.setSaturation(20);
						online.setFoodLevel(20);
						online.setFireTicks(0);
					}

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (pvp * 60) + " &cPvP in&8:&7");
				}
				
				if (heal == -1) {
					PlayerUtils.broadcast(Main.prefix() + "The chat has been enabled.");
					game.setMuted(false);
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
					PlayerUtils.broadcast(Main.prefix() + "PvP/iPvP has been enabled.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						PlayerUtils.sendTitle(online, "", "§4PvP has been enabled!", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
					}
					
					for (World world : Bukkit.getWorlds()) {
						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}
						
						world.setPVP(true);
						
						if (game.getBorderShrinkTime() == Border.PVP) {
							world.getWorldBorder().setSize(299, meetup * 60);
						}
					}
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (meetup * 60) + " &cMeetup in&8:&7");
				}
				
				if (meetup == 0) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					PlayerUtils.broadcast(ChatColor.RED + " ");
					PlayerUtils.broadcast(ChatColor.RED + " Meetup has started!");	
					if (game.getBorderShrinkTime() == Border.MEETUP) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 120 &6Meetup is now! &8❘ &cBorder will start shrinking in&8:&7");
						PlayerUtils.broadcast(ChatColor.RED + " The border will start shrinking in §a2 §cminutes.");	
					} else {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 &6Meetup is now!");
					}
					PlayerUtils.broadcast(ChatColor.RED + " ");
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					
					for (Player online : PlayerUtils.getPlayers()) {
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
				
				if (meetup == -2 && game.getBorderShrinkTime() == Border.MEETUP) {
					PlayerUtils.broadcast(Main.prefix() + "Border will now shrink to §6300x300 §7over §a10 §7minutes.");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 600 &6Meetup is now! &8❘ &cBorder stops shrinking in&8:&7");
					
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
				
				if (meetup == -12 && game.getBorderShrinkTime() == Border.MEETUP) {
					PlayerUtils.broadcast(Main.prefix() + "Border has stopped shrinking.");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 &6Meetup is now! &8❘ &cBorder has stopped shrinking.");
					
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
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a45 §7minutes.");
					return;
				}
				
				if (pvp == 30) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a30 §7minutes.");
					return;
				}
				
				if (pvp == 15) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a15 §7minutes.");
					return;
				}
				
				if (pvp == 10) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a10 §7minutes.");
					return;
				}
				
				if (pvp == 5) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a5 §7minutes.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (pvp == 1) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a1 §7minute.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 120) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a2 §hours.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 105) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a1 §7hour and §a45 §7minutes.");
					return;
				}
				
				if (meetup == 90) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a1 §7hour and §a30 §7minutes.");
					return;
				}
				
				if (meetup == 75) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a1 §7hour and §a15 §7minutes.");
					return;
				}
				
				if (meetup == 60) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a1 §7hour.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 45) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a45 §7minutes.");
					return;
				}
				
				if (meetup == 30) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a30 §7minutes.");
					return;
				}
				
				if (meetup == 15) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a15 §7minutes.");
					return;
				}
				
				if (meetup == 10) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a10 §7minutes.");
					return;
				}
				
				if (meetup == 5) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a5 §7minutes.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 1) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a1 §7minute.");
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
			}
		}, 1200, 1200);
	}
}