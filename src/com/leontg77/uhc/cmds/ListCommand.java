package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.utils.PlayerUtils;

public class ListCommand implements CommandExecutor {
	private Settings settings = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("list")) {
			if (PlayerUtils.getPlayers().size() < 1) {
		    	sender.sendMessage(Main.prefix() + "There are no players online.");
				return true;
			}
			
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
	    			
	    	sender.sendMessage(Main.prefix() + "There are §6" + (p - 1) + " §7out of§6 " + settings.getConfig().getInt("maxplayers") + " §7players online.");
	    	sender.sendMessage("§8» §7Players§8: §a" + list.toString() + "§8.");
        }
		return true;
	}
}