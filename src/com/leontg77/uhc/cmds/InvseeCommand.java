package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Spectator;

public class InvseeCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can open inventories.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("invsee")) {
			if (player.hasPermission("uhc.invsee") || Spectator.getManager().isSpectating(sender.getName())) {
				if (args.length == 0) {
		    		player.sendMessage(ChatColor.RED + "Usage: /invsee <player>");
		    		return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[0]);
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				} 
				
				InvGUI.getManager().openInv(player, target);
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
        }
		return true;
	}
}