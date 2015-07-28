package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.leontg77.uhc.Main;

public class ButcherCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("butcher")) {
			if (sender.hasPermission("uhc.butcher")) {
				int mobs = 0;
				
	        	for (World world : Bukkit.getWorlds()) {
	        		for (Entity mob : world.getEntities()) {
						if (mob.getType() == EntityType.BLAZE ||
							mob.getType() == EntityType.CAVE_SPIDER ||
							mob.getType() == EntityType.CREEPER ||
							mob.getType() == EntityType.ENDERMAN ||
							mob.getType() == EntityType.ZOMBIE ||
							mob.getType() == EntityType.WITCH ||
							mob.getType() == EntityType.WITHER ||
							mob.getType() == EntityType.GHAST ||
							mob.getType() == EntityType.GIANT ||
							mob.getType() == EntityType.MAGMA_CUBE ||
							mob.getType() == EntityType.SKELETON ||
							mob.getType() == EntityType.SPIDER ||
							mob.getType() == EntityType.GHAST ||
							mob.getType() == EntityType.ENDER_DRAGON ||
							mob.getType() == EntityType.SLIME ||
							mob.getType() == EntityType.SILVERFISH ||
							mob.getType() == EntityType.SKELETON) {
								
							mob.remove();
							mobs++;
						}
	        		}
	           	}
	        	
				sender.sendMessage(Main.prefix() + "Killed §6" + mobs + " §7hostile mobs.");
				mobs = 0;
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}