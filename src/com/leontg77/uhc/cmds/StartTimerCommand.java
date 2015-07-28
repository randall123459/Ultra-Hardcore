package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.cmds.ConfigCommand.Border;
import com.leontg77.uhc.util.PlayerUtils;

public class StartTimerCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("starttimer")) {
			if (sender.hasPermission("uhc.starttimer")) {
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /starttimer <timetofinalheal> <timetopvp> <timetomeetup>");
					return true;
				}
				
				int a;
				
				try {
					a = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild number.");
					return true;
				}
				
				int b;
				
				try {
					b = Integer.parseInt(args[1]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild number.");
					return true;
				}
				
				int c;
				
				try {
					c = Integer.parseInt(args[2]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild number.");
					return true;
				}
				
				Runnables.finalheal = a;
				Runnables.pvp = b;
				Runnables.meetup = c;
				
				Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
					public void run() {
						Runnables.finalheal--;
						Runnables.pvp--;
						Runnables.meetup--;
						
						if (Runnables.finalheal == 0) {
							for (Player online : PlayerUtils.getPlayers()) {
								online.sendMessage(Main.prefix() + "§6§lFinal heal has been given, do not ask for another one.");
								online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
								online.setHealth(20.0);
								online.setFireTicks(0);
								online.setFoodLevel(20);
								online.setSaturation(20);
							}

							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.pvp * 60) + " &aPvP in:&7");
						}
						
						if (Runnables.finalheal == -2) {
							for (World world : Bukkit.getWorlds()) {
								if (world.getName().equals("lobby") || world.getName().equals("arena")) {
									continue;
								}
								
								world.setGameRuleValue("doMobSpawning", "true");
							}
						}

						if (Runnables.pvp == 0) {
							for (Player online : PlayerUtils.getPlayers()) {
								online.sendMessage(Main.prefix() + "§6§lPvP has been enabled.");
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

								if (Main.border == Border.PVP) {
									if (world.getName().equals("lobby") || world.getName().equals("arena")) {
										continue;
									}
										
									world.getWorldBorder().setSize(299, Runnables.meetup * 60);
								}
							}
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.meetup * 60) + " &aMeetup in:&7");
						}
						
						if (Runnables.pvp == 5) {
							PlayerUtils.broadcast(Main.prefix() + "5 minutes to pvp, do §6/stalk §7if you know you're being stalked.");
							for (Player online : PlayerUtils.getPlayers()) {
								online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
							}
						}
						
						if (Runnables.pvp == 1) {
							PlayerUtils.broadcast(Main.prefix() + "1 minute to pvp, do §6/stalk §7if you know you're being stalked.");
							for (Player online : PlayerUtils.getPlayers()) {
								online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
							}
						}
						
						if (Runnables.meetup == 0) {
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
							}
						}
						
						if (Runnables.meetup == 1) {
							PlayerUtils.broadcast(Main.prefix() + "1 minute to meetup, Pack your stuff and get ready to head to 0,0.");
							for (Player online : PlayerUtils.getPlayers()) {
								online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
							}
						}
					}
				}, 1200, 1200);

				
				if (Runnables.finalheal > 0) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.finalheal * 60) + " &aFinal heal in:&7");
					return true;
				}
				
				if (Runnables.pvp > 0) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.pvp * 60) + " &aPvP in:&7");
					return true;
				}

				if (Runnables.meetup > 0) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.meetup * 60) + " &aMeetup in:&7");
					return true;
				}
				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 &6Meetup is now! Head to 0,0 and only stop for a fight!");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}