package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Teams;

/**
 * TeamLoc command class.
 * 
 * @author LeonTG77
 */
public class TlCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can display their location to their.");
			return true;
		}
		
		Player player = (Player) sender;
		
		Spectator spec = Spectator.getInstance();
		Teams teams = Teams.getInstance();
		
		Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
        	player.sendMessage(ChatColor.RED + "You are not on a team.");
        	return true;
		} 
		
		teams.sendMessage(team, "§9§lTeam §8» §7" + player.getName() + "'s loc: §fx:" + player.getLocation().getBlockX() + " y:" + player.getLocation().getBlockY() + " z:" + player.getLocation().getBlockZ() + " (" + player.getWorld().getEnvironment().name().replaceAll("_", " ").replaceAll("NORMAL", "overworld").toLowerCase().replaceAll("normal", "overworld") + ")");
		return true;
	}
}