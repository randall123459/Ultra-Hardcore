package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * SlaveMarket scenario class
 * 
 * @author LeonTG77
 */
public class SlaveMarket extends Scenario implements Listener, CommandExecutor {
	private boolean enabled = false;
	
	private boolean bidProgressing = false;
	private int bidTime = 0, biggestBid = -1;
	private String bidWinner = null;
	private ArrayList<String> traders = new ArrayList<String>(); 
	
	public SlaveMarket() {
		super("SlaveMarket", "8 slave owners are chosen and they get 30 diamonds to bid on players as they choose. Any spare diamonds will be cleared.");
		Main main = Main.plugin;
		
		main.getCommand("slavereset").setExecutor(this);
		main.getCommand("slaveowner").setExecutor(this);
		main.getCommand("startbid").setExecutor(this);
		main.getCommand("bid").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public String prefix() {
		return Main.prefix().replaceAll("UHC", "Slave");
	}
	
	@EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {	
        event.setCancelled(true);
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        event.setCancelled(true);
	}
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("slavereset")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"SlaveMarket\" is not enabled.");
				return true;
			}
			
			if (sender.hasPermission("uhc.slavemarket")) {
				bidProgressing = false;
				bidWinner = null;
				traders.clear();
				biggestBid = -1;
				bidTime = 0;
				
				for (Team teams : Teams.getManager().getTeamsWithPlayers()) {
					for (String entry : teams.getEntries()) {
						teams.removeEntry(entry);
					}
				}
				
				PlayerUtils.broadcast(prefix() + "SlaveMarket has been reset.");
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("slaveowner")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"SlaveMarket\" is not enabled.");
				return true;
			}
			
			if (sender.hasPermission("uhc.slavemarket")) {
				if (args.length == 0) {
					sender.sendMessage(Main.prefix() + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length >= 2) {
						Player target = Bukkit.getPlayer(args[1]);
						
						if (target == null) {
							sender.sendMessage(ChatColor.RED + "That player is not online.");
							return true;
						}
						
						traders.add(target.getName());
						Team t = Teams.getManager().findAvailableTeam();
						
						if (t == null) {
							sender.sendMessage(ChatColor.RED + "Could not find any open teams.");
							return true;
						}
						
						t.addEntry(target.getName());
						PlayerUtils.broadcast(prefix() + ChatColor.GREEN + target.getName() + " §7is now a slave owner.");
						
						if (args.length >= 3) {
							int i;
							
							try {
								i = Integer.parseInt(args[2]);
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invaild number.");
								return true;
							}
							
							target.getInventory().addItem(new ItemStack (Material.DIAMOND, i));
						} else {
							target.getInventory().addItem(new ItemStack (Material.DIAMOND, 30));
						}
					} else {
						sender.sendMessage(Main.prefix() + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
					}
				}
				else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length >= 2) {
						Player target = Bukkit.getPlayer(args[1]);
						
						if (target == null) {
							sender.sendMessage(ChatColor.RED + "That player is not online.");
							return true;
						}
						
						traders.remove(args[1]);
						Team t = target.getScoreboard().getEntryTeam(target.getName());
						
						if (t != null) {
							t.removeEntry(target.getName());
						}
						
						PlayerUtils.broadcast(prefix() + ChatColor.GREEN + args[1] + " §7is no longer a slave owner!");
						target.getInventory().clear();
					} else {
						sender.sendMessage(Main.prefix() + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
					}
				}
				else if (args[0].equalsIgnoreCase("list")) {
					StringBuilder s = new StringBuilder();
					
					for (String l : traders) {
						if (s.length() > 0) {
							s.append("§8, §a");
						}
						
						s.append(ChatColor.GREEN + l);
					}
					
					sender.sendMessage(prefix() + "Current slaves: " + s.toString().trim());
				}
				else {
					sender.sendMessage(Main.prefix() + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
				}
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("startbid")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"SlaveMarket\" is not enabled.");
				return true;
			}
			
			if (sender.hasPermission("uhc.slavemarket")) {
	    		if (args.length < 2) {
	    			sender.sendMessage(ChatColor.RED + "Usage: /startbid <player> <time>");
					return true;
				}

	    		PlayerUtils.broadcast(prefix() + "The bidding of player §a" + args[0] + "§7 is about to start.");
	    		
				for (Player online : PlayerUtils.getPlayers()) {
		    		online.playSound(online.getLocation(), Sound.FIREWORK_LAUNCH, 1, 0);
		    	}
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
			    		PlayerUtils.broadcast(prefix() + "Bidding starts in §a3§7.");
			    		
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
				    	}
					}
				}, 20);
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
			    		PlayerUtils.broadcast(prefix() + "Bidding starts in §a2§7.");
			    		
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
				    	}
					}
				}, 40);
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
			    		PlayerUtils.broadcast(prefix() + "Bidding starts in §a1§7.");
			    		
						for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
				    	}
					}
				}, 60);
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						int i;
						
						try {
							i = Integer.parseInt(args[1]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Invaild number.");
							return;
						}

			    		PlayerUtils.broadcast(prefix() + "The bidding of player §a" + args[0] + "§7 has started.");
			    		
						for (Player online : PlayerUtils.getPlayers()) {
				    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
				    	}
						
				    	bidProgressing = true;
				    	bidTime = i;
				    	biggestBid = -1;
				    	bidWinner = null;
				    	
				    	new BukkitRunnable() {
				    		public void run() {
								bidTime--;
								if (bidTime == 0) {
									cancel();
									bidProgressing = false;
							    	if (bidWinner != null) {
							    		Player target = Bukkit.getPlayer(bidWinner);
							    		if (target == null) {
								    		PlayerUtils.broadcast(prefix() + "Bid winner is offline...");
								    		return;
							    		}
							    		PlayerUtils.broadcast(prefix() + ChatColor.GREEN + bidWinner + "§7 has won the bidding on §a" + args[0] + "§7 for §a" + biggestBid + "§7 diamonds.");

										for (Player online : PlayerUtils.getPlayers()) {
								    		online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
								    	}
							    		
							    		for (ItemStack item : target.getInventory().getContents()) {
							    			if (item != null && item.getType() == Material.DIAMOND) {
							    				if (item.getAmount() > biggestBid) {
							    					item.setAmount(item.getAmount() - biggestBid);
							    				} else {
							    					target.getInventory().remove(item);
							    				}
							    				break;
							    			}
							    		}
							    		
							    		Team t = target.getScoreboard().getEntryTeam(target.getName());
							    		
							    		if (t == null) {
							    			sender.sendMessage(ChatColor.RED + "Could not join team.");
							    		} else {
							    			t.addEntry(args[0]);
							    		}
							    	} else {
							    		PlayerUtils.broadcast(prefix() + "None of the slave traders bid on §a" + args[0] + "§7.");
							    	}
							    	return;
								}
								if (bidTime < 4) {
									PlayerUtils.broadcast(prefix() + "Bidding ends in §a" + bidTime + "§7.");

									for (Player online : PlayerUtils.getPlayers()) {
							    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
							    	}
								}
				    		}
				    	}.runTaskTimer(Main.plugin, 0, 20);
					}
				}, 80);
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("bid")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can bid on players.");
				return true;
			}
			
			Player player = (Player) sender;
			
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"SlaveMarket\" is not enabled.");
				return true;
			}
			
			if (traders.contains(player.getName())) {
				if (args.length == 0) {
					player.sendMessage(Main.prefix() + "Usage: /bid <amount>");
					return true;
				}
				
				int i;
				
				try {
					i = Integer.parseInt(args[0]);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED + "Invaild number.");
					return true;
				}
				
				if (i > 0) {
					if (bidProgressing) {
						if (!PlayerUtils.hasEnough(player, Material.DIAMOND, i)) {
							player.sendMessage(prefix() + "You can't bid more diamonds than you have.");
							return true;
						}
						
						if (i > biggestBid) {
							biggestBid = i;
							bidWinner = player.getName();
							if (bidTime < 3) {
								bidTime = bidTime + 3;
							}
							PlayerUtils.broadcast(prefix() + "§a" + player.getName() + "§7 bid §a" + i + "§7.");
						} else {
							player.sendMessage(prefix() + "Bids must be greater than the previous bid.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "There are no slaves being bid on right now.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "Bids cannot be negative.");
				}
			} else {
				player.sendMessage(prefix() + "You are not a slave owner.");
			}
		}
		return true;
	}
}