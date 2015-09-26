package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.managers.Parkour;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.utils.ScatterUtils;

public class SpreadCommand implements CommandExecutor {
	public static final HashMap<String, Location> scatterLocs = new HashMap<String, Location>();
	public static boolean isReady = true;

	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("spread")) {
			if (sender.hasPermission("uhc.spread")) {
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /spread <radius> <teamspread> <player|*>");
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
					isReady = false;
					int t = 0;
					int s = 0;
					
					for (Team te : Teams.getManager().getTeams()) {
						if (te.getSize() > 0) {
							t++;
						}
					}
					
					for (OfflinePlayer online : Bukkit.getServer().getWhitelistedPlayers()) {
						if (Scoreboards.getManager().board.getEntryTeam(online.getName()) == null) {
							s++;
						}
					}
					
					final int te = t;
					final int so = s;
					
					if (teams) {
						PlayerUtils.broadcast(Main.prefix() + "Scattering §a" + t + " §7teams and §a" + s + " §7solos...");
					} else {
						PlayerUtils.broadcast(Main.prefix() + "Scattering §a" + Bukkit.getServer().getWhitelistedPlayers().size() + " §7players...");
					}
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
					}
					
					Parkour.getManager().shutdown();
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 §aScatter is currently going.");
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							PlayerUtils.broadcast(Main.prefix() + "Finding scatter locations...");

							for (Player online : PlayerUtils.getPlayers()) {
								online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
							}
							
							if (teams) {
								List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")), radius, te + so);
								
								int index = 0;
								
								for (Team tem : Teams.getManager().getTeamsWithPlayers()) {
									for (String player : tem.getEntries()) {
										scatterLocs.put(player, loc.get(index));
										
										PlayerUtils.getOfflinePlayer(player).setWhitelisted(true);
									}
									index++;
								}
								
								for (OfflinePlayer online : Bukkit.getServer().getWhitelistedPlayers()) {
									if (Scoreboards.getManager().board.getEntryTeam(online.getName()) == null) {
										scatterLocs.put(online.getName(), loc.get(index));
										index++;
									}
								}
							} else {
								List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")), radius, Bukkit.getServer().getWhitelistedPlayers().size());
							
								int index = 0;
								
								for (OfflinePlayer online : Bukkit.getServer().getWhitelistedPlayers()) {
									scatterLocs.put(online.getName(), loc.get(index));
									index++;
								}
							}
						}
					}, 30);
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							PlayerUtils.broadcast(Main.prefix() + "Locations found, loading chunks...");

							for (Player online : PlayerUtils.getPlayers()) {
								online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
							}
							
							final ArrayList<Location> a = new ArrayList<Location>(scatterLocs.values());
							final ArrayList<String> b = new ArrayList<String>(scatterLocs.keySet());
							
							new BukkitRunnable() {
								int i = 0;
								
								public void run() {
									if (i < a.size()) {
										if (sender instanceof Player) {
											Player player = (Player) sender;
											player.teleport(a.get(i));
										} else {
											a.get(i).getChunk().load(true);
										}
										i++;
									} else {
										cancel();
										a.clear();
										PlayerUtils.broadcast(Main.prefix() + "All chunks loaded, starting scatter...");

										for (Player online : PlayerUtils.getPlayers()) {
											online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
										}
										
										new BukkitRunnable() {
											int i = 0;
											
											public void run() {
												if (i < b.size()) {
													Player scatter = Bukkit.getServer().getPlayer(b.get(i));
													isReady = true;
													
													if (scatter == null) {
														PlayerUtils.broadcast(Main.prefix() + "- §c" + b.get(i) + " §7offline, scheduled.");
														
														for (Player online : PlayerUtils.getPlayers()) {
															online.playSound(online.getLocation(), "random.pop", 1, 0);
														}
													} else {
														scatter.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
														scatter.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
														scatter.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
														scatter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
														scatter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
														scatter.teleport(scatterLocs.get(b.get(i)));
														PlayerUtils.broadcast(Main.prefix() + "- §a" + b.get(i) + " §7has been scattered.");
														scatterLocs.remove(b.get(i));
														
														for (Player online : PlayerUtils.getPlayers()) {
															online.playSound(online.getLocation(), "random.pop", 1, 0);
														}
													}
													i++;
												} else {
													cancel();
													PlayerUtils.broadcast(Main.prefix() + "The scatter has finished.");
													b.clear();
													
													Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
													Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 §aScatter has finished, the game will start in just a bit.");
													
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
					}, 60);
				} else {
					final Player target = Bukkit.getPlayer(args[2]);
					
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}

					PlayerUtils.broadcast(Main.prefix() + "Scattering §a" + target.getName() + "§7...");
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							PlayerUtils.broadcast(Main.prefix() + "Finding scatter location...");
							
							if (teams) {
								if (target.getScoreboard().getEntryTeam(target.getName()) == null) {
									List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")), radius, 1);
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
								List<Location> loc = ScatterUtils.getScatterLocations(Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")), radius, 1);
								scatterLocs.put(target.getName(), loc.get(0));
							}
						}
					}, 30);
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							PlayerUtils.broadcast(Main.prefix() + "Location found, scattering...");
							
							if (!target.isOnline()) {
								PlayerUtils.broadcast(Main.prefix() + "- §c" + target.getName() + " §7offline, scheduled.");
							} else {
								if (State.isState(State.SCATTER)) {
									target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
									target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
									target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
									target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
									target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
								}
								target.teleport(scatterLocs.get(target.getName()));
								PlayerUtils.broadcast(Main.prefix() + "- §a" + target.getName() + " §7has been scattered.");
								scatterLocs.remove(target.getName());
							}
						}
					}, 60);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}