package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Border command class.
 * 
 * @author LeonTG77
 */
public class BorderCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /border <world> [size]");
			return true;
		}
		
		World world = Bukkit.getWorld(args[0]);
		
		if (world == null) {
			sender.sendMessage(ChatColor.RED + "There are no worlds with the name " + args[0]);
			return true;
		}

		WorldBorder border = world.getWorldBorder();
		
		if (args.length == 1) {
			int size = (int) border.getSize();
			
			sender.sendMessage(Main.PREFIX + "The borders in '§6" + world.getName() + "§7' are currently: §a" + size + "x" + size);
			return true;
		}
		
		int radius;
		
		try {
			radius = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not an vaild radius.");
			return true;
		}

		border.setSize(radius);
		border.setWarningDistance(0);
		border.setWarningTime(60);
		border.setDamageAmount(0.1);
		border.setDamageBuffer(0);
		
		if (world.getEnvironment() == Environment.NETHER) {
			border.setCenter(0.5, 0.5);
		} else {
			border.setCenter(0.0, 0.0);
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "Borders in world '§6" + world.getName() + "§7' has been setup with radius §a" + radius + "x" + radius + "§7.");
		return true;
	}
}