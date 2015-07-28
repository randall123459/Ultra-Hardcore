package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.PlayerUtils;

public class ScenarioCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("scenario")) {
			if (args.length == 0) {
				if (ScenarioManager.getManager().getEnabledScenarios().size() <= 0) {
	           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
					return true;
				}
	           	sender.sendMessage(Main.prefix() + "Enabled scenarios:");
	           	for (Scenario s : ScenarioManager.getManager().getEnabledScenarios()) {
	           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
	           	}
	           	return true;
			}
			
			if (sender.hasPermission("uhc.scenarioadmin")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("list")) {
						StringBuilder list = new StringBuilder("");
						
						for (Scenario s : ScenarioManager.getManager().getScenarios()) {
							if (list.length() > 0) {
								list.append(", ");
							}
							list.append(s.getName());
						}
						sender.sendMessage(Main.prefix() + "All available scenarios:");
						sender.sendMessage(list.toString().trim());
						return true;
					}
					
					sender.sendMessage(ChatColor.RED + "Usage: /scenario <enable|disable|list> <scenario>");
					return true;
				}
		
				if (args[0].equalsIgnoreCase("enable")) {
					Scenario s = ScenarioManager.getManager().getScenario(args[1]);
					
					if (s == null) {
						sender.sendMessage(ChatColor.RED + "That is not a known scenario.");
						return true;
					}
					
					if (s.isEnabled()) {
						sender.sendMessage(Main.prefix(ChatColor.GOLD) + s.getName() + " §7is already enabled.");
						return true;
					}

					PlayerUtils.broadcast(Main.prefix(ChatColor.GOLD) + s.getName() + " §7has been enabled.");
					s.setEnabled(true);
				} else if (args[0].equalsIgnoreCase("disable")) {
					Scenario s = ScenarioManager.getManager().getScenario(args[1]);
					
					if (s == null) {
						sender.sendMessage(ChatColor.RED + "That is not a known scenario.");
						return true;
					}
					
					if (!s.isEnabled()) {
						sender.sendMessage(Main.prefix(ChatColor.GOLD) + s.getName() + " §7is not enabled.");
						return true;
					}

					PlayerUtils.broadcast(Main.prefix(ChatColor.GOLD) + s.getName() + " §7has been disabled.");
					s.setEnabled(false);
				} else if (args[0].equalsIgnoreCase("list")) {
					Scenario s = ScenarioManager.getManager().getScenario(args[1]);
					
					if (s == null) {
						sender.sendMessage(ChatColor.RED + "That is not a known scenario.");
						return true;
					}
					
					sender.sendMessage(Main.prefix() + "Info about " + s.getName());
					sender.sendMessage(s.getDescription());
				} else {
					if (ScenarioManager.getManager().getEnabledScenarios().size() <= 0) {
		           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
						return true;
					}
					sender.sendMessage(Main.prefix() + "Enabled scenarios:");
		           	for (Scenario s : ScenarioManager.getManager().getEnabledScenarios()) {
		           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
		           	}
				}
			} else {
				if (ScenarioManager.getManager().getEnabledScenarios().size() <= 0) {
	           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
					return true;
				}
				sender.sendMessage(Main.prefix() + "Enabled scenarios:");
	           	for (Scenario s : ScenarioManager.getManager().getEnabledScenarios()) {
	           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
	           	}
			}
		}
		return true;
	}
}