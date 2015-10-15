package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.utils.PlayerUtils;

public class AboardCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Game game = Game.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("aboard")) {
			if (sender.hasPermission("uhc.aboard")) {
				if (game.arenaBoard()) {
					for (String e : Arena.getInstance().arenaKills.getScoreboard().getEntries()) {
						Arena.getInstance().resetScore(e);
					}
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been disabled.");
					Scoreboards.getInstance().kills.setDisplaySlot(DisplaySlot.SIDEBAR);
					game.setArenaBoard(false);
				} else {
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been enabled.");
					Arena.getInstance().arenaKills.setDisplaySlot(DisplaySlot.SIDEBAR);
					game.setPregameBoard(false);
					game.setArenaBoard(true);

					Arena.getInstance().setScore("§8» §a§lPvE", 1);
					Arena.getInstance().setScore("§8» §a§lPvE", 0);
				}
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		return true;
	}
}