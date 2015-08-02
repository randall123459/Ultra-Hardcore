package com.leontg77.uhc.cmds;

import java.lang.management.ManagementFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.NumberUtils;
import com.leontg77.uhc.util.DateUtils;
import com.leontg77.uhc.util.PlayerUtils;
import com.leontg77.uhc.util.ServerUtils;

public class TpsCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("tps")) {
			double tps = ServerUtils.getTps();
			ChatColor color;
			if (tps >= 18.0) {
				color = ChatColor.GREEN;
			}
			else if (tps >= 15.0) {
				color = ChatColor.YELLOW;
			}
			else {
				color = ChatColor.RED;
			}
			
			sender.sendMessage(Main.prefix() + "Server status:");
			sender.sendMessage("§8§l» §7Current TPS: " + color + NumberUtils.convertDouble(ServerUtils.getTps()));
			sender.sendMessage("§8§l» §7Uptime: §a" + DateUtils.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
			sender.sendMessage("§8§l» §7RAM Usage: §a" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB");
			sender.sendMessage("§8§l» §7Max Memory: §a4096 MB");
			int i = 0;
			int j = 0;
			for (World world : Bukkit.getWorlds()) {
				i = i + world.getEntities().size();
				j = j + world.getLoadedChunks().length;
			}
			i = i - PlayerUtils.getPlayers().size();
			sender.sendMessage("§8§l» §7Entities: §a" + i);
			sender.sendMessage("§8§l» §7Loaded chunks: §a" + j);
		}
		return true;
	}
}