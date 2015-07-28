package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class BestBTC extends Scenario implements Listener {
	private ArrayList<String> list = new ArrayList<String>();
	private boolean enabled = false;
	private BukkitRunnable task;

	public BestBTC() {
		super("BestBTC", "BestBTC is enabled 20 minutes in. For every 10 minutes you are under Y=50, you gain a heart. Going above Y=50 will take you off the list. To get back on, you must mine a diamond.");
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
							online.sendMessage(ChatColor.GREEN + "You were rewarded for your BTC skills!");
						} else {
							online.sendMessage(ChatColor.GREEN + "BestBTC players gained a heart!");
						}
					}
				}
			};
			
			task.runTaskTimer(Main.plugin, 12000, 12000);
		} else {
			list.clear();
			task.cancel();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public List<String> getList() {
		return list;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}

		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (!list.contains(player.getName()) && block.getType() == Material.DIAMOND_ORE) {
			PlayerUtils.broadcast(ChatColor.GREEN + player.getName() + " mined a diamond! He is back on the Best BTC List!");
			list.add(player.getName());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getPlayer();

		if (list.contains(player.getName()) && event.getTo().getBlockY() > 50) {
			PlayerUtils.broadcast(ChatColor.RED + player.getName() + " moved above y:50!");
			list.remove(player.getName());
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player sender = event.getPlayer();
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/blist")) {
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "BestBTC is not enabled.");
				event.setCancelled(true);
				return;
			}
			
			StringBuilder pvelist = new StringBuilder("");
			
			for (int i = 0; i < list.size(); i++) {
				if (pvelist.length() > 0 && i == list.size() - 1) {
					pvelist.append(" §7and §6");
				}
				else if (pvelist.length() > 0 && pvelist.length() != list.size()) {
					pvelist.append("§7, §6");
				}
				
				pvelist.append(ChatColor.GOLD + list.get(i));
			}
			
			event.getPlayer().sendMessage(Main.prefix() + "People still on the best pve list: §6" + (pvelist.length() > 0 ? pvelist.toString().trim() : "None") + "§7.");
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/btc")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "BestBTC is not enabled.");
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
					sender.sendMessage(Main.prefix() + "Help for best btc:");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc remove <player> - Removes an player manually to the list.");
					return;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (list.contains(args[1])) {
						sender.sendMessage(ChatColor.RED + args[1] + " already on the best btc list.");
						return;
					}
					
					list.add(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " added to the best btc list.");
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (!list.contains(args[1])) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not on the best btc list.");
						return;
					}
					
					list.remove(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " removed from the best btc list.");
				} else {
					sender.sendMessage(Main.prefix() + "Help for best btc:");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc remove <player> - Removes an player manually to the list.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
	}
}