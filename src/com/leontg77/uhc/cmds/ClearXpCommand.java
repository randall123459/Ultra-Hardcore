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
 * ClearXp command class.
 * 
 * @author LeonTG77
 */
public class ClearXpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!sender.hasPermission("uhc.clearxp")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can clear their own xp level.");
				return true;
			}
			
			Player player = (Player) sender;
			User user = User.get(player);
			
			player.sendMessage(Main.PREFIX + "You cleared your own xp.");
			user.resetExp();
			return true;
		}
		
		if (!sender.hasPermission("uhc.clearxp.other")) {
			sender.sendMessage(Main.PREFIX + "§cYou cannot clear other players xp levels.");
			return true;
		}
		
		if (args[0].equals("*")) {
			for (Player online : PlayerUtils.getPlayers()) {
				User user = User.get(online);
				user.resetExp();
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All players xp levels has been cleared.");
		} else {
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
				return true;
			}
			
			User user = User.get(target);
			user.resetExp();

			sender.sendMessage(Main.PREFIX + "You cleared §a" + target.getName() + "'s §7xp level.");
			target.sendMessage(Main.PREFIX + "Your xp level was cleared.");
		}
		return true;
	}
}