package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class StaffChatCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ac")) {
			if (sender.hasPermission("uhc.staff")) {
				if (args.length == 0) {
		    		sender.sendMessage(ChatColor.RED + "Usage: /ac <message>");
		        	return true;
		        } 
		        
		    	StringBuilder message = new StringBuilder("");
				
				for (int i = 0; i < args.length; i++) {
					message.append(args[i]).append(" ");
				}
		               
		        String msg = message.toString().trim();

				PlayerUtils.broadcast("§c§lStaffChat §8» §a" + (sender instanceof Player ? sender.getName() : NameUtils.fixString(sender.getName(), false)) + "§8: §f" + msg, "uhc.staff");
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
}