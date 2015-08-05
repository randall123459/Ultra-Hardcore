package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.uhc.Settings;

public class HOFCommand implements CommandExecutor {
	public static HashMap<Player, Integer> page = new HashMap<Player, Integer>();
	private static Settings settings = Settings.getInstance();
	
	public static Inventory inv = Bukkit.getServer().createInventory(null, 54, settings.getConfig().getString("game.host") + "'s Hall of Fame");
	public static Inventory inv2 = Bukkit.getServer().createInventory(null, 54, settings.getConfig().getString("game.host") + "'s Hall of Fame");

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can view the hall of fame.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("hof")) {
			page.put(player, 1);
			
			int i = 0;
			
			inv.clear();
			inv2.clear();
			
			if (!settings.getConfig().contains(settings.getConfig().getString("game.host"))) {
				settings.getConfig().createSection(settings.getConfig().getString("game.host"));
				settings.saveConfig();
			}
			
			for (String section : Settings.getInstance().getHOF().getConfigurationSection(settings.getConfig().getString("game.host")).getKeys(false)) {
				ItemStack game = new ItemStack (Material.GOLDEN_APPLE);
				ItemMeta meta = game.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + settings.getConfig().getString("game.host") + "'s #" + section);
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(" ");
				lore.add("§aWinners: ");
				for (String winners : settings.getHOF().getStringList(settings.getConfig().getString("game.host") + "." + section + ".winners")) {
					lore.add("§7" + winners);
				}
				lore.add(" ");
				lore.add("§aKills: §7" + settings.getHOF().getString(settings.getConfig().getString("game.host") + "." + section + ".kills"));
				lore.add("§aTeamsize: §7" + settings.getHOF().getString(settings.getConfig().getString("game.host") + "." + section + ".teamsize"));
				lore.add("§aGamemodes: §7" + settings.getHOF().getString(settings.getConfig().getString("game.host") + "." + section + ".scenarios"));
				lore.add(" ");
				lore.add("§aMatch post: §7" + settings.getHOF().getString(settings.getConfig().getString("game.host") + "." + section + ".post"));
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
}