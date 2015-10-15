package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class BoardCommand implements CommandExecutor {	
	private Settings settings = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Game game = Game.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("board")) {
			if (sender.hasPermission("uhc.board")) {
				if (game.pregameBoard()) {
					for (String e : Scoreboards.getInstance().kills.getScoreboard().getEntries()) {
						Scoreboards.getInstance().resetScore(e);
					}
					
					PlayerUtils.broadcast(Main.PREFIX + "Pregame board disabled.");
					game.setPregameBoard(false);
				} else {
					for (String e : Scoreboards.getInstance().kills.getScoreboard().getEntries()) {
						Scoreboards.getInstance().resetScore(e);
					}
					
					PlayerUtils.broadcast(Main.PREFIX + "Pregame board enabled.");
					game.setPregameBoard(true);

					if (game.teamManagement()) {
						Scoreboards.getInstance().setScore("§e ", 13);
						Scoreboards.getInstance().setScore("§8» §cTeam:", 12);
						Scoreboards.getInstance().setScore("§8» §7/team", 11);
					}
					if (Arena.getInstance().isEnabled()) {
						Scoreboards.getInstance().setScore("§a ", 10);
						Scoreboards.getInstance().setScore("§8» §cArena:", 9);
						Scoreboards.getInstance().setScore("§8» §7/a ", 8);
					}
					if (!GameUtils.getTeamSize().isEmpty()) {
						Scoreboards.getInstance().setScore("§b ", 7);
						Scoreboards.getInstance().setScore("§8» §cTeamsize:", 6);
						Scoreboards.getInstance().setScore("§8» §7" + GameUtils.getTeamSize(), 5);
					}
					Scoreboards.getInstance().setScore("§c ", 4);
					Scoreboards.getInstance().setScore("§8» §cScenarios:", 3);
					for (String scen : settings.getConfig().getString("game.scenarios").split(" ")) {
						Scoreboards.getInstance().setScore("§8» §7" + scen, 2);
					}
					Scoreboards.getInstance().setScore("§d ", 1);
				}
			} else {
				sender.sendMessage(Main.PREFIX + ChatColor.RED + "You can't use that command.");
			}
		}
		return true;
	}
}