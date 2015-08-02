package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.util.PlayerUtils;

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
					
					Runnables.finalheal = a;
					Runnables.pvp = b;
					Runnables.meetup = c;
					
					Runnables.start();
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
					
					if (Runnables.finalheal > 0) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 60 &aFinal heal in:&7");
					} else if (Runnables.pvp > 0) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.pvp * 60) + " &aPvP in:&7");
					} else if (Runnables.meetup > 0) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer " + (Runnables.meetup * 60) + " &aMeetup in:&7");
					} else {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer -1 &6Meetup is now! Head to 0,0 and only stop for a fight!");
					}
					break;
				case LOBBY:
					sender.sendMessage(ChatColor.RED + "You cannot start the game without scattering first.");
					break;
				case SCATTER:
					PlayerUtils.broadcast(Main.prefix() + "The game is starting.");
					Main.startCountdown();
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