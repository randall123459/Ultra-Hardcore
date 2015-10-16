package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Broadcast command class.
 * 
 * @author LeonTG77
 */
public class BroadcastCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.broadcast")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		StringBuilder message = new StringBuilder("");
		
		for (int i = 0; i < args.length; i++) {
			message.append(args[i]).append(" ");
		}
		
		String msg = ChatColor.translateAlternateColorCodes('&', message.toString().trim()).trim();
		
		PlayerUtils.broadcast(Main.PREFIX + msg);
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
		}
		return true;
	}
}