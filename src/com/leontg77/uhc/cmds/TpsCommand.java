package com.leontg77.uhc.cmds;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Tps command class.
 * 
 * @author LeonTG77
 */
public class TpsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		double tps = Main.getTps();
		ChatColor color;
		
		if (tps >= 18.0 && tps <= 20.0) {
			color = ChatColor.GREEN;
		} else if (tps >= 15.0 && tps < 18.0) {
			color = ChatColor.YELLOW;
		} else {
			color = ChatColor.RED;
		}
		
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		
		long startTime = runtime.getStartTime();
		long ramUsage = (Runtime.getRuntime().totalMemory() / 1024 / 1024);
		
		sender.sendMessage(Main.PREFIX + "Server performance:");
		sender.sendMessage("§8§l» §7Current TPS: " + color + NumberUtils.convertDouble(tps));
		
		if (!sender.hasPermission("uhc.tps")) {
			return true;
		}
		
		sender.sendMessage("§8§l» §7Uptime: §a" + DateUtils.formatDateDiff(startTime));
		sender.sendMessage("§8§l» §7RAM Usage: §a" + ramUsage + " MB");
		sender.sendMessage("§8§l» §7Max Memory: §a4096 MB");
		
		int entities = 0;
		int chunks = 0;
		
		for (World world : Bukkit.getWorlds()) {
			entities = entities + world.getEntities().size();
			chunks = chunks + world.getLoadedChunks().length;
		}
		
		entities = entities - PlayerUtils.getPlayers().size();
		
		sender.sendMessage("§8§l» §7Entities: §a" + entities);
		sender.sendMessage("§8§l» §7Loaded chunks: §a" + chunks);
		return true;
	}
}