package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class BestPvE extends Scenario implements Listener {
	private HashSet<String> list = new HashSet<String>();
	private boolean enabled = false;
	private BukkitRunnable task;

	public BestPvE() {
		super("BestPvE", "Everyone starts on a list called bestpve list, if you take damage you are removed from the list. The only way to get back on the list is getting a kill, All players on the bestpve list gets 1 extra heart each 10 minutes.");
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
		if (!isEnabled()) {
			return;
		}
		
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
		if (!isEnabled()) {
			return;
		}
		
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
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player sender = event.getPlayer();
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/pvelist")) {
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "BestPvE is not enabled.");
				event.setCancelled(true);
				return;
			}
			
			ArrayList<String> pve = new ArrayList<String>(list);
			StringBuilder pvelist = new StringBuilder("");
			
			for (int i = 0; i < pve.size(); i++) {
				if (pvelist.length() > 0 && i == pve.size() - 1) {
					pvelist.append(" §7and §6");
				}
				else if (pvelist.length() > 0 && pvelist.length() != pve.size()) {
					pvelist.append("§7, §6");
				}
				
				pvelist.append(ChatColor.GOLD + pve.get(i));
			}
			
			event.getPlayer().sendMessage(Main.prefix() + "People still on the best pve list: §6" + (pvelist.length() > 0 ? pvelist.toString().trim() : "None") + "§7.");
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/pve")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "BestPvE is not enabled.");
				return;
			}
			
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			String[] args = ar.toArray(new String[ar.size()]);
			
			if (sender.hasPermission("uhc.bestpve.admin")) {
				if (args.length < 2) {
					sender.sendMessage(Main.prefix() + "Help for best pve:");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve remove <player> - Removes an player manually to the list.");
					return;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (list.contains(args[1])) {
						sender.sendMessage(ChatColor.RED + args[1] + " already on the best pve list.");
						return;
					}
					
					list.add(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " added to the best pve list.");
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (!list.contains(args[1])) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not on the best pve list.");
						return;
					}
					
					list.remove(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " removed from the best pve list.");
				} else {
					sender.sendMessage(Main.prefix() + "Help for best pve:");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/pve remove <player> - Removes an player manually to the list.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
	}
}