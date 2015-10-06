package com.leontg77.uhc.scenario.types;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * TrainingRabbits scenario class
 * 
 * @author LeonTG77
 */
public class TrainingRabbits extends Scenario implements Listener {
	public static HashMap<String, Integer> jump = new HashMap<String, Integer>();
	private boolean enabled = false;

	public TrainingRabbits() {
		super("TrainingRabbits", "Everyone gets jump boost 2 for the entire game, and as you get kills, your level of jump boost will increase. Fall damage is disabled.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 1));
				jump.put(online.getName(), 1);
			}
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.removePotionEffect(PotionEffectType.JUMP);
			}
			jump.clear();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.MILK_BUCKET) {
			event.getPlayer().sendMessage(Main.prefix() + "You cannot drink milk in TrainingRabbits.");
			event.setCancelled(true);
			event.setItem(new ItemStack (Material.AIR));
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (jump.containsKey(player.getName())) {
			jump.put(player.getName(), jump.get(player.getName()) + 1);
		} else {
			jump.put(player.getName(), 1);
		}
		
		int level = jump.get(player.getName());
		
		if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, level));
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() == null) {
			return;
		}

		Player player = event.getEntity().getKiller();

		if (jump.containsKey(player.getName())) {
			jump.put(player.getName(), jump.get(player.getName()) + 1);
		} else {
			jump.put(player.getName(), 2);
		}
		
		int level = jump.get(player.getName());
		
		player.removePotionEffect(PotionEffectType.JUMP);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, level));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (event.getCause() != DamageCause.FALL) {
			return;
		}
		
		event.setCancelled(true);
	}
}