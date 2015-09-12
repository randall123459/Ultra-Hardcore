package com.leontg77.uhc.cmds;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class UnbanIPCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unban")) {
			if (sender.hasPermission("uhc.unban")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /unbanip <ip>");
					return true;
				}
		    	
				PlayerUtils.broadcast(Main.prefix() + "An ip has been unbanned.", "uhc.unban");
		   		Bukkit.getServer().getBanList(Type.IP).pardon(args[0]);
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}