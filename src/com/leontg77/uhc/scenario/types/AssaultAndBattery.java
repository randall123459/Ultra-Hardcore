package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

public class AssaultAndBattery extends Scenario implements Listener, CommandExecutor {
	private HashMap<String, Type> types = new HashMap<String, Type>();
	private boolean enabled = false;

	public AssaultAndBattery() {
		super("AssaultAndBattery", "To2 Where one person can only do meelee damage to players, while the other one can only do ranged attacks. If a teammate dies, you can do both meelee and ranged attacks.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				Team team = online.getScoreboard().getEntryTeam(online.getName());
				
				if (team == null) {
					online.kickPlayer("You are not on a To2");
					online.setWhitelisted(false);
					continue;
				}
				
				if (team.getSize() != 2) {
					online.kickPlayer("You are not on a To2");
					online.setWhitelisted(false);
					team.removeEntry(online.getName());
				}
			}
			
			for (Team team : Teams.getManager().getTeamsWithPlayers()) {
				if (team.getSize() == 2) {
					ArrayList<String> entry = new ArrayList<String>(team.getEntries());
					types.put(entry.get(0), Type.ASSAULT);
					types.put(entry.get(1), Type.BATTERY);
				} else {
					for (String entry : team.getEntries()) {
						Player player = Bukkit.getServer().getPlayer(entry);
						
						if (player != null) {
							player.kickPlayer("You are not on a To2");
							player.setWhitelisted(false);
							team.removeEntry(player.getName());
						}
					}
				}
			}
			
			for (Player online : PlayerUtils.getPlayers()) {
				online.chat("/class");
			}
		} else {
			types.clear();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}
		 
		Player player = event.getEntity();
		Team team = player.getScoreboard().getEntryTeam(player.getName());
		
		if (team != null) {
			for (String entry : team.getEntries()) {
				if (entry != player.getName()) {
					types.put(entry, Type.BOTH);
					break;
				}
			}
		}
		types.remove(player.getName());
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.getEntity() instanceof Player) {
			if (event.getDamager() instanceof Player) {
				Player player = (Player) event.getDamager();
				
				if (types.get(player.getName()).equals(Type.BATTERY)) {
					player.sendMessage(ChatColor.RED + "You cannot do melee damage.");
					event.setCancelled(true);
				}
			} else if (event.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) event.getDamager();
				
				if (proj.getShooter() instanceof Player) {
					Player player = (Player) proj.getShooter();
					
					if (types.get(player.getName()).equals(Type.ASSAULT)) {
						player.sendMessage(ChatColor.RED + "You cannot do projectile damage.");
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player sender = event.getPlayer();
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/class")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "AssaultAndBattery is not enabled.");
				return;
			}
			
			if (!types.containsKey(sender.getName())) {
				sender.sendMessage(ChatColor.RED + "Error while checking class.");
				return;
			}
			
			switch (types.get(sender.getName())) {
			case ASSAULT:
				sender.sendMessage(Main.prefix() + "You are the assaulter, you can only do melee damage.");
				break;
			case BATTERY:
				sender.sendMessage(Main.prefix() + "You are the battery, you can only do projectile damage.");
				break;
			case BOTH:
				sender.sendMessage(Main.prefix() + "You are both, you can only do all types of damage.");
				break;
			}
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/listclass")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "AssaultAndBattery is not enabled.");
				return;
			}
			
			StringBuilder assault = new StringBuilder("");
			StringBuilder battery = new StringBuilder("");
			StringBuilder both = new StringBuilder("");
			
			for (String key : types.keySet()) {
				if (types.get(key) == Type.ASSAULT) {
					if (assault.length() > 0) {
						assault.append("§7, §a");
					}
					
					assault.append(ChatColor.GREEN + key);
				} 
				else if (types.get(key) == Type.BATTERY) {
					if (battery.length() > 0) {
						battery.append("§7, §a");
					}
					
					battery.append(ChatColor.GREEN + key);
				}
				else {
					if (both.length() > 0) {
						both.append("§7, §a");
					}
					
					both.append(ChatColor.GREEN + key);
				}
			}
			
			sender.sendMessage(Main.prefix() + "Assaulters: " + assault.toString().trim());
			sender.sendMessage(Main.prefix() + "Batteries: " + battery.toString().trim());
			sender.sendMessage(Main.prefix() + "Both: " + both.toString().trim());
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Players can't use assassins commands.");
		}
		
		if (cmd.getName().equalsIgnoreCase("class")) {
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "AssaultAndBattery is not enabled.");
				return true;
			}
			
			if (!types.containsKey(sender.getName())) {
				sender.sendMessage(ChatColor.RED + "Error while checking class.");
				return true;
			}
			
			switch (types.get(sender.getName())) {
			case ASSAULT:
				sender.sendMessage(Main.prefix() + "You are the assaulter, you can only do melee damage.");
				break;
			case BATTERY:
				sender.sendMessage(Main.prefix() + "You are the battery, you can only do projectile damage.");
				break;
			case BOTH:
				sender.sendMessage(Main.prefix() + "You are both, you can only do all types of damage.");
				break;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("listclass")) {
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "AssaultAndBattery is not enabled.");
				return true;
			}
			
			StringBuilder assault = new StringBuilder("");
			StringBuilder battery = new StringBuilder("");
			StringBuilder both = new StringBuilder("");
			
			for (String key : types.keySet()) {
				if (types.get(key) == Type.ASSAULT) {
					if (assault.length() > 0) {
						assault.append("§7, §a");
					}
					
					assault.append(ChatColor.GREEN + key);
				} 
				else if (types.get(key) == Type.BATTERY) {
					if (battery.length() > 0) {
						battery.append("§7, §a");
					}
					
					battery.append(ChatColor.GREEN + key);
				}
				else {
					if (both.length() > 0) {
						both.append("§7, §a");
					}
					
					both.append(ChatColor.GREEN + key);
				}
			}
			
			sender.sendMessage(Main.prefix() + "Assaulters: " + assault.toString().trim());
			sender.sendMessage(Main.prefix() + "Batteries: " + battery.toString().trim());
			sender.sendMessage(Main.prefix() + "Both: " + both.toString().trim());
		}
		return false;
	}
	
	public enum Type {
		ASSAULT, BATTERY, BOTH;
	}
}