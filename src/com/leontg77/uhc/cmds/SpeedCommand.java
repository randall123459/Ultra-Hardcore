package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

public class SpeedCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can change their fly speed.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("speed")) {
        	if (player.hasPermission("uhc.speed") || Main.spectating.contains(player.getName())) {
        		if (args.length == 0) {
        			player.sendMessage(ChatColor.RED + "Usage: /speed <1-4>");
        			return true;
        		}
        		
        		if (args[0].equalsIgnoreCase("1")) {
        			player.setFlySpeed(0.1f);
            		player.sendMessage(Main.prefix() + "You set your flying speed to §61§7.");
        		} 
	    		else if (args[0].equalsIgnoreCase("2")) {
        			player.setFlySpeed(0.2f);
            		player.sendMessage(Main.prefix() + "You set your flying speed to §62§7.");
        		} 
        		else if (args[0].equalsIgnoreCase("3")) {
        			player.setFlySpeed(0.3f);
            		player.sendMessage(Main.prefix() + "You set your flying speed to §63§7.");
        		} 
        		else if (args[0].equalsIgnoreCase("4")) {
        			player.setFlySpeed(0.4f);
            		player.sendMessage(Main.prefix() + "You set your flying speed to §64§7.");
        		} 
        		else {
        			player.sendMessage(ChatColor.RED + "Usage: /speed <1-4>");
        		}	
        	}
		}
		return true;
	}
}