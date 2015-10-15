package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * StaffChat command class
 * 
 * @author LeonTG77
 */
public class StaffChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.staff")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
    		sender.sendMessage(Main.PREFIX + "Usage: /ac <message>");
        	return true;
        } 
        
    	StringBuilder message = new StringBuilder("");
		
		for (int i = 0; i < args.length; i++) {
			message.append(args[i]).append(" ");
		}
		
        String msg = message.toString().trim();

		PlayerUtils.broadcast("§8[§4StaffChat§8] §c" + sender.getName() + "§8 » §f" + msg, "uhc.staff");
		return true;
	}
}