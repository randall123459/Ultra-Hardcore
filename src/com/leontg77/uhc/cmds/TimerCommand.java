package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class TimerCommand implements CommandExecutor, TabCompleter {
	private BukkitRunnable run = null;
	private boolean countdown = true;
	private String message;
	private int ticks;
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.timer")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /timer <duration> <message>");
			return true;
		}
		
		if (args.length == 1 && !args[0].equalsIgnoreCase("cancel")) {
			sender.sendMessage(Main.PREFIX + "Usage: /timer <duration> <message>");
			return true;
		}

		if (args.length >= 1 && args[0].equalsIgnoreCase("cancel")) {
			if (run != null) {
				run.cancel();
				run = null;
				sender.sendMessage(Main.PREFIX + "Timer cancelled.");
			} else {
				sender.sendMessage(ChatColor.RED + "Timer is not running.");
			}
			return true;
		}
		
		if (run != null) {
			sender.sendMessage(ChatColor.RED + "Timer is already running.");
			return true;
		}

		int millis;

		try {
			millis = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild number.");
			return true;
		}
		
		if (millis <= 0) {
			countdown = false;
		} else {
			countdown = true;
		}

		StringBuilder msg = new StringBuilder();
		
		for (int i = 1; i < args.length; i++) {
			msg.append(args[i]).append(" ");
		}
		
		run = new BukkitRunnable() {
			public void run() {
				if (countdown) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, message + " " + DateUtils.ticksToString(ticks)); 
					}
					ticks--;
					
					if (ticks < 0) {
						cancel();
						run = null;
					}
				} else {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, message); 
					}
				}
			}
		};

		sender.sendMessage(Main.PREFIX + "Timer started.");
		run.runTaskTimer(Main.plugin, 0, 20);
		
		this.message = ChatColor.translateAlternateColorCodes('&', msg.toString().trim());
		this.ticks = millis;
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.timer")) {
			return null;
		}
		
    	ArrayList<String> toReturn = new ArrayList<String>();
    	
    	if (args.length == 1) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("cancel");
        	types.add("");
        	
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
    	
    	if (args.length > 1) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("&7The game is starting in &8»&a");
        	
        	if (args[1].equals("")) {
        		for (String type : types) {
        			toReturn.add(type);
        		}
        	} else {
        		for (String type : types) {
        			if (type.startsWith(args[1].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
    	}

    	return toReturn;
	}
}