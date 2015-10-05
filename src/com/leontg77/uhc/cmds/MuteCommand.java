package com.leontg77.uhc.cmds;

import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PlayerUtils;
 
public class MuteCommand implements CommandExecutor {

	public boolean onCommand(CommandSender player, Command cmd, String label, String[] args) {
		Game game = Game.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("mute")) {
			if (player.hasPermission("uhc.mute")) {
				if (args.length == 0) {
					if (game.isMuted()) {
						game.setMuted(false);
						PlayerUtils.broadcast(Main.prefix() + "Global chat has been enabled.");
						return true;
					} 
					
					PlayerUtils.broadcast(Main.prefix() + "Global chat has been disabled.");
					game.setMuted(true);
					return true;
				}

				TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
				Player target = Bukkit.getServer().getPlayer(args[0]);
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				User user = User.get(target);
	
				if (user.isMuted()) {
					user.setMuted(false, "NOT_MUTED", null, "NONE");
					player.sendMessage(Main.prefix() + "§6" + target.getName() + " §7has been unmuted.");
					target.sendMessage(Main.prefix() + "You are no longer muted.");
					return true;
				} 
				
				if (args.length < 3) {
					player.sendMessage(Main.prefix() + "Usage: /mute <player> <time> <reason>");
					return true;
				}
				
	    		StringBuilder reason = new StringBuilder("");
	    		
	        	for (int i = 2; i < args.length; i++) {
	        		reason.append(args[i]).append(" ");
	        	}

				long time = DateUtils.parseDateDiff(args[1], true);
	        	String msg = reason.toString().trim();
				
				PlayerUtils.broadcast(Main.prefix() + "§6" + target.getName() + " §7has been muted for §a" + msg);
				target.sendMessage(Main.prefix() + "You have been muted.");
				user.setMuted(true, msg, (args[1].equals("-") ? null : new Date(time)), player.getName());
			} else {
				player.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
}