package com.leontg77.uhc.scenario.types;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.uhc.scenario.Scenario;

/**
 * Depths scenario class
 * 
 * @author LeonTG77
 */
public class Depths extends Scenario implements Listener {
	private boolean enabled = false;
	
	public Depths() {
		super("Depths", "The lower you go, the more mobs damage you.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Entity || (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Entity)) {
			if (event.getDamager() instanceof Player || (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
			
			Entity e = event.getEntity();
			
			if (e.getLocation().getBlockY() <= 15) {
				event.setDamage(event.getDamage() * 5);
			}
			else if (e.getLocation().getBlockY() <= 30) {
				event.setDamage(event.getDamage() * 3);
			}
			else if (e.getLocation().getBlockY() <= 45) {
				event.setDamage(event.getDamage() * 2);
			}
			else if (e.getLocation().getBlockY() <= 60) {
				event.setDamage(event.getDamage() * 1.5);
			} 
		}
	}
}