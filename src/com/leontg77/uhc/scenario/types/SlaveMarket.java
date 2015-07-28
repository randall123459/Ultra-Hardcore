package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class SlaveMarket extends Scenario implements Listener {
	private boolean enabled = false;
	
	private boolean bidProgressing = false;
	private int bidTime = 0, biggestBid = -1;
	private String bidWinner = null;
	private ArrayList<String> traders = new ArrayList<String>(); 
	
	public SlaveMarket() {
		super("SlaveMarket", "");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public String prefix() {
		return Main.prefix().replaceAll("UHC", "SlaveMarket");
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().startsWith("/slavereset")) {
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			final Player player = event.getPlayer();
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "SlaveMarket is not enabled.");
				return;
			}
			bidProgressing = false;
			traders.clear();
			biggestBid = -1;
			bidWinner = null;
			bidTime = 0;
			for (Team team : Teams.getManager().getTeams()) {
				for (String p : team.getEntries()) {
					team.removeEntry(p);
				}
			}
			player.sendMessage(prefix() + "SlaveMarket reset.");
		}
		
		if (event.getMessage().startsWith("/slaveowner")) {
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			final Player player = event.getPlayer();
			final String[] args = ar.toArray(new String[ar.size()]);
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "SlaveMarket is not enabled.");
				return;
			}
			
			if (player.hasPermission("slave.admin")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
					return;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length >= 2) {
						Player target = Bukkit.getPlayer(args[1]);
						
						if (target == null) {
							player.sendMessage(ChatColor.RED + "That player is not online.");
							return;
						}
						
						traders.add(target.getName());
						Team t = null;
						
						for (Team team : Teams.getManager().getTeams()) {
							if (team.getSize() == 0) {
								t = team;
								break;
							}
						}
						
						if (t == null) {
							player.sendMessage(ChatColor.RED + "No more available teams.");
							return;
						}
						
						t.addEntry(target.getName());
						PlayerUtils.broadcast(prefix() + ChatColor.GREEN + target.getName() + " §7is now a slave owner!");
						
						if (args.length >= 3) {
							int i;
							
							try {
								i = Integer.parseInt(args[2]);
							} catch (Exception e) {
								player.sendMessage(ChatColor.RED + "Invaild number.");
								return;
							}
							
							target.getInventory().addItem(new ItemStack (Material.DIAMOND, i));
						} else {
							target.getInventory().addItem(new ItemStack (Material.DIAMOND, 30));
						}
					} else {
						player.sendMessage(ChatColor.RED + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
					}
				}
				else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length >= 2) {
						Player target = Bukkit.getPlayer(args[1]);
						
						if (target == null) {
							player.sendMessage(ChatColor.RED + "That player is not online.");
							return;
						}
						
						traders.remove(args[1]);
						Team t = target.getScoreboard().getEntryTeam(target.getName());
						
						if (t != null) {
							t.removeEntry(target.getName());
						}
						
						PlayerUtils.broadcast(prefix() + ChatColor.GREEN + args[1] + " §7is no longer a slave owner!");
						target.getInventory().clear();
					} else {
						player.sendMessage(ChatColor.RED + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
					}
				}
				else if (args[0].equalsIgnoreCase("list")) {
					StringBuilder s = new StringBuilder();
					
					for (String l : traders) {
						if (s.length() > 0) {
							s.append("§7, §a");
						}
						
						s.append(ChatColor.GREEN + l);
					}
					
					player.sendMessage(prefix() + "Current slaves: " + s.toString().trim());
				}
				else {
					player.sendMessage(ChatColor.RED + "Usage: /slaveowner <add|remove|list> [player] [amountofdias]");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		
		if (event.getMessage().startsWith("/startbid")) {
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			final Player player = event.getPlayer();
			final String[] args = ar.toArray(new String[ar.size()]);
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "SlaveMarket is not enabled.");
				return;
			}
			
			if (player.hasPermission("slave.admin")) {
	    		if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Usage: /startbid <player> <time>");
					return;
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
		    		online.sendMessage(prefix() + "The bidding of player §a" + args[0] + "§7 is about to start.");
		    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
		    	}
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.sendMessage(prefix() + "Bidding starts in 3");
				    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
				    	}
					}
				}, 20);
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
				    	for (Player online : PlayerUtils.getPlayers()) {
				    		online.sendMessage(prefix() + "Bidding starts in 2");
				    		online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
				    	}
					}
				}, 40);
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						for (Player online : PlayerUtils.getPlayers()) {
				    		online.sendMessage(prefix() + "Bidding starts in 1");
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
							player.sendMessage(ChatColor.RED + "Invaild number.");
							return;
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
							    		target.getInventory().remove(new ItemStack (Material.DIAMOND, biggestBid));
							    		PlayerUtils.broadcast(prefix() + ChatColor.GREEN + bidWinner + "§7 has won the midding on §a" + args[0] + "§7 for §a" + biggestBid);
							    		Team t = target.getScoreboard().getEntryTeam(target.getName());
							    		
							    		if (t == null) {
							    			player.sendMessage(ChatColor.RED + "Could not join team.");
							    		} else {
							    			t.addEntry(args[0]);
							    		}
							    	} else {
							    		PlayerUtils.broadcast(prefix() + "None of the slave traders bid on §a" + args[0] + "§7.");
							    	}
								}
								if (bidTime < 6) {
									PlayerUtils.broadcast(prefix() + "Bidding ends in " + bidTime);
								}
				    		}
				    	}.runTaskTimer(Main.plugin, 0, 20);
					}
				}, 80);
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		
		if (event.getMessage().startsWith("/bid")) {
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			final Player player = event.getPlayer();
			final String[] args = ar.toArray(new String[ar.size()]);
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "SlaveMarket is not enabled.");
				return;
			}
			
			if (traders.contains(player.getName())) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Usage: /bid <amount>");
					return;
				}
				
				int i;
				
				try {
					i = Integer.parseInt(args[0]);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED + "Invaild number.");
					return;
				}
				
				if (i >= 0) {
					if (bidProgressing) {
						if (!hasenough(player, Material.DIAMOND, i)) {
							player.sendMessage(prefix() + "You have to bid more diamonds than you have.");
							return;
						}
						
						if (i > biggestBid) {
							biggestBid = i;
							bidWinner = player.getName();
							if (bidTime < 5) {
								bidTime = bidTime + 5;
							}
							PlayerUtils.broadcast(prefix() + "§a" + player.getName() + "§7 bid §a" + i);
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
				player.sendMessage(ChatColor.RED + "You are not a slave trader.");
			}
		}
	}

	private boolean hasenough(Player player, Material diamond, int i) {
		int a = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item.getType() == diamond) {
				a = a + item.getAmount();
			}
		}
		return i >= a;
	}
}