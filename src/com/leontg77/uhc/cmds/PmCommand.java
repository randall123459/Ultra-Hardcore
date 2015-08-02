package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;

public class PmCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use teamchat.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("pm")) {
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /pm <message>");
				return true;
			}
			
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			
			if (team == null) { 
	        	player.sendMessage(ChatColor.RED + "You are not on a team.");
	        	return true;
			} 
			
			StringBuilder message = new StringBuilder();
            
	        for (int i = 0; i < args.length; i++) {
	        	message.append(args[i]).append(" ");
	        }
	               
	        String msg = message.toString().trim();
	        
			for (String entry : player.getScoreboard().getEntryTeam(player.getName()).getEntries()) {
				Player teammate = Bukkit.getServer().getPlayer(entry);
				
				if (teammate != null) {
					teammate.sendMessage(Main.spectating.contains(player.getName()) ? "§9§lSpecChat §8§l» §7" + player.getName() + ": §f" + msg : "§9§lTeam §8§l» §7" + player.getName() + ": §f" + msg);
				}
			}
		}
		return true;
	}
}