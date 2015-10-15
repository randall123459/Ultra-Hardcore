package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * BestPvE scenario class
 * 
 * @author LeonTG77
 */
public class BestPvE extends Scenario implements Listener, CommandExecutor {
	private HashSet<String> list = new HashSet<String>();
	private boolean enabled = false;
	private BukkitRunnable task;

	public BestPvE() {
		super("BestPvE", "Everyone starts on a list called bestpve list, if you take damage you are removed from the list. The only way to get back on the list is getting a kill, All players on the bestpve list gets 1 extra heart each 10 minutes.");
		Main main = Main.plugin;
	
		main.getCommand("pve").setExecutor(this);
		main.getCommand("pvelist").setExecutor(this);
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				list.add(online.getName());
			}
			
			this.task = new BukkitRunnable() {
				public void run() {
					for (Player online : PlayerUtils.getPlayers()) {
						if (list.contains(online.getName())) {
							online.setMaxHealth(online.getMaxHealth() + 2);
							online.setHealth(online.getHealth() + 2);
							online.sendMessage(ChatColor.GREEN + "You were rewarded for your PvE skills!");
						} else {
							online.sendMessage(ChatColor.GREEN + "BestPvE players gained a heart!");
						}
					}
				}
			};
			
			task.runTaskTimer(Main.plugin, 10800, 12000);
		} else {
			list.clear();
			task.cancel();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public Set<String> getList() {
		return list;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() == null) {
			return;
		}

		final Player player = event.getEntity().getKiller();

		if (!list.contains(player.getName())) {
			PlayerUtils.broadcast(ChatColor.GREEN + player.getName() + " got a kill! He is back on the Best PvE List!");
			Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
				public void run() {
					list.add(player.getName());
				}
			}, 20);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (list.contains(player.getName())) {
			PlayerUtils.broadcast(ChatColor.RED + player.getName() + " took damage!");
			list.remove(player.getName());
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pvelist")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"BestPvE\" is not enabled.");
				return true;
			}

			ArrayList<String> btc = new ArrayList<String>(list);
			StringBuilder btclist = new StringBuilder("");
			
			for (int i = 0; i < list.size(); i++) {
				if (btclist.length() > 0 && i == btc.size() - 1) {
					btclist.append(" §7and §a");
				}
				else if (btclist.length() > 0 && btclist.length() != btc.size()) {
					btclist.append("§7, §a");
				}
				
				btclist.append(ChatColor.GOLD + btc.get(i));
			}
			
			sender.sendMessage(Main.prefix() + "People still on the BestPvE list:\n§a" + (btclist.length() > 0 ? btclist.toString().trim() : "None") + "§7.");
		}
		
		if (cmd.getName().equalsIgnoreCase("pve")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"BestPvE\" is not enabled.");
				return true;
			}
			
			if (sender.hasPermission("uhc.bestpve.admin")) {
				if (args.length < 2) {
					sender.sendMessage(Main.prefix() + "Help for BestPvE:");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve remove <player> - Removes an player manually to the list.");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (list.contains(args[1])) {
						sender.sendMessage(Main.prefix() + ChatColor.RED + args[1] + " already on the BestPvE list.");
						return true;
					}
					
					list.add(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " added to the BestPvE list.");
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (!list.contains(args[1])) {
						sender.sendMessage(Main.prefix() + ChatColor.RED + args[1] + " is not on the BestPvE list.");
						return true;
					}
					
					list.remove(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " removed from the BestPvE list.");
				} else {
					sender.sendMessage(Main.prefix() + "Help for BestPvE:");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve remove <player> - Removes an player manually to the list.");
				}
			} else {
				sender.sendMessage(Main.NO_PERM_MSG);
			}
		}
		return true;
	}
}