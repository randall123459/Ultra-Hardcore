package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.utils.PlayerUtils;

public class AboardCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("aboard")) {
			if (sender.hasPermission("uhc.aboard")) {
				if (Main.aboard) {
					for (String e : Arena.getManager().arenaKills.getScoreboard().getEntries()) {
						Arena.getManager().resetScore(e);
					}
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been disabled.");
					Scoreboards.getManager().kills.setDisplaySlot(DisplaySlot.SIDEBAR);
					Main.aboard = false;
				} else {
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been enabled.");
					Arena.getManager().arenaKills.setDisplaySlot(DisplaySlot.SIDEBAR);
					Main.aboard = true;
					Main.board = false;

					Arena.getManager().setScore("§8» §a§lPvE", 1);
					Arena.getManager().setScore("§8» §a§lPvE", 0);
				}
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		return true;
	}
}