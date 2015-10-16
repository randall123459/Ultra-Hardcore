package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * ClearInv command class.
 * 
 * @author LeonTG77
 */
public class ClearInvCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!sender.hasPermission("uhc.clearinv")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can clear their own inventories.");
				return true;
			}
			
			Player player = (Player) sender;
			User user = User.get(player);
			
			player.sendMessage(Main.PREFIX + "You cleared your own inventory.");
			user.resetInventory();
			return true;
		}
		
		if (!sender.hasPermission("uhc.clearinv.other")) {
			sender.sendMessage(Main.PREFIX + "§cYou cannot clear other players inventories.");
			return true;
		}
		
		if (args[0].equals("*")) {
			for (Player online : PlayerUtils.getPlayers()) {
				User user = User.get(online);
				user.resetInventory();
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All players inventories has been cleared.");
		} else {
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
				return true;
			}
			
			User user = User.get(target);
			user.resetInventory();

			sender.sendMessage(Main.PREFIX + "You cleared §a" + target.getName() + "'s §7inventory.");
			target.sendMessage(Main.PREFIX + "Your inventory was cleared.");
		}
		return true;
	}
}