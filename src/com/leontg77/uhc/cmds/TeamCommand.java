package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.util.PlayerUtils;

public class TeamCommand implements CommandExecutor, TabCompleter {
	public static HashMap<Player, ArrayList<Player>> invites = new HashMap<Player, ArrayList<Player>>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can create and manage teams.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("team")) {
			if (args.length == 0) {
				player.sendMessage(Main.prefix() + "Team help:");
				player.sendMessage("§7- §f/team create - Create a team.");
				player.sendMessage("§7- §f/team leave - Leave your team.");
				player.sendMessage("§7- §f/team invite <player> - Invite a player to your team.");
				player.sendMessage("§7- §f/team kick <player> - Kick a player from your team.");
				player.sendMessage("§7- §f/team accept <player> - Accept the players request.");
				player.sendMessage("§7- §f/team deny <player> - Deny the players request.");
				player.sendMessage("§7- §f/team info - Display your teammates.");
				if (player.hasPermission("uhc.teamadmin")) {
					player.sendMessage("§7- §f/team clear - Clear all teams.");
				}
				return true;
			}
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("create")) {
					if (Main.ffa) {
						player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
						return true;
					}
					
					if (!State.isState(State.LOBBY)) {
						player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
						return true;
					}
				
					if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
						player.sendMessage(ChatColor.RED + "You are already on a team.");
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
						sender.sendMessage(ChatColor.RED + "No more available teams.");
						return true;
					}
					
					t.addEntry(player.getName());
					player.sendMessage(Main.prefix() + "Team created! Use /team invite <player> to invite a player.");
				}
				else if (args[0].equalsIgnoreCase("leave")) {
					if (Main.ffa) {
						player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
						return true;
					}
					
					if (!State.isState(State.LOBBY)) {
						player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
						return true;
					}
					
					Team team = player.getScoreboard().getEntryTeam(player.getName());
					
					if (team == null) {
						player.sendMessage(ChatColor.RED + "You are not on a team.");
						return true;
					}
					team.removeEntry(player.getName());
					player.sendMessage(Main.prefix() + "You left your team.");
				}
				else if (args[0].equalsIgnoreCase("invite")) {
					if (Main.ffa) {
						player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
						return true;
					}
					
					if (!State.isState(State.LOBBY)) {
						player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
						return true;
					}
					
					player.sendMessage(ChatColor.RED + "Usage: /team invite <player>");
				}
				else if (args[0].equalsIgnoreCase("kick")) {
					if (Main.ffa) {
						player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
						return true;
					}
					
					if (!State.isState(State.LOBBY)) {
						player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
						return true;
					}
					
					player.sendMessage(ChatColor.RED + "Usage: /team kick <player>");
				}
				else if (args[0].equalsIgnoreCase("accept")) {
					if (Main.ffa) {
						player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
						return true;
					}
					
					if (!State.isState(State.LOBBY)) {
						player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
						return true;
					}
					
					player.sendMessage(ChatColor.RED + "Usage: /team accept <player>");
				}
				else if (args[0].equalsIgnoreCase("deny")) {
					if (Main.ffa) {
						player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
						return true;
					}
					
					if (!State.isState(State.LOBBY)) {
						player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
						return true;
					}
					
					player.sendMessage(ChatColor.RED + "Usage: /team deny <player>");
				}
				else if (args[0].equalsIgnoreCase("clear")) {
					if (!player.hasPermission("uhc.teamadmin")) {
						player.sendMessage(ChatColor.RED + "You cannot clear teams.");
						return true;
					}
					
					for (Team team : Teams.getManager().getTeams()) {
						for (String p : team.getEntries()) {
							team.removeEntry(p);
						}
					}
					player.sendMessage(Main.prefix() + "Teams cleared.");
				}
				else if (args[0].equalsIgnoreCase("info")) {
					Team team = player.getScoreboard().getEntryTeam(player.getName());
					
					if (team == null) {
						player.sendMessage(ChatColor.RED + "You are not on a team.");
						return true;
					}
					
					StringBuilder list = new StringBuilder("");
					
					for (String p : team.getEntries()) {
						OfflinePlayer players = PlayerUtils.getOfflinePlayer(p);
						
						if (list.length() > 0) {
							list.append("§f, ");
						}
						
						if (players.isOnline()) {
							list.append(ChatColor.GREEN + players.getName());
						} else {
							list.append(ChatColor.RED + players.getName());
						}
					}
					
					player.sendMessage(Main.prefix() + "Your teammates: (red name = offline)");
					player.sendMessage(list.toString().trim());
				}
				else {
					player.sendMessage(Main.prefix() + "Team help:");
					player.sendMessage("§7- §f/team create - Create a team.");
					player.sendMessage("§7- §f/team leave - Leave your team.");
					player.sendMessage("§7- §f/team invite <player> - Invite a player to your team.");
					player.sendMessage("§7- §f/team kick <player> - Kick a player from your team.");
					player.sendMessage("§7- §f/team accept <player> - Accept the players request.");
					player.sendMessage("§7- §f/team deny <player> - Deny the players request.");
					player.sendMessage("§7- §f/team info - Display your teammates.");
					if (player.hasPermission("uhc.teamadmin")) {
						player.sendMessage("§7- §f/team clear - Clear all teams.");
					}
				}
				return true;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[1]);
			
			if (args[0].equalsIgnoreCase("create")) {
				if (Main.ffa) {
					player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
					return true;
				}
				
				if (!State.isState(State.LOBBY)) {
					player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
					return true;
				}
			
				if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
					player.sendMessage(ChatColor.RED + "You are already on a team.");
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
					sender.sendMessage(ChatColor.RED + "No more available teams.");
					return true;
				}
				
				t.addEntry(player.getName());
				player.sendMessage(Main.prefix() + "Team created! Use /team invite <player> to invite a player.");
			}
			else if (args[0].equalsIgnoreCase("leave")) {
				if (Main.ffa) {
					player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
					return true;
				}
				
				if (!State.isState(State.LOBBY)) {
					player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
					return true;
				}
			
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(ChatColor.RED + "You are not on a team.");
					return true;
				}
				
				team.removeEntry(player.getName());
				player.sendMessage(Main.prefix() + "You left your team.");
				for (String s : team.getEntries()) {
					Player players = Bukkit.getServer().getPlayer(s);
					if (players != null) {
						((Player) players).sendMessage(Main.prefix() + target.getName() + " left your team.");
					}
				}
			}
			else if (args[0].equalsIgnoreCase("invite")) {
				if (Main.ffa) {
					player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
					return true;
				}
				
				if (!State.isState(State.LOBBY)) {
					player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(ChatColor.RED + "You are not on a team.");
					return true;
				}
				
				if (team.getSize() >= Main.teamSize) {
					player.sendMessage(ChatColor.RED + "Your team has reached the max teamsize.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				Team team1 = player.getScoreboard().getEntryTeam(target.getName());
				
				if (team1 != null) {
					player.sendMessage(ChatColor.RED + "That player is already on a team.");
					return true;
				}
				
				for (String s : team.getEntries()) {
					Player players = Bukkit.getServer().getPlayer(s);
					if (players != null) {
						((Player) players).sendMessage(Main.prefix() + target.getName() + " were invited to your team.");
					}
				}

				if (!invites.containsKey(player)) {
					invites.put(player, new ArrayList<Player>());
				}
				invites.get(player).add(target);
				target.sendMessage(Main.prefix() + "You have been invited to " + player.getName() + "'s team.");
				target.sendMessage("§7- §fTo accept, type /team accept " + player.getName());
				target.sendMessage("§7- §fTo decline, type /team deny " + player.getName());
			}
			else if (args[0].equalsIgnoreCase("kick")) {
				if (Main.ffa) {
					player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
					return true;
				}
				
				if (!State.isState(State.LOBBY)) {
					player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(ChatColor.RED + "You are not on a team.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				Team team1 = player.getScoreboard().getEntryTeam(target.getName());
				
				if (team1 == null || team != team1) {
					player.sendMessage(ChatColor.RED + "That player is not on your team.");
					return true;
				}
				
				team.removeEntry(target.getName());
				target.sendMessage(Main.prefix() + "You were kicked from your team.");
				
				for (String s : team.getEntries()) {
					Player players = Bukkit.getServer().getPlayer(s);
					if (players != null) {
						((Player) players).sendMessage(Main.prefix() + target.getName() + " was kicked from your team.");
					}
				}
			
			}
			else if (args[0].equalsIgnoreCase("accept")) {
				if (Main.ffa) {
					player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
					return true;
				}
				
				if (!State.isState(State.LOBBY)) {
					player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
					player.sendMessage(ChatColor.RED + "You are already on a team.");
					return true;
				}
				
				if (invites.containsKey(target) && invites.get(target).contains(player)) {
					Team team = target.getScoreboard().getEntryTeam(target.getName());
					if (team == null) {
						player.sendMessage(ChatColor.RED + "That player is not on a team.");
						return true;
					}
					
					if (team.getSize() >= Main.teamSize) {
						player.sendMessage(ChatColor.RED + "That team has reached the max teamsize.");
						return true;
					}
				
					player.sendMessage(Main.prefix() + "Request accepted.");
					team.addEntry(player.getName());
					for (String s : team.getEntries()) {
						Player players = Bukkit.getServer().getPlayer(s);
						if (players != null) {
							((Player) players).sendMessage(Main.prefix() + player.getName() + " joined your team.");
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "That player has not sent you any requests.");
				}
			}
			else if (args[0].equalsIgnoreCase("deny")) {
				if (Main.ffa) {
					player.sendMessage(Main.teamSize > 1 ? ChatColor.RED + "You cannot create teams in random teams games." : ChatColor.RED + "You cannot create teams in FFA games.");
					return true;
				}
				
				if (!State.isState(State.LOBBY)) {
					player.sendMessage(ChatColor.RED + "You cannot create teams when the game has started.");
					return true;
				}
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				
				if (invites.containsKey(target) && invites.get(target).contains(player)) {
					Team team = target.getScoreboard().getEntryTeam(target.getName());
					if (team == null) {
						player.sendMessage(ChatColor.RED + "That player is not on a team.");
						return true;
					}
					target.sendMessage(Main.prefix() + player.getName() + " denied your request.");
					player.sendMessage(Main.prefix() + "Request denied.");
				} else {
					player.sendMessage(ChatColor.RED + "That player has not sent you any requests.");
				}
			}
			else if (args[0].equalsIgnoreCase("info")) {
				Team team = player.getScoreboard().getEntryTeam(player.getName());
				
				if (team == null) {
					player.sendMessage(ChatColor.RED + "You are not on a team.");
					return true;
				}
				
				StringBuilder list = new StringBuilder("");
				
				for (String s : team.getEntries()) {
					OfflinePlayer players = PlayerUtils.getOfflinePlayer(s);
					if (list.length() > 0) {
						list.append("§f, ");
					}
					
					if (players.isOnline()) {
						list.append(ChatColor.GREEN + players.getName());
					} else {
						list.append(ChatColor.RED + players.getName());
					}
				}
				
				player.sendMessage(Main.prefix() + "Your teammates: (red name = offline)");
				player.sendMessage(list.toString().trim());
			}
			else if (args[0].equalsIgnoreCase("clear")) {
				if (!player.hasPermission("uhc.teamadmin")) {
					player.sendMessage(ChatColor.RED + "You cannot clear teams.");
					return true;
				}
				for (Team team : Teams.getManager().getTeams()) {
					for (String p : team.getEntries()) {
						team.removeEntry(p);
					}
				}
				player.sendMessage(Main.prefix() + "Teams cleared.");
			}
			else {
				player.sendMessage(Main.prefix() + "Team help:");
				player.sendMessage("§7- §f/team create - Create a team.");
				player.sendMessage("§7- §f/team leave - Leave your team.");
				player.sendMessage("§7- §f/team invite <player> - Invite a player to your team.");
				player.sendMessage("§7- §f/team kick <player> - Kick a player from your team.");
				player.sendMessage("§7- §f/team accept <player> - Accept the players request.");
				player.sendMessage("§7- §f/team deny <player> - Deny the players request.");
				player.sendMessage("§7- §f/team info - Display your teammates.");
				if (player.hasPermission("uhc.teamadmin")) {
					player.sendMessage("§7- §f/team clear - Clear all teams.");
				}
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
	        	types.add("accept");
	        	types.add("kick");
	        	types.add("deny");
	        	types.add("info");
	        	if (sender.hasPermission("uhc.teamadmin")) {
		        	types.add("clear");
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
}