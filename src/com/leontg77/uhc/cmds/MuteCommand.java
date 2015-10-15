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

/**
 * Mute command class
 * 
 * @author LeonTG77
 */
public class MuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.mute")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			Game game = Game.getInstance();
			
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
			sender.sendMessage(ChatColor.RED + "That player is not online.");
			return true;
		}
		
		User user = User.get(target);

		if (user.isMuted()) {
			user.unmute();
			
			PlayerUtils.broadcast(Main.prefix() + "§6" + target.getName() + " §7has been unmuted.");
			target.sendMessage(Main.prefix() + "You are no longer muted.");
			return true;
		} 
		
		if (args.length < 3) {
			sender.sendMessage(Main.prefix() + "Usage: /mute <player> <time> <reason>");
			return true;
		}
		
		StringBuilder message = new StringBuilder("");
		
    	for (int i = 2; i < args.length; i++) {
    		message.append(args[i]).append(" ");
    	}
    	
    	String reason = message.toString().trim();
		
		PlayerUtils.broadcast(Main.prefix() + "§6" + target.getName() + " §7has been " + (args[1].equals("-") ? "muted" : "temp-muted") + " for §a" + reason);
		target.sendMessage(Main.prefix() + "You have been muted for §a" + reason + "§7.");
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		long time = DateUtils.parseDateDiff(args[1], true);
		
		user.mute(reason, (time <= 0 ? null : new Date(time)));
		return true;
	}
}