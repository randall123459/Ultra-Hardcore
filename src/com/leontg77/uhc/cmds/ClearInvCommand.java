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

public class ClearInvCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("clearinv")) {
			if (sender.hasPermission("uhc.clearinv")) {
				if (args.length == 0) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.getInventory().clear();
						player.getInventory().setArmorContents(null);
						player.setItemOnCursor(new ItemStack (Material.AIR));
						player.sendMessage(Main.prefix() + "You cleared your own inventory.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can clear their own inventories.");
					}
					return true;
				}
				
				if (sender.hasPermission("uhc.clearinv.other")) {
					if (args[0].equals("*")) {
						for (Player online : PlayerUtils.getPlayers()) {
							online.getInventory().clear();
							online.getInventory().setArmorContents(null);
							online.setItemOnCursor(new ItemStack (Material.AIR));
							online.sendMessage(Main.prefix() + "Your inventory was cleared.");
						}
						sender.sendMessage(Main.prefix() + "You cleared everyones inventory.");
					} else {
						Player target = Bukkit.getServer().getPlayer(args[0]);
						
						if (target == null) {
							sender.sendMessage(ChatColor.RED + "That player is not online.");
						}
						
						target.getInventory().clear();
						target.getInventory().setArmorContents(null);
						target.setItemOnCursor(new ItemStack (Material.AIR));
						target.sendMessage(Main.prefix() + "Your inventory was cleared.");
						sender.sendMessage(Main.prefix() + "You cleared " + target.getName() + "'s inventory.");
					}
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.getInventory().clear();
						player.getInventory().setArmorContents(null);
						player.setItemOnCursor(new ItemStack (Material.AIR));
						player.sendMessage(Main.prefix() + "You cleared your own inventory.");
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can clear their own inventories.");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}