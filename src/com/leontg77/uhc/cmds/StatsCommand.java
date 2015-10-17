package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;

/**
 * Stats command class.
 * 
 * @author LeonTG77
 */
public class StatsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		Game game = Game.getInstance();
		
		if (game.isRecordedRound()) {
			sender.sendMessage(Main.PREFIX + "Stats are disabled in RR's.");
			return true;
		}

		strhs
		// TODO: Finish status inventory.
		return true;
	}
}