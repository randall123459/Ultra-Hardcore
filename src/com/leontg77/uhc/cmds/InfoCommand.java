package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

public class InfoCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("perma")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can enable permaday.");
				return true;
			}
			
			Player player = (Player) sender;
			
			if (player.hasPermission("uhc.perma")) {
				player.getWorld().setGameRuleValue("doDaylightCycle", "false");
				player.getWorld().setTime(6000);
				PlayerUtils.broadcast(Main.prefix() + "Permaday enabled.");
			} else {
				player.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		return true;
	}
}