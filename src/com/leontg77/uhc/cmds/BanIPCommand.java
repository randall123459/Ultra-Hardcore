package com.leontg77.uhc.cmds;

import java.util.ArrayList;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * BanIP command class
 * 
 * @author LeonTG77
 */
public class BanIPCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("banip")) {
			if (sender.hasPermission("uhc.ban")) {
				if (args.length < 3) {
					sender.sendMessage(Main.prefix() + "Usage: /banip <ip> <reason>");
					return true;
				}
							
				StringBuilder reason = new StringBuilder("");
					
				for (int i = 1; i < args.length; i++) {
					reason.append(args[i]).append(" ");
				}
				
				final String msg = reason.toString().trim().trim();

	    		BanEntry ban = Bukkit.getBanList(Type.IP).addBan(args[0], msg, null, sender.getName());
				PlayerUtils.broadcast(Main.prefix() + "An IP has been banned for §a" + msg);
				
		    	for (Player online : PlayerUtils.getPlayers()) {
		    		if (online.getAddress().getAddress().getHostAddress().equals(args[0])) {
		    			Scoreboards.getInstance().resetScore(args[0]);
				    	Scoreboards.getInstance().resetScore(online.getName());
				    	
				    	PlayerDeathEvent event = new PlayerDeathEvent(online, new ArrayList<ItemStack>(), 0, null);
						Bukkit.getServer().getPluginManager().callEvent(event);
						
						online.kickPlayer(
				    	"§8» §7You have been §4IP banned §7from §6Arctic UHC §8«" +
				    	"\n" + 
				    	"\n§cReason §8» §7" + ban.getReason() +
				    	"\n§cBanned by §8» §7" + ban.getSource() +
			 			"\n" +
				   		"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				    	);
		    		}
		    	}
			} else {
				sender.sendMessage(Main.NO_PERM_MSG);
			}
		}
		return true;
	}
}