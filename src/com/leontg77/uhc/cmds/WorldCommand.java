package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.worlds.Manager;

/**
 * World command class.
 * 
 * @author LeonT77
 */
public class WorldCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.world")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "World management help:");
			sender.sendMessage("§8» §7/world create §8- §7§oCreate a world.");
			sender.sendMessage("§8» §7/world delete §8- §7§oDelete a world.");
			sender.sendMessage("§8» §7/world load §8- §7§oLoad a world.");
			sender.sendMessage("§8» §7/world unload §8- §7§oUnload a world.");
			sender.sendMessage("§8» §7/world list §8- §7§oList all worlds.");
			sender.sendMessage("§8» §7/world tp §8- §7§oTeleport to a world.");
			return true;
		}
		
		Manager manager = Manager.getInstance();
		
		if (args[0].equalsIgnoreCase("create")) {
			if (args.length < 7) {
				sender.sendMessage(Main.PREFIX + "Usage: /world create <worldname> <radius> <seed> <worldtype> <nether> <end>");
				return true;
			}
			
			String worldname;
			int radius;
			long seed;
			WorldType worldtype;
			boolean nether;
			boolean end;
			
			worldname = args[1];
			
			try {
				radius = Integer.parseInt(args[2]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a vaild radius.");
				return true;
			}
			
			try {
				seed = Long.parseLong(args[3]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[3] + " is not a vaild seed.");
				return true;
			}
			
			try {
				worldtype = WorldType.valueOf(args[4].toUpperCase());
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[4] + " is not an vaild worldtype.");
				return true;
			}
			
			if (args[5].equalsIgnoreCase("true")) {
				nether = true;
			} else if (args[5].equalsIgnoreCase("false")) {
				nether = false;
			} else {
				sender.sendMessage(ChatColor.RED + "Nether can only be true or false.");
				return true;
			}
			
			if (args[6].equalsIgnoreCase("true")) {
				end = true;
			} else if (args[6].equalsIgnoreCase("false")) {
				end = false;
			} else {
				sender.sendMessage(ChatColor.RED + "End can only be true or false.");
				return true;
			}
			
			sender.sendMessage(Main.PREFIX + "Starting creation of world '§a" + worldname + "§7'...");
			sender.sendMessage("§8» §7Nether: §6" + nether);
			sender.sendMessage("§8» §7End: §6" + end);
			sender.sendMessage("§8» §7Radius: §6" + radius);
			sender.sendMessage("§8» §7Seed: §6" + seed);
			sender.sendMessage("§8» §7Type: §6" + worldtype);
			
			manager.createWorld(worldname, radius, seed, Environment.NORMAL, worldtype);
			
			if (nether) {
				manager.createWorld(worldname + "_nether", radius, seed, Environment.NETHER, worldtype);
			}
			
			if (end) {
				manager.createWorld(worldname + "_end", radius, seed, Environment.THE_END, worldtype);
			}

			sender.sendMessage(Main.PREFIX + "World creation finished.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("delete")) {
			if (args.length == 1) {
				sender.sendMessage(Main.PREFIX + "Usage: /world delete <world>");
				return true;
			}
			
			World world = Bukkit.getServer().getWorld(args[1]);
			
			if (world == null) {
				sender.sendMessage(ChatColor.RED + args[1] + " does not exist.");
				return true;
			}
			
			if (world == Bukkit.getWorlds().get(0)) {
				sender.sendMessage(ChatColor.RED + "You cannot delete the main world.");
				return true;
			}

			if (manager.deleteWorld(world)) {
				sender.sendMessage(Main.PREFIX + "World '§a" + world.getName() + "§7' has been deleted.");
			} else {
				sender.sendMessage(Main.PREFIX + "Couldn't delete '§a" + world.getName() + "§7'.");
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("tp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can teleport to worlds.");
				return true;
			}
			
			Player player = (Player) sender;
			
			if (args.length == 1) {
				player.sendMessage(Main.PREFIX + "Usage: /world tp <world>");
				return true;
			}
			
			World world = Bukkit.getServer().getWorld(args[1]);
			
			if (world == null) {
				player.sendMessage(ChatColor.RED + args[1] + " does not exist.");
				return true;
			}
			
			player.sendMessage(Main.PREFIX + "Teleported to world '§a" + world.getName() + "§7'.");
			player.teleport(world.getSpawnLocation());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			sender.sendMessage(Main.PREFIX + "List of all worlds:");
			
			for (World world : Bukkit.getWorlds()) {
				ChatColor color;
				
				switch (world.getEnvironment()) {
				case NETHER:
					color = ChatColor.RED;
					break;
				case NORMAL:
					color = ChatColor.GREEN;
					break;
				case THE_END:
					color = ChatColor.AQUA;
					break;
				default:
					return true;
				}
				
				sender.sendMessage("§8» §7" + world.getName() + " §8- " + color + world.getEnvironment().name());
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("load")) {
			if (args.length == 1) {
				sender.sendMessage(Main.PREFIX + "Usage: /world unload <world>");
				return true;
			}
			
			World world = Bukkit.getServer().getWorld(args[1]);
			
			if (world != null) {
				sender.sendMessage(ChatColor.RED + args[1] + " is already loaded.");
				return true;
			}
			
			try {
				manager.loadWorld(args[1]);
				sender.sendMessage(Main.PREFIX + "World '§a" + args[1] + "§7' has been loaded.");
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "This world doesn't exist.");
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("unload")) {
			if (args.length == 1) {
				sender.sendMessage(Main.PREFIX + "Usage: /world unload <world>");
				return true;
			}
			
			World world = Bukkit.getServer().getWorld(args[1]);
			
			if (world == null) {
				sender.sendMessage(ChatColor.RED + args[1] + " does not exist.");
				return true;
			}
			
			if (world == Bukkit.getWorlds().get(0)) {
				sender.sendMessage(ChatColor.RED + "You cannot unload the main world.");
				return true;
			}
			
			sender.sendMessage(Main.PREFIX + "World '§a" + world.getName() + "§7' has been unloaded.");
			manager.unloadWorld(world);
			return true;
		}
		return true;
	}
	
	@Override
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