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

/**
 * Spectate command class.
 * 
 * @author LeonTG77
 */
public class SpectateCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.spectate")) {
			sender.sendMessage(Main.NO_PERM_MSG);
    		return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /spec <on|off|toggle|list|cmdspy|info> [player]");
    		return true;
		}
		
		Spectator spec = Spectator.getInstance();
		
		if (args[0].equalsIgnoreCase("list")) {
			if (spec.spectators.size() < 1) {
		    	sender.sendMessage(Main.PREFIX + "There are no spectators.");
				return true;
			}
			
			ArrayList<String> players = new ArrayList<String>(spec.spectators);
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
	    			
	    	sender.sendMessage(Main.PREFIX + "There are §6" + (p - 1) + " §7spectators.");
	    	sender.sendMessage("§8» §7Spectators§8: §a" + list.toString() + "§8.");
			return true;
		}
		
		if (!(sender instanceof Player) && args.length == 1) {
			sender.sendMessage(ChatColor.RED + "Only players can manage their spectator mode.");
			return true;
		}
		
		Player target = (Player) sender;
		
		if (args.length > 1 && sender.hasPermission("uhc.spectate.others")) {
			target = Bukkit.getServer().getPlayer(args[1]);
		}
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("toggle")) {
			spec.toggle(target, false);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("on")) {
			spec.enableSpecmode(target, false);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("off")) {
			spec.disableSpecmode(target, false);
			return true;
		}

		if (args[0].equalsIgnoreCase("info")) {
			if (spec.specinfo.contains(target.getName())) {
				spec.specinfo.remove(target.getName());
				target.sendMessage(Main.PREFIX + "Your specinfo has been enabled.");
			} else {
				spec.specinfo.add(target.getName());
				target.sendMessage(Main.PREFIX + "Your specinfo has been disabled.");
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("cmdspy")) {
			if (!target.hasPermission("uhc.cmdspy")) {
				sender.sendMessage(Main.NO_PERM_MSG);
				return true;
			}
			
			if (spec.cmdspy.contains(target.getName())) {
				spec.cmdspy.remove(target.getName());
				target.sendMessage(Main.PREFIX + "Your commandspy has been disabled.");
			} else {
				spec.cmdspy.add(target.getName());
				target.sendMessage(Main.PREFIX + "Your commandspy has been disabled.");
			}
			return true;
		}
		
		target.sendMessage(Main.PREFIX + "Usage: /spec <on|off|toggle|list|cmdspy|info> [player]");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.spectate")) {
    		return null;
		}
		
		ArrayList<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("on");
        	types.add("off");
        	types.add("toggle");
        	types.add("list");
        	types.add("cmdspy");
        	types.add("info");
        	
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
			if (!sender.hasPermission("uhc.spectate.other")) {
		        return null;
			}
			
			if (args[0].equalsIgnoreCase("on")) {
	        	if (args[1].equals("")) {
	        		for (Player online : PlayerUtils.getPlayers()) {
        				if (!Spectator.getInstance().isSpectating(online)) {
	        				toReturn.add(online.getName());
        				}
	        		}
	        	} else {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				if (!Spectator.getInstance().isSpectating(online)) {
		        				toReturn.add(online.getName());
	        				}
	        			}
	        		}
	        	}
        	}
        	else if (args[0].equalsIgnoreCase("off")) {
	        	if (!args[1].equals("")) {
	        		for (Player online : PlayerUtils.getPlayers()) {
        				if (Spectator.getInstance().isSpectating(online)) {
	        				toReturn.add(online.getName());
        				}
	        		}
	        	} else {
	        		for (Player online : PlayerUtils.getPlayers()) {
	        			if (online.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				if (Spectator.getInstance().isSpectating(online)) {
		        				toReturn.add(online.getName());
	        				}
	        			}
	        		}
	        	}
        	}
        	else if (args[0].equalsIgnoreCase("toggle")) {
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
        	else if (args[0].equalsIgnoreCase("cmdspy")) {
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
        	else if (args[0].equalsIgnoreCase("info")) {
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
		return toReturn;
	}
}