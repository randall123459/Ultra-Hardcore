package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Teams;

/**
 * Pm command class.
 * 
 * @author LeonTG77
 */
public class PmCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use teamchat.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length == 0) {
			player.sendMessage(Main.PREFIX + "Usage: /pm <message>");
			return true;
		}
		
		Spectator spec = Spectator.getInstance();
		Teams teams = Teams.getInstance();
		
		Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
        	player.sendMessage(ChatColor.RED + "You are not on a team.");
        	return true;
		} 
		
		StringBuilder message = new StringBuilder();
        
        for (int i = 0; i < args.length; i++) {
        	message.append(args[i]).append(" ");
        }
               
        String msg = message.toString().trim();
        
        teams.sendMessage(team, "§9§lTeam §8» §7" + player.getName() + ": §f" + msg);
		return true;
	}
}