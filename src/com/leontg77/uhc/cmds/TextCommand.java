package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

public class TextCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can spawn texts.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("text")) {
			if (player.hasPermission("uhc.text")) {
				if (args.length == 0) {
					player.sendMessage(Main.prefix() + "Usage: /text <message>");
					return true;
				}
				
				StringBuilder name = new StringBuilder();
				
				for (int i = 0; i < args.length; i++) {
					name.append(args[i]).append(" ");
				}
				
				ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
				stand.setCustomName(ChatColor.translateAlternateColorCodes('&', name.toString().trim()));
				stand.setCustomNameVisible(true);
				stand.setGravity(false);
				stand.setVisible(false);
				stand.setSmall(true);
			} else {
				player.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		return true;
	}
}