package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

/**
 * Fly command class.
 * 
 * @author LeonTG77
 */
public class FlyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.fly")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can can toggle flymode.");
				return true;
			}
			
			Player player = (Player) sender;
			
			if (player.getAllowFlight()) {
				player.sendMessage(Main.PREFIX + "Your fly mode is now disabled.");
				player.setAllowFlight(false);
				player.setFlying(false);
			} else {
				player.sendMessage(Main.PREFIX + "Your fly mode is now enabled.");
				player.setAllowFlight(true);
			}
			return true;
		}
		
		if (!sender.hasPermission("uhc.fly.other")) {
			sender.sendMessage(Main.PREFIX + "§cYou cannot toggle other fly mode.");
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}
		
		if (target.getAllowFlight()) {
			sender.sendMessage(Main.PREFIX + "You disabled §a" + target.getName() + "'s §7 fly mode.");
			target.sendMessage(Main.PREFIX + "Your fly mode is now disabled.");
			target.setAllowFlight(false);
			target.setFlying(false);
		} else {
			sender.sendMessage(Main.PREFIX + "You enabled §a" + target.getName() + "'s §7 fly mode.");
			target.sendMessage(Main.PREFIX + "Your fly mode is now enabled.");
			target.setAllowFlight(true);
		}
		return true;
	}
}