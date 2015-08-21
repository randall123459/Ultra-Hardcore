package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Data;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;
 
public class MuteCommand implements CommandExecutor {

	public boolean onCommand(CommandSender player, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mute")) {
			if (player.hasPermission("uhc.mute")) {
				if (args.length == 0) {
					if (Main.muted) {
						Main.muted = false;
						PlayerUtils.broadcast(Main.prefix() + "The chat has been enabled.");
						return true;
					} 
					
					PlayerUtils.broadcast(Main.prefix() + "The chat has been disabled.");
					Main.muted = true;
					return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[0]);
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				Data data = Data.getFor(target);
	
				if (data.isMuted()) {
					data.setMuted(false);
					player.sendMessage(Main.prefix() + target.getName() + " unmuted.");
					target.sendMessage(Main.prefix() + "You are no longer muted.");
					return true;
				} 
				
				player.sendMessage(Main.prefix() + target.getName() + " muted");
				target.sendMessage(Main.prefix() + "You have been muted.");
				data.setMuted(true);
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}