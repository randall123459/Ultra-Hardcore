package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * BestBTC scenario class
 * 
 * @author LeonTG77
 */
public class BestBTC extends Scenario implements Listener, CommandExecutor {
	private HashSet<String> list = new HashSet<String>();
	private boolean enabled = false;
	private BukkitRunnable task;

	public BestBTC() {
		super("BestBTC", "For every 10 minutes you are under Y=50, you gain a heart. Going above Y=50 will take you off the list. To get back on, you must mine a diamond.");
		Main main = Main.plugin;
		
		main.getCommand("btc").setExecutor(this);
		main.getCommand("btclist").setExecutor(this);
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
			task = null;
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public Set<String> getList() {
		return list;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (!list.contains(player.getName()) && block.getType() == Material.DIAMOND_ORE) {
			PlayerUtils.broadcast(ChatColor.GREEN + player.getName() + " mined a diamond! He is back on the Best BTC List!");
			list.add(player.getName());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (list.contains(player.getName()) && event.getTo().getBlockY() > 50) {
			PlayerUtils.broadcast(ChatColor.RED + player.getName() + " moved above y:50!");
			list.remove(player.getName());
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("btclist")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"BestBTC\" is not enabled.");
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
			
			sender.sendMessage(Main.prefix() + "People still on the BestBTC list:\n§a" + (btclist.length() > 0 ? btclist.toString().trim() : "None") + "§7.");
		}
		
		if (cmd.getName().equalsIgnoreCase("btc")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"BestBTC\" is not enabled.");
				return true;
			}
			
			if (sender.hasPermission("uhc.bestpve.admin")) {
				if (args.length < 2) {
					sender.sendMessage(Main.prefix() + "Help for BestBTC:");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc remove <player> - Removes an player manually to the list.");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (list.contains(args[1])) {
						sender.sendMessage(Main.prefix() + ChatColor.RED + args[1] + " already on the BestBTC list.");
						return true;
					}
					
					list.add(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " added to the BestBTC list.");
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (!list.contains(args[1])) {
						sender.sendMessage(Main.prefix() + ChatColor.RED + args[1] + " is not on the BestBTC list.");
						return true;
					}
					
					list.remove(args[1]);
					sender.sendMessage(Main.prefix() + args[1] + " removed from the BestBTC list.");
				} else {
					sender.sendMessage(Main.prefix() + "Help for BestBTC:");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc add <player> - Adds an player manually to the list.");
					sender.sendMessage(ChatColor.GRAY + "- §f/btc remove <player> - Removes an player manually to the list.");
				}
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
}