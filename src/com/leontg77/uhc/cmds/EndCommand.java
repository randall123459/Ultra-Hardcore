package com.leontg77.uhc.cmds;

import org.bukkit.Achievement;
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

import com.leontg77.uhc.GameState;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.listeners.SpecInfoListener;

public class EndCommand implements CommandExecutor {
	private Settings settings = Settings.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("end")) {
			if (sender.hasPermission("uhc.end")) {
				World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
				double x = settings.getData().getDouble("spawn.x");
				double y = settings.getData().getDouble("spawn.y");
				double z = settings.getData().getDouble("spawn.z");
				float yaw = (float) settings.getData().getDouble("spawn.yaw");
				float pitch = (float) settings.getData().getDouble("spawn.pitch");
				
				Location loc = new Location(w, x, y, z, yaw, pitch);
				
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					online.sendMessage(Main.prefix() + "The UHC has been ended.");
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
					for (Achievement ach : Achievement.values()) {
						if (online.hasAchievement(ach)) {
							online.removeAchievement(ach);
						}
					}
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
				GameState.setState(GameState.LOBBY);
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