package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.uhc.Main;

public class SkullCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can spawn player heads.");
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("skull")) {
			if (player.hasPermission("uhc.skull")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Usage: /skull <name>");
					return true;
				}
				
				ItemStack head = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setOwner(args[0]);
				head.setItemMeta(meta);
				player.getInventory().addItem(head);
				player.sendMessage(Main.prefix() + "You've been given the head of §6" + args[0] + "§7.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}