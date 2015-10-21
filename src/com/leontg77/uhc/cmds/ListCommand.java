package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * List command class.
 * 
 * @author LeonTG77
 */
public class ListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (PlayerUtils.getPlayers().size() < 1) {
	    	sender.sendMessage(Main.PREFIX + "There are no players online.");
			return true;
		}

		Game game = Game.getInstance();
		
		ArrayList<Player> players = new ArrayList<Player>(PlayerUtils.getPlayers());
		Collections.shuffle(players);
		
    	StringBuilder list = new StringBuilder();
    	int p = 1;
    		
    	for (int i = 0; i < players.size(); i++) {
    		if (sender instanceof Player && !((Player) sender).canSee(players.get(i))) {
    			continue;
    		}
    		
			if (list.length() > 0) {
				if (p == players.size()) {
					list.append(" §8and §a");
				} else {
					list.append("§8, §a");
				}
			}
			
			list.append(players.get(i).getName());
			p++;
		}
    			
    	sender.sendMessage(Main.PREFIX + "There are §6" + (p - 1) + " §7out of§6 " + game.getMaxPlayers() + " §7players online.");
    	sender.sendMessage("§8» §7Players§8: §a" + list.toString() + "§8.");
		return true;
	}
}