package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.utils.PlayerUtils;

public class StartCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("start")) {
			if (sender.hasPermission("uhc.start")) {
				switch (State.getState()) {
				case INGAME:
					if (args.length < 3) {
						sender.sendMessage(ChatColor.RED + "Usage: /start <timetofinalheal> <timetopvp> <timetomeetup>");
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
					
					Runnables.heal = a;
					Runnables.pvp = b;
					Runnables.meetup = c;

					if (Game.getInstance().isRR()) {
						Runnables.timerRR();
					} else {
						Runnables.timer();
					}
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					
					if (!Game.getInstance().isRR()) {
						if (Runnables.heal > 0) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 60 &7Final heal is given in &8»&a");
						} 
						else if (Runnables.pvp > 0) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.pvp * 60) + " &7PvP is enabled in &8»&a");
						} 
						else if (Runnables.meetup > 0) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.meetup * 60) + " &7Meetup is in &8»&a");
						} 
						else {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 &6Meetup is now!");
						}
					}
					break;
				case LOBBY:
					sender.sendMessage(ChatColor.RED + "You cannot start the game without scattering first.");
					break;
				case SCATTER:
					PlayerUtils.broadcast(Main.prefix() + "The game is starting.");
					
					if (Game.getInstance().isRR()) {
						Runnables.startRR();
					} else {
						PlayerUtils.broadcast(Main.prefix() + "Remember to read the match post: " + Settings.getInstance().getConfig().getString("matchpost"));
						PlayerUtils.broadcast(Main.prefix() + "If you have any questions, use /helpop.");
						Runnables.start();
					}
					break;
				default:
					break;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}