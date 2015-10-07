package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Spectator;

public class TlCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can display their location to their.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("teamloc")) {
			if (Spectator.getManager().isSpectating(player)) {
				player.sendMessage(ChatColor.RED + "You can't do this as a spectator.");
	        	return true;
	        }
			
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			
			if (team == null) { 
	        	player.sendMessage(ChatColor.RED + "You are not on a team.");
	        	return true;
			} 
			
			for (String entry : player.getScoreboard().getEntryTeam(player.getName()).getEntries()) {
				Player teammate = Bukkit.getServer().getPlayer(entry);
				
				if (teammate != null) {
					teammate.sendMessage("§9§lTeam §8» §7" + player.getName() + "'s coords: §fx:" + player.getLocation().getBlockX() + " y:" + player.getLocation().getBlockY() + " z:" + player.getLocation().getBlockZ() + " (" + player.getWorld().getEnvironment().name().replaceAll("_", " ").replaceAll("NORMAL", "overworld").toLowerCase().replaceAll("normal", "overworld") + ")");
				}
			}
		}
		return true;
	}
}