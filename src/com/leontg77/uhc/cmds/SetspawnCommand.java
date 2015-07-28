package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

public class SetspawnCommand implements CommandExecutor {
	private Settings settings = Settings.getInstance();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can set the spawn.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("setspawn")) {
			if (player.hasPermission("uhc.setspawn")) {
				settings.getData().set("spawn.world", player.getLocation().getWorld().getName());
				settings.getData().set("spawn.x", player.getLocation().getX());
				settings.getData().set("spawn.y", player.getLocation().getY());
				settings.getData().set("spawn.z", player.getLocation().getZ());
				settings.getData().set("spawn.yaw", player.getLocation().getYaw());
				settings.getData().set("spawn.pitch", player.getLocation().getPitch());
		        settings.saveData();
		        player.sendMessage(Main.prefix() + "You have set the lobby spawn.");
		        return true;
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}