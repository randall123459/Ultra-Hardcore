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
 * Give command class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class GiveCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.give")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Main.PREFIX + "Usage: /give <player> <item> [amount] [durability]");
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
		}
		
		Material material = null;
		int amount = 1;
		short durability = 0;
		
		try {
			material = Material.getMaterial(Integer.parseInt(args[1]));
		} 
		catch (Exception e) {
			for (Material types : Material.values()) {
				if (types.name().startsWith(args[1].toUpperCase())) {
					material = types;
					break;
				}
			}
		}
		
		if (material == null) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not a vaild type.");
			return true;
		}
		
		if (args.length > 2) {
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a vaild number.");
				return true;
			}
		}
		
		if (args.length > 3) {
			try {
				durability = Short.parseShort(args[3]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[3] + " is not a vaild number.");
				return true;
			}
		}

		ItemStack item = new ItemStack(material, amount, durability);
		PlayerUtils.broadcast(Main.PREFIX + "You recieved §a" + amount + " " + item.getType().name().toLowerCase().replaceAll("_", " ") + (amount > 1 ? "s" : "") + "§7.");

		PlayerUtils.giveItem(target, item);
		return true;
	}
}