package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.GameState;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

@SuppressWarnings("deprecation")
public class SpreadCommand implements CommandExecutor {

	public boolean onCommand(CommandSender player, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("spread")) {
			if (player.hasPermission("uhc.spread")) {
				if (args.length < 4) {
					player.sendMessage(ChatColor.RED + "Usage: /spread <radius> <maxradius> <teamspread> <player|*>");
					return true;
				}
				
				if (args[3].equalsIgnoreCase("*")) {
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						online.sendMessage(Main.prefix() + "Scattering...");
						online.sendMessage(Main.prefix() + "Loading chunks, expect lag for a sec.");
						online.teleport(new Location(Bukkit.getWorld(Settings.getInstance().getData().getString("game.world")), 0.5, 200, 0.5));
						online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
						online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
						online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
						online.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
						online.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
					}
					
					final int radius;
					final int maxradius;
					
					try {
						radius = Integer.parseInt(args[0]);
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Invaild radius.");
						return true;
					}
					
					try {
						maxradius = Integer.parseInt(args[1]);
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Invaild max radius.");
						return true;
					}
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						@Override
						public void run() {
							for (Player online : Bukkit.getServer().getOnlinePlayers()) {
								online.sendMessage(Main.prefix() + "Chunks loaded, scattering...");
							}
						}
					}, 30);
						
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						@Override
						public void run() {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 " + radius + " " + maxradius + " " + args[2] + " @a");
							for (Player online : Bukkit.getServer().getOnlinePlayers()) {
								if (online.getScoreboard().getEntryTeam(online.getName()) == null) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 " + radius + " " + maxradius + " false " + online.getName());
								}
								online.teleport(new Location(online.getWorld(), online.getLocation().getX(), 255, online.getLocation().getZ()));
								online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
								online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
								online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
								online.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
								online.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
								online.sendMessage(Main.prefix() + "Everyone has been scattered.");
							}
						}
					}, 60);
					GameState.setState(GameState.WAITING);
				} else {
					final Player target = Bukkit.getServer().getPlayer(args[3]);
					
					if (target == null) {
						player.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}

					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						online.sendMessage(Main.prefix() + "Scattering " + target.getName() + "...");
					}
					target.teleport(new Location(Bukkit.getWorld(Settings.getInstance().getData().getString("game.world")), 0.5, 200, 0.5));
					target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
					target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
					target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
					target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
					target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
					
					final int radius;
					final int maxradius;
					
					try {
						radius = Integer.parseInt(args[0]);
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Invaild radius.");
						return true;
					}
					
					try {
						maxradius = Integer.parseInt(args[1]);
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Invaild max radius.");
						return true;
					}
						
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						@Override
						public void run() {
							for (Player online : Bukkit.getServer().getOnlinePlayers()) {
								online.sendMessage(Main.prefix() + "Scattered " + target.getName() + ".");
							}
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 " + radius + " " + maxradius + " false " + target.getName());
							target.teleport(new Location(target.getWorld(), target.getLocation().getX(), 255, target.getLocation().getZ()));
							target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
							target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
							target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
						}
					}, 60);
					
					if (args[2].equalsIgnoreCase("true")) {
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
							@Override
							public void run() {
								Team t = target.getScoreboard().getPlayerTeam(target);
								if (t == null) {
									return;
								}
								for (Player online2 : Bukkit.getServer().getOnlinePlayers()) {
									Team t2 = online2.getScoreboard().getPlayerTeam(online2);
									if (t2 == null) {
										continue;
									}
									if (t == t2 && target != online2) {
										target.teleport(online2);
										target.teleport(target.getLocation().add(0, 2, 0));
									}	
								}	
							}
						}, 70);
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}