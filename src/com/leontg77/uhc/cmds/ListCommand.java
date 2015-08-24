package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
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
						list.append(" §7and §f");
					} else {
						list.append("§7, §f");
					}
				}
				
				list.append(players.get(i).getName());
				p++;
			}
	    			
	    	sender.sendMessage(Main.prefix() + "There are currently " + ChatColor.GOLD + (p - 1) + ChatColor.GRAY + " out of " + ChatColor.GOLD + settings.getConfig().getInt("maxplayers") + ChatColor.GRAY + " players online.");
	    	sender.sendMessage("§7Players: §f" + list.toString());
        }
		return true;
	}
}