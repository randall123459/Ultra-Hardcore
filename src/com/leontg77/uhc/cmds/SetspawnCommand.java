package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

/**
 * Setspawn command class.
 * 
 * @author LeonTG77
 */
public class SetspawnCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can set the spawn point.");
			return true;
		}

		Settings settings = Settings.getInstance();
		Player player = (Player) sender;
		
		if (!player.hasPermission("uhc.setspawn")) {
			player.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		settings.getData().set("spawn.world", player.getLocation().getWorld().getName());
		settings.getData().set("spawn.x", player.getLocation().getX());
		settings.getData().set("spawn.y", player.getLocation().getY());
		settings.getData().set("spawn.z", player.getLocation().getZ());
		settings.getData().set("spawn.yaw", player.getLocation().getYaw());
		settings.getData().set("spawn.pitch", player.getLocation().getPitch());
        settings.saveData();
        
        player.sendMessage(Main.PREFIX + "You have set the spawnpoint.");
		return true;
	}
}