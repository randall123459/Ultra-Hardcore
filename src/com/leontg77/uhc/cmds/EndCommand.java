package com.leontg77.uhc.cmds;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Data;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.listeners.SpecInfoListener;
import com.leontg77.uhc.util.PlayerUtils;
import com.leontg77.uhc.util.ServerUtils;

public class EndCommand implements CommandExecutor {
	private Settings settings = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("end")) {
			if (sender.hasPermission("uhc.end")) {
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /end <kills> <winners...>");
					return true;
				}
				
				int kills;
				
				try {
					kills = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild kill amount.");
					return true;
				}
				
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
				
				StringBuilder win = new StringBuilder();
				
				for (int i = 0; i < winners.size(); i++) {
					if (win.length() > 0 && i == winners.size() - 1) {
						win.append(" and ");
					}
					else if (win.length() > 0 && win.length() != winners.size()) {
						win.append(", ");
					}
					
					win.append(winners.get(i));
				}
				
				Team team = Scoreboards.getManager().sb.getEntryTeam(args[0]);
				
				if (team != null) {
					for (String entry : team.getEntries()) {
						OfflinePlayer m8 = PlayerUtils.getOfflinePlayer(entry);
						
						Data data = Data.getData(m8);
						data.increaseStat("wins");
					}
				} else {
					for (String entry : winners) {
						OfflinePlayer m9 = PlayerUtils.getOfflinePlayer(entry);
						
						Data data = Data.getData(m9);
						data.increaseStat("wins");
					}
				}
				
				String host = ServerUtils.getCurrentHost();
				
				if (settings.getHOF().getConfigurationSection(host) == null) {
					settings.getHOF().set(host + "." + 1 + ".winners", winners);
					settings.getHOF().set(host + "." + 1 + ".kills", kills);
					settings.getHOF().set(host + "." + 1 + ".teamsize", ServerUtils.getTeamSize());
					settings.getHOF().set(host + "." + 1 + ".scenarios", settings.getConfig().getString("game.scenarios"));
					settings.saveHOF();
				} else {
					int id = settings.getHOF().getConfigurationSection(host).getKeys(false).size() + 1;
				
					settings.getHOF().set(host + "." + id + ".winners", winners);
					settings.getHOF().set(host + "." + id + ".kills", kills);
					settings.getHOF().set(host + "." + id + ".teamsize", ServerUtils.getTeamSize());
					settings.getHOF().set(host + "." + id + ".scenarios", settings.getConfig().getString("game.scenarios"));
					settings.saveHOF();
				}
				
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					online.sendMessage(Main.prefix() + "The UHC has ended, the winners are " + win.toString().trim() + " with " + kills + " kills.");
					if (Main.spectating.contains(online.getName())) {
						Spectator.getManager().set(online, false);
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
					for (PotionEffect effect : online.getActivePotionEffects()) {
						online.removePotionEffect(effect.getType());	
					}
				}
				
				for (String e : Scoreboards.getManager().kills.getScoreboard().getEntries()) {
					Scoreboards.getManager().resetScore(e);
				}

				for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
       				whitelisted.setWhitelisted(false);
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
					Bukkit.getLogger().warning("§cCould not cancel a task.");
				}

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel");
				State.setState(State.LOBBY);
				Main.relog.clear();
				SpecInfoListener.totalG.clear();
				SpecInfoListener.totalD.clear();
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}