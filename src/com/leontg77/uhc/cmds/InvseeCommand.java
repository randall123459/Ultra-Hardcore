package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;

/**
 * Invsee command class
 * 
 * @author LeonTG77
 */
public class InvseeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can open player invs.");
			return true;
		}

		Player player = (Player) sender;
		
		Spectator spec = Spectator.getManager();
		InvGUI inv = InvGUI.getManager();
		
		if (!spec.isSpectating(player)) {
			player.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length == 0) {
    		player.sendMessage(Main.prefix() + "Usage: /invsee <player>");
    		return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		
		if (target == null) {
			player.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		} 
		
		inv.openPlayerInventory(player, target);
		return true;
	}
}