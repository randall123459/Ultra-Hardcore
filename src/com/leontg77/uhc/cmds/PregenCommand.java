package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.worlds.pregen.Pregenner;

/**
 * Pregen command class.
 * 
 * @author LeonTG77
 */
public class PregenCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.pregen")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		Pregenner pregen = Pregenner.getInstance();
		
		if (args.length < 2) {
			if (args.length > 0 && args[0].equalsIgnoreCase("cancel")) {
				/*pregen.stop();*/
				return true;
			}
			
			sender.sendMessage(Main.PREFIX + "Usage: /pregen <world> <radius>");
			return true;
		}
		
		World world = Bukkit.getServer().getWorld(args[0]);
		
		if (world == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not an world.");
			return true;
		}
		
		int radius;
		
		try {
			radius = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild radius.");
			return true;
		}
		
		/*pregen.start(world, radius, false);*/
		return true;
	}
}