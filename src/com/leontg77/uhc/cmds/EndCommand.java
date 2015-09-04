package com.leontg77.uhc.cmds;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Data;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Parkour;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class EndCommand implements CommandExecutor {
	private Settings settings = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("end")) {
			if (sender.hasPermission("uhc.end")) {
				if (args.length < 2) {
					sender.sendMessage(Main.prefix() + "Usage: /end <kills> <winners>");
					return true;
				}
				
				int kills;
				
				try {
					kills = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild kill amount.");
					return true;
				}
				
				HandlerList.unregisterAll(new SpecInfo());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
				Spectator.getManager().spectators.clear();
				PlayerUtils.playWinnerFireworks();
				SpecInfo.totalDiamonds.clear();
				Parkour.getManager().setup();
				State.setState(State.LOBBY);
				SpecInfo.totalGold.clear();
				TeamCommand.sTeam.clear();
				Main.teamKills.clear();
				Main.relog.clear();
				Main.kills.clear();
				
				ArrayList<String> winners = new ArrayList<String>();
				
				for (int i = 1; i < args.length; i++) {
					winners.add(args[i]);
				}
				
				World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
				double x = settings.getData().getDouble("spawn.x");
				double y = settings.getData().getDouble("spawn.y");
				double z = settings.getData().getDouble("spawn.z");
				float yaw = (float) settings.getData().getDouble("spawn.yaw");
				float pitch = (float) settings.getData().getDouble("spawn.pitch");

				Location loc = new Location(w, x, y, z, yaw, pitch);
				String host = GameUtils.getCurrentHost();
				
				PlayerUtils.broadcast(Main.prefix() + "The UHC game has ended, Thanks for playing!");
				PlayerUtils.broadcast(" ");
				PlayerUtils.broadcast(Main.prefix() + "The winners are:");
				
				for (String entry : winners) {
					OfflinePlayer m9 = PlayerUtils.getOfflinePlayer(entry);
					
					Data data = Data.getFor(m9);
					data.increaseStat("wins");
					PlayerUtils.broadcast("§8» §7" + entry);
				}
				
				PlayerUtils.broadcast(" ");
				PlayerUtils.broadcast(Main.prefix() + "With §a" + kills + "§7 kills.");
				PlayerUtils.broadcast(Main.prefix() + "Congratz on the win!");
				
				if (settings.getHOF().getConfigurationSection(host).equals(null)) {
					settings.getHOF().set(host + "." + 1 + ".winners", winners);
					settings.getHOF().set(host + "." + 1 + ".kills", kills);
					settings.getHOF().set(host + "." + 1 + ".teamsize", GameUtils.getTeamSize().trim());
					settings.getHOF().set(host + "." + 1 + ".scenarios", settings.getConfig().getString("game.scenarios"));
					settings.saveHOF();
				} else {
					int id = settings.getHOF().getConfigurationSection(host).getKeys(false).size() + 1;
				
					settings.getHOF().set(host + "." + id + ".winners", winners);
					settings.getHOF().set(host + "." + id + ".kills", kills);
					settings.getHOF().set(host + "." + id + ".teamsize", GameUtils.getTeamSize().trim());
					settings.getHOF().set(host + "." + id + ".scenarios", settings.getConfig().getString("game.scenarios"));
					settings.saveHOF();
				}

				settings.getConfig().set("game.scenarios", "games scheduled");
				settings.getConfig().set("game.teamsize", 0);
				settings.getConfig().set("matchpost", "none");
				settings.getConfig().set("game.ffa", true);
				settings.saveConfig();

				Bukkit.getServer().setIdleTimeout(60);
				Main.teamSize = 0;
				Main.ffa = true;
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().isSpectating(online)) {
						Spectator.getManager().disableSpecmode(online, true);
					}
					
					online.setMaxHealth(20.0);
					online.setHealth(20.0);
					online.setFireTicks(0);
					online.setSaturation(20);
					online.teleport(loc);
					online.setLevel(0);
					online.setExp(0);
					online.setFoodLevel(20);
					online.getInventory().clear();
					online.getInventory().setArmorContents(null);
					online.setItemOnCursor(new ItemStack (Material.AIR));
					online.setGameMode(GameMode.SURVIVAL);
					
					for (PotionEffect effect : online.getActivePotionEffects()) {
						online.removePotionEffect(effect.getType());	
					}
				}
				
				for (String e : Scoreboards.getManager().kills.getScoreboard().getEntries()) {
					Scoreboards.getManager().resetScore(e);
				}

				for (OfflinePlayer whitelisted : Bukkit.getServer().getWhitelistedPlayers()) {
       				whitelisted.setWhitelisted(false);
       			}

				for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
       				scen.disableScenario();
       			}
				
				for (BukkitRunnable run : Main.relog.values()) {
					try {
						run.cancel();
					} catch (Exception e) {
						Bukkit.getLogger().warning("§cCould not cancel task " + run.getTaskId());
					}
				}
				
				try {
					Bukkit.getServer().getScheduler().cancelTask(Runnables.task);;
				} catch (Exception e) {
					Bukkit.getLogger().warning("§cCould not cancel task " + Runnables.task);
				}
				
				File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
				File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
				
				for (File dataFiles : playerData.listFiles()) {
					dataFiles.delete();
				}
				
				for (File statsFiles : stats.listFiles()) {
					statsFiles.delete();
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}