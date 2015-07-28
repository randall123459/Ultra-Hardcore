package com.leontg77.uhc.scenario.types;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class TrainingRabbits extends Scenario implements Listener {
	private boolean enabled = false;

	public TrainingRabbits() {
		super("TrainingRabbits", "Everyone gets jump boost 2 for the entire game, and as you get kills, your level of jump boost will increase. Fall damage is disabled.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 1));
			}
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.removePotionEffect(PotionEffectType.JUMP);
			}
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
		
		if (event.getEntity().getKiller() == null) {
			return;
		}

		Player player = event.getEntity().getKiller();

		int level = 1;
		
		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType() == PotionEffectType.JUMP) {
				level = effect.getAmplifier() + 1;
				break;
			}
		}
		
		player.removePotionEffect(PotionEffectType.JUMP);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, level));
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.getCause() != DamageCause.FALL) {
			return;
		}
		
		event.setCancelled(true);
	}
}