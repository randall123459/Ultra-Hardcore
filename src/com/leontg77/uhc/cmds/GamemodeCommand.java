package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

/**
 * Gamemode command class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class GamemodeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.gamemode")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /gamemode <mode> [player]");
			return true;
		}
		
		GameMode mode = null;
		
		try {
			mode = GameMode.getByValue(Integer.parseInt(args[0]));
		} 
		catch (Exception e) {
			for (GameMode modes : GameMode.values()) {
				if (modes.name().startsWith(args[0].toUpperCase())) {
					mode = modes;
					break;
				}
			}
		}
		
		if (mode == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild gamemode.");
			return true;
		}
		
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can change their own gamemode."); 
				return true;
			}
			
			Player player = (Player) sender;
			
			player.sendMessage(Main.PREFIX + "You are now in §6" + mode.name().toLowerCase() + " §7mode.");
			player.setGameMode(mode);
			return true;
		}
		
		if (!sender.hasPermission("uhc.gamemode.other")) {
			sender.sendMessage(Main.PREFIX + "§cYou cannot change the gamemode of other players.");
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[1]);
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}

		sender.sendMessage(Main.PREFIX + "You have changed §a" + target.getName() + "'s §7gamemode to §6" + mode.name().toLowerCase() + " §7mode.");
		target.sendMessage(Main.PREFIX + "You are now in §6" + mode.name().toLowerCase() + " §7mode.");
		target.setGameMode(mode);
		return true;
	}
}