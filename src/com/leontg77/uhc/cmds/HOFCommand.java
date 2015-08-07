package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.uhc.Settings;
import com.leontg77.uhc.util.ServerUtils;

public class HOFCommand implements CommandExecutor, TabCompleter {
	public static HashMap<Player, Integer> page = new HashMap<Player, Integer>();
	private static Settings settings = Settings.getInstance();
	
	public static Inventory inv;
	public static Inventory inv2;

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can view the hall of fame.");
			return true;
		}
		
		Player player = (Player) sender;
		String host = ServerUtils.getCurrentHost();
		
		if (cmd.getName().equalsIgnoreCase("hof")) {
			if (args.length > 0) {
				host = ServerUtils.getHost(args[0]);
			}
			
			if (host == null) {
				sender.sendMessage(ChatColor.RED + "No one is currently hosting.");
				return true;
			}
			
			if (Settings.getInstance().getHOF().getConfigurationSection(host) == null) {
				sender.sendMessage(ChatColor.RED + "That player has not hosted any games here.");
				return true;
			}
			
			inv = Bukkit.getServer().createInventory(null, 54, host + "'s Hall of Fame");
			inv2 = Bukkit.getServer().createInventory(null, 54, host + "'s Hall of Fame");

			page.put(player, 1);
			
			int i = 0;
			
			inv.clear();
			inv2.clear();
			
			for (String section : Settings.getInstance().getHOF().getConfigurationSection(host).getKeys(false)) {
				ItemStack game = new ItemStack (Material.GOLDEN_APPLE);
				ItemMeta meta = game.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + host + "'s #" + section);
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(" ");
				lore.add("§aWinners: ");
				for (String winners : settings.getHOF().getStringList(host + "." + section + ".winners")) {
					lore.add("§7" + winners);
				}
				lore.add(" ");
				lore.add("§aKills: §7" + settings.getHOF().getString(host + "." + section + ".kills"));
				lore.add("§aTeamsize: §7" + settings.getHOF().getString(host + "." + section + ".teamsize"));
				lore.add("§aGamemodes: §7" + settings.getHOF().getString(host + "." + section + ".scenarios"));
				lore.add(" ");
				meta.setLore(lore);
				game.setItemMeta(meta);

				if (i < 45) {
					inv.addItem(game);
				} else {
					inv2.addItem(game);
				}
				
				ItemStack nextpage = new ItemStack (Material.ARROW);
				ItemMeta pagemeta = nextpage.getItemMeta();
				pagemeta.setDisplayName(ChatColor.GREEN + "Next page");
				pagemeta.setLore(Arrays.asList("§7Switch to the next page."));
				nextpage.setItemMeta(pagemeta);
				inv.setItem(49, nextpage);
				
				ItemStack prevpage = new ItemStack (Material.ARROW);
				ItemMeta prevmeta = prevpage.getItemMeta();
				prevmeta.setDisplayName(ChatColor.GREEN + "Previous page");
				prevmeta.setLore(Arrays.asList("§7Switch to the previous page."));
				prevpage.setItemMeta(prevmeta);
				inv2.setItem(49, prevpage);
				
				i++;
			}
			
			player.openInventory(inv);
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("hof")) {
			if (args.length == 1) {
	        	ArrayList<String> arg = new ArrayList<String>();
	        	ArrayList<String> types = new ArrayList<String>();
	        	types.add("Axlur");
	        	types.add("Leon");
	        	types.add("Polar");
	        	types.add("Pop");
	        	
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
		}
		return null;
	}
}