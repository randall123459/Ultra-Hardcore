package com.leontg77.uhc.cmds;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Helpop command class.
 * 
 * @author LeonTG77
 */
public class HelpopCommand implements CommandExecutor {
	public static ArrayList<CommandSender> cooldown = new ArrayList<CommandSender>();

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
           	sender.sendMessage(ChatColor.RED + "Usage: /helpop <message>");
           	return true;
		}
		
		if (cooldown.contains(sender)) {
			sender.sendMessage("§4§lHelp§8§l-§4§lOp §8» §7Do not spam helpops.");
			return true;
		}
		
		Spectator spec = Spectator.getInstance();
		StringBuilder sb = new StringBuilder("");
		
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]).append(" ");
		}
		
		String msg = sb.toString().trim();

		for (Player online : PlayerUtils.getPlayers()) {
			if (!online.hasPermission("uhc.staff") && !spec.isSpectating(online)) {
				continue;
			}
			
			online.sendMessage("§4§lHelp§8§l-§4§lOp §8» §7" + sender.getName() + "§7: §6" + msg);
			online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
		}
		
		Bukkit.getLogger().info("§4§lHelp§8§l-§4§lOp §8» §7" + sender.getName() + "§7: §6" + msg);
		cooldown.add(sender);
		
		sender.sendMessage("§4§lHelp§8§l-§4§lOp §8» §7Helpop sent, please don't spam this.");
		sender.sendMessage("§4§lHelp§8§l-§4§lOp §8» §7Your message: §6" + msg);
		
		new BukkitRunnable() {
			public void run() {
				cooldown.remove(sender);
			}
		}.runTaskLater(Main.plugin, 100);
		return true;
	}
}