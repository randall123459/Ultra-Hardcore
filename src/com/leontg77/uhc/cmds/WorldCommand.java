package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

public class WorldCommand implements CommandExecutor {
	private Settings config = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("world")) {
			if (sender.hasPermission("uhc.world")) {
				if (args.length < 6) {
					sender.sendMessage(Main.PREFIX + "Usage: /world <worldname> <radius> <seed> <worldtype> <nether> <end>");
					return true;
				}
				
				String worldname;
				int radius;
				long seed;
				WorldType worldtype;
				boolean nether;
				boolean end;
				
				if (args[0].matches("[a-zA-Z]")) {
					worldname = args[0];
				} else {
					sender.sendMessage(ChatColor.RED + "Invaild world name.");
					return true;
				}
				
				try {erge
					radius = Integer.parseInt(args[1]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild radius.");
					return true;
				}
				
				try {
					seed = Long.parseLong(args[2]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild seed.");
					return true;
				}
				
				try {
					worldtype = WorldType.valueOf(args[3]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild worldtype.");
					return true;
				}
				
				if (args[4].equalsIgnoreCase("true")) {
					nether = true;
				} else if (args[4].equalsIgnoreCase("false")) {
					nether = false;
				} else {
					sender.sendMessage(ChatColor.RED + "Nether can only be true or false.");
					return true;
				}
				
				if (args[5].equalsIgnoreCase("true")) {
					end = true;
				} else if (args[5].equalsIgnoreCase("false")) {
					end = false;
				} else {
					sender.sendMessage(ChatColor.RED + "End can only be true or false.");
					return true;
				}
				
				WorldCreator creator = new WorldCreator(worldname);
				creator.environment(Environment.NORMAL);
				creator.generateStructures(true);
				creator.type(worldtype);
				creator.seed(seed);
				
				World world = creator.createWorld();
				world.setDifficulty(Difficulty.HARD);
				world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0), 0);
				
				world.getWorldBorder().setWarningDistance(0);
				world.getWorldBorder().setDamageAmount(0.1);
				world.getWorldBorder().setSize(radius - 1);
				world.getWorldBorder().setDamageBuffer(30);
				world.getWorldBorder().setCenter(0.5, 0.5);
				world.getWorldBorder().setWarningTime(60);
				
				world.save();

				config.getWorlds().set("worlds." + world.getName() + ".name", worldname);
				config.getWorlds().set("worlds." + world.getName() + ".radius", radius);
				config.getWorlds().set("worlds." + world.getName() + ".seed", seed);
				config.getWorlds().set("worlds." + world.getName() + ".environment", Environment.NORMAL.name());
				config.getWorlds().set("worlds." + world.getName() + ".worldtype", worldtype.name());
				config.saveWorlds();
				
				if (nether) {
					WorldCreator netherCreator = new WorldCreator(worldname + "_nether");
					netherCreator.environment(Environment.NETHER);
					netherCreator.type(WorldType.NORMAL);
					netherCreator.generateStructures(true);
					netherCreator.seed(seed);
					
					World netherWorld = creator.createWorld();
					netherWorld.setDifficulty(Difficulty.HARD);
					netherWorld.setSpawnLocation(0, netherWorld.getHighestBlockYAt(0, 0), 0);

					netherWorld.getWorldBorder().setWarningDistance(0);
					netherWorld.getWorldBorder().setDamageAmount(0.1);
					netherWorld.getWorldBorder().setSize(radius - 1);
					netherWorld.getWorldBorder().setDamageBuffer(30);
					netherWorld.getWorldBorder().setCenter(0, 0);
					netherWorld.getWorldBorder().setWarningTime(60);
					
					netherWorld.save();
					
					config.getWorlds().set("worlds." + world.getName() + ".name", worldname + "_nether");
					config.getWorlds().set("worlds." + world.getName() + ".radius", radius);
					config.getWorlds().set("worlds." + world.getName() + ".seed", seed);
					config.getWorlds().set("worlds." + world.getName() + ".environment", Environment.NETHER.name());
					config.getWorlds().set("worlds." + world.getName() + ".worldtype", WorldType.NORMAL.name());
					config.saveWorlds();
				}
				
				if (end) {
					WorldCreator endCreator = new WorldCreator(worldname + "_end");
					endCreator.environment(Environment.THE_END);
					endCreator.type(WorldType.NORMAL);
					endCreator.generateStructures(true);
					endCreator.seed(seed);
					
					World endWorld = endCreator.createWorld();
					endWorld.setDifficulty(Difficulty.HARD);
					endWorld.setSpawnLocation(100, 49, 0);
					
					WorldBorder border = endWorld.getWorldBorder();

					border.setWarningDistance(0);
					border.setWarningTime(60);
					border.setDamageAmount(0.1);
					border.setDamageBuffer(30);
					
					border.setSize(radius - 1);
					border.setCenter(0.5, 0.5);
					
					endWorld.save();
					
					config.getWorlds().set("worlds." + endWorld.getName() + ".name", worldname + "_end");
					config.getWorlds().set("worlds." + endWorld.getName() + ".radius", radius);
					config.getWorlds().set("worlds." + endWorld.getName() + ".seed", seed);
					config.getWorlds().set("worlds." + endWorld.getName() + ".environment", Environment.THE_END.name());
					config.getWorlds().set("worlds." + endWorld.getName() + ".worldtype", WorldType.NORMAL.name());
					config.saveWorlds();
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("world")) {
			if (sender.hasPermission("uhc.world")) {
				if (args.length == 1) {
		        	ArrayList<String> arg = new ArrayList<String>();
		        	ArrayList<String> types = new ArrayList<String>();
		        	types.add("cancel");
		        	
		        	if (!args[0].equals("")) {
		        		for (String type : types) {
		        			if (type.startsWith(args[0].toLowerCase())) {
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
			}
		}
		return null;
	}
}