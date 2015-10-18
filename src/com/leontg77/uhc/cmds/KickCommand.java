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
 * Kick command class.
 * 
 * @author LeonTG77
 */
public class KickCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.kick")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Main.PREFIX + "Usage: /kick <player> <reason>");
			return true;
		}
					
		StringBuilder reason = new StringBuilder("");
			
		for (int i = 1; i < args.length; i++) {
			reason.append(args[i]).append(" ");
		}
				
		String msg = reason.toString().trim();

		if (args[0].equals("*")) {
			for (Player online : PlayerUtils.getPlayers()) {
				if (!online.hasPermission("uhc.prelist")) {
			    	online.kickPlayer("§8» §7" + msg + " §8«");
				}
			}
			
	    	PlayerUtils.broadcast(Main.PREFIX + "§7All players has been kicked for §6" + msg);
			return true;
		}
    	
    	Player target = Bukkit.getServer().getPlayer(args[0]);
		
    	if (target == null) {
    		sender.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
		}
    	
    	PlayerUtils.broadcast(Main.PREFIX + "§6" + args[0] + " §7has been kicked for §a" + msg);
    	target.kickPlayer("§8» §7" + msg + " §8«");
		return true;
	}
}