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

/**
 * GiveAll command class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class GiveallCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.giveall")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /giveall <item> [amount] [durability]");
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
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild type.");
			return true;
		}
		
		if (args.length > 1) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild number.");
				return true;
			}
		}
		
		if (args.length > 2) {
			try {
				durability = Short.parseShort(args[2]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a vaild number.");
				return true;
			}
		}

		ItemStack item = new ItemStack(material, amount, durability);
		PlayerUtils.broadcast(Main.PREFIX + "All players recieved §a" + amount + " " + item.getType().name().toLowerCase().replaceAll("_", " ") + (amount > 1 ? "s" : "") + "§7.");
		
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			PlayerUtils.giveItem(online, item);
		}
		return true;
	}
}