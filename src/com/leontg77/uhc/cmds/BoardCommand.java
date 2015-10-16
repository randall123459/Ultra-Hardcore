package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Board command class.
 * 
 * @author LeonTG77
 */
public class BoardCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.board")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		Scoreboards score = Scoreboards.getInstance();
		Game game = Game.getInstance();
		
		if (game.pregameBoard()) {
			for (String entry : score.board.getEntries()) {
				score.resetScore(entry);
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Cleared pregame board.");
			game.setPregameBoard(false);
			return true;
		}
		
		for (String entry : score.board.getEntries()) {
			score.resetScore(entry);
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "Generated pregame board.");
		game.setPregameBoard(true);

		if (game.teamManagement()) {
			score.setScore("§e ", 12);
			score.setScore("§8» §cTeam:", 11);
			score.setScore("§8» §7/team", 10);
		}
		
		if (Arena.getInstance().isEnabled()) {
			score.setScore("§a ", 9);
			score.setScore("§8» §cArena:", 8);
			score.setScore("§8» §7/a ", 7);
		}
		
		if (!GameUtils.getTeamSize().isEmpty()) {
			score.setScore("§b ", 6);
			score.setScore("§8» §cTeamsize:", 5);
			score.setScore("§8» §7" + GameUtils.getAdvancedTeamSize(), 4);
		}
		
		score.setScore("§c ", 3);
		score.setScore("§8» §cScenarios:", 2);
		
		for (String scen : game.getScenarios().split(" ")) {
			score.setScore("§8» §7" + scen, 1);
		}
		
		score.setScore("§d ", 1);
		score.setScore("§d ", 0);
		return true;
	}
}