package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.State;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.GameUtils;

public class TimeLeftCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("timeleft")) {
			if (Game.getInstance().isRecordedRound()) {
				sender.sendMessage(Main.prefix() + "Current Episode: §a" + Runnables.meetup + " mins");
				sender.sendMessage(Main.prefix() + "Time to next episode: §a" + Runnables.heal + " mins");
				return true;
			}
			
			if (GameUtils.getTeamSize().startsWith("No") || GameUtils.getTeamSize().startsWith("Open")) {
				sender.sendMessage(Main.prefix() + "There are no matches running.");
				return true;
			}
			
			if (!State.isState(State.INGAME)) {
				sender.sendMessage(Main.prefix() + "The game has not started yet.");
				return true;
			}
			
			sender.sendMessage(Main.prefix() + "Timers:");
			sender.sendMessage(Runnables.healSeconds <= 0 ? "§8» §eFinal heal has passed." : "§8» §7Final heal in: §a" + DateUtils.ticksToString(Runnables.healSeconds));
			sender.sendMessage(Runnables.pvpSeconds <= 0 ? "§8» §aPvP is enabled." : "§8» §7PvP in: §a" + DateUtils.ticksToString(Runnables.pvpSeconds));
			sender.sendMessage(Runnables.meetupSeconds <= 0 ? "§8» §cMeetup is now!" : "§8» §7Meetup in: §a" + DateUtils.ticksToString(Runnables.meetupSeconds));
		}
		return true;
	}
}