package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.NumberUtils;

public class HealthCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("health")) {
			if (ScenarioManager.getInstance().getScenario("Paranoia").isEnabled()) {
				sender.sendMessage(ChatColor.RED + "You cannot view peoples health in paranoia.");
				return true;
			}
			
			if (ScenarioManager.getInstance().getScenario("TeamHealth").isEnabled()) {
				sender.sendMessage(ChatColor.RED + "You cannot view peoples health in TeamHealth.");
				return true;
			}
			
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					
					double health = player.getHealth();
					double maxhealth = player.getMaxHealth();

					sender.sendMessage(Main.prefix() + "§7You are at §6" + NumberUtils.makePercent(health) + "%" + (maxhealth == 20 ? "" : " §7out of maximum §6" + NumberUtils.makePercent(maxhealth) + "%"));
				} else {
					sender.sendMessage(Main.prefix() + "Usage: /health <player>");
				}
				return true;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "That player is not online.");
				return true;
			}
			
			double health = target.getHealth();
			double maxhealth = target.getMaxHealth();

			sender.sendMessage(Main.prefix() + "§a" + target.getName() + " §7is at §6" + NumberUtils.makePercent(health) + "%" + (maxhealth == 20 ? "" : " §7out of maximum §6" + NumberUtils.makePercent(maxhealth) + "%"));
		}
		return true;
	}
}