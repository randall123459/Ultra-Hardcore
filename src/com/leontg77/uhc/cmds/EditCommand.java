package com.leontg77.uhc.cmds;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;

public class EditCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can change sign lines.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("edit")) {
			if (player.hasPermission("uhc.edit")) {
				if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Usage: /edit <line> <message>");
					return true;
				}
				
				Block block = player.getTargetBlock((Set<Material>) null, 100);
				
				if (block != null && (block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
					Sign sign = (Sign) block.getState();
					int line = 0;
					try {
						line = Integer.parseInt(args[0]);
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Invaild line number.");
						return true;
					}
					
					line = line - 1;
					StringBuilder sb = new StringBuilder("");
						
					for (int i = 1; i < args.length; i++){
					    sb.append(args[i]).append(" ");
					}
					               
					String msg = sb.toString().trim();
					sign.setLine(line, msg);
					sign.update();
					player.sendMessage(Main.prefix() + "You set the sign's §6" + String.valueOf(line + 1) + " §7line to: §f" + msg);
				} else {
					player.sendMessage(ChatColor.RED + "You are not looking at a sign.");
				}
			
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}