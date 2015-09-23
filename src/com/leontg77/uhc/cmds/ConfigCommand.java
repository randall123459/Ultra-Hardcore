package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.Border;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.State;
import com.leontg77.uhc.utils.PlayerUtils;

public class ConfigCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		Game game = Game.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("config")) {
			if (sender.hasPermission("uhc.config")) {
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /config <type> <value>");
					return true;
				}
				
				ConfigValue type;
				
				try {
					type = ConfigValue.valueOf(args[0].toUpperCase());
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
				
				switch (type) {
				case ABSORPTION:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "Absorption has been enabled.");
						game.setAbsorption(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "Absorption has been disabled.");
						game.setAbsorption(false);
					} else {
						sender.sendMessage(ChatColor.RED + "Absorption can only be true or false.");
					}
					break;
				case BORDER:
					Border border;
					
					try {
						border = Border.valueOf(args[1].toUpperCase());
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild border type.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Border will now shrink " + (border == Border.START ? "from" : "at") + " " + border.name().toLowerCase());
					game.setBorderShrinkTime(border);
					break;
				case DEATHLIGHTNING:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "DeathLightning has been enabled.");
						game.setDeathLightning(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "DeathLightning has been disabled.");
						game.setDeathLightning(false);
					} else {
						sender.sendMessage(ChatColor.RED + "DeathLightning can only be true or false.");
					}
					break;
				case FFA:
					if (args[1].equalsIgnoreCase("true")) {
						sender.sendMessage(Main.prefix() + "You have enabled ffa mode.");
						game.setFFA(true);
						
						for (Player online : PlayerUtils.getPlayers()) {
							PlayerUtils.setTabList(online);
						}
					} else if (args[1].equalsIgnoreCase("false")) {
						sender.sendMessage(Main.prefix() + "You have disabled ffa mode.");
						game.setFFA(false);
						
						for (Player online : PlayerUtils.getPlayers()) {
							PlayerUtils.setTabList(online);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "FFA can only be true or false.");
					}
					break;
				case FLINTRATE:
					int f;
					
					try {
						f = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild flintrate.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Flint rates are now �a" + f + "�7.");
					game.setFlintRates(f);
					break;
				case GHASTDROPS:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "Ghasts now drop gold ingots.");
						game.setGhastDropGold(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "Ghasts now drop ghast tears.");
						game.setGhastDropGold(false);
					} else {
						sender.sendMessage(ChatColor.RED + "GhastDrops can only be true or false.");
					}
					break;
				case GOLDENHEADS:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "GoldenHeads has been enabled.");
						game.setGoldenHeads(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "GoldenHeads has been disabled.");
						game.setGoldenHeads(false);
					} else {
						sender.sendMessage(ChatColor.RED + "GoldenHeads can only be true or false.");
					}
					break;
				case HEADHEALS:
					int heal;
					
					try {
						heal = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild head heal amount.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "GoldenHeads now heal �a" + heal + "�7 hearts.");
					game.setGoldenHeadsHeal(heal);
					break;
				case HOST:
					Settings.getInstance().getConfig().set("game.host", args[1]);
					Settings.getInstance().saveConfig();
					
					Scoreboards.getManager().kills.setDisplayName("�4UHC �8- �7" + args[1]);
					sender.sendMessage(Main.prefix() + "You set the host to �a" + args[1] + "�7.");

					for (Player online : PlayerUtils.getPlayers()) {
						PlayerUtils.setTabList(online);
					}
					break;
				case MATCHPOST:
					Settings.getInstance().getConfig().set("matchpost", args[1]);
					Settings.getInstance().saveConfig();
					
					sender.sendMessage(Main.prefix() + "You set the matchpost to �a" + args[1] + "�7.");
					break;
				case MAXPLAYERS:
					int max;
					
					try {
						max = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild maxplayers.");
						return true;
					}
					
					Settings.getInstance().getConfig().set("maxplayers", max);
					Settings.getInstance().saveConfig();

					PlayerUtils.broadcast(Main.prefix() + "Max player slots are now �a" + max + "�7.");
					break;
				case MEETUP:
					int meetup;
					
					try {
						meetup = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild meetup time.");
						return true;
					}
					
					Settings.getInstance().getConfig().set("time.meetup", meetup);
					Settings.getInstance().saveConfig();

					Runnables.meetup = meetup;
					PlayerUtils.broadcast(Main.prefix() + "Meetup is now �a" + meetup + "�7 minutes in.");
					break;
				case NERFEDSTRENGTH:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "Strength is now nerfed.");
						game.setNerfedStrength(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "Strength is no longer nerfed.");
						game.setNerfedStrength(false);
					} else {
						sender.sendMessage(ChatColor.RED + "NerfedStrength can only be true or false.");
					}
					break;
				case NETHER:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "Nether has been enabled.");
						game.setNether(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "Nether has been disabled.");
						game.setNether(false);
					} else {
						sender.sendMessage(ChatColor.RED + "Nether can only be true or false.");
					}
					break;
				case NOTCHAPPLES:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "NotchApples has been enabled.");
						game.setNotchApples(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "NotchApples has been disabled.");
						game.setNotchApples(false);
					} else {
						sender.sendMessage(ChatColor.RED + "NotchApples can only be true or false.");
					}
					break;
				case PEARLDAMAGE:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "PearlDamage has been enabled.");
						game.setPearlDamage(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "PearlDamage has been disabled.");
						game.setPearlDamage(false);
					} else {
						sender.sendMessage(ChatColor.RED + "PearlDamage can only be true or false.");
					}
					break;
				case PVP:
					int pvp;
					
					try {
						pvp = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild pvp time.");
						return true;
					}
					
					Settings.getInstance().getConfig().set("time.pvp", pvp);
					Settings.getInstance().saveConfig();

					Runnables.pvp = pvp;
					PlayerUtils.broadcast(Main.prefix() + "PvP is now �a" + pvp + "�7 minutes in.");
					break;
				case SCENARIOS:
					StringBuilder bu = new StringBuilder();
											
					for (int s = 1; s < args.length; s++) {
						bu.append(args[s]).append(" ");
					}
					
					Settings.getInstance().getConfig().set("game.scenarios", ChatColor.translateAlternateColorCodes('&', bu.toString().trim()));
					Settings.getInstance().saveConfig();

					sender.sendMessage(Main.prefix() + "You set the scenarios to �a" + ChatColor.translateAlternateColorCodes('&', bu.toString().trim()) + "�7.");

					for (Player online : PlayerUtils.getPlayers()) {
						PlayerUtils.setTabList(online);
					}
					break;
				case SHEARRATE:
					int s;
					
					try {
						s = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild teamsize.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Shear rates are now �a" + s + "�7.");
					game.setShearRates(s);
					break;
				case SHEARS:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "Shears has been enabled.");
						game.setShears(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "Shears has been disabled.");
						game.setShears(false);
					} else {
						sender.sendMessage(ChatColor.RED + "Shears can only be true or false.");
					}
					break;
				case STATE:
					State st;
					
					try {
						st = State.valueOf(args[1].toUpperCase());
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "That is not a vaild state.");
						return true;
					}
					
					State.setState(st);
					sender.sendMessage(Main.prefix() + "You set the state to �a" + args[1] + "�7.");
					break;
				case TEAMSIZE:
					int tz;
					
					try {
						tz = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild teamsize.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "The teamsize is now �a" + tz + "�7.");
					game.setTeamSize(tz);
					
					for (Player online : PlayerUtils.getPlayers()) {
						PlayerUtils.setTabList(online);
					}
					break;
				case TABCOLORS:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "Tab will now have the color of your health.");
						game.setTabShowsHealthColor(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "Tab will no longer have the color of your health.");
						game.setTabShowsHealthColor(false);
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.setPlayerListName(null);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "TheEnd can only be true or false.");
					}
					break;
				case HARDERCRAFTING:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "HarderCrafting has been enabled.");
						game.setGoldenMelonNeedsIngots(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "HarderCrafting has been disabled.");
						game.setGoldenMelonNeedsIngots(false);
					} else {
						sender.sendMessage(ChatColor.RED + "TheEnd can only be true or false.");
					}
					break;
				case THEEND:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "TheEnd has been enabled.");
						game.setTheEnd(true);
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "TheEnd has been disabled.");
						game.setTheEnd(false);
					} else {
						sender.sendMessage(ChatColor.RED + "TheEnd can only be true or false.");
					}
					break;
				case WORLD:
					Settings.getInstance().getConfig().set("game.world", args[1]);
					Settings.getInstance().saveConfig();

					sender.sendMessage(Main.prefix() + "You set the game world to �a" + args[1] + "�7.");
					break;
				case RR:
					if (args[1].equalsIgnoreCase("true")) {
						PlayerUtils.broadcast(Main.prefix() + "This is now an recorded round.");
						game.setRR(true);
						
						Scoreboards.getManager().kills.setDisplayName("�6" + game.getRRName());
					} else if (args[1].equalsIgnoreCase("false")) {
						PlayerUtils.broadcast(Main.prefix() + "This is no longer an recorded round.");
						game.setRR(false);
						
						Scoreboards.getManager().kills.setDisplayName("�4UHC �8- �7" + Settings.getInstance().getConfig().getString("game.host"));
					} else {
						sender.sendMessage(ChatColor.RED + "RR can only be true or false.");
					}
					break;
				case RRNAME:
					sender.sendMessage(Main.prefix() + "You set the RR name to �a" + args[1].replaceAll("_", " ") + "�7.");
					game.setRRName(args[1].replaceAll("_", " "));
					
					if (game.isRR()) {
						Scoreboards.getManager().kills.setDisplayName("�6" + game.getRRName());
					}
					break;
				default:
					sender.sendMessage(ChatColor.RED + "You typed the wrong type or value.");
					break;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("config")) {
			if (sender.hasPermission("uhc.config")) {
				if (args.length == 1) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	
		        	if (!args[0].equals("")) {
		        		for (ConfigValue type : ConfigValue.values()) {
		        			if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) {
		        				arg.add(type.name().toLowerCase());
		        			}
		        		}
		        	}
		        	else {
		        		for (ConfigValue type : ConfigValue.values()) {
		        			arg.add(type.name().toLowerCase());
		        		}
		        	}
		        	return arg;
		        }
				
				if (args.length == 2) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	
		        	ConfigValue type;
					
					try {
						type = ConfigValue.valueOf(args[0].toUpperCase());
					} catch (Exception e) {
						return null;
					}
		        	
		        	switch (type) {
					case ABSORPTION:
						arg.add("true");
						arg.add("false");
						break;
					case BORDER:
						for (Border border : Border.values()) {
							arg.add(border.name().toLowerCase());
						}
						break;
					case DEATHLIGHTNING:
						arg.add("true");
						arg.add("false");
						break;
					case FFA:
						arg.add("true");
						arg.add("false");
						break;
					case GHASTDROPS:
						arg.add("true");
						arg.add("false");
						break;
					case GOLDENHEADS:
						arg.add("true");
						arg.add("false");
						break;
					case NERFEDSTRENGTH:
						arg.add("true");
						arg.add("false");
						break;
					case NETHER:
						arg.add("true");
						arg.add("false");
						break;
					case NOTCHAPPLES:
						arg.add("true");
						arg.add("false");
						break;
					case PEARLDAMAGE:
						arg.add("true");
						arg.add("false");
						break;
					case SHEARS:
						arg.add("true");
						arg.add("false");
						break;
					case TABCOLORS:
						arg.add("true");
						arg.add("false");
						break;
					case HARDERCRAFTING:
						arg.add("true");
						arg.add("false");
						break;
					case STATE:
						for (State state : State.values()) {
							arg.add(state.name().toLowerCase());
						}
						break;
					case THEEND:
						arg.add("true");
						arg.add("false");
						break;
					case WORLD:
						for (World world : Bukkit.getWorlds()) {
							arg.add(world.getName());
						}
						break;
					default:
						break;
					}
		        	return arg;
		        }
			}
		}
		return null;
	}
	
	public enum ConfigValue {
		HOST, WORLD, FFA, TEAMSIZE, SCENARIOS, SHEARS, SHEARRATE, FLINTRATE, BORDER, ABSORPTION, GOLDENHEADS, HEADHEALS, NOTCHAPPLES, PEARLDAMAGE, DEATHLIGHTNING, NETHER, THEEND, GHASTDROPS, NERFEDSTRENGTH, TABCOLORS, HARDERCRAFTING, PVP, MEETUP, MATCHPOST, MAXPLAYERS, STATE, RR, RRNAME;
	}
}