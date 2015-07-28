package com.leontg77.uhc.cmds;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.util.PlayerUtils;

public class ListCommand implements CommandExecutor {
	private Settings settings = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("list")) {
			List<Player> players = PlayerUtils.getPlayers();
	    	StringBuilder onlineList = new StringBuilder();
	    	int p = 0;
	    		
	    	for (int i = 0; i < players.size(); i++) {
				if (onlineList.length() > 0 && i == players.size() - 1) {
					onlineList.append(" §7and §f");
				}
				else if (onlineList.length() > 0 && onlineList.length() != players.size()) {
					onlineList.append("§7, §f");
				}
				
				onlineList.append(players.get(i).getName());
				p++;
			}
	    			
	    	sender.sendMessage(Main.prefix() + "There are currently " + ChatColor.GOLD + p + ChatColor.GRAY + " out of " + ChatColor.GOLD + settings.getData().getInt("maxplayers") + ChatColor.GRAY + " players online.");
	    	sender.sendMessage("§7Players: §f" + onlineList.toString());
	    	p = 0;
        }
		return true;
	}
}