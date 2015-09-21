package com.leontg77.uhc.scenario.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.uhc.scenario.Scenario;

/**
 * Switcheroo scenario class
 * 
 * @author LeonTG77
 */
public class Switcheroo extends Scenario implements Listener {
	private boolean enabled = false;

	public Switcheroo() {
		super("Switcheroo", "When you shoot someone, you trade places with them");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if (event.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) event.getDamager();
				
				if (proj.getShooter() instanceof Player) {
					Player shooter = (Player) proj.getShooter();
					
					Location loc1 = player.getLocation();
					Location loc2 = shooter.getLocation();

					shooter.teleport(loc1);
					player.teleport(loc2);
				}
			}
		}
	}
}