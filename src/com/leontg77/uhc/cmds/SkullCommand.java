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
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Skull command class.
 * 
 * @author LeonTG77
 */
public class SkullCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can spawn player heads.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("uhc.skull")) {
			player.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			player.sendMessage(Main.PREFIX + "Usage: /skull <name>");
			return true;
		}
		
		ItemStack item = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(args[0]);
		item.setItemMeta(meta);
		
		PlayerUtils.giveItem(player, item);
		
		player.sendMessage(Main.PREFIX + "You've been given the head of §a" + args[0] + "§7.");
		return true;
	}
}