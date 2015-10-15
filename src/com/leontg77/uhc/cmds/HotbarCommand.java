package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

@SuppressWarnings("deprecation")
public class HotbarCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("giveall")) {
			if (sender.hasPermission("uhc.giveall")) {
				if (args.length == 0) {
					sender.sendMessage(Main.prefix() + "Usage: /giveall <item> [amount] [durability]");
					return true;
				}
				
				Material material = null;
				int amount = 1;
				short durability = 0;
				
				try {
					material = Material.getMaterial(Integer.parseInt(args[0]));
				} 
				catch (Exception e) {
					for (Material types : Material.values()) {
						if (types.name().startsWith(args[0].toUpperCase())) {
							material = types;
							break;
						}
					}
				}
				
				if (material == null) {
					sender.sendMessage(ChatColor.RED + "Unknown item name.");
					return true;
				}
				
				if (args.length > 1) {
					try {
						amount = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "That is not a vaild amount.");
						return true;
					}
				}
				
				if (args.length > 2) {
					try {
						durability = Short.parseShort(args[2]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "That is not a vaild durability.");
						return true;
					}
				}

				ItemStack item = new ItemStack(material, amount, durability);
				PlayerUtils.broadcast(Main.prefix() + "You've been given §a" + amount + " §7of §6" + item.getType().name().toLowerCase().replaceAll("_", " ") + "§7.");
				
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					PlayerUtils.giveItem(online, item);
				}
			} else {
				sender.sendMessage(Main.NO_PERM_MSG);
			}
		}
		return true;
	}
}