package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Ms command class.
 * 
 * @author LeonTG77
 */
public class MsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can check their ping.");
				return true;
			}
			
			Player player = (Player) sender;
			player.sendMessage(Main.PREFIX + "Your ping: §6" + PlayerUtils.getPing(player));
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}
		
		sender.sendMessage(Main.PREFIX + target.getName() + "'s ping: §6" + PlayerUtils.getPing(target));
		return true;
	}
}