package com.leontg77.uhc.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Data;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class ArenaListener implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!Arena.getManager().isEnabled()) {
			return;
		}

		final Player player = event.getEntity();

		for (Player p : Arena.getManager().getPlayers()) {
			p.sendMessage(event.getDeathMessage());
		}   	
    	event.setDeathMessage(null);
		Data data = Data.getData(player);
		data.increaseStat("arenadeaths");
 
    	ItemStack skull = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta skullMeta = skull.getItemMeta();
		skullMeta.setDisplayName("§6Golden Head");
		skullMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood.", ChatColor.AQUA + "Made from the head of: " + player.getName()));
		skull.setItemMeta(skullMeta);
    	
		event.getDrops().clear();
		event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
		event.getDrops().add(new ItemStack(Material.ARROW, 32));
		event.getDrops().add(skull);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				Arena.getManager().removePlayer(player, true);
			}
		}, 20);
		
		if (player.getKiller() == null) {
			player.sendMessage(Main.prefix() + "You were killed by PvE.");
			return;
		}
		
		if (Arena.getManager().killstreak.containsKey(player) && Arena.getManager().killstreak.get(player) > 4) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getName() + "'s §7killstreak of " + Arena.getManager().killstreak.get(player) + " was shut down by §a" + player.getKiller().getName());
		}
		
		Arena.getManager().killstreak.put(player, 0);
		
		player.getKiller().setLevel(player.getKiller().getLevel() + 1);
		Data killerData = Data.getData(player.getKiller());
		killerData.increaseStat("arenakills");
		player.sendMessage(Main.prefix() + "You were killed by §a" + player.getKiller().getName() + "§7.");
		
		if (Arena.getManager().killstreak.containsKey(player.getKiller())) {
			Arena.getManager().killstreak.put(player.getKiller(), Arena.getManager().killstreak.get(player.getKiller()) + 1);
		} else {
			Arena.getManager().killstreak.put(player.getKiller(), 0);
		}
		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 5) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 5 killstreak");
		}

		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 10) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 10 killstreak");
		}

		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 15) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 15 killstreak");
		}

		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 20) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 20 killstreak");
		}

		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 30) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 30 killstreak");
		}
		
		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 50) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 50 killstreak");
		}

		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 75) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 75 killstreak");
		}
		
		if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 100) {
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getKiller().getName() + " §7is now on a 100 killstreak");
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!Arena.getManager().isEnabled()) {
			return;
		}

		Player player = event.getPlayer();

		if (Arena.getManager().isEnabled() && Arena.getManager().hasPlayer(player)) {
			Arena.getManager().removePlayer(player, false);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (!Arena.getManager().isEnabled()) {
			return;
		}
		
		Location loc = event.getBlock().getLocation();

		for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
			for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
				for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
					if (loc.getWorld().getBlockAt(x, y, z).getType() == Material.FIRE) {
						loc.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
						loc.getWorld().getBlockAt(x, y, z).getState().update();
					}
				}
			}
		}

		event.setCancelled(true);
	}
}