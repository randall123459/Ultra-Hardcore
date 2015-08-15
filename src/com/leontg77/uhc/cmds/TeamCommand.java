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

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.util.PlayerUtils;

public class TeamCommand implements CommandExecutor, TabCompleter {
	public static HashMap<Player, ArrayList<Player>> invites = new HashMap<Player, ArrayList<Player>>();
	private boolean enabled = false;

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can create and manage teams.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("team")) {
			if (args.length == 0) {
				sendHelp(player);
				return true;
			}
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("create")) {
					if (!enabled) {
						player.sendMessage(Main.prefix() + "Team management is currently disabled.");
						return true;
					}
				
					if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
						player.sendMessage(Main.prefix() + "You are already on a team.");
						return true;
					}
					
					Team t = null;
					
					for (Team team : Teams.getManager().getTeams()) {
						if (team.getSize() == 0) {
							t = team;
							break;
						}
					}
					
					if (t == null) {
						sender.sendMessage(Main.prefix() + "There are no more available teams.");
						return true;
					}

					player.sendMessage(Main.prefix() + "Team created! Use §a/team invite <player>§7 to invite a player.");
					t.addEntry(player.getName());
				}
				else if (args[0].equalsIgnoreCase("leave")) {
					if (!enabled) {
						player.sendMessage(Main.prefix() + "Team management is currently disabled.");
						return true;
					}
					
					Team team = player.getScoreboard().getEntryTeam(player.getName());
					
					if (team == null) {
						player.sendMessage(Main.prefix() + "You are not on a team.");
						return true;
					}
					team.removeEntry(player.getName());
					player.sendMessage(Main.prefix() + "You left your team.");
					
					for (String entry : team.getEntries()) {
						Player teammate = Bukkit.getServer().getPlayer(entry);
						
						if (teammate != null) {
							teammate.sendMessage(Main.prefix() + player.getName() + " left your team.");
						}
					}
				}
				else if (args[0].equalsIgnoreCase("info")) {
					Team team = player.getScoreboard().getEntryTeam(player.getName());
					
					if (team == null) {
						player.sendMessage(Main.prefix() + "You are not on a team.");
						return true;
					}
					
					StringBuilder list = new StringBuilder("");
					
					for (String entry : team.getEntries()) {
						if (list.length() > 0) {
							list.append("§f, ");
						}

						OfflinePlayer teammates = PlayerUtils.getOfflinePlayer(entry);
						
						if (teammates.isOnline()) {
							list.append(ChatColor.GREEN + teammates.getName());
						} else {
							list.append(ChatColor.RED + teammates.getName());
						}
					}
					
					player.sendMessage(Main.prefix() + "Your teammates: §o(Names in read means they are offline)");
					player.sendMessage("§8» §f" + list.toString().trim());
				}
				else if (args[0].equalsIgnoreCase("clear")) {
					if (player.hasPermission("uhc.teamadmin")) {
						for (Team team : Scoreboards.getManager().sb.getTeams()) {
							for (String p : team.getEntries()) {
								team.removeEntry(p);
							}
						}
						
						PlayerUtils.broadcast(Main.prefix() + "All teams has been cleared.");
					} else {
						sendHelp(player);
					}
				}
				else if (args[0].equalsIgnoreCase("list")) {
					if (player.hasPermission("uhc.teamadmin")) {
						player.sendMessage(Main.prefix() + "Teams:");
						for (Team team : Teams.getManager().getTeamsWithPlayers()) {
							StringBuilder list = new StringBuilder("");
							
							for (String entry : team.getEntries()) {
								if (list.length() > 0) {
									list.append("§f, ");
								}
								list.append(entry);
							}
							
							player.sendMessage(team.getPrefix() + team.getName() + ": §f" + list.toString().trim() + ".");
						}
					} else {
						sendHelp(player);
					}
				}
				else if (args[0].equalsIgnoreCase("enable")) {
					if (player.hasPermission("uhc.teamadmin")) {
						if (enabled) {
							player.sendMessage(Main.prefix() + "Team management is already enabled.");
							return true;
						}
						
						PlayerUtils.broadcast(Main.prefix() + "Team management has been enabled.");
						enabled = true;
					} else {
						sendHelp(player);
					}
				}
				else if (args[0].equalsIgnoreCase("disable")) {
					if (player.hasPermission("uhc.teamadmin")) {
						if (!enabled) {
							player.sendMessage(Main.prefix() + "Team management is not enabled.");
							return true;
						}
						
						PlayerUtils.broadcast(Main.prefix() + "Team management has been disabled.");
						enabled = false;
					} else {
						sendHelp(player);
					}
				}
				else {
					sendHelp(player);
				}
				return true;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[1]);
			
			if (args[0].equalsIgnoreCase("create")) {
				if (!enabled) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
			
				if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
					player.sendMessage(Main.prefix() + "You are already on a team.");
					return true;
				}
				
				Team t = null;
				
				for (Team team : Teams.getManager().getTeams()) {
					if (team.getSize() == 0) {
						t = team;
						break;
					}
				}
				
				if (t == null) {
					sender.sendMessage(Main.prefix() + "There are no more available teams.");
					return true;
				}

				player.sendMessage(Main.prefix() + "Team created! Use §a/team invite <player>§7 to invite a player.");
				t.addEntry(player.getName());
			}
			else if (args[0].equalsIgnoreCase("enable")) {
				if (player.hasPermission("uhc.teamadmin")) {
					if (enabled) {
						player.sendMessage(Main.prefix() + "Team management is already enabled.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Team management has been enabled.");
					enabled = true;
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("disable")) {
				if (player.hasPermission("uhc.teamadmin")) {
					if (!enabled) {
						player.sendMessage(Main.prefix() + "Team management is not enabled.");
						return true;
					}
					
					PlayerUtils.broadcast(Main.prefix() + "Team management has been disabled.");
					enabled = false;
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("leave")) {
				if (!enabled) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(Main.prefix() + "You are not on a team.");
					return true;
				}
				team.removeEntry(player.getName());
				player.sendMessage(Main.prefix() + "You left your team.");
				
				for (String entry : team.getEntries()) {
					Player teammate = Bukkit.getServer().getPlayer(entry);
					
					if (teammate != null) {
						teammate.sendMessage(Main.prefix() + ChatColor.GREEN + player.getName() + " §7left your team.");
					}
				}
			}
			else if (args[0].equalsIgnoreCase("info")) {
				if (!player.hasPermission("uhc.teamadmin")) {
					sendHelp(player);
					return true;
				}
				
				if (target == null) {
					player.sendMessage(Main.prefix() + "That player is not online.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(target.getName());
				
				if (team == null) {
					player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7is not on a team.");
					return true;
				}
				
				StringBuilder list = new StringBuilder("");
				
				for (String entry : team.getEntries()) {
					if (list.length() > 0) {
						list.append("§f, ");
					}
					
					OfflinePlayer teammate = PlayerUtils.getOfflinePlayer(entry);
					
					if (teammate.isOnline()) {
						list.append(ChatColor.GREEN + teammate.getName());
					} else {
						list.append(ChatColor.RED + teammate.getName());
					}
				}
				
				player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7is on team §a" + team.getName() + "§7, teammates:");
				player.sendMessage("§8» §f" + list.toString().trim());
			}
			else if (args[0].equalsIgnoreCase("clear")) {
				if (player.hasPermission("uhc.teamadmin")) {
					for (Team team : Scoreboards.getManager().sb.getTeams()) {
						for (String p : team.getEntries()) {
							team.removeEntry(p);
						}
					}
					
					PlayerUtils.broadcast(Main.prefix() + "All teams has been cleared.");
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("list")) {
				if (player.hasPermission("uhc.teamadmin")) {
					player.sendMessage(Main.prefix() + "Teams:");
					for (Team team : Teams.getManager().getTeamsWithPlayers()) {
						StringBuilder list = new StringBuilder("");
						
						for (String entry : team.getEntries()) {
							if (list.length() > 0) {
								list.append("§f, ");
							}
							list.append(entry);
						}
						
						player.sendMessage(team.getPrefix() + team.getName() + ": §f" + list.toString().trim() + ".");
					}
				} else {
					sendHelp(player);
				}
			}
			else if (args[0].equalsIgnoreCase("invite")) {
				if (!enabled) {
					player.sendMessage(Main.prefix() + "Team management is currently disabled.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(Main.prefix() + "You are not on a team.");
					return true;
				}
				
				if (team.getSize() >= Main.teamSize) {
					player.sendMessage(Main.prefix() + "Your team is currently full.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(Main.prefix() + "That player is not online.");
					return true;
				}
				
				Team team1 = player.getScoreboard().getEntryTeam(target.getName());
				
				if (team1 != null) {
					player.sendMessage(Main.prefix() + "That player is already on a team.");
					return true;
				}
				
				for (String entry : team.getEntries()) {
					Player teammate = Bukkit.getServer().getPlayer(entry);
					
					if (teammate != null) {
						teammate.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7was invited to your team.");
					}
				}

				if (!invites.containsKey(player)) {
					invites.put(player, new ArrayList<Player>());
				}
				invites.get(player).add(target);
				target.sendMessage(Main.prefix() + "You have been invited to §a" + player.getName() + "'s §7team.");
				
				ComponentBuilder builder = new ComponentBuilder("");
				builder.append(Main.prefix() + "§6§l§nClick here to accept his request.");
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + player.getName()));
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to join the team.") }));
				target.spigot().sendMessage(builder.create());
			}
			else if (args[0].equalsIgnoreCase("kick")) {
				if (!enabled) {
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
				
				Team team1 = player.getScoreboard().getEntryTeam(target.getName());
				
				if (team1 == null) {
					player.sendMessage(Main.prefix() + "That player is not on a team.");
					return true;
				}
				
				if (team != team1) {
					player.sendMessage(Main.prefix() + "That player is not on your team.");
					return true;
				}
				
				team.removeEntry(target.getName());
				target.sendMessage(Main.prefix() + "You got kicked out of your team.");
				
				for (String entry : team.getEntries()) {
					Player teammate = Bukkit.getServer().getPlayer(entry);
					
					if (teammate != null) {
						teammate.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7was kicked from your team.");
					}
				}
			
			}
			else if (args[0].equalsIgnoreCase("accept")) {
				if (!enabled) {
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
					
					if (team.getSize() >= Main.teamSize) {
						player.sendMessage(Main.prefix() + "That team is currently full.");
						return true;
					}
				
					player.sendMessage(Main.prefix() + "Request accepted.");
					team.addEntry(player.getName());
					
					for (String entry : team.getEntries()) {
						Player teammate = Bukkit.getServer().getPlayer(entry);
						
						if (teammate != null) {
							teammate.sendMessage(Main.prefix() + ChatColor.GREEN + player.getName() + " §7joined your team.");
						}
					}
					
					invites.get(target).remove(player);
				} else {
					player.sendMessage(Main.prefix() + ChatColor.GREEN + target.getName() + " §7hasn't sent you any requests.");
				}
			}
			else if (args[0].equalsIgnoreCase("deny")) {
				if (!enabled) {
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
	        	return arg;
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
		player.sendMessage("§8» §f/team info - Display your teammates.");
		if (player.hasPermission("uhc.teamadmin")) {
			player.sendMessage(Main.prefix() + "Team admin help:");
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