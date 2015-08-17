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
import com.leontg77.uhc.util.PlayerUtils;

public class ScenarioCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("scenario")) {
			if (args.length == 0) {
				if (ScenarioManager.getInstance().getEnabledScenarios().size() <= 0) {
	           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
					return true;
				}
				
	           	sender.sendMessage(Main.prefix() + "Enabled scenarios:");
	           	for (Scenario s : ScenarioManager.getInstance().getEnabledScenarios()) {
	           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
	           	}
	           	return true;
			}
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					StringBuilder list = new StringBuilder("");
					
					for (Scenario s : ScenarioManager.getInstance().getScenarios()) {
						if (list.length() > 0) {
							list.append(", ");
						}
						list.append(s.getName());
					}
					sender.sendMessage(Main.prefix() + "All available scenarios:");
					sender.sendMessage(list.toString().trim());
					return true;
				}
				
				if (sender.hasPermission("uhc.scenario")) {
					sender.sendMessage(ChatColor.RED + "Usage: /scenario <enable|disable> <scenario>");
				} else {
					if (ScenarioManager.getInstance().getEnabledScenarios().size() <= 0) {
		           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
						return true;
					}
					
		           	sender.sendMessage(Main.prefix() + "Enabled scenarios:");
		           	for (Scenario s : ScenarioManager.getInstance().getEnabledScenarios()) {
		           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
		           	}
				}
				return true;
			}
	
			if (args[0].equalsIgnoreCase("enable")) {
				if (!sender.hasPermission("uhc.scenario")) {
					if (ScenarioManager.getInstance().getEnabledScenarios().size() <= 0) {
		           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
						return true;
					}
					
		           	sender.sendMessage(Main.prefix() + "Enabled scenarios:");
		           	for (Scenario s : ScenarioManager.getInstance().getEnabledScenarios()) {
		           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
		           	}
					return true;
				}
				
				Scenario s = ScenarioManager.getInstance().getScenario(args[1]);
				
				if (s == null) {
					sender.sendMessage(ChatColor.RED + "That is not a known scenario.");
					return true;
				}
				
				if (s.isEnabled()) {
					sender.sendMessage(Main.prefix() + ChatColor.GOLD + s.getName() + " §7is already enabled.");
					return true;
				}

				PlayerUtils.broadcast(Main.prefix() + ChatColor.GOLD + s.getName() + " §7has been enabled.");
				s.setEnabled(true);
			} else if (args[0].equalsIgnoreCase("disable")) {
				if (!sender.hasPermission("uhc.scenario")) {
					if (ScenarioManager.getInstance().getEnabledScenarios().size() <= 0) {
		           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
						return true;
					}
					
		           	sender.sendMessage(Main.prefix() + "Enabled scenarios:");
		           	for (Scenario s : ScenarioManager.getInstance().getEnabledScenarios()) {
		           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
		           	}
					return true;
				}
				
				Scenario s = ScenarioManager.getInstance().getScenario(args[1]);
				
				if (s == null) {
					sender.sendMessage(ChatColor.RED + "That is not a known scenario.");
					return true;
				}
				
				if (!s.isEnabled()) {
					sender.sendMessage(Main.prefix() + ChatColor.GOLD + s.getName() + " §7is not enabled.");
					return true;
				}

				PlayerUtils.broadcast(Main.prefix() + ChatColor.GOLD + s.getName() + " §7has been disabled.");
				s.setEnabled(false);
			} else if (args[0].equalsIgnoreCase("list")) {
				Scenario s = ScenarioManager.getInstance().getScenario(args[1]);
				
				if (s == null) {
					sender.sendMessage(ChatColor.RED + "That is not a known scenario.");
					return true;
				}
				
				sender.sendMessage(Main.prefix() + "Info about " + s.getName());
				sender.sendMessage(s.getDescription());
			} else {
				if (ScenarioManager.getInstance().getEnabledScenarios().size() <= 0) {
	           		sender.sendMessage(Main.prefix() + "No scenarios are enabled.");
					return true;
				}
				
				sender.sendMessage(Main.prefix() + "Enabled scenarios:");
	           	for (Scenario s : ScenarioManager.getInstance().getEnabledScenarios()) {
	           		sender.sendMessage("§6" + s.getName() + ": §f" + s.getDescription());
	           	}
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("scenario")) {
			if (args.length == 1) {
	        	ArrayList<String> arg = new ArrayList<String>();
	        	ArrayList<String> types = new ArrayList<String>();
	        	types.add("list");
	        	if (sender.hasPermission("uhc.scenario")) {
		        	types.add("enable");
		        	types.add("disable");
	        	}
	        	
	        	if (!args[0].equals("")) {
	        		for (String type : types) {
	        			if (type.startsWith(args[0].toLowerCase())) {
	        				arg.add(type);
	        			}
	        		}
	        	}
	        	else {
	        		for (String type : types) {
	        			arg.add(type);
	        		}
	        	}
	        	return arg;
	        }
			
			if (args.length == 2) {
	        	if (sender.hasPermission("uhc.scenario")) {
	        		ArrayList<String> arg = new ArrayList<String>();
		        	
		        	if (args[0].equalsIgnoreCase("enable")) {
		        		if (!args[1].equals("")) {
			        		for (Scenario scen : ScenarioManager.getInstance().getDisabledScenarios()) {
			        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
			        				arg.add(scen.getName());
			        			}
			        		}
			        	}
			        	else {
			        		for (Scenario scen : ScenarioManager.getInstance().getDisabledScenarios()) {
		        				arg.add(scen.getName());
			        		}
			        	}
		        	}
		        	else if (args[0].equalsIgnoreCase("disable")) {
			        	if (!args[1].equals("")) {
			        		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
			        				arg.add(scen.getName());
			        			}
			        		}
			        	}
			        	else {
			        		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
		        				arg.add(scen.getName());
			        		}
			        	}
		        	}
		        	else if (args[0].equalsIgnoreCase("list")) {
			        	if (!args[1].equals("")) {
			        		for (Scenario scen : ScenarioManager.getInstance().getScenarios()) {
			        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
			        				arg.add(scen.getName());
			        			}
			        		}
			        	}
			        	else {
			        		for (Scenario scen : ScenarioManager.getInstance().getScenarios()) {
		        				arg.add(scen.getName());
			        		}
			        	}
		        	}
		        	return arg;
	        	}
	        }
		}
		return null;
	}
}