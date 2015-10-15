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

import com.leontg77.uhc.Main.BorderShrink;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.EntityUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * The runnable class for all the runnables
 * <p>
 * This class contains methods for starting countdowns, timers, RR timer and RR countdown.
 * 
 * @author LeonTG77
 */
public class Timers {
	private static Settings settings = Settings.getInstance();
	private static Game game = Game.getInstance();

	public static int taskSeconds;
	public static int taskMinutes;
	
	public static int heal;
	public static int pvp;
	public static int meetup;
	
	public static int healSeconds;
	public static int pvpSeconds;
	public static int meetupSeconds;
	
	/**
	 * Start the countdown for the game.
	 */
	public static void start() {
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.sendTitle(online, "§c3", "", 1, 20, 1);
			online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}, 20);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§a1", "", 1, 20, 1);
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
						PacketUtils.sendTitle(online, "§aGo!", "§7Have fun spectating!", 1, 20, 1);
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
					
					if (ScenarioManager.getInstance().getScenario("SlaveMarket").isEnabled()) {
						for (ItemStack item : online.getInventory().getContents()) {
							if (item == null) {
								continue;
							}
							
							if (item.getType() == Material.DIAMOND) {
								continue;
							}
							
							online.getInventory().remove(item);	
						}
					} else {
						online.getInventory().clear();
					}

					online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 100));
					if (!online.getUniqueId().toString().equals("cb996bf0-333f-42c2-85ca-3a9bec231cc6")) {
						online.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 6000, 100));
					}
					
					online.setItemOnCursor(new ItemStack (Material.AIR));
					online.awardAchievement(Achievement.OPEN_INVENTORY);
					online.getInventory().setArmorContents(null);
					online.setHealth(online.getMaxHealth());
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
					
					PacketUtils.sendTitle(online, "§aGo!", "§7Good luck, have fun!", 1, 20, 1);
					
					User data = User.get(online);
					data.increaseStat(Stat.GAMESPLAYED);
				}
				
				for (Team team : Teams.getInstance().getTeamsWithPlayers()) {
					Main.teamKills.put(team.getName(), 0);
					
					ArrayList<String> players = new ArrayList<String>();
					TeamCommand.sTeam.put(team.getName(), team.getEntries());
				}

				Scoreboards sb = Scoreboards.getInstance();
				
				for (String entry : sb.board.getEntries()) {
					if (sb.getScore(entry) > 0) {
						sb.setScore(entry, sb.getScore(entry) + 50);
					}
				}

				game.setPregameBoard(false);
				game.setArenaBoard(false);
				
				Bukkit.getServer().getPluginManager().registerEvents(new SpecInfo(), Main.plugin);
				State.setState(State.INGAME);
				Scoreboards.getInstance().setScore("§8» §a§lPvE", 1);
				Scoreboards.getInstance().setScore("§8» §a§lPvE", 0);
				
				heal = 1;
				pvp = settings.getConfig().getInt("time.pvp");
				meetup = settings.getConfig().getInt("time.meetup");
				
				healSeconds = 60;
				pvpSeconds = (settings.getConfig().getInt("time.pvp") * 60);
				meetupSeconds = (settings.getConfig().getInt("time.meetup") * 60);

				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(healSeconds));
				}
				
				timer();
				
				for (World world : GameUtils.getGameWorlds()) {
					world.setTime(0);
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					
					world.setGameRuleValue("doDaylightCycle", "true");
					world.setSpawnFlags(false, true);
					world.setThundering(false);
					world.setStorm(false);
					
					
					for (Entity mob : world.getEntities()) {
						if (EntityUtils.isButcherable(mob.getType())) {
							mob.remove();
						}
					}
					
					if (game.getBorderShrink() == BorderShrink.START) {
						world.getWorldBorder().setSize(300, meetup * 60);
					}
				}
			}
		}, 60);
	}
	
	/**
	 * Start the timers.
	 */
	public static void timer() {
		if (Bukkit.getScheduler().isQueued(taskMinutes) || Bukkit.getScheduler().isCurrentlyRunning(taskMinutes)) {
			Bukkit.getScheduler().cancelTask(taskMinutes);
		}
		
		if (Bukkit.getScheduler().isQueued(taskSeconds) || Bukkit.getScheduler().isCurrentlyRunning(taskSeconds)) {
			Bukkit.getScheduler().cancelTask(taskSeconds);
		}
		
		taskMinutes = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				Scoreboards sb = Scoreboards.getInstance();
				
				heal--;
				pvp--;
				meetup--;
				
				if (heal == 0) {
					PlayerUtils.broadcast(Main.prefix() + "Final heal has been given, do not ask for another one.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendTitle(online, "§6Final heal!", "§aDo not ask for another one.", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
						online.setHealth(online.getMaxHealth());
						online.setSaturation(20);
						online.setFoodLevel(20);
						online.setFireTicks(0);
					}
				}
				
				if (heal == -1) {
					PlayerUtils.broadcast(Main.prefix() + "The chat has been enabled.");
					game.setMuted(false);
				}
				
				if (heal == -2) {
					for (World world : GameUtils.getGameWorlds()) {
						world.setSpawnFlags(true, true);
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Hostile mobs can now spawn.");
				}

				if (pvp == 0) {
					PlayerUtils.broadcast(Main.prefix() + "PvP/iPvP has been enabled.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendTitle(online, "", "§4PvP has been enabled!", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
					}
					
					for (World world : GameUtils.getGameWorlds()) {
						world.setPVP(true);
						
						if (game.getBorderShrink() == BorderShrink.PVP) {
							world.getWorldBorder().setSize(300, meetup * 60);
						}
					}
					
					for (String entry : sb.board.getEntries()) {
						if (!entry.equals("§8» §a§lPvE")) {
							sb.resetScore(entry);
						}
					}
					return;
				}
				
				if (meetup == 0) {
					
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					PlayerUtils.broadcast(" ");
					PlayerUtils.broadcast(ChatColor.RED + " Meetup is now, head to 0,0!");
					PlayerUtils.broadcast(" ");
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.WITHER_DEATH, 1, 1);
					}

					for (World world : GameUtils.getGameWorlds()) {
						world.setThundering(false);
						world.setStorm(false);

						if (!ScenarioManager.getInstance().getScenario("Astrophobia").isEnabled()) {
							world.setGameRuleValue("doDaylightCycle", "false");
							world.setTime(6000);
						}
					}
					return;
				}
				
				if (meetup == -2 && game.getBorderShrink() == BorderShrink.MEETUP) {
					PlayerUtils.broadcast(Main.prefix() + "Border will now shrink to §6300x300 §7over §a10 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}

					for (World world : GameUtils.getGameWorlds()) {
						world.getWorldBorder().setSize(300, 600);
					}
				}
				
				if (meetup == -12 && game.getBorderShrink() == BorderShrink.MEETUP) {
					PlayerUtils.broadcast(Main.prefix() + "Border has stopped shrinking.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
				}
				
				if (pvp == 45) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a45 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (pvp == 30) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a30 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (pvp == 15) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a15 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (pvp == 10) {
					PlayerUtils.broadcast(Main.prefix() + "PvP will be enabled in §a10 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
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
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 90) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a1 §7hour and §a30 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 75) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a1 §7hour and §a15 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
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
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 30) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a30 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 15) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a15 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
					return;
				}
				
				if (meetup == 10) {
					PlayerUtils.broadcast(Main.prefix() + "Meetup is in §a10 §7minutes.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
					}
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
		
		taskSeconds = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				healSeconds--;
				pvpSeconds--;
				meetupSeconds--;
				
				if (healSeconds > 0) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(healSeconds));
					}
				}
				else if (pvpSeconds > 0) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7PvP is enabled in §8» §a" + DateUtils.ticksToString(pvpSeconds));
					}
				}
				else if (meetupSeconds > 0) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7Meetup is in §8» §a" + DateUtils.ticksToString(meetupSeconds));
					}
				}
				else {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§8» §6Meetup is now! §8«");
					}
				}
			}
		}, 20, 20);
	}
	
	/**
	 * Start the countdown for the recorded round.
	 */
	public static void startRR() {
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.sendTitle(online, "§c3", "", 1, 20, 1);
			online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}, 20);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§a1", "", 1, 20, 1);
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
						PacketUtils.sendTitle(online, "§aGo!", "§7Have fun spectating!", 1, 20, 1);
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
					
					online.getInventory().clear();
					online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 100));
					online.setItemOnCursor(new ItemStack (Material.AIR));
					online.awardAchievement(Achievement.OPEN_INVENTORY);
					online.getInventory().setArmorContents(null);
					online.setHealth(online.getMaxHealth());
					online.setAllowFlight(false);
					online.setSaturation(20);
					online.setFoodLevel(20);
					online.setFlying(false);
					online.setFireTicks(0);
					online.setLevel(0);
					online.setExp(0);

					SpecInfo.totalDiamonds.clear();
					SpecInfo.totalGold.clear();
					
					PacketUtils.sendTitle(online, "§aGo!", "§7Good luck, have fun!", 1, 20, 1);
				}
				
				for (String e : Scoreboards.getInstance().kills.getScoreboard().getEntries()) {
					Scoreboards.getInstance().resetScore(e);
				}

				game.setPregameBoard(false);
				game.setArenaBoard(false);
				
				timerRR();
				Bukkit.getServer().getPluginManager().registerEvents(new SpecInfo(), Main.plugin);
				PlayerUtils.broadcast(Main.prefix() + "Start of episode 1");
				State.setState(State.INGAME);
				Scoreboards.getInstance().kills.setDisplayName("§6" + game.getRRName());
				pvp = 0;
				meetup = 1;
				
				for (World world : GameUtils.getGameWorlds()) {
					world.setTime(0);
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					
					world.setGameRuleValue("doMobSpawning", "false");
					world.setGameRuleValue("doDaylightCycle", "true");
					
					world.setThundering(false);
					world.setStorm(false);
					
					for (Entity mobs : world.getEntities()) {
						if (EntityUtils.isButcherable(mobs.getType())) {
							mobs.remove();
						}
					}
				}
			}
		}, 60);
	}
	
	/**
	 * Start the recorded round timers.
	 */
	public static void timerRR() {
		if (Bukkit.getScheduler().isQueued(taskMinutes) || Bukkit.getScheduler().isCurrentlyRunning(taskMinutes)) {
			Bukkit.getScheduler().cancelTask(taskMinutes);
		}
		
		taskMinutes = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				pvp++;
				heal--;
				
				if (pvp == 20) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 1 | Start of episode 2");
					PlayerUtils.broadcast(Main.prefix() + "PvP has been enabled!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					
					for (World world : GameUtils.getGameWorlds()) {
						world.setPVP(true);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 40) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 2 | Start of episode 3");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 60) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 3 | Start of episode 4");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 80) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 4 | Start of episode 5");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 100) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 5 | Start of episode 6");
					PlayerUtils.broadcast(Main.prefix() + "Perma day activated!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					
					for (World world : Bukkit.getWorlds()) {
						if (world.getName().equals("lobby") || world.getName().equals("arena")) {
							continue;
						}

						world.setGameRuleValue("doDaylightCycle", "false");
						world.setTime(6000);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 120) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 6 | Start of episode 7");
					PlayerUtils.broadcast(Main.prefix() + "Meetup is now!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 140) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 7 | Start of episode 8");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 160) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 8 | Start of episode 9");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					heal = 20;
					meetup++;
				}
				
				if (pvp == 180) {
					PlayerUtils.broadcast(Main.prefix() + "End of episode 9 | Start of episode 10");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					heal = 20;
					meetup++;
				}
			}
		}, 1200, 1200);
	}
}