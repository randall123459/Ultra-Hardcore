package com.leontg77.uhc.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Arena listener class.
 * <p> 
 * Contains all eventhandlers for arena releated events.
 * 
 * @author LeonTG77
 */
public class ArenaListener implements Listener {
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Arena arena = Arena.getInstance();
		
		if (!arena.isEnabled()) {
			return;
		} 

		Player player = event.getEntity();
		Teams teams = Teams.getInstance();
		
		Team team = teams.getTeam(player);
		User user = User.get(player);
		
    	event.setDeathMessage(null);
		event.setDroppedExp(0);

		user.increaseStat(Stat.ARENADEATHS);
		arena.removePlayer(player, true);
    	
		ItemStack skull = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta skullMeta = skull.getItemMeta();
		skullMeta.setDisplayName("§6Golden Head");
		skullMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
		skull.setItemMeta(skullMeta);
		
		event.getDrops().clear();
		event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
		event.getDrops().add(new ItemStack(Material.ARROW, 32));
		event.getDrops().add(skull);
		
		Player killer = player.getKiller();
		
		if (killer == null) {
			player.sendMessage(Main.PREFIX + "You were killed by PvE.");
			Arena.getInstance().killstreak.put(player, 0); 

			if (Arena.getInstance().killstreak.containsKey(player) && Arena.getInstance().killstreak.get(player) > 4) {
				PlayerUtils.broadcast(Main.PREFIX + "§6" + player.getName() + "'s §7killstreak of §a" + Arena.getInstance().killstreak.get(player) + " §7was shut down by PvE");
			}
			
			for (Player p : Arena.getInstance().getPlayers()) {
				p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by PvE");
			}
			
			Arena.getInstance().setScore("§8» §a§lPvE", Arena.getInstance().getScore("§8» §a§lPvE") + 1);
			Arena.getInstance().resetScore(player.getName());
			return;
		}
		
		if (Arena.getInstance().killstreak.containsKey(player) && Arena.getInstance().killstreak.get(player) > 4) {
			PlayerUtils.broadcast(Main.PREFIX + "§6" + player.getName() + "'s §7killstreak of §a" + Arena.getInstance().killstreak.get(player) + " §7was shut down by §6" + player.getKiller().getName());
		}
		
		player.sendMessage(Main.PREFIX + "You were killed by §a" + killer.getName() + "§7.");
		Arena.getInstance().killstreak.put(player, 0);
		
		Team kTeam = Teams.getInstance().getTeam(killer);
		killer.setLevel(killer.getLevel() + 1);
		User uKiller = User.get(killer);
		
		if (!Bukkit.hasWhitelist()) {
			uKiller.increaseStat(Stat.ARENAKILLS);
		}
		
		for (Player p : Arena.getInstance().getPlayers()) {
			p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by " + (kTeam == null ? "§f" : kTeam.getPrefix()) + killer.getName());
		}   

		Arena.getInstance().setScore(killer.getName(), Arena.getInstance().getScore(killer.getName()) + 1);
	    Arena.getInstance().resetScore(player.getName());
		
		if (Arena.getInstance().killstreak.containsKey(killer)) {
			Arena.getInstance().killstreak.put(killer, Arena.getInstance().killstreak.get(killer) + 1);
		} else {
			Arena.getInstance().killstreak.put(killer, 1);
		}
		
		if (Arena.getInstance().killstreak.containsKey(killer)) {
			String killstreak = String.valueOf(Arena.getInstance().killstreak.get(killer));
			
			if (killstreak.endsWith("0") || killstreak.endsWith("5")) {
				PlayerUtils.broadcast(Main.PREFIX + "§6" + killer.getName() + " §7is now on a §a" + killstreak + " §7killstreak!");
			}
		}
	}
}