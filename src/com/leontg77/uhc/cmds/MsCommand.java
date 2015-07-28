package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class MsCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("ms")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.sendMessage(Main.prefix() + "Your ping: §6" + PlayerUtils.getPing(player));
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can check their ping.");
				}
				return true;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "That player is not online.");
				return true;
			}
			
			sender.sendMessage(Main.prefix(ChatColor.GOLD) + target.getName() + "'s §7ping: §6" + PlayerUtils.getPing(target));
		}
		return true;
	}
}