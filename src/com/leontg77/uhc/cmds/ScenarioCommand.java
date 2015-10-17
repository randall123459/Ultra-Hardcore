package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Scenario command class.
 * 
 * @author LeonTG77
 */
public class ScenarioCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ScenarioManager scen = ScenarioManager.getInstance();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("enable")) {
				if (sender.hasPermission("uhc.scenario")) {
					if (args.length == 1) {
						sender.sendMessage(Main.PREFIX + "Usage: /scen enable <scenario>");
						return true;
					}
					
					Scenario scenario = scen.getScenario(args[1]);
					
					if (scenario == null) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not a scenario.");
						return true;
					}
					
					if (scenario.isEnabled()) {
						sender.sendMessage(Main.PREFIX + ChatColor.GOLD + scenario.getName() + " §7is already enabled.");
						return true;
					}

					PlayerUtils.broadcast(Main.PREFIX + ChatColor.GOLD + scenario.getName() + " §7has been enabled.");
					scenario.enable();
					return true;
				}
			} else if (args[0].equalsIgnoreCase("disable")) {
				if (sender.hasPermission("uhc.scenario")) {
					if (args.length == 1) {
						sender.sendMessage(Main.PREFIX + "Usage: /scen disable <scenario>");
						return true;
					}
					
					Scenario scenario = scen.getScenario(args[1]);
					
					if (scenario == null) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not a scenario.");
						return true;
					}
					
					if (!scenario.isEnabled()) {
						sender.sendMessage(Main.PREFIX + ChatColor.GOLD + scenario.getName() + " §7is not enabled.");
						return true;
					}

					PlayerUtils.broadcast(Main.PREFIX + ChatColor.GOLD + scenario.getName() + " §7has been disabled.");
					scenario.disable();
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (args.length == 1) {
					StringBuilder list = new StringBuilder("");
					
					for (Scenario scens : scen.getScenarios()) {
						if (list.length() > 0) {
							list.append("§8, §7");
						}
						
						list.append((scens.isEnabled() ? "§a" : "§c") + scens.getName());
					}
					sender.sendMessage(Main.PREFIX + "All available scenarios: §o(Green = enabled, Red = disabled)");
					sender.sendMessage("§8» §7" + list.toString().trim());
					return true;
				}
				
				Scenario scenario = scen.getScenario(args[1]);
				
				if (scenario == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not a scenario.");
					return true;
				}
				
				sender.sendMessage(Main.PREFIX + "Info about §a" + scenario.getName() + "§7:");
				sender.sendMessage("§8» §7" + scenario.getDescription());
				return true;
			}
		}
		
		if (scen.getEnabledScenarios().size() <= 0) {
       		sender.sendMessage(Main.PREFIX + "No scenarios are enabled.");
			return true;
		}
		
       	sender.sendMessage(Main.PREFIX + "All enabled scenarios:");
       	
       	for (Scenario s : ScenarioManager.getInstance().getEnabledScenarios()) {
       		sender.sendMessage("§8» §7" + s.getName() + ": §f" + s.getDescription());
       	}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ScenarioManager manager = ScenarioManager.getInstance();
    	ArrayList<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("list");
        	
        	if (sender.hasPermission("uhc.scenario")) {
	        	types.add("enable");
	        	types.add("disable");
        	}
        	
        	if (args[0].equals("")) {
        		for (String type : types) {
        			toReturn.add(type);
        		}
        	} else {
        		for (String type : types) {
        			if (type.startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
        }
		
		if (args.length == 2) {
        	if (!sender.hasPermission("uhc.scenario")) {
	        	return null;
        	}
        	
        	if (args[0].equalsIgnoreCase("enable")) {
        		if (args[1].equals("")) {
	        		for (Scenario scen : manager.getDisabledScenarios()) {
        				toReturn.add(scen.getName());
	        		}
	        	} else {
	        		for (Scenario scen : manager.getDisabledScenarios()) {
	        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(scen.getName());
	        			}
	        		}
	        	}
        	} else if (args[0].equalsIgnoreCase("disable")) {
	        	if (args[1].equals("")) {
	        		for (Scenario scen : manager.getEnabledScenarios()) {
        				toReturn.add(scen.getName());
	        		}
	        	}
	        	else {
	        		for (Scenario scen : manager.getEnabledScenarios()) {
	        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(scen.getName());
	        			}
	        		}
	        	}
        	} else if (args[0].equalsIgnoreCase("list")) {
	        	if (args[1].equals("")) {
	        		for (Scenario scen : manager.getScenarios()) {
        				toReturn.add(scen.getName());
	        		}
	        	} else {
	        		for (Scenario scen : manager.getScenarios()) {
	        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(scen.getName());
	        			}
	        		}
	        	}
        	}
		}
    	return toReturn;
	}
}