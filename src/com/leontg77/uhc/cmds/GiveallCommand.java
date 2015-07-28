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

@SuppressWarnings("deprecation")
public class GiveallCommand implements CommandExecutor {	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("giveall")) {
			if (sender.hasPermission("uhc.giveall")) {
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /giveall <item> <amount> [durability]");
					return true;
				}
					
				Material material = null;
				
				for (Material m : Material.values()) {
					if (m.name().equals(args[0].toUpperCase()) || args[0].startsWith(Integer.toString(m.getId()))) {
						material = m;
					}
				}
				
				int amount = 1;
				short durability = 0;
				
				if (material == null) {
					sender.sendMessage(ChatColor.RED + "Unknown item name.");
					return true;
				}
				
				try {
					amount = Integer.parseInt(args[1]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "That is not a vaild amount.");
					return true;
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
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					online.sendMessage(Main.prefix() + "You've been given §6" + amount + " §7of §6" + item.getType().name().toLowerCase().replaceAll("_", "") + "§7.");
					item.setAmount(amount);
					online.getInventory().addItem(item);
				}
			}
		}
		return true;
	}
}