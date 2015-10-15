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
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * AssaultAndBattery scenario class
 * 
 * @author LeonTG77
 */
public class AssaultAndBattery extends Scenario implements Listener, CommandExecutor {
	private HashMap<String, Type> types = new HashMap<String, Type>();
	private boolean enabled = false;

	public AssaultAndBattery() {
		super("AssaultAndBattery", "To2 Where one person can only do meelee damage to players, while the other one can only do ranged attacks. If a teammate dies, you can do both meelee and ranged attacks.");
		Main main = Main.plugin;
		
		main.getCommand("class").setExecutor(this);
		main.getCommand("listclass").setExecutor(this);
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
			
			for (Team team : Teams.getInstance().getTeamsWithPlayers()) {
				if (team.getSize() == 2) {
					ArrayList<String> entry = new ArrayList<String>(team.getEntries());
					types.put(entry.get(0), Type.ASSAULT);
					types.put(entry.get(1), Type.BATTERY);
				} 
				else {
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
				if (!types.containsKey(online.getName())) {
					online.sendMessage(Main.prefix() + "You are both, you can only do all types of damage.");
					return;
				}
				
				switch (types.get(online.getName())) {
				case ASSAULT:
					online.sendMessage(Main.prefix() + "You are the assaulter, you can only do melee damage.");
					break;
				case BATTERY:
					online.sendMessage(Main.prefix() + "You are the battery, you can only do projectile damage.");
					break;
				case BOTH:
					online.sendMessage(Main.prefix() + "You are both, you can only do all types of damage.");
					break;
				}
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

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
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
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use AssaultAndBattery commands.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("class")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"AssaultAndBattery\" is not enabled.");
				return true;
			}
			
			if (!types.containsKey(player.getName())) {
				player.sendMessage(Main.prefix() + "You are both, you can only do all types of damage.");
				return true;
			}
			
			switch (types.get(player.getName())) {
			case ASSAULT:
				player.sendMessage(Main.prefix() + "You are the assaulter, you can only do melee damage.");
				break;
			case BATTERY:
				player.sendMessage(Main.prefix() + "You are the battery, you can only do projectile damage.");
				break;
			case BOTH:
				player.sendMessage(Main.prefix() + "You are both, you can only do all types of damage.");
				break;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("listclass")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"AssaultAndBattery\" is not enabled.");
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
			
			player.sendMessage(Main.prefix() + "Assaulters: " + assault.toString().trim());
			player.sendMessage(Main.prefix() + "Batteries: " + battery.toString().trim());
			player.sendMessage(Main.prefix() + "Both: " + both.toString().trim());
		}
		return true;
	}
	
	public enum Type {
		ASSAULT, BATTERY, BOTH;
	}
}