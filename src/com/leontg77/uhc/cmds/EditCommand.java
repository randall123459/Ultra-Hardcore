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

/**
 * Edit command class.
 * 
 * @author LeonTG77
 */
public class EditCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can change sign lines.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("uhc.edit")) {
			player.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			player.sendMessage(Main.PREFIX + "Usage: /edit <line> <message>");
			return true;
		}
		
		Block block = player.getTargetBlock((Set<Material>) null, 100);
		
		if (block == null) {
			player.sendMessage(ChatColor.RED + "You are not looking at a block.");
			return true;
		}
		
		if (!(block.getState() instanceof Sign)) {
			player.sendMessage(ChatColor.RED + "You are not looking at a sign.");
			return true;
		}
		
		Sign sign = (Sign) block.getState();
		int line;
		
		try {
			line = Integer.parseInt(args[0]);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + " is not a vaild number.");
			return true;
		}
		
		if (line < 1) {
			line = 1;
		}
		
		if (line > 4) {
			line = 4;
		}
		
		StringBuilder sb = new StringBuilder("");
			
		for (int i = 1; i < args.length; i++){
		    sb.append(args[i]).append(" ");
		}
		               
		String msg = sb.toString().trim();
		
		player.sendMessage(Main.PREFIX + "You set the sign's §a" + line + " §7line to: §6" + msg);
		line--;
		
		sign.setLine(line, msg);
		sign.update();
		return true;
	}
}