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

/**
 * Team command class.
 * 
 * @author LeonTG77
 */
public class TeamCommand implements CommandExecutor, TabCompleter {
	public static HashMap<Player, List<Player>> invites = new HashMap<Player, List<Player>>();
	public static HashMap<String, List<String>> savedTeams = new HashMap<String, List<String>>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Teams teams = Teams.getInstance();
		Game game = Game.getInstance();
		
		if (args.length == 0) {
			sendHelp(sender);
			return true;
		}
		
		if (args.length > 1) {
			Player target = Bukkit.getServer().getPlayer(args[1]);
			
			if (args[0].equalsIgnoreCase("info")) {
				if (!sender.hasPermission("uhc.team")) {
					sendHelp(sender);
					return true;
				}
				
				if (target == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
					return true;
				}
				
				Team team = target.getScoreboard().getEntryTeam(target.getName());
				
				if (team == null || Spectator.getInstance().isSpectating(target)) {
					sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + "'s §7team info:");
					sender.sendMessage("§8» §7Team: §cNone");
					
					if (Main.kills.containsKey(target.getName())) {
						sender.sendMessage("§8» §7Kills: §a" + Main.kills.get(target.getName()));
					}
					return true;
				}
				
				if (!savedTeams.containsKey(team.getName())) {
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.savedTeams.put(team.getName(), players);
				}
				
				StringBuilder list = new StringBuilder("");
				int i = 1;
				
				for (String entry : savedTeams.get(team.getName())) {
					if (list.length() > 0) {
						if (i == savedTeams.get(team.getName()).size()) {
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
				
				sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + "'s §7team info:");
				sender.sendMessage("§8» §7Team: " + team.getPrefix() + team.getName());
				
				if (Main.kills.containsKey(target.getName())) {
					sender.sendMessage("§8» §7Kills: §a" + Main.kills.get(target.getName()));
				}
				
				if (Main.teamKills.containsKey(team.getName())) {
					sender.sendMessage("§8» §7Team Kills: §a" + Main.teamKills.get(team.getName()));
				}
				
				sender.sendMessage("§8» ---------------------------");
				sender.sendMessage("§8» §7Teammates: §o(Names in red means they are offline)");
				sender.sendMessage("§8» §f" + list.toString().trim());
				return true;
			}
		
			if (args[0].equalsIgnoreCase("invite")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only players can create and manage teams.");
					return true;
				}
				
				Player player = (Player) sender;
				
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(sender.getName());
				
				if (team == null) {
					sender.sendMessage(Main.PREFIX + "You are not on a team.");
					return true;
				}
				
				if (target == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
					return true;
				}
				
				if (team.getSize() >= Game.getInstance().getTeamSize()) {
					sender.sendMessage(Main.PREFIX + "Your team is currently full.");
					return true;
				}
				
				Team team1 = player.getScoreboard().getEntryTeam(target.getName());
				
				if (team1 != null) {
					sender.sendMessage(Main.PREFIX + "That player is already on a team.");
					return true;
				}
				
				teams.sendMessage(team, Main.PREFIX + ChatColor.GREEN + target.getName() + " §7has been invited to your team.");

				if (!invites.containsKey(sender)) {
					invites.put(player, new ArrayList<Player>());
				}
				invites.get(sender).add(target);
				target.sendMessage(Main.PREFIX + "You have been invited to §a" + sender.getName() + "'s §7team.");
				
				ComponentBuilder builder = new ComponentBuilder("");
				builder.append(Main.PREFIX + "§6§l§nClick here to accept his request.");
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + sender.getName()));
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to join " + sender.getName() + "'s team.") }));
				target.spigot().sendMessage(builder.create());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("kick")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only players can create and manage teams.");
					return true;
				}
				
				Player player = (Player) sender;
				
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}
				
				Team team = player.getScoreboard().getEntryTeam(sender.getName());
				
				if (team == null) {
					sender.sendMessage(Main.PREFIX + "You are not on a team.");
					return true;
				}
				
				if (target == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
					return true;
				}
				
				if (!team.getEntries().contains(target.getName())) {
					sender.sendMessage(Main.PREFIX + "That player is not on your team.");
					return true;
				}
				
				team.removeEntry(target.getName());
				target.sendMessage(Main.PREFIX + "You got kicked out of your team.");
				
				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				TeamCommand.savedTeams.put(team.getName(), players);
				teams.sendMessage(team, Main.PREFIX + ChatColor.GREEN + target.getName() + " §7was kicked from your team.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("accept")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only players can create and manage teams.");
					return true;
				}
				
				Player player = (Player) sender;
				
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}
				
				if (target == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
					return true;
				}
				
				if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
					sender.sendMessage(Main.PREFIX + "You are already on a team.");
					return true;
				}
				
				if (invites.containsKey(target) && invites.get(target).contains(sender)) {
					Team team = target.getScoreboard().getEntryTeam(target.getName());
					
					if (team == null) {
						sender.sendMessage(Main.PREFIX + "That player is not on a team.");
						return true;
					}
					
					if (team.getSize() >= Game.getInstance().getTeamSize()) {
						sender.sendMessage(Main.PREFIX + "That team is currently full.");
						return true;
					}
				
					sender.sendMessage(Main.PREFIX + "Request accepted.");
					team.addEntry(sender.getName());
					
					teams.sendMessage(team, Main.PREFIX + ChatColor.GREEN + sender.getName() + " §7joined your team.");
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.savedTeams.put(team.getName(), players);
					
					invites.get(target).remove(sender);
				} else {
					sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7hasn't sent you any requests.");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("deny")) {
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}
				
				if (target == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
					return true;
				}
				
				if (invites.containsKey(target) && invites.get(target).contains(sender)) {
					target.sendMessage(Main.PREFIX + ChatColor.GREEN + sender.getName() + " §7denied your request.");
					sender.sendMessage(Main.PREFIX + "Request denied.");
					
					invites.get(target).remove(sender);
				} else {
					sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7hasn't sent you any requests.");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("remove")) {
				if (!sender.hasPermission("uhc.team")) {
					sendHelp(sender);
					return true;
				}
				
				if (target == null) {
					OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);
					
					Team team = teams.getTeam(offline);
					
					if (team == null) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
						return true;
					}
					
					sender.sendMessage(Main.PREFIX + ChatColor.GREEN + offline.getName() + " §7was removed from his team.");
					teams.leaveTeam(offline);

					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.savedTeams.put(team.getName(), players);
					return true;
				}
				
				Team team = teams.getTeam(target);
				
				if (team == null) {
					sender.sendMessage(Main.PREFIX + "That player is not on a team.");
					return true;
				}
				
				sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7was removed from his team.");
				teams.leaveTeam(target);

				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				savedTeams.put(team.getName(), players);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("delete")) {
				if (!sender.hasPermission("uhc.team")) {
					sendHelp(sender);
					return true;
				}
				
				Team team = teams.getTeam(args[1]);
				
				if (team == null) {
					sender.sendMessage(Main.PREFIX + "That team does not exist.");
					return true;
				}
				
				for (String p : team.getEntries()) {
					team.removeEntry(p);
				}
				
				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				TeamCommand.savedTeams.put(team.getName(), players);
				
				sender.sendMessage(Main.PREFIX + "Team " + team.getName() + " has been deleted.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("friendlyfire")) {
				if (!sender.hasPermission("uhc.team")) {
					sendHelp(sender);
					return true;
				}
				
				boolean enable;
				
				if (args[1].equalsIgnoreCase("true")) {
					enable = true;
				}
				else if (args[1].equalsIgnoreCase("false")) {
					enable = false;
				}
				else {
					sender.sendMessage(Main.PREFIX + "FriendlyFire can only be true or false.");
					return true;
				}
				
				for (Team team : Scoreboards.getInstance().board.getTeams()) {
					team.setAllowFriendlyFire(enable);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "FriendlyFire is now " + (enable ? "enabled." : "disabled."));
				return true;
			}
		}
	
		if (args.length > 2) {
			if (args[0].equalsIgnoreCase("add")) {
				if (!sender.hasPermission("uhc.team")) {
					sendHelp(sender);
					return true;
				}
				
				Team team = teams.getTeam(args[1]);
				
				if (team == null) {
					sender.sendMessage(Main.PREFIX + "That team does not exist.");
					return true;
				}
				
				OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[2]);
				
				teams.joinTeam(team, offline);
				
				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				TeamCommand.savedTeams.put(team.getName(), players);
				
				sender.sendMessage(Main.PREFIX + ChatColor.GREEN + offline.getName() + "§7 was added to team " + team.getName() + ".");
				return true;
			} 
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can create and manage teams.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args[0].equalsIgnoreCase("create")) {
			if (!game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
				return true;
			}
		
			if (teams.getTeam(player) != null) {
				sender.sendMessage(Main.PREFIX + "You are already on a team.");
				return true;
			}
			
			Team team = teams.findAvailableTeam();
			
			if (team == null) {
				sender.sendMessage(Main.PREFIX + "There are no more available teams.");
				return true;
			}
			
			teams.joinTeam(team, player);
			
			ArrayList<String> players = new ArrayList<String>(team.getEntries());
			TeamCommand.savedTeams.put(team.getName(), players);
			
			sender.sendMessage(Main.PREFIX + "Team created! Use §a/team invite <player>§7 to invite a player.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("leave")) {
			if (!game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
				return true;
			}
			
			Team team = teams.getTeam(player);
			
			if (team == null) {
				sender.sendMessage(Main.PREFIX + "You are not on a team.");
				return true;
			}

			sender.sendMessage(Main.PREFIX + "You left your team.");
			teams.leaveTeam(player);
			
			ArrayList<String> players = new ArrayList<String>(team.getEntries());
			TeamCommand.savedTeams.put(team.getName(), players);
			
			teams.sendMessage(team, Main.PREFIX + sender.getName() + " left your team.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("info")) {
			Team team = teams.getTeam(player);
			
			if (team == null || Spectator.getInstance().isSpectating(player)) {
				sender.sendMessage(Main.PREFIX + "You are not on a team.");
				return true;
			}
			
			if (!savedTeams.containsKey(team.getName())) {
				ArrayList<String> players = new ArrayList<String>(team.getEntries());
				TeamCommand.savedTeams.put(team.getName(), players);
			}
			
			StringBuilder list = new StringBuilder("");
			int i = 1;
			
			for (String entry : savedTeams.get(team.getName())) {
				if (list.length() > 0) {
					if (i == savedTeams.get(team.getName()).size()) {
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
			
			sender.sendMessage(Main.PREFIX + "Your teammates: §o(Names in red means they are offline)");
			sender.sendMessage("§8» §f" + list.toString().trim());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("clear")) {
			if (!sender.hasPermission("uhc.team")) {
				sendHelp(sender);
				return true;
			}
			
			if (sender.hasPermission("uhc.team")) {
				for (Team team : Scoreboards.getInstance().board.getTeams()) {
					for (String p : team.getEntries()) {
						team.removeEntry(p);
					}
				}
				
				for (String key : savedTeams.keySet()) {
					savedTeams.get(key).clear();
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "All teams has been cleared.");
			} else {
				sendHelp(sender);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("color")) {
			if (!sender.hasPermission("uhc.team")) {
				sendHelp(sender);
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All teams has been re-colored.");
			teams.setup();
			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			if (teams.getTeamsWithPlayers().size() == 0) {
				sender.sendMessage(Main.PREFIX + "There are no teams.");
				return true;
			}
			
			sender.sendMessage(Main.PREFIX + "List of teams:");
			
			for (Team team : teams.getTeamsWithPlayers()) {
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
				
				sender.sendMessage(team.getPrefix() + team.getName() + ": §f" + list.toString().trim() + ".");
			}
			return true;
		}
		
		Scoreboards board = Scoreboards.getInstance();
		
		if (args[0].equalsIgnoreCase("enable")) {
			if (!sender.hasPermission("uhc.team")) {
				sendHelp(sender);
				return true;
			}
			
			if (game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is already enabled.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Team management has been enabled.");

			if (game.pregameBoard()) {
				board.setScore("§e ", 12);
				board.setScore("§8» §cTeam:", 11);
				board.setScore("§8» §7/team", 10);
			}
			game.setTeamManagement(true);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("disable")) {
			if (!sender.hasPermission("uhc.team")) {
				sendHelp(sender);
				return true;
			}
			
			if (!game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is not enabled.");
				return true;
			}

			if (game.pregameBoard()) {
				board.resetScore("§e ");
				board.resetScore("§8» §cTeam:");
				board.resetScore("§8» §7/team");
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Team management has been disabled.");
			game.setTeamManagement(false);
			return true;
		}
		
		sendHelp(sender);
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("create");
        	types.add("invite");
        	types.add("kick");
        	types.add("accept");
        	types.add("deny");
        	types.add("info");
        	types.add("list");
        	
        	if (sender.hasPermission("uhc.team")) {
	        	types.add("clear");
	        	types.add("add");
	        	types.add("remove");
	        	types.add("delete");
	        	types.add("friendlyfire");
        	}
        	
        	if (args[0].equals("")) {
        		for (String type : types) {
        			toReturn.add(type);
        		}
        	} else {
        		for (String type : types) {
        			if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
        }
		
		if (args.length == 2) {
        	if (args[0].equalsIgnoreCase("add")) {
	        	if (args[1].equals("")) {
	        		for (Team teams : Teams.getInstance().getTeams()) {
	        			toReturn.add(teams.getName());
	        		}
	        	} else {
	        		for (Team teams : Teams.getInstance().getTeams()) {
	        			if (teams.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(teams.getName());
	        			}
	        		}
	        	}
        	}
        	else if (args[0].equalsIgnoreCase("delete")) {
	        	if (args[1].equals("")) {
	        		for (Team teams : Teams.getInstance().getTeams()) {
	        			toReturn.add(teams.getName());
	        		}
	        	} else {
	        		for (Team teams : Teams.getInstance().getTeams()) {
	        			if (teams.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(teams.getName());
	        			}
	        		}
	        	}
        	}
        	else if (args[0].equalsIgnoreCase("friendlyfire")) {
	        	toReturn.add("true");
	        	toReturn.add("false");
        	} else {
	        	if (args[1].equals("")) {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			toReturn.add(online.getName());
	        		}
	        	} else {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(online.getName());
	        			}
	        		}
	        	}
        	}
        }
		
		if (args.length == 3) {
        	if (args[0].equalsIgnoreCase("add")) {
	        	if (args[2].equals("")) {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			toReturn.add(online.getName());
	        		}
	        	} else {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			if (online.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
	        				toReturn.add(online.getName());
	        			}
	        		}
	        	}
        	}
        }
		
    	return toReturn;
	}
	
	/**
	 * Sends the help list to a player.
	 * 
	 * @param sender the player.
	 */
	public void sendHelp(CommandSender sender) {
		sender.sendMessage(Main.PREFIX + "Team help:");
		sender.sendMessage("§8» §f/pm <message> §7- §f§oTalk in team chat.");
		sender.sendMessage("§8» §f/tl §7- §f§oTell your coords to your teammates.");
		sender.sendMessage("§8» §f/team info §7- §f§oDisplay your team info.");
		sender.sendMessage("§8» §f/team list §7- §f§oList all teams.");
		
		if (Game.getInstance().teamManagement()) {
			sender.sendMessage("§8» §f/team create §7- §f§oCreate a team.");
			sender.sendMessage("§8» §f/team leave §7- §f§oLeave your team.");
			sender.sendMessage("§8» §f/team invite <player> §7- §f§oInvite a player to your team.");
			sender.sendMessage("§8» §f/team kick <player> §7- §f§oKick a player to your team.");
			sender.sendMessage("§8» §f/team accept <player> §7- §f§oAccept the players request.");
			sender.sendMessage("§8» §f/team deny <player> §7- §f§oDeny the players request.");
		}
		
		if (sender.hasPermission("uhc.team")) {
			sender.sendMessage(Main.PREFIX + "Team admin help:");
			sender.sendMessage("§8» §f/team info <player> §7- §f§oDisplay the targets team info.");
			sender.sendMessage("§8» §f/team enable §7- §f§oEnable team management.");
			sender.sendMessage("§8» §f/team disable §7- §f§oDisable team management.");
			sender.sendMessage("§8» §f/team add <team> <player> §7- §f§oAdd a player to a team.");
			sender.sendMessage("§8» §f/team remove <player> §7- §f§oRemove a player from his team.");
			sender.sendMessage("§8» §f/team delete <team> §7- §f§oEmpty a specific team.");
			sender.sendMessage("§8» §f/team friendlyfire <true|false> §7- §f§oToggle FriendlyFire.");
			sender.sendMessage("§8» §f/team clear §7- §f§oClear all teams.");
		}
	}
}