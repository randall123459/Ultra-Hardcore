package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Assassins scenario class
 * 
 * @author audicymc
 */
public class Assassins extends Scenario implements Listener, CommandExecutor {
	private HashMap<String, String> assassins = new HashMap<String, String>();
	private boolean enabled = false;

	public Assassins() {
		super("Assassins", "Each player has a target that they must kill. Killing anyone that is not your target or assassin will result in no items dropping. When your target dies, you get their target.");
		Main main = Main.plugin;
		
		main.getCommand("target").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			PlayerUtils.broadcast(Main.prefix() + "Assigning targets...");
			ArrayList<Player> players = new ArrayList<Player>(PlayerUtils.getPlayers());
			Collections.shuffle(players);
	          
			for (int i = 0; i < players.size(); i++) {
				Player assassin = players.get(i);
	            Player target = players.get(i < players.size() - 1 ? i + 1 : 0);
	            
	            setTarget(assassin.getName(), target.getName());
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		
		if (assassins.containsKey(player.getName())) {
			String assassin = getAssassin(player.getName());
			String target = getTarget(player.getName());
			
			if (killer != null && !killer.getName().equals(assassin) && !killer.getName().equals(target)) {
				event.getDrops().clear();
			}
			
			setTarget(assassin, target);
			assassins.remove(player.getName());
			event.setDeathMessage(Main.prefix() + ChatColor.GREEN + player.getName() + " §7was eliminated!");
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String target = getAssassin(player.getName());
			
		if (target != null) {
			Player targetP = Bukkit.getServer().getPlayer(target);
			
			if (targetP != null) {
				targetP.setCompassTarget(player.getLocation());
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can have targets.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("target")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Assassins\" is not enabled.");
				return true;
			}
			
			player.sendMessage(Main.prefix() + "Your target is: §a" + getTarget(player.getName()));
		}
		return true;
	}

	/**
	 * Get the assassins map
	 * @return the Assassins map
	 */
	public Map<String, String> getAssassins() {
		return assassins;
	}

	/**
	 * Get the assassin that has the target.
	 * @param target the target.
	 * @return the assassin.
	 */
	private String getAssassin(String target) {
		for (Entry<String, String> e : assassins.entrySet()) {
			if (e.getValue().equalsIgnoreCase(target)) {
				return e.getKey();
			}
		}
		return null;
	}

	/**
	 * Get the target for an assassin
	 * @param assassin the assassin.
	 * @return the target of the assassin
	 */
	private String getTarget(String assassin) {
		for (Entry<String, String> e : assassins.entrySet()) {
			if (e.getKey().equalsIgnoreCase(assassin)) {
				return e.getValue();
			}
		}
		return null;
	}

	/**
	 * Set a new target for the assassin.
	 * @param assassin the assassin.
	 * @param target the new target.
	 */
	private void setTarget(String assassin, String target) {
		assassins.put(assassin, target);
		
		Player player = Bukkit.getServer().getPlayer(assassin);
		
		if (player != null) {
			player.sendMessage(Main.prefix() + "Your target is now: " + ChatColor.GREEN + target);
		}
	}
}