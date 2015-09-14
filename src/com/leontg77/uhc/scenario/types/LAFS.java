package com.leontg77.uhc.scenario.types;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * LAFS scenario class
 * 
 * @author LeonTG77
 */
public class LAFS extends Scenario implements Listener {
	private boolean enabled = false;

	public LAFS() {
		super("LAFS", "Stands for love at first sight, you team with the first player you see in the game, in order to get on a team with them right click the player.");
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			Player player = event.getPlayer();
			Player clicked = (Player) event.getRightClicked();
			
			if (player.getWorld().getName().equals("lobby") || player.getWorld().getName().equals("arena")) {
				return;
			}
			
			if (clicked.getWorld().getName().equals("lobby") || clicked.getWorld().getName().equals("arena")) {
				return;
			}
			
			if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
				player.sendMessage(ChatColor.RED + "You are already on a team");
				return;
			}
			
			if (clicked.getScoreboard().getEntryTeam(clicked.getName()) != null) {
				player.sendMessage(ChatColor.RED + "That player is already on a team.");
				return;
			}
			
			Team t = Teams.getManager().findAvailableTeam();
			
			if (t == null) {
				player.sendMessage(ChatColor.RED + "No more available teams.");
				return;
			}
			
			t.addEntry(player.getName());
			t.addEntry(clicked.getName());
			PlayerUtils.broadcast(Main.prefix().replaceAll("UHC", "§d§lLAFS") + ChatColor.GREEN + player.getName() + " §7and§a " + clicked.getName() + " §7has found each other.");
		}
	}
}