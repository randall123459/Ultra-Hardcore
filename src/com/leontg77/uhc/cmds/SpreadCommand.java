package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Parkour;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.utils.EntityUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.utils.ScatterUtils;

/**
 * Spread command class.
 * 
 * @author LeonTG77
 */
public class SpreadCommand implements CommandExecutor {
	public static final HashMap<String, Location> scatterLocs = new HashMap<String, Location>();
	public static boolean isReady = true;

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.spread")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 3) {
			sender.sendMessage(Main.PREFIX + "Usage: /spread <radius> <teamspread> <player|*>");
			return true;
		}

		Settings settings = Settings.getInstance();
		final String name = settings.getConfig().getString("game.world");
		
		if (Bukkit.getWorld(name) == null) {
			sender.sendMessage(ChatColor.RED + "There are no worlds called " + name + ".");
			return true;
		}
		
		final boolean teams;
		final int radius;
		
		try {
			radius = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invaild radius.");
			return true;
		}
		
		if (args[1].equalsIgnoreCase("true")) {
			teams = true;
		}
		else if (args[1].equalsIgnoreCase("false")) {
			teams = false;
		} 
		else {
			sender.sendMessage(ChatColor.RED + "Teamspread must be true of false"); 
			return true;
		}
		
		if (args[2].equalsIgnoreCase("*")) {
			State.setState(State.SCATTER);
			Parkour.getInstance().reset();
			isReady = false;
			
			int t = 0;
			int s = 0;

			Game game = Game.getInstance();
			
			if (!game.isFFA() && game.getTeamSize() > 1) {
				for (OfflinePlayer whitelisted : Bukkit.getServer().getWhitelistedPlayers()) {
					if (Scoreboards.getInstance().board.getEntryTeam(whitelisted.getName()) == null) {
						Team team = Teams.getInstance().findAvailableTeam();
						
						if (team != null) {
							team.addEntry(whitelisted.getName());
						}
					}
				}
			}
			
			for (Team te : Teams.getInstance().getTeams()) {
				if (te.getSize() > 0) {
					if (te.getSize() > 1) {
						t++;
					} else {
						s++;
					}
				}
			}
			
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
			}
			
			final int te = t;
			final int so = s;
			
			if (teams) {
				PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + t + " §7teams and §a" + s + " §7solos...");
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + Bukkit.getServer().getWhitelistedPlayers().size() + " §7players...");
			}
			
			for (Player online : PlayerUtils.getPlayers()) {
				online.playSound(online.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
			}
			
			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Finding scatter locations...");

					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
					}
					
					if (teams) {
						List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(name), radius, te + so);
						
						int index = 0;
						
						for (Team tem : Teams.getInstance().getTeamsWithPlayers()) {
							for (String player : tem.getEntries()) {
								scatterLocs.put(player, loc.get(index));
								
								PlayerUtils.getOfflinePlayer(player).setWhitelisted(true);
							}
							index++;
						}
						
						for (OfflinePlayer online : Bukkit.getServer().getWhitelistedPlayers()) {
							if (Scoreboards.getInstance().board.getEntryTeam(online.getName()) == null) {
								scatterLocs.put(online.getName(), loc.get(index));
								index++;
							}
						}
					} else {
						List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(name), radius, Bukkit.getServer().getWhitelistedPlayers().size());
					
						int index = 0;
						
						for (OfflinePlayer online : Bukkit.getServer().getWhitelistedPlayers()) {
							scatterLocs.put(online.getName(), loc.get(index));
							index++;
						}
					}
				}
			}.runTaskLater(Main.plugin, 30);

			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Locations found, loading chunks...");

					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
					}
					
					final ArrayList<Location> locs = new ArrayList<Location>(scatterLocs.values());
					final ArrayList<String> names = new ArrayList<String>(scatterLocs.keySet());
					
					new BukkitRunnable() {
						int i = 0;
						
						public void run() {
							if (i < locs.size()) {
								if (sender instanceof Player) {
									Player player = (Player) sender;
									player.teleport(locs.get(i));
								} else {
									locs.get(i).getChunk().load(true);
								}
								i++;
							} else {
								cancel();
								locs.clear();
								PlayerUtils.broadcast(Main.PREFIX + "All chunks loaded, starting scatter...");

								for (Player online : PlayerUtils.getPlayers()) {
									online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
								}
								
								new BukkitRunnable() {
									int i = 0;
									
									public void run() {
										if (i < names.size()) {
											Player scatter = Bukkit.getServer().getPlayer(names.get(i));
											
											if (scatter == null) {
												PlayerUtils.broadcast(Main.PREFIX + "- §c" + names.get(i) + " §7offline, scheduled.");
												
												for (Player online : PlayerUtils.getPlayers()) {
													online.playSound(online.getLocation(), "random.pop", 1, 0);
												}
											} else {
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 2));
												scatter.teleport(scatterLocs.get(names.get(i)));
												PlayerUtils.broadcast(Main.PREFIX + "- §a" + names.get(i) + " §7has been scattered.");
												scatterLocs.remove(names.get(i));
												
												for (Player online : PlayerUtils.getPlayers()) {
													online.playSound(online.getLocation(), "random.pop", 1, 0);
												}
											}
											i++;
										} else {
											PlayerUtils.broadcast(Main.PREFIX + "The scatter has finished.");
											isReady = true;
											names.clear();
											cancel();
											
											for (Player online : PlayerUtils.getPlayers()) {
												online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
											}
										}
									}
								}.runTaskTimer(Main.plugin, 40, 3);
							}
						}
					}.runTaskTimer(Main.plugin, 5, 5);
				}
			}.runTaskLater(Main.plugin, 60);
		} else {
			final Player target = Bukkit.getPlayer(args[2]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "That player is not online.");
				return true;
			}

			PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + target.getName() + "§7...");

			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Finding scatter location...");
					
					if (teams) {
						if (target.getScoreboard().getEntryTeam(target.getName()) == null) {
							List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(name), radius, 1);
							scatterLocs.put(target.getName(), loc.get(0));
							return;
						}
						
						Team tem = target.getScoreboard().getEntryTeam(target.getName());
						
						for (String tm : tem.getEntries()) {
							Player temmate = Bukkit.getServer().getPlayer(tm);
							
							if (temmate != null) {
								scatterLocs.put(target.getName(), temmate.getLocation());
								break;
							}
						}
					} else {
						List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(name), radius, 1);
						scatterLocs.put(target.getName(), loc.get(0));
					}
				}
			}.runTaskLater(Main.plugin, 30);

			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Location found, scattering...");
					
					if (!target.isOnline()) {
						PlayerUtils.broadcast(Main.PREFIX + "- §c" + target.getName() + " §7offline, scheduled.");
					} else {
						if (State.isState(State.SCATTER)) {
							target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
							target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
							target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 2));
						}
						
						target.teleport(scatterLocs.get(target.getName()));
						PlayerUtils.broadcast(Main.PREFIX + "- §a" + target.getName() + " §7has been scattered.");
						scatterLocs.remove(target.getName());
					}
				}
			}.runTaskLater(Main.plugin, 60);
		}
		return true;
	}
}