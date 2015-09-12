package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.utils.PlayerUtils;

public class TeamCommand implements CommandExecutor, TabCompleter {
	public static HashMap<Player, ArrayList<Player>> invites = new HashMap<Player, ArrayList<Player>>();
	public static HashMap<String, ArrayList<String>> sTeam = new HashMap<String, ArrayList<String>>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can create and manage teams.");
			return true;
		}
		
		Player player = (Player) sender;
		Teams teams = Teams.getManager();
		Game game = Game.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("team")) {
			if (args.length == 0) {
				sendHelp(player);
				return true;
			}
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("create")) {
					if (!game.teamManagement()) {
						player.sendMessage(Main.prefix() + "Team management is currently disabled.");
						return true;
					}
				
					if (teams.getTeam(player) != null) {
						player.sendMessage(Main.prefix() + "You are already on a team.");
						return true;
					}
					
					Team team = teams.findAvailableTeam();
					
					if (team == null) {
						sender.sendMessage(Main.prefix() + "There are no more available teams.");
						return true;
					}
					
					teams.joinTeam(team, player);
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
					
					player.sendMessage(Main.prefix() + "Team created! Use §a/team invite <player>§7 to invite a player.");
				}
				else if (args[0].equalsIgnoreCase("leave")) {
					if (!game.teamManagement()) {
						player.sendMessage(Main.prefix() + "Team management is currently disabled.");
						return true;
					}
					
					Team team = teams.getTeam(player);
					
					if (team == null) {
						player.sendMessage(Main.prefix() + "You are not on a team.");
						return true;
					}

					player.sendMessage(Main.prefix() + "You left your team.");
					teams.leaveTeam(player);
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
					
					teams.sendMessage(team, Main.prefix() + player.getName() + " left your team.");
				}
				else if (args[0].equalsIgnoreCase("info")) {
					Team team = teams.getTeam(player);
					
					if (team == null || Spectator.getManager().isSpectating(player)) {
						player.sendMessage(Main.prefix() + "You are not on a team.");
						return true;
					}
					
					if (!sTeam.containsKey(team.getName())) {
						ArrayList<String> players = new ArrayList<String>(team.getEntries());
						TeamCommand.sTeam.put(team.getName(), players);
					}
					
					StringBuilder list = new StringBuilder("");
					int i = 1;
					
					for (String entry : sTeam.get(team.getName())) {
						if (list.length() > 0) {
							if (i == sTeam.get(team.getName()).size()) {
								list.append(" §7and §f");
							} else {
								list.append("§7, §f");
							}
						}
						
						OfflinePlayer teammates = PlayerUtils.getOfflinePlayer(entry);
						
						if (teammates.isOnline()) {
							list.append(ChatColor.GREEN + teammates.getName());
						} else {
							list.append(ChatColor.RED + teammates.getName());
						}
						i++;
					}
					
					player.sendMessage(Main.prefix() + "Your teammates: §o(Names in red means they are offline)");
					player.sendMessage("§8» §f" + list.toString().trim());
				}
				else if (args[0].equalsIgnoreCase("clear")) {
					if (player.hasPermission("uhc.teamadmin")) {
						for (Team team : Scoreboards.getManager().board.getTeams()) {
							for (String p : team.getEntries()) {
								team.removeEntry(p);
							}
						}
						
						for (String key : sTeam.keySet()) {
							sTeam.get(key).clear();
						}
						
						PlayerUtils.broadcast(Main.prefix() + "All teams has been cleared.");
					} else {
						sendHelp(player);
					}
				}
				else if (args[0].equalsIgnoreCase("list")) {
					if (player.hasPermission("uhc.teamadmin")) {
						player.sendMessage(Main.prefix() + "List of teams:");
						for (Team team : Teams.getManager().getTeamsWithPlayers()) {
							StringBuilder list = new StringBuilder("");
							int i = 1;
							
							for (String entry : team.getEntries()) {
								if (list.length() > 0) {
									if (i == team.getEntries().size()) {
										list.append(" and ");
									} else {
										list.append(", ");
									}
								}
								
								list.append(entry);
								i++;
							}
							
							player.sendMessage(team.getPrefix() + team.getName() + ": §f" + list.toString().trim() + ".");
						}
					} else {
						sendHelp(player);
					}
				}
				else if (args[0].equalsIgnoreCase("enable")) {
					if (player.hasPermission("uhc.teamadmin")) {
						if (game.teamManagement()) {
							player.sendMessage(Main.prefix() + "Team management is already enabled.");
							return true;
						}
						
						PlayerUtils.broadcast(Main.prefix() + "Team management has been enabled.");

						if (game.pregameBoard()) {
							Scoreboards.getManager().setScore("§e ", 13);
							Scoreboards.getManager().setScore("§8» §cTeam:", 12);
							Scoreboards.getManager().setScore("§8» §7/team", 11);
						}
						game.setTeamManagement(true);
					} else {
						sendHelp(player);
					}
				}
				else if (args[0].equalsIgnoreCase("disable")) {
					if (player.hasPermission("uhc.teamadmin")) {
						if (!game.teamManagement()) {
							player.sendMessage(Main.prefix() + "Team management is not enabled.");
							return true;
						}

						if (game.pregameBoard()) {
							Scoreboards.getManager().resetScore("§e ");
							Scoreboards.getManager().resetScore("§8» §cTeam:");
							Scoreboards.getManager().resetScore("§8» §7/team");
						}
						PlayerUtils.broadcast(Main.prefix() + "Team management has been disabled.");
						game.setTeamManagement(false);
					} else {
						sendHelp(player);
					}
				}
				else {
					sendHelp(player);
				}
				return true;
			}
			
			if (args.length > 2) {
				if (args[0].equalsIgnoreCase("add")) {
					if (player.hasPermission("uhc.teamadmin")) {
						Team team = teams.getTeam(args[1]);
						
						if (team == null) {
							player.sendMessage(Main.prefix() + "That team does not exist.");
							return true;
						}
						
						OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[2]);
						
						teams.joinTeam(team, offline);
						
						ArrayList<String> players = new ArrayList<String>(team.getEntries());
						TeamCommand.sTeam.put(team.getName(), players);
						
						player.sendMessage(Main.prefix() + ChatColor.GREEN + offline.getName() + "§7 was added to team " + team.getName() + ".");
					} else {
						sendHelp(player);
					}
				} else {
					player.chat("/team " + args[0] + args[1]);
				}
				return true;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[1]);
			
			if (args[0].equalsIgnoreCase("create")) {
				if (!game.teamManagement()) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
			
				if (teams.getTeam(player) != null) {
					player.sendMessage(Main.prefix() + "You are already on a team.");
					return true;
				}
				
				Team team = teams.findAvailableTeam();
				
				if (team == null) {
					sender.sendMessage(Main.prefix() + "There are no more available teams.");
					return true;
				}
				
				teams.joinTeam(team, player);
				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				TeamCommand.sTeam.put(team.getName(), players);

				player.sendMessage(Main.prefix() + "Team created! Use §a/team invite <player>§7 to invite a player.");
			}
			else if (args[0].equalsIgnoreCase("leave")) {
				if (!game.teamManagement()) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
				
				Team team = teams.getTeam(player);
				
				if (team == null) {
					player.sendMessage(Main.prefix() + "You are not on a team.");
					return true;
				}

				player.sendMessage(Main.prefix() + "You left your team.");
				teams.leaveTeam(player);
				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				TeamCommand.sTeam.put(team.getName(), players);
				teams.sendMessage(team, Main.prefix() + player.getName() + " left your team.");
			}
			else if (args[0].equalsIgnoreCase("clear")) {
				if (player.hasPermission("uhc.teamadmin")) {
					for (Team team : Scoreboards.getManager().board.getTeams()) {
						for (String p : team.getEntries()) {
							team.removeEntry(p);
						}
					}
					
					for (String key : sTeam.keySet()) {
						sTeam.get(key).clear();
					}
					
					PlayerUtils.broadcast(Main.prefix() + "All teams has been cleared.");
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("list")) {
				if (player.hasPermission("uhc.teamadmin")) {
					player.sendMessage(Main.prefix() + "List of teams:");
					for (Team team : Teams.getManager().getTeamsWithPlayers()) {
						StringBuilder list = new StringBuilder("");
						int i = 1;
						
						for (String entry : team.getEntries()) {
							if (list.length() > 0) {
								if (i == team.getEntries().size()) {
									list.append(" and ");
								} else {
									list.append(", ");
								}
							}
							
							list.append(entry);
							i++;
						}
						
						player.sendMessage(team.getPrefix() + team.getName() + ": §f" + list.toString().trim() + ".");
					}
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("enable")) {
				if (player.hasPermission("uhc.teamadmin")) {
					if (game.teamManagement()) {
						player.sendMessage(Main.prefix() + "Team management is already enabled.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Team management has been enabled.");

					if (game.pregameBoard()) {
						Scoreboards.getManager().setScore("§e ", 13);
						Scoreboards.getManager().setScore("§8» §cTeam:", 12);
						Scoreboards.getManager().setScore("§8» §7/team", 11);
					}
					game.setTeamManagement(true);
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("disable")) {
				if (player.hasPermission("uhc.teamadmin")) {
					if (!game.teamManagement()) {
						player.sendMessage(Main.prefix() + "Team management is not enabled.");
						return true;
					}

					if (game.pregameBoard()) {
						Scoreboards.getManager().resetScore("§e ");
						Scoreboards.getManager().resetScore("§8» §cTeam:");
						Scoreboards.getManager().resetScore("§8» §7/team");
					}
					PlayerUtils.broadcast(Main.prefix() + "Team management has been disabled.");
					game.setTeamManagement(false);
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("info")) {
				if (player.hasPermission("uhc.teamadmin")) {
					if (target == null) {
						player.sendMessage(Main.prefix() + "That player is not online.");
						return true;
					}
					
					Team team = player.getScoreboard().getEntryTeam(target.getName());
					
					if (team == null || Spectator.getManager().isSpectating(target)) {
						player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + "'s §7team info:");
						player.sendMessage("§8» §7Team: §cNone");
						if (Main.kills.containsKey(target.getName())) {
							player.sendMessage("§8» §7Kills:" + Main.kills.get(target.getName()));
						}
						return true;
					}
					
					if (!sTeam.containsKey(team.getName())) {
						ArrayList<String> players = new ArrayList<String>(team.getEntries());
						TeamCommand.sTeam.put(team.getName(), players);
					}
					
					StringBuilder list = new StringBuilder("");
					int i = 1;
					
					for (String entry : sTeam.get(team.getName())) {
						if (list.length() > 0) {
							if (i == sTeam.get(team.getName()).size()) {
								list.append(" §7and §f");
							} else {
								list.append("§7, §f");
							}
						}
						
						OfflinePlayer teammates = PlayerUtils.getOfflinePlayer(entry);
						
						if (teammates.isOnline()) {
							list.append(ChatColor.GREEN + teammates.getName());
						} else {
							list.append(ChatColor.RED + teammates.getName());
						}
						i++;
					}
					
					player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + "'s §7team info:");
					player.sendMessage("§8» §7Team: " + team.getPrefix() + team.getName());
					if (Main.kills.containsKey(target.getName())) {
						player.sendMessage("§8» §7Kills: §a" + Main.kills.get(target.getName()));
					}
					if (Main.teamKills.containsKey(team.getName())) {
						player.sendMessage("§8» §7Team Kills: §a" + Main.teamKills.get(team.getName()));
					}
					player.sendMessage("§8» ---------------------------");
					player.sendMessage("§8» §7Teammates: §o(Names in red means they are offline)");
					player.sendMessage("§8» §f" + list.toString().trim());
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("invite")) {
				if (!game.teamManagement()) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(Main.prefix() + "You are not on a team.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(Main.prefix() + "That player is not online.");
					return true;
				}
				
				if (team.getSize() >= Game.getInstance().getTeamSize()) {
					player.sendMessage(Main.prefix() + "Your team is currently full.");
					return true;
				}
				
				Team team1 = player.getScoreboard().getEntryTeam(target.getName());
				
				if (team1 != null) {
					player.sendMessage(Main.prefix() + "That player is already on a team.");
					return true;
				}
				
				teams.sendMessage(team, Main.prefix() + ChatColor.GREEN + target.getName() + " §7has been invited to your team.");

				if (!invites.containsKey(player)) {
					invites.put(player, new ArrayList<Player>());
				}
				invites.get(player).add(target);
				target.sendMessage(Main.prefix() + "You have been invited to §a" + player.getName() + "'s §7team.");
				
				ComponentBuilder builder = new ComponentBuilder("");
				builder.append(Main.prefix() + "§6§l§nClick here to accept his request.");
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + player.getName()));
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to join " + player.getName() + "'s team.") }));
				target.spigot().sendMessage(builder.create());
			}
			else if (args[0].equalsIgnoreCase("kick")) {
				if (!game.teamManagement()) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(Main.prefix() + "You are not on a team.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(Main.prefix() + "That player is not online.");
					return true;
				}
				
				if (!team.getEntries().contains(target.getName())) {
					player.sendMessage(Main.prefix() + "That player is not on your team.");
					return true;
				}
				
				team.removeEntry(target.getName());
				target.sendMessage(Main.prefix() + "You got kicked out of your team.");
				
				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				TeamCommand.sTeam.put(team.getName(), players);
				teams.sendMessage(team, Main.prefix() + ChatColor.GREEN + target.getName() + " §7was kicked from your team.");
			
			}
			else if (args[0].equalsIgnoreCase("accept")) {
				if (!game.teamManagement()) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(Main.prefix() + "That player is not online.");
					return true;
				}
				
				if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
					player.sendMessage(Main.prefix() + "You are already on a team.");
					return true;
				}
				
				if (invites.containsKey(target) && invites.get(target).contains(player)) {
					Team team = target.getScoreboard().getEntryTeam(target.getName());
					
					if (team == null) {
						player.sendMessage(Main.prefix() + "That player is not on a team.");
						return true;
					}
					
					if (team.getSize() >= Game.getInstance().getTeamSize()) {
						player.sendMessage(Main.prefix() + "That team is currently full.");
						return true;
					}
				
					player.sendMessage(Main.prefix() + "Request accepted.");
					team.addEntry(player.getName());
					
					teams.sendMessage(team, Main.prefix() + ChatColor.GREEN + player.getName() + " §7joined your team.");
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
					
					invites.get(target).remove(player);
				} else {
					player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7hasn't sent you any requests.");
				}
			}
			else if (args[0].equalsIgnoreCase("deny")) {
				if (!game.teamManagement()) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(Main.prefix() + "That player is not online.");
					return true;
				}
				
				if (invites.containsKey(target) && invites.get(target).contains(player)) {
					target.sendMessage(Main.prefix() + ChatColor.GREEN + player.getName() + " §7denied your request.");
					player.sendMessage(Main.prefix() + "Request denied.");
					
					invites.get(target).remove(player);
				} else {
					player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7hasn't sent you any requests.");
				}
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				if (player.hasPermission("uhc.teamadmin")) {
					if (target == null) {
						OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);
						
						Team team = teams.getTeam(offline);
						
						if (team == null) {
							player.sendMessage(Main.prefix() + "That player is not on a team.");
							return true;
						}
						
						player.sendMessage(Main.prefix() + ChatColor.GREEN + offline.getName() + " §7was removed from his team.");
						teams.leaveTeam(offline);

						ArrayList<String> players = new ArrayList<String>(team.getEntries());
						TeamCommand.sTeam.put(team.getName(), players);
						return true;
					}
					
					Team team = teams.getTeam(target);
					
					if (team == null) {
						player.sendMessage(Main.prefix() + "That player is not on a team.");
						return true;
					}
					
					player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7was removed from his team.");
					teams.leaveTeam(target);

					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("delete")) {
				if (player.hasPermission("uhc.teamadmin")) {
					Team team = teams.getTeam(args[1]);
					
					if (team == null) {
						player.sendMessage(Main.prefix() + "That team does not exist.");
						return true;
					}
					
					for (String p : team.getEntries()) {
						team.removeEntry(p);
					}
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.sTeam.put(team.getName(), players);
					
					player.sendMessage(Main.prefix() + "Team " + team.getName() + " has been deleted.");
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("friendlyfire")) {
				if (player.hasPermission("uhc.teamadmin")) {
					boolean enable;
					
					if (args[1].equalsIgnoreCase("true")) {
						enable = true;
					}
					else if (args[1].equalsIgnoreCase("false")) {
						enable = false;
					}
					else {
						player.sendMessage(Main.prefix() + "FriendlyFire can only be true or false.");
						return true;
					}
					
					for (Team team : Scoreboards.getManager().board.getTeams()) {
						team.setAllowFriendlyFire(enable);
					}
					
					PlayerUtils.broadcast(Main.prefix() + "FriendlyFire is now " + (enable ? "enabled." : "disabled."));
				} else {
					sendHelp(player);
				}
			}
			else {
				sendHelp(player);
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("team")) {
			if (args.length == 1) {
	        	ArrayList<String> arg = new ArrayList<String>();
	        	ArrayList<String> types = new ArrayList<String>();
	        	types.add("create");
	        	types.add("invite");
	        	types.add("kick");
	        	types.add("accept");
	        	types.add("deny");
	        	types.add("info");
	        	if (sender.hasPermission("uhc.teamadmin")) {
		        	types.add("clear");
		        	types.add("list");
		        	types.add("add");
		        	types.add("remove");
		        	types.add("delete");
		        	types.add("friendlyfire");
	        	}
	        	
	        	if (!args[0].equals("")) {
	        		for (String type : types) {
	        			if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
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
	        	ArrayList<String> arg = new ArrayList<String>();
	        	
	        	if (args[0].equalsIgnoreCase("add")) {
		        	if (!args[1].equals("")) {
		        		for (Team teams : Teams.getManager().getTeams()) {
		        			if (teams.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
		        				arg.add(teams.getName());
		        			}
		        		}
		        	}
		        	else {
		        		for (Team teams : Teams.getManager().getTeams()) {
		        			arg.add(teams.getName());
		        		}
		        	}
	        	}
	        	else if (args[0].equalsIgnoreCase("delete")) {
		        	if (!args[1].equals("")) {
		        		for (Team teams : Teams.getManager().getTeams()) {
		        			if (teams.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
		        				arg.add(teams.getName());
		        			}
		        		}
		        	}
		        	else {
		        		for (Team teams : Teams.getManager().getTeams()) {
		        			arg.add(teams.getName());
		        		}
		        	}
	        	}
	        	else if (args[0].equalsIgnoreCase("friendlyfire")) {
		        	arg.add("true");
		        	arg.add("false");
	        	} else {
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
			
			if (args.length == 3) {
	        	ArrayList<String> arg = new ArrayList<String>();
	        	
	        	if (args[0].equalsIgnoreCase("add")) {
		        	if (!args[2].equals("")) {
		        		for (Player online : PlayerUtils.getPlayers()) {
		        			if (online.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
		        				arg.add(online.getName());
		        			}
		        		}
		        	}
		        	else {
		        		for (Player online : PlayerUtils.getPlayers()) {
		        			arg.add(online.getName());
		        		}
		        	}
		        	
		        	return arg;
	        	}
	        }
		}
		return null;
	}
	
	/**
	 * Sends the help list to a player.
	 * @param player the player.
	 */
	public void sendHelp(Player player) {
		player.sendMessage(Main.prefix() + "Team help:");
		player.sendMessage("§8» §f/team create - Create a team.");
		player.sendMessage("§8» §f/team leave - Leave your team.");
		player.sendMessage("§8» §f/team invite <player> - Invite a player to your team.");
		player.sendMessage("§8» §f/team kick <player> - Kick a player to your team.");
		player.sendMessage("§8» §f/team accept <player> - Accept the players request.");
		player.sendMessage("§8» §f/team deny <player> - Deny the players request.");
		player.sendMessage("§8» §f/team info - Display your team info.");
		if (player.hasPermission("uhc.teamadmin")) {
			player.sendMessage(Main.prefix() + "Team admin help:");
			player.sendMessage("§8» §f/team info <player> - Display the targets team info.");
			player.sendMessage("§8» §f/team enable - Enable team management.");
			player.sendMessage("§8» §f/team disable - Disable team management.");
			player.sendMessage("§8» §f/team add <team> <player> - Add a player to a team.");
			player.sendMessage("§8» §f/team remove <player> - Remove a player from his team.");
			player.sendMessage("§8» §f/team delete <team> - Empty a specific team.");
			player.sendMessage("§8» §f/team friendlyfire <true|false> - Toggle FriendlyFire.");
			player.sendMessage("§8» §f/team list - List all teams.");
			player.sendMessage("§8» §f/team clear - Clear all teams.");
		}
	}
}