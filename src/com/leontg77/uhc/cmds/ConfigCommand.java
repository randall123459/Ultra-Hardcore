package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.BorderShrink;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Config command class.
 * 
 * @author LeonTG77
 */
public class ConfigCommand implements CommandExecutor, TabCompleter {

	/**
	 * ConfigValue class
	 * <p>
	 * Class used for the config command to 
	 * get all the possible config options.
	 * 
	 * @author LeonTG77
	 */
	public enum ConfigValue {
		APPLERATES, BORDERSHRINK, FLINTRATES, HOST, MATCHPOST, MAXPLAYERS, MEETUP, PVP, RRNAME, SCENARIOS, TEAMSIZE, WORLD, FFA, HEADSHEAL, SHEARRATES;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.config")) {
			sender.sendMessage(Main.NO_PERM_MSG); 
			return true;
		}

		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Main.PREFIX + "Usage: /config <type> <value>");
				return true;
			}
			
			Player player = (Player) sender;

			InvGUI inv = InvGUI.getInstance();
			inv.openConfigOptions(player);
			return true;
		}
		
		if (args.length == 1) {
			sender.sendMessage(Main.PREFIX + "Usage: /config <type> <value>");
			return true;
		}
		
		Game game = Game.getInstance();
		ConfigValue type;
		
		try {
			type = ConfigValue.valueOf(args[0].toUpperCase());
		} catch (Exception e) {
			StringBuilder types = new StringBuilder();
			int i = 1;
			
			for (ConfigValue value : ConfigValue.values()) {
				if (types.length() > 0) {
					if (i == ConfigValue.values().length) {
						types.append(" §7and§a ");
					} else {
						types.append("§7, §a");
					}
				}
				
				types.append(value.name().toLowerCase());
				i++;
			}
			
			sender.sendMessage(Main.PREFIX + "Available config types: §a" + types.toString().trim() + "§7.");
			return true;
		}
		
		switch (type) {
		case APPLERATES:
			int appleRate;
			
			try {
				appleRate = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild apple rate.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Apple rates has been changed to §a" + appleRate + "%");
			game.setAppleRates(appleRate);
			break;
		case BORDERSHRINK:
			BorderShrink border;
			
			try {
				border = BorderShrink.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild bordertype.");
				return true;
			}
			
			if (border == BorderShrink.NEVER) {
				PlayerUtils.broadcast(Main.PREFIX + "Border will no longer shrink.");
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Border will now shrink " + border.getPreText() + border.name().toLowerCase());
			}
			game.setBorderShrink(border);
			break;
		case FFA:
			if (args[1].equalsIgnoreCase("true")) {
				game.setFFA(true);

				PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + GameUtils.getTeamSize() + game.getScenarios() + "§7.");
			} else if (args[1].equalsIgnoreCase("false")) {
				game.setFFA(false);

				PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + GameUtils.getTeamSize() + game.getScenarios() + "§7.");
			} else {
				sender.sendMessage(ChatColor.RED + "FFA can only be true or false, not " + args[1] + ".");
			}
			break;
		case FLINTRATES:
			int flintRate;
			
			try {
				flintRate = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild flint rate.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Flint rates has been changed to §a" + flintRate + "%");
			game.setFlintRates(flintRate);
			break;
		case HEADSHEAL:
			int headheals;
			
			try {
				headheals = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild heal amount.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Golden heads now heal §a" + headheals + "§7 hearts.");
			game.setShearRates(headheals);
			break;
		case HOST:
			PlayerUtils.broadcast(Main.PREFIX + "The host has been changed to §a" + args[1] + "§7.");
			game.setHost(args[1]);
			break;
		case MATCHPOST:
			PlayerUtils.broadcast(Main.PREFIX + "The matchpost has been changed to §a" + args[1] + "§7.");
			game.setMatchPost(args[1]);
			break;
		case MAXPLAYERS:
			int maxplayers;
			
			try {
				maxplayers = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild player limit.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "The max player limit is now §a" + maxplayers + "§7.");
			game.setMaxPlayers(maxplayers);
			break;
		case MEETUP:
			int meetup;
			
			try {
				meetup = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild meetup time.");
				return true;
			}

			PlayerUtils.broadcast(Main.PREFIX + "Meetup is now §a" + meetup + " §7minutes in.");
			game.setMeetup(meetup);
			break;
		case PVP:
			int pvp;
			
			try {
				pvp = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild pvp time.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "PvP will now be enabled §a" + pvp + " §7minutes in.");
			game.setPvP(pvp);
			break;
		case RRNAME:
			StringBuilder nameBuilder = new StringBuilder();
			
			for (int ni = 1; ni < args.length; ni++) {
				nameBuilder.append(args[ni]).append(" ");
			}
			
			game.setRRName(nameBuilder.toString().trim());
			PlayerUtils.broadcast(Main.PREFIX + "The recorded round is now called §a" + game.getRRName() + "§7.");
			break;
		case SCENARIOS:
			StringBuilder scenarioBuilder = new StringBuilder();
			
			for (int ni = 1; ni < args.length; ni++) {
				scenarioBuilder.append(args[ni]).append(" ");
			}
			
			game.setScenarios(scenarioBuilder.toString().trim());
			PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + GameUtils.getTeamSize() + game.getScenarios() + "§7.");
			break;
		case SHEARRATES:
			int shearRate;
			
			try {
				shearRate = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild shear rate.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Shear rates has been changed to §a" + shearRate + "%");
			game.setShearRates(shearRate);
			break;
		case TEAMSIZE:
			int teamSize;
			
			try {
				teamSize = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild teamsize.");
				return true;
			}
			
			game.setTeamSize(teamSize);
			PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + GameUtils.getTeamSize() + game.getScenarios() + "§7.");
			break;
		case WORLD:
			PlayerUtils.broadcast(Main.PREFIX + "The game will now be played in '§a" + args[1] + "§7'.");
			game.setWorld(args[1]);
			break;
		default:
			return true;
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!sender.hasPermission("uhc.config")) {
			return null;
		}
		
    	ArrayList<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	if (args[0].equals("")) {
        		for (ConfigValue type : ConfigValue.values()) {
        			toReturn.add(type.name().toLowerCase());
        		}
        	} else {
        		for (ConfigValue type : ConfigValue.values()) {
        			if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(type.name().toLowerCase());
        			}
        		}
        	}
        }
		
    	return toReturn;
	}
}