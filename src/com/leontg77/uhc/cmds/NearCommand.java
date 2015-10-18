package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Near command class.
 * 
 * @author LeonTG77
 */
public class NearCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can see nearby players.");
			return true;
		}

		Spectator spec = Spectator.getInstance();
		Player player = (Player) sender;
		
		if (!sender.hasPermission("uhc.invsee") && !spec.isSpectating(player)) {
			player.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		StringBuilder nearList = new StringBuilder("");
		
		for (Entity near : PlayerUtils.getNearby(player.getLocation(), 200)) {
			if (near instanceof Player) {
				Player nearby = (Player) near;
				
				if (!player.canSee(nearby)) {
					continue;
				}
				
				if (nearList.length() > 0) {
					nearList.append("§8, ");
				}
				
				if (nearby != player) {
					nearList.append("§a" + nearby.getName() + "§7(§c" + ((int) player.getLocation().distance(nearby.getLocation())) + "m§7)§a");
				}
			}
		}
		
		player.sendMessage(Main.PREFIX + "Nearby players:");
		player.sendMessage("§8» §7" + (nearList.length() > 0 ? nearList.toString().trim() : "There are no players nearby."));
		return true;
	}
}