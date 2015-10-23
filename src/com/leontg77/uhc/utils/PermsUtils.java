package com.leontg77.uhc.utils;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Rank;

/**
 * Permissions utilities class.
 * <p>
 * Contains perms related methods.
 * 
 * @author LeonTG77
 */
public class PermsUtils {
	
	/**
	 * Handle the permissions for the given player.
	 * 
	 * @param player the player.
	 */
	public static void addPermissions(Player player) {
		File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
        boolean found = false;
		
		for (File file : folder.listFiles()) {
			if (file.getName().startsWith(player.getUniqueId().toString())) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			return;
		}
		
		if (Main.permissions.get(player.getName()) == null) {
			Main.permissions.put(player.getName(), player.addAttachment(Main.plugin));
		}

		PermissionAttachment perm = Main.permissions.get(player.getName());
		
		User user = User.get(player);
		Rank rank = user.getRank();
	
		perm.setPermission("mv.bypass.gamemode.*", true);
		
		if (rank == Rank.USER) {
			return;
		}

		perm.setPermission("uhc.prelist", true);
		
		if (rank == Rank.STAFF || rank == Rank.TRIAL || rank == Rank.HOST) {
			perm.setPermission("uhc.ban", true);
			perm.setPermission("uhc.broadcast", true);
			perm.setPermission("uhc.fly", true);
			perm.setPermission("uhc.info", true);
			perm.setPermission("uhc.invsee", true);
			perm.setPermission("uhc.kick", true);
			perm.setPermission("uhc.mute", true);
			perm.setPermission("uhc.pvp", true);
			perm.setPermission("uhc.scenario", true);
			perm.setPermission("uhc.sethealth", true);
			perm.setPermission("uhc.setmaxhealth", true);
			perm.setPermission("uhc.spectate", true);
			perm.setPermission("uhc.seemsg", true);
			perm.setPermission("uhc.cmdspy", true);
			perm.setPermission("uhc.staff", true);
			perm.setPermission("uhc.admin", true);
			perm.setPermission("uhc.team", true);
			perm.setPermission("uhc.tempban", true);
			perm.setPermission("uhc.tp", true);
			perm.setPermission("uhc.whitelist", true);
			
			if (rank == Rank.TRIAL || rank == Rank.HOST) {
				perm.setPermission("uhc.clearinv.other", true);
				perm.setPermission("uhc.clearxp.other", true);
				perm.setPermission("uhc.setmaxhealth", true);
				perm.setPermission("uhc.arenaadmin", true);
				perm.setPermission("uhc.heal.other", true);
				perm.setPermission("uhc.feed.other", true);
				perm.setPermission("uhc.sethealth", true);
				perm.setPermission("uhc.teamadmin", true);
				perm.setPermission("uhc.scenario", true);
				perm.setPermission("uhc.clearinv", true);
				perm.setPermission("uhc.gamemode", true);
				perm.setPermission("uhc.clearxp", true);
				perm.setPermission("uhc.giveall", true);
				perm.setPermission("uhc.spread", true);
				perm.setPermission("uhc.random", true);
				perm.setPermission("uhc.border", true);
				perm.setPermission("uhc.config", true);
				perm.setPermission("uhc.random", true);
				perm.setPermission("uhc.start", true);
				perm.setPermission("uhc.board", true);
				perm.setPermission("uhc.vote", true);
				perm.setPermission("uhc.heal", true);
				perm.setPermission("uhc.feed", true);
				perm.setPermission("uhc.end", true);
				
				if (rank == Rank.HOST) {
					perm.setPermission("uhc.skull", true);
					perm.setPermission("uhc.speed", true);
					perm.setPermission("uhc.moles", true);
					perm.setPermission("uhc.invsee", true);
					perm.setPermission("uhc.unban", true);
					perm.setPermission("uhc.tp", true);
					perm.setPermission("uhc.spectate.other", true);
				}
			}
		}
	}
	
	/**
	 * Handle the permissions for the given player if he leaves.
	 * 
	 * @param player the player.
	 */
	public static void removePermissions(Player player) {
		if (Main.permissions.get(player.getName()) == null) {
			return;
		}
		
		try {
			player.removeAttachment(Main.permissions.get(player.getName()));
		} catch (Exception e) {
			// uhh?
		}
		
		Main.permissions.remove(player.getName());
	}
}