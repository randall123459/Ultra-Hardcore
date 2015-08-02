package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

public class SpeedCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("speed")) {
        	if (sender.hasPermission("uhc.speed")) {
        		if (args.length == 0) {
        			sender.sendMessage(ChatColor.RED + "Usage: /speed <speed> [player]");
        			return true;
        		}
        		
        		float speed;
        		float orgspeed;
        		
        		try {
        			speed = Float.parseFloat(args[0]);
        		} catch (Exception e) {
        			sender.sendMessage(ChatColor.RED + "Invaild number.");
        			return true;
        		}
        		
        		if (speed > 10f) {
        			speed = 10f;
        			orgspeed = 10f;
        		} else if (speed < 0.0001f) {
    				speed = 0.0001f;
        			orgspeed = 0.0001f;
    			} else {
    				orgspeed = speed;
    			}
        		
        		if (args.length == 1) {
        			if (sender instanceof Player) {
            			Player player = (Player) sender;
            			
                		float defaultSpeed = player.isFlying() ? 0.1f : 0.2f;
                		float maxSpeed = 1f;

                		if (speed < 1f) {
                			speed = defaultSpeed * speed;
                		} else {
                			float ratio = ((speed - 1) / 9) * (maxSpeed - defaultSpeed);
                			speed = ratio + defaultSpeed;
                		}
            			
                		if (player.isFlying()) {
                			player.setFlySpeed(speed);
                    		player.sendMessage(Main.prefix() + "You set your flying speed to §6" + orgspeed + "§7.");
                		} else {
                			player.setWalkSpeed(speed);
                    		player.sendMessage(Main.prefix() + "You set your walking speed to §6" + orgspeed + "§7.");
                		}
        			} else {
        				sender.sendMessage(ChatColor.RED + "Only players can change their walk/fly speed.");
        			}
        			return true;
        		}
        		
        		Player target = Bukkit.getServer().getPlayer(args[1]);
        		
        		if (target == null) {
        			sender.sendMessage(ChatColor.RED + "That player is not online.");
        			return true;
        		}
    			
        		float defaultSpeed = target.isFlying() ? 0.1f : 0.2f;
        		float maxSpeed = 1f;

        		if (speed < 1f) {
        			speed = defaultSpeed * speed;
        		} else {
        			float ratio = ((speed - 1) / 9) * (maxSpeed - defaultSpeed);
        			speed = ratio + defaultSpeed;
        		}
    			
        		if (target.isFlying()) {
        			target.setFlySpeed(speed);
            		sender.sendMessage(Main.prefix() + "You set your flying speed to §6" + orgspeed + "§7.");
        		} else {
        			target.setWalkSpeed(speed);
        			sender.sendMessage(Main.prefix() + "You set your walking speed to §6" + orgspeed + "§7.");
        		}
        	}
		}
		return true;
	}
}