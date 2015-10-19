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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main.BorderShrink;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.inventory.InvGUI;
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
	private static Timers instance = new Timers();
	private Game game = Game.getInstance();

	public static int taskSeconds;
	public static int taskMinutes;

	public static int time;
	public static int pvp;
	public static int meetup;
	
	public static int timeSeconds;
	public static int pvpSeconds;
	public static int meetupSeconds;
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Timers getInstance() {
		return instance;
	}
	
	/**
	 * Start the countdown for the game.
	 */
	public void start() {
		for (int i = 0; i < 150; i++) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.sendMessage("§0");
			}
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "The game will start in 30 seconds.");
		PlayerUtils.broadcast(Main.PREFIX + "Opening game info inventory in 5 seconds.");

		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					InvGUI.getInstance().openGameInfo(online);
				}
			}
		}.runTaskLater(Main.plugin, 100);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "Remember to use §a/uhc §7for all game information.");
			}
		}.runTaskLater(Main.plugin, 250);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "If you have a question and §a/uhc§7 didn't help, ask in §a/helpop");
			}
		}.runTaskLater(Main.plugin, 300);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "To find the matchpost, use §a/post");
			}
		}.runTaskLater(Main.plugin, 350);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "Useful commands can be found in the tab list.");
			}
		}.runTaskLater(Main.plugin, 400);

		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§45", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §45.");
			}
		}.runTaskLater(Main.plugin, 500);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§c4", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §c4.");
			}
		}.runTaskLater(Main.plugin, 520);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§63", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §63.");
			}
		}.runTaskLater(Main.plugin, 540);

		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §e2.");
			}
		}.runTaskLater(Main.plugin, 560);

		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§a1", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §a1.");
			}
		}.runTaskLater(Main.plugin, 580);
		
		new BukkitRunnable() {
			public void run() {
				ScenarioManager scen = ScenarioManager.getInstance();
				Spectator spec = Spectator.getInstance();

				Scoreboards sb = Scoreboards.getInstance();
				Teams teams = Teams.getInstance();

				PluginManager manager = Bukkit.getPluginManager();
				manager.registerEvents(new SpecInfo(), Main.plugin);
				
				State.setState(State.INGAME);
				game.setArenaBoard(false);
				
				sb.setScore("§8» §a§lPvE", 1);
				sb.setScore("§8» §a§lPvE", 0);
				
				pvp = game.getPvP();
				meetup = game.getMeetup();
				
				pvpSeconds = (pvp * 60);
				meetupSeconds = (meetup * 60);
				
				timer();

				Bukkit.getServer().setIdleTimeout(10);
				SpecInfo.totalDiamonds.clear();
				SpecInfo.totalGold.clear();
				
				PlayerUtils.broadcast("§8» --------------------------------- «");
				PlayerUtils.broadcast(Main.PREFIX + "The game has started!");
				PlayerUtils.broadcast(Main.PREFIX + "PvP will be enabled in: §a" + pvp + " minutes.");
				PlayerUtils.broadcast(Main.PREFIX + "Meetup is in: §a" + meetup + " minutes.");
				PlayerUtils.broadcast("§8» --------------------------------- «");

				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(20));
				}
				
				for (Team team : teams.getTeamsWithPlayers()) {
					Main.teamKills.put(team.getName(), 0);
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.savedTeams.put(team.getName(), players);
				}
				
				for (String entry : sb.board.getEntries()) {
					if (sb.getScore(entry) > 0) {
						sb.setScore(entry, sb.getScore(entry) + 50);
					}
				}
				
				for (World world : GameUtils.getGameWorlds()) {
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					world.setTime(0);
					
					world.setGameRuleValue("doDaylightCycle", "true");
					world.setSpawnFlags(false, true);
					world.setThundering(false);
					world.setStorm(false);
					
					if (game.getBorderShrink() == BorderShrink.START) {
						world.getWorldBorder().setSize(300, meetupSeconds);
					}
					
					for (Entity mob : world.getEntities()) {
						if (EntityUtils.isButcherable(mob.getType())) {
							mob.remove();
						}
					}
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
					for (Achievement a : Achievement.values()) {
						if (online.hasAchievement(a)) {
							online.removeAchievement(a);
						}
					}
					
					if (spec.isSpectating(online)) {
						PacketUtils.sendTitle(online, "§aGo!", "§7Have fun spectating!", 1, 20, 1);
					} else {
						PacketUtils.sendTitle(online, "§aGo!", "§7Good luck, have fun!", 1, 20, 1);
						
						if (online.getGameMode() != GameMode.SURVIVAL) {
							online.setGameMode(GameMode.SURVIVAL);
						}
					}
					
					User user = User.get(online);
					user.increaseStat(Stat.GAMESPLAYED);

					user.resetHealth();
					user.resetFood();
					user.resetExp();
					
					if (scen.getScenario("Kings").isEnabled()) {
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
						user.resetEffects();
					}
					
					if (scen.getScenario("SlaveMarket").isEnabled()) {
						PlayerInventory inv = online.getInventory();

						for (ItemStack item : inv.getContents()) {
							if (item == null) {
								continue;
							}
							
							if (item.getType() == Material.DIAMOND) {
								continue;
							}
							
							inv.removeItem(item);	
						}
						
				        inv.setArmorContents(null);
				        online.setItemOnCursor(new ItemStack(Material.AIR));

				        InventoryView openInventory = online.getOpenInventory();
				        
				        if (openInventory.getType() == InventoryType.CRAFTING) {
				            openInventory.getTopInventory().clear();
				        }
					} else {
						user.resetInventory();
					}

					online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100));
					Main.kills.put(online.getName(), 0);
				}
			}
		}.runTaskLater(Main.plugin, 600);
	}
	
	/**
	 * Start the timers.
	 */
	public void timer() {
		if (Bukkit.getScheduler().isQueued(taskMinutes) || Bukkit.getScheduler().isCurrentlyRunning(taskMinutes)) {
			Bukkit.getScheduler().cancelTask(taskMinutes);
		}
		
		if (Bukkit.getScheduler().isQueued(taskSeconds) || Bukkit.getScheduler().isCurrentlyRunning(taskSeconds)) {
			Bukkit.getScheduler().cancelTask(taskSeconds);
		}
		
		taskMinutes = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				ScenarioManager scen = ScenarioManager.getInstance();
				Scoreboards sb = Scoreboards.getInstance();
				
				time++;
				pvp--;
				meetup--;
				
				if (time == 1) {
					PlayerUtils.broadcast(Main.PREFIX + "The chat has been enabled.");
					game.setMuted(false);
				}
				
				if (time == 2) {
					for (World world : GameUtils.getGameWorlds()) {
						world.setSpawnFlags(true, true);
					}
					
					PlayerUtils.broadcast(Main.PREFIX + "Hostile mobs can now spawn.");
				}

				if (pvp == 0) {
					PlayerUtils.broadcast(Main.PREFIX + "PvP/iPvP has been enabled.");
					
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
				}
				
				if (meetup == 0) {
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					PlayerUtils.broadcast(" ");
					PlayerUtils.broadcast(ChatColor.RED + " Meetup is now, head to 0,0!");
					PlayerUtils.broadcast(" ");
					PlayerUtils.broadcast(ChatColor.RED + " You may be do anything you want as long");
					PlayerUtils.broadcast(ChatColor.RED + " as your inside 300x300 on the surface!");
					PlayerUtils.broadcast(" ");
					
					if (game.getBorderShrink() == BorderShrink.MEETUP) {
						PlayerUtils.broadcast(ChatColor.RED + " Borders will shrink in 2 minutes.");
						PlayerUtils.broadcast(" ");
					}
					
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.WITHER_DEATH, 1, 1);
					}

					for (World world : GameUtils.getGameWorlds()) {
						world.setThundering(false);
						world.setStorm(false);

						if (!scen.getScenario("Astrophobia").isEnabled()) {
							world.setGameRuleValue("doDaylightCycle", "false");
							world.setTime(6000);
						}
					}
				}
				
				if (game.getBorderShrink() == BorderShrink.MEETUP) {
					if (meetup == -2) {
						PlayerUtils.broadcast(Main.PREFIX + "Border will now shrink to §6300x300 §7over §a10 §7minutes.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}

						for (World world : GameUtils.getGameWorlds()) {
							world.getWorldBorder().setSize(300, 600);
						}
					}
					
					if (meetup == -12) {
						PlayerUtils.broadcast(Main.PREFIX + "Border has stopped shrinking.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
					}
				}
				
				if (meetup > 0) {
					String meetupToString = String.valueOf(meetup);
					
					if (meetupToString.equals("1") || meetupToString.endsWith("5") || meetupToString.endsWith("0")) {
						PlayerUtils.broadcast(Main.PREFIX + "Meetup is in §a" + DateUtils.advancedTicksToString(meetup * 60) + "§7.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
						
						if (meetupToString.equals("1")) {
							PlayerUtils.broadcast(Main.PREFIX + "Start preparing to head to 0,0.");
							return;
						}
					}
				}
				
				if (pvp > 0) {
					String pvpToString = String.valueOf(pvp);
					
					if (pvpToString.equals("1") || pvpToString.endsWith("5") || pvpToString.endsWith("0")) {
						PlayerUtils.broadcast(Main.PREFIX + "PvP will be enabled in §a" + DateUtils.advancedTicksToString(pvp * 60) + "§7.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
					}
				}
			}
		}, 1200, 1200);
		
		taskSeconds = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			int finalHeal = 20;
			int timeToBorder = 120;
			
			public void run() {
				timeSeconds++;
				pvpSeconds--;
				meetupSeconds--;
				
				if (timeSeconds == 20) {
					PlayerUtils.broadcast(Main.PREFIX + "Final heal has been given.");
					PlayerUtils.broadcast(Main.PREFIX + "Do not ask for another one.");
					
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendTitle(online, "§6Final heal!", "§7Do not ask for another one", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
						
						User user = User.get(online);
						user.resetHealth();
						user.resetFood();
					}
				}
				
				if (timeSeconds < 20) {
					finalHeal--;
					
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(finalHeal));
					}
				} else if (pvpSeconds > 0) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7PvP is enabled in §8» §a" + DateUtils.ticksToString(pvpSeconds));
					}
				} else if (meetupSeconds > 0) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7Meetup is in §8» §a" + DateUtils.ticksToString(meetupSeconds));
					}
				} else {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§8» §6Meetup is now! §8«");
					}
					
					timeToBorder--;
					
					if (timeToBorder == 60 || timeToBorder == 30 || timeToBorder == 10 || timeToBorder == 5 || 
						timeToBorder == 4 || timeToBorder == 3 || timeToBorder == 2 || timeToBorder == 1) {
						
						PlayerUtils.broadcast(Main.PREFIX + "The border starts shrinking in §a" + DateUtils.advancedTicksToString(timeToBorder) + "§7.");
					}
				}
			}
		}, 20, 20);
	}
	
	/**
	 * Start the countdown for the recorded round.
	 */
	public void startRR() {
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
					
					if (Spectator.getInstance().isSpectating(online)) {
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
				PlayerUtils.broadcast(Main.PREFIX + "Start of episode 1");
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
	public void timerRR() {
		if (Bukkit.getScheduler().isQueued(taskMinutes) || Bukkit.getScheduler().isCurrentlyRunning(taskMinutes)) {
			Bukkit.getScheduler().cancelTask(taskMinutes);
		}
		
		taskMinutes = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				pvp++;
				time--;
				
				if (pvp == 20) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 1 | Start of episode 2");
					PlayerUtils.broadcast(Main.PREFIX + "PvP has been enabled!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					
					for (World world : GameUtils.getGameWorlds()) {
						world.setPVP(true);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 40) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 2 | Start of episode 3");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 60) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 3 | Start of episode 4");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 80) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 4 | Start of episode 5");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 100) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 5 | Start of episode 6");
					PlayerUtils.broadcast(Main.PREFIX + "Perma day activated!");
					
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
					time = 20;
					meetup++;
				}
				
				if (pvp == 120) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 6 | Start of episode 7");
					PlayerUtils.broadcast(Main.PREFIX + "Meetup is now!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 140) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 7 | Start of episode 8");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 160) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 8 | Start of episode 9");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 180) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode 9 | Start of episode 10");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
			}
		}, 1200, 1200);
	}
}