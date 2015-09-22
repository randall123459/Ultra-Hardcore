package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * TeamHealth scenario class
 * 
 * @author LeonTG77
 */
public class TeamHealth extends Scenario implements Listener {
	public BukkitRunnable run = null;
	private boolean enabled = false;

	private Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	private Objective teamHealth = board.getObjective("teamHealth");
	private Objective teamHealthName = board.getObjective("teamHealthName");
	
	public TeamHealth() {
		super("TeamHealth", "The percent health shown in tab/bellow the name is all the teammates health combined.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			teamHealth = board.registerNewObjective("teamHealth", "dummy");
			teamHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			
			teamHealthName = board.registerNewObjective("teamHealthName", "dummy");
			teamHealthName.setDisplaySlot(DisplaySlot.BELOW_NAME);
			teamHealthName.setDisplayName("§4♥");
			
			run = new BukkitRunnable() {
				public void run() {
					for (Player online : PlayerUtils.getPlayers()) {	
						Team team = Teams.getManager().getTeam(online);
						
						if (team == null) {
							int percent = NumberUtils.makePercent(online.getHealth());
							
							if (teamHealth != null) {
								Score score = teamHealth.getScore(online.getName());
								score.setScore(percent);
							}
							
							if (teamHealthName != null) {
								Score score = teamHealthName.getScore(online.getName());
								score.setScore(percent);
							}
						}
						else {
							double health = 0;
							
							for (String entry : team.getEntries()) {
								Player target = Bukkit.getServer().getPlayer(entry);
								
								if (target != null) {
									health = health + target.getHealth();
								}
							}

							int percent = NumberUtils.makePercent(health);
							
							if (teamHealth != null) {
								Score score = teamHealth.getScore(online.getName());
								score.setScore(percent);
							}
							
							if (teamHealthName != null) {
								Score score = teamHealthName.getScore(online.getName());
								score.setScore(percent);
							}
						}
					}
				}
			};
			
			run.runTaskTimer(Main.plugin, 1, 1);
		} else {
			teamHealthName.unregister();
			teamHealth.unregister();
			
			run.cancel();
			run = null;
			
			Scoreboards.getManager().setup();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
    public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Team team = Teams.getManager().getTeam(player);

        if (team != null) {
            team.removeEntry(player.getName());
        }
    }
}