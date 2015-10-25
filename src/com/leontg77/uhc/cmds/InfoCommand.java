package com.leontg77.uhc.cmds;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Info command class.
 * 
 * @author LeonTG77
 */
public class InfoCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.info")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /info <player>");
			return true;
		}

        File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
        boolean found = false;
        
		OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[0]);
		
		for (File file : folder.listFiles()) {
			if (file.getName().startsWith(target.getUniqueId().toString())) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			sender.sendMessage(Main.PREFIX + args[0] + " has never joined this server.");
			return true;
		}
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		User user = User.get(target);
		
		long lastlogout = user.getFile().getLong("lastlogout", -1l);
		BanList list = Bukkit.getBanList(Type.NAME);
		BanEntry entry = list.getBanEntry(target.getName());
		
		sender.sendMessage(Main.PREFIX + "Info about §6" + target.getName() + "§8:");
		sender.sendMessage("§8» §7Status: §6" + (target.getPlayer() == null ? "§cNot online" : "§aOnline"));
		sender.sendMessage("§8» §7UUID: §6" + user.getFile().getString("uuid"));
		if (sender.hasPermission("uhc.info.ip")) {
			sender.sendMessage("§8» §7IP: §6" + user.getFile().getString("ip"));
		} else {
			sender.sendMessage("§8» §7IP: §6§m###.##.##.###");
		}
		sender.sendMessage("§8»----------------------------«");
		sender.sendMessage("§8» §7First Joined: §6" + new Date(user.getFile().getLong("firstjoined")));
		sender.sendMessage("§8» §7Last login: §6" + DateUtils.formatDateDiff(user.getFile().getLong("lastlogin")));
		sender.sendMessage("§8» §7Last logout: §6" + (lastlogout == -1l ? "§cHasn't logged out" : DateUtils.formatDateDiff(lastlogout)));
		sender.sendMessage("§8»----------------------------«");
		
		sender.sendMessage("§8» §7Banned: §6" + (list.isBanned(target.getName()) ? "§aTrue§7, Reason: §6" + entry.getReason() : "§cFalse"));
		sender.sendMessage("§8» §7Muted: §6" + (user.isMuted() ? "§aTrue§7, Reason: §6" + user.getMutedReason() : "§cFalse"));
		sender.sendMessage("§8»----------------------------«");
		return true;
	}
}