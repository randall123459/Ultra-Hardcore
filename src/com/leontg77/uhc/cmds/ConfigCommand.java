package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.Border;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;

public class ConfigCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("config")) {
			if (sender.hasPermission("uhc.config")) {
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /config set <type> <value>");
					return true;
				}
				
				ConfigValue type;
				
				try {
					type = ConfigValue.valueOf(args[1].toUpperCase());
				} catch (Exception e) {
					StringBuilder b = new StringBuilder();
					for (ConfigValue types : ConfigValue.values()) {
						if (b.length() > 0) {
							b.append(", ");
						}
						b.append(types.name().toLowerCase());
					}
					sender.sendMessage(ChatColor.RED + "Available config types: " + b.toString().trim());
					return true;
				}
				
				try {
					switch (type) {
						case ABSORB:
							if (args[2].equalsIgnoreCase("true")) {
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), true);
								Settings.getInstance().saveData();
								Main.absorption = true;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else if (args[2].equalsIgnoreCase("false")) {
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), false);
								Settings.getInstance().saveData();
								Main.absorption = false;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else {
								sender.sendMessage(ChatColor.RED + "This value can only be true or false.");
							}
							break;
						case BORDER:
							Border b;
							
							try {
								b = Border.valueOf(args[2].toUpperCase());
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild border type.");
								return true;
							}
							Main.border = b;
							Settings.getInstance().getData().set("options." + args[1].toLowerCase(), b.name());
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case APPLERATE:
							int i;
							
							try {
								i = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild number.");
								return true;
							}
							
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), i);
							Settings.getInstance().saveData();
							Main.applerate = i;
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case CURRENTSTATE:
							State st;
							
							try {
								st = State.valueOf(args[2].toUpperCase());
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild state.");
								return true;
							}
							State.setState(st);
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case FFA:
							if (args[2].equalsIgnoreCase("true")) {
								Settings.getInstance().getData().set("game." + args[1].toLowerCase(), true);
								Settings.getInstance().saveData();
								Main.ffa = true;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else if (args[2].equalsIgnoreCase("false")) {
								Settings.getInstance().getData().set("game." + args[1].toLowerCase(), false);
								Settings.getInstance().saveData();
								Main.ffa = false;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else {
								sender.sendMessage(ChatColor.RED + "This value can only be true or false.");
							}
							break;
						case FLINTRATE:
							int f;
							
							try {
								f = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild number.");
								return true;
							}
							
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), f);
							Settings.getInstance().saveData();
							Main.flintrate = f;
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case GHEAD:
							if (args[2].equalsIgnoreCase("true")) {
								Bukkit.getServer().resetRecipes();
								Main.addRecipes();
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), true);
								Settings.getInstance().saveData();
								Main.ghead = true;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else if (args[2].equalsIgnoreCase("false")) {
								Bukkit.getServer().resetRecipes();
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), false);
								Settings.getInstance().saveData();
								Main.ghead = false;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else {
								sender.sendMessage(ChatColor.RED + "This value can only be true or false.");
							}
							break;
						case GODAPPLE:
							if (args[2].equalsIgnoreCase("true")) {
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), true);
								Settings.getInstance().saveData();
								Main.godapple = true;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else if (args[2].equalsIgnoreCase("false")) {
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), false);
								Settings.getInstance().saveData();
								Main.godapple = false;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else {
								sender.sendMessage(ChatColor.RED + "This value can only be true or false.");
							}
							break;
						case HOST:
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), args[2]);
							Settings.getInstance().saveData();
							Scoreboards.getManager().kills.setDisplayName("§4UHC §8- §7" + args[2]);
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case POST:
							Settings.getInstance().getData().set("match." + args[1].toLowerCase(), args[2]);
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case MAXPLAYERS:
							int i1;
							
							try {
								i1 = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild number.");
								return true;
							}
							
							Settings.getInstance().getData().set(args[1].toLowerCase(), i1);
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case MEETUP:
							int m;
							
							try {
								m = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild number.");
								return true;
							}
							
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), m);
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case SCENARIOS:
							StringBuilder bu = new StringBuilder();
							
							for (int s = 2; s < args.length; s++) {
								bu.append(args[s]).append(" ");
							}
							
							Settings.getInstance().getData().set("motd", ChatColor.translateAlternateColorCodes('&', bu.toString().trim()));
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + ChatColor.translateAlternateColorCodes('&', bu.toString().trim()) + ".");
							break;
						case PEARLDMG:
							if (args[2].equalsIgnoreCase("true")) {
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), true);
								Settings.getInstance().saveData();
								Main.pearldmg = true;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else if (args[2].equalsIgnoreCase("false")) {
								Settings.getInstance().getData().set("options." + args[1].toLowerCase(), false);
								Settings.getInstance().saveData();
								Main.pearldmg = false;
								sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							} else {
								sender.sendMessage(ChatColor.RED + "This value can only be true or false.");
							}
							break;
						case WORLD:
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), args[2]);
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case PVP:
							int p;
							
							try {
								p = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild number.");
								return true;
							}
							
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), p);
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							break;
						case SHEARRATE:
							int s;
							
							try {
								s = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild number.");
								return true;
							}
							
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), s);
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							Main.shearrate = s;
							break;
						case TEAMSIZE:
							int t;
							
							try {
								t = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "That is not a vaild number.");
								return true;
							}
							
							Settings.getInstance().getData().set("game." + args[1].toLowerCase(), t);
							Settings.getInstance().saveData();
							sender.sendMessage(Main.prefix() + "Set " + args[1].toLowerCase() + " to " + args[2] + ".");
							Main.teamSize = t;
							break;
						default:
							break;
					}
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "You might not have typed the right value.");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
	
	public enum ConfigValue {
		FFA, PVP, MEETUP, HOST, BORDER, FLINTRATE, APPLERATE, SHEARRATE, TEAMSIZE, CURRENTSTATE, ABSORB, GHEAD, PEARLDMG, GODAPPLE, POST, SCENARIOS, MAXPLAYERS, WORLD;
	}
}