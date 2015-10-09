package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * 100Hearts scenario class
 * 
 * @author LeonTG77
 */
public class HundredHearts extends Scenario implements Listener {
	private boolean enabled = false;

	public HundredHearts() {
		super("100Hearts", "Everyone has 100 hearts, golden apples heal 20% of your max health.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setMaxHealth(200);
				online.setHealth(200);
			}
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setMaxHealth(20);
			}
		}
	}

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
    	Player player = event.getPlayer();

        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
        	player.removePotionEffect(PotionEffectType.REGENERATION);

            double regenTicks = (player.getMaxHealth() / 5) * 25;
            int regenTicksRounded = (int) regenTicks;

            double excessHealth = regenTicks - regenTicksRounded;

            player.setHealth(player.getHealth() + excessHealth);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regenTicksRounded, 1));

        }
    }

	public boolean isEnabled() {
		return enabled;
	}
}