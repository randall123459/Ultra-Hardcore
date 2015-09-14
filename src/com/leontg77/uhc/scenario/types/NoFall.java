package com.leontg77.uhc.scenario.types;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.leontg77.uhc.scenario.Scenario;

/**
 * No fall scenario class
 * 
 * @author LeonTG77
 */
public class NoFall extends Scenario implements Listener {
	private boolean enabled = false;
	
	public NoFall() {
		super("NoFall", "You cannot take fall damage.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
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