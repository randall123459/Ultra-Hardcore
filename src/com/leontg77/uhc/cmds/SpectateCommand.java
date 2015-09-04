package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.utils.PlayerUtils;

public class SpectateCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("spectate")) {
			if (sender.hasPermission("uhc.spectate")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle|list> [player]");
		    		return true;
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (args[0].equalsIgnoreCase("toggle")) {
							Spectator.getManager().toggle(player, false);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("on")) {
							Spectator.getManager().enableSpecmode(player, false);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("off")) {
							Spectator.getManager().disableSpecmode(player, false);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("list")) {
							if (Spectator.getManager().spectators.size() < 1) {
						    	sender.sendMessage(Main.prefix() + "There are no spectators.");
								return true;
							}
							
							ArrayList<String> players = new ArrayList<String>(Spectator.getManager().spectators);
							Collections.shuffle(players);
							
					    	StringBuilder list = new StringBuilder();
					    	int p = 1;
					    		
					    	for (int i = 0; i < players.size(); i++) {
					    		if (list.length() > 0) {
									if (p == players.size()) {
										list.append(" §8and §a");
									} else {
										list.append("§8, §a");
									}
								}
								
					    		String s = players.get(i);
								list.append(Bukkit.getPlayer(s) == null ? "§c" + s : "§a" + s);
								p++;
							}
					    			
					    	sender.sendMessage(Main.prefix() + "There are §6" + (p - 1) + " §7spectators.");
					    	sender.sendMessage("§8» §7Spectators§8: §a" + list.toString() + "§8.");
							return true;
						}
						
						player.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle> [player]");
					} else {
						if (args[0].equalsIgnoreCase("list")) {
							if (Spectator.getManager().spectators.size() < 1) {
						    	sender.sendMessage(Main.prefix() + "There are no spectators.");
								return true;
							}
							
							ArrayList<String> players = new ArrayList<String>(Spectator.getManager().spectators);
							Collections.shuffle(players);
							
					    	StringBuilder list = new StringBuilder();
					    	int p = 1;
					    		
					    	for (int i = 0; i < players.size(); i++) {
					    		if (list.length() > 0) {
									if (p == players.size()) {
										list.append(" §8and §a");
									} else {
										list.append("§8, §a");
									}
								}
								
					    		String s = players.get(i);
								list.append(Bukkit.getPlayer(s) == null ? "§c" + s : "§a" + s);
								p++;
							}
					    			
					    	sender.sendMessage(Main.prefix() + "There are §6" + (p - 1) + " §7spectators.");
					    	sender.sendMessage("§8» §7Spectators§8: §a" + list.toString() + "§8.");
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + "Only players can spectate.");
					}
		    		return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[1]);
				
				if (sender.hasPermission("uhc.spectateother")) {
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					} 
					
					if (args[0].equalsIgnoreCase("toggle")) {
						Spectator.getManager().toggle(target, false);
						return true;
					}
					
					if (args[0].equalsIgnoreCase("on")) {
						Spectator.getManager().enableSpecmode(target, false);
						return true;
					}
					
					if (args[0].equalsIgnoreCase("off")) {
						Spectator.getManager().disableSpecmode(target, false);
			    		return true;
					}
					
					if (args[0].equalsIgnoreCase("list")) {
						if (Spectator.getManager().spectators.size() < 1) {
					    	sender.sendMessage(Main.prefix() + "There are no spectators.");
							return true;
						}
						
						ArrayList<String> players = new ArrayList<String>(Spectator.getManager().spectators);
						Collections.shuffle(players);
						
				    	StringBuilder list = new StringBuilder();
				    	int p = 1;
				    		
				    	for (int i = 0; i < players.size(); i++) {
				    		if (list.length() > 0) {
								if (p == players.size()) {
									list.append(" §8and §a");
								} else {
									list.append("§8, §a");
								}
							}
							
				    		String s = players.get(i);
							list.append(Bukkit.getPlayer(s) == null ? "§c" + s : "§a" + s);
							p++;
						}
				    			
				    	sender.sendMessage(Main.prefix() + "There are §6" + (p - 1) + " §7spectators.");
				    	sender.sendMessage("§8» §7Spectators§8: §a" + list.toString() + "§8.");
						return true;
					}
					
					sender.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle> [player]");
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (args[0].equalsIgnoreCase("toggle")) {
							Spectator.getManager().toggle(player, false);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("on")) {
							Spectator.getManager().enableSpecmode(player, false);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("off")) {
							Spectator.getManager().disableSpecmode(player, false);
							return true;
						}
						
						if (args[0].equalsIgnoreCase("list")) {
							if (Spectator.getManager().spectators.size() < 1) {
						    	sender.sendMessage(Main.prefix() + "There are no spectators.");
								return true;
							}
							
							ArrayList<String> players = new ArrayList<String>(Spectator.getManager().spectators);
							Collections.shuffle(players);
							
					    	StringBuilder list = new StringBuilder();
					    	int p = 1;
					    		
					    	for (int i = 0; i < players.size(); i++) {
					    		if (list.length() > 0) {
									if (p == players.size()) {
										list.append(" §8and §a");
									} else {
										list.append("§8, §a");
									}
								}
								
					    		String s = players.get(i);
								list.append(Bukkit.getPlayer(s) == null ? "§c" + s : "§a" + s);
								p++;
							}
					    			
					    	sender.sendMessage(Main.prefix() + "There are §6" + (p - 1) + " §7spectators.");
					    	sender.sendMessage("§8» §7Spectators§8: §a" + list.toString() + "§8.");
							return true;
						}
						player.sendMessage(ChatColor.RED + "Usage: /spec <on|off|toggle> [player]");
					} else {
						if (args[0].equalsIgnoreCase("list")) {
							if (Spectator.getManager().spectators.size() < 1) {
						    	sender.sendMessage(Main.prefix() + "There are no spectators.");
								return true;
							}
							
							ArrayList<String> players = new ArrayList<String>(Spectator.getManager().spectators);
							Collections.shuffle(players);
							
					    	StringBuilder list = new StringBuilder();
					    	int p = 1;
					    		
					    	for (int i = 0; i < players.size(); i++) {
					    		if (list.length() > 0) {
									if (p == players.size()) {
										list.append(" §8and §a");
									} else {
										list.append("§8, §a");
									}
								}
								
					    		String s = players.get(i);
								list.append(Bukkit.getPlayer(s) == null ? "§c" + s : "§a" + s);
								p++;
							}
					    			
					    	sender.sendMessage(Main.prefix() + "There are §6" + (p - 1) + " §7spectators.");
					    	sender.sendMessage("§8» §7Spectators§8: §a" + list.toString() + "§8.");
							return true;
						}
						
						sender.sendMessage(ChatColor.RED + "Only players can spectate.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("spectate")) {
			if (sender.hasPermission("uhc.spectate")) {
				if (args.length == 1) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	ArrayList<String> types = new ArrayList<String>();
		        	types.add("on");
		        	types.add("off");
		        	types.add("toggle");
		        	
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
					if (sender.hasPermission("uhc.spectate")) {
						ArrayList<String> arg = new ArrayList<String>();
			        	
			        	if (args[0].equalsIgnoreCase("on")) {
				        	if (!args[1].equals("")) {
				        		for (Player online : PlayerUtils.getPlayers()) {
				        			if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
				        				if (!Spectator.getManager().isSpectating(online)) {
					        				arg.add(online.getName());
				        				}
				        			}
				        		}
				        	}
				        	else {
				        		for (Player online : PlayerUtils.getPlayers()) {
			        				if (!Spectator.getManager().isSpectating(online)) {
				        				arg.add(online.getName());
			        				}
				        		}
				        	}
			        	}
			        	else if (args[0].equalsIgnoreCase("off")) {
				        	if (!args[1].equals("")) {
				        		for (Player online : PlayerUtils.getPlayers()) {
				        			if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
				        				if (Spectator.getManager().isSpectating(online)) {
					        				arg.add(online.getName());
				        				}
				        			}
				        		}
				        	}
				        	else {
				        		for (Player online : PlayerUtils.getPlayers()) {
			        				if (Spectator.getManager().isSpectating(online)) {
				        				arg.add(online.getName());
			        				}
				        		}
				        	}
			        	}
			        	else if (args[0].equalsIgnoreCase("toggle")) {
				        	if (!args[1].equals("")) {
				        		for (Player online : PlayerUtils.getPlayers()) {
				        			if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
				        				arg.add(online.getName());
				        			}
				        		}
				        	}
				        	else {
				        		for (Player online : PlayerUtils.getPlayers()) {
			        				arg.add(online.getName());
				        		}
				        	}
			        	}
			        	return arg;
					}
		        }
			}
		}
		return null;
	}
}