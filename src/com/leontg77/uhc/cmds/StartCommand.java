package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.State;
import com.leontg77.uhc.utils.PlayerUtils;

public class StartCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("start")) {
			if (sender.hasPermission("uhc.start")) {
				if (State.isState(State.LOBBY)) {
					sender.sendMessage(ChatColor.RED + "You cannot start the game without scattering first.");
				}
				else if (State.isState(State.SCATTER)) {
					PlayerUtils.broadcast(Main.prefix() + "The game is starting.");
					
					if (Game.getInstance().isRecordedRound()) {
						Runnables.startRR();
					} else {
						PlayerUtils.broadcast(Main.prefix() + "Remember to read the match post: " + Settings.getInstance().getConfig().getString("matchpost"));
						PlayerUtils.broadcast(Main.prefix() + "If you have any questions, use /helpop.");
						Runnables.start();
					}
				}
				else if (State.isState(State.INGAME)) {
					if (args.length < 3) {
						sender.sendMessage(ChatColor.RED + "Usage: /start <timetofinalheal> <timetopvp> <timetomeetup>");
						return true;
					}
					
					int heal;
					int pvp;
					int meetup;
					
					try {
						heal = Integer.parseInt(args[0]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild number.");
						return true;
					}
					
					try {
						pvp = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild number.");
						return true;
					}
					
					try {
						meetup = Integer.parseInt(args[2]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild number.");
						return true;
					}
					
					Runnables.heal = heal;
					Runnables.pvp = pvp;
					Runnables.meetup = meetup;
					
					Runnables.healSeconds = (heal > 0 ? (heal * 60) : 0);
					Runnables.pvpSeconds = (pvp > 0 ? (pvp * 60) : 0);
					Runnables.meetupSeconds = (meetup > 0 ? (meetup * 60) : 0);

					if (Game.getInstance().isRecordedRound()) {
						Runnables.timerRR();
					} else {
						Runnables.timer();
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}