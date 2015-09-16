package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.utils.PlayerUtils;
 
public class MuteCommand implements CommandExecutor {

	public boolean onCommand(CommandSender player, Command cmd, String label, String[] args) {
		Game game = Game.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("mute")) {
			if (player.hasPermission("uhc.mute")) {
				if (args.length == 0) {
					if (game.isMuted()) {
						game.setMuted(false);
						PlayerUtils.broadcast(Main.prefix() + "The chat has been enabled.");
						return true;
					} 
					
					PlayerUtils.broadcast(Main.prefix() + "The chat has been disabled.");
					game.setMuted(true);
					return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[0]);
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				User user = User.get(target);
	
				if (user.isMuted()) {
					user.setMuted(false);
					player.sendMessage(Main.prefix() + target.getName() + " unmuted.");
					target.sendMessage(Main.prefix() + "You are no longer muted.");
					return true;
				} 
				
				player.sendMessage(Main.prefix() + target.getName() + " muted");
				target.sendMessage(Main.prefix() + "You have been muted.");
				user.setMuted(true);
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}