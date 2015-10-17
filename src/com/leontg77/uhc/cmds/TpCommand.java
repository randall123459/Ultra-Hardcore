package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;

/**
 * Tp command class
 * 
 * @author LeonTG77
 */
public class TpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Spectator spec = Spectator.getInstance();
		
		if (!sender.hasPermission("uhc.tp") && !spec.isSpectating(sender.getName())) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /tp <player> [player]");
			return true;
		}
		
		Player targetOne = Bukkit.getServer().getPlayer(args[0]);
		
		if (targetOne == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}
		
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can teleport to players.");
				return true;
			}

			Player player = (Player) sender;
			
			player.sendMessage(Main.PREFIX + "You teleported to §a" + targetOne.getName() + "§7.");
			player.teleport(targetOne);
			return true;
		}
		
		if (!sender.hasPermission("uhc.tp.other")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		Player targetTwo = Bukkit.getServer().getPlayer(args[1]);
		
		if (targetTwo == null) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
			return true;
		}
		
		sender.sendMessage(Main.PREFIX + "You teleported §a" + targetOne.getName() + "§7 to §a" + targetTwo.getName() + "§7.");
		targetOne.teleport(targetTwo);
		return true;
	}
}