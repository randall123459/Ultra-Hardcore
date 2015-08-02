package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Runnables;

public class TimeLeftCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("timeleft")) {
			if (State.isState(State.INGAME)) {
				sender.sendMessage(Main.prefix() + "Time left information:");
				sender.sendMessage(Runnables.finalheal <= 0 ? "§8§l» §7Final heal has passed." : "§8§l» §7" + Runnables.finalheal + " minutes to final heal.");
				sender.sendMessage(Runnables.pvp <= 0 ? "§8§l» §7PvP is enabled." : "§8§l» §7" + Runnables.finalheal + " minutes to pvp.");
				sender.sendMessage(Runnables.meetup <= 0 ? "§8§l» §7Meetup is now!" : "§8§l» §7" + Runnables.meetup + " minutes to meetup.");
			} else {
				sender.sendMessage(Main.prefix() + "The game has not started yet.");
			}
		}
		return true;
	}
}