package com.leontg77.uhc.cmds;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Fireworks;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Parkour;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * End command class.
 * 
 * @author LeonTG77
 */
public class EndCommand implements CommandExecutor {
	private Settings settings = Settings.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!sender.hasPermission("uhc.end")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Main.PREFIX + "Usage: /end <kills> <winners>");
			return true;
		}
		
		int kills;
		
		try {
			kills = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild number.");
			return true;
		}
		
		ArrayList<String> winners = new ArrayList<String>();
		
		PlayerUtils.broadcast(Main.PREFIX + "The game is now over!");
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "The winners are:");
		
		for (int i = 1; i < args.length; i++) {
			OfflinePlayer winner = PlayerUtils.getOfflinePlayer(args[i]);
			
			User data = User.get(winner);
			data.increaseStat(Stat.WINS);
			
			PlayerUtils.broadcast("§8» §7" + args[i]);
			winners.add(args[i]);
		}
		
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "With §a" + kills + "§7 kills.");
		PlayerUtils.broadcast(Main.PREFIX + "View the hall of fame with §a/hof");
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "Congrats on the win and thanks for playing!");
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String host = GameUtils.getCurrentHost();
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		
		int matchcount = 1;
		
		if (settings.getHOF().contains(host)) {
			matchcount = settings.getHOF().getConfigurationSection(host).getKeys(false).size() + 1;
		}

		settings.getHOF().set(host + "." + matchcount + ".date", dateFormat.format(date));
		settings.getHOF().set(host + "." + matchcount + ".winners", winners);
		settings.getHOF().set(host + "." + matchcount + ".kills", kills);
		settings.getHOF().set(host + "." + matchcount + ".teamsize", GameUtils.getTeamSize().trim());
		settings.getHOF().set(host + "." + matchcount + ".scenarios", settings.getConfig().getString("game.scenarios"));

		settings.getConfig().set("game.scenarios", "games scheduled");
		settings.getConfig().set("game.teamsize", 0);
		settings.getConfig().set("matchpost", "none");
		settings.getConfig().set("game.ffa", true);
		
		settings.saveConfig();
		settings.saveHOF();
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			scen.disableScenario();
		}

		Fireworks firework = Fireworks.getInstance();
		Parkour parkour = Parkour.getInstance();
		
		Spectator spec = Spectator.getInstance();
		Game game = Game.getInstance();

		for (Player online : PlayerUtils.getPlayers()) {
			for (Player onlineTwo : PlayerUtils.getPlayers()) {
				online.showPlayer(onlineTwo);
				onlineTwo.showPlayer(online);
			}
			
			if (spec.isSpectating(online)) {
				spec.disableSpecmode(online, true);
			}

			online.setGameMode(GameMode.SURVIVAL);
			online.teleport(Main.getSpawn());
			online.setMaxHealth(20.0);
			online.setFireTicks(0);
			
			User user = User.get(online);
			user.reset();
		}
		
		HandlerList.unregisterAll(new SpecInfo());
		State.setState(State.LOBBY);

		firework.startFireworkShow();
		parkour.setup();
		
		spec.spectators.clear();
		TeamCommand.sTeam.clear();
		
		SpecInfo.totalDiamonds.clear();
		SpecInfo.totalGold.clear();
		
		Main.teamKills.clear();
		Main.kills.clear();

		Bukkit.getServer().setIdleTimeout(60);
		Main.plugin.saveData();
		
		game.setTeamSize(0);
		game.setFFA(true);
		
		Teams teams = Teams.getInstance();
		Team team = teams.getTeam("spec");
		
		for (String member : team.getEntries()) {
			team.removeEntry(member);
		}

		for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
			whitelisted.setWhitelisted(false);
		}

		new BukkitRunnable() {
			public void run() {
				Scoreboards board = Scoreboards.getInstance();
				
				for (String entry : board.board.getEntries()) {
					Scoreboards.getInstance().resetScore(entry);
				}
				
				Teams teams = Teams.getInstance();
				
				for (Team team : teams.getTeams()) {
					for (String member : team.getEntries()) {
						team.removeEntry(member);
					}
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Reset scoreboards and teams.");
				
			}
		}.runTaskLater(Main.plugin, 600);
		
		File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
		File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
		
		for (File dataFiles : playerData.listFiles()) {
			dataFiles.delete();
		}
		
		for (File statsFiles : stats.listFiles()) {
			statsFiles.delete();
		}
		
		try {
			Bukkit.getServer().getScheduler().cancelTask(Timers.taskMinutes);
		} catch (Exception e) {
			plugin.getLogger().warning("Could not cancel task " + Timers.taskMinutes);
		}
		
		try {
			Bukkit.getServer().getScheduler().cancelTask(Timers.taskSeconds);
		} catch (Exception e) {
			plugin.getLogger().warning("Could not cancel task " + Timers.taskSeconds);
		}
		return true;
	}
}