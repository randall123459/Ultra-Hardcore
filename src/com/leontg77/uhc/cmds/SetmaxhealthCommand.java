package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * SetMaxhealth command class.
 * 
 * @author LeonTG77
 */
public class SetmaxhealthCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.setmaxhealth")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /setmaxhealth <health> [player|*]");
			return true;
		}
		
		double health;
		
		try {
			health = Double.parseDouble(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild health.");
			return true;
		}
		
		if (health < 1) {
			health = 1;
		}
		
		if (health > 2000) {
			health = 2000;
		}
		
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can change their max health.");
				return true;
			}
			
			Player player = (Player) sender;
			
			player.setMaxHealth(health);
			player.sendMessage(Main.PREFIX + "You set your max health to §6" + NumberUtils.makePercent(health).substring(2) + "%");
			return true;
		}
		
		if (args[1].equals("*")) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setMaxHealth(health);
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All players max health was set to §6" + NumberUtils.makePercent(health).substring(2) + "%");
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[1]);
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
			return true;
		}

		sender.sendMessage(Main.PREFIX + "You set §6" + target.getName() + "'s §7max health to §6" + NumberUtils.makePercent(health).substring(2) + "%");
		target.sendMessage(Main.PREFIX + "Your max health was set to §6" + NumberUtils.makePercent(health).substring(2) + "%");
		target.setMaxHealth(health);
		return true;
	}
}