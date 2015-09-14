package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

/**
 * MeleeFun scenario class.
 * 
 * @author D4mnX
 */
public class MeleeFun extends Scenario implements Listener {
	private boolean enabled = false;
	
	public MeleeFun() {
		super("MeleeFun", "The plugin cancels out the \"noDamageTicks\" so there is no delay between hits. However fast you click is how fast you hit someone.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (event.getCause() != DamageCause.ENTITY_ATTACK) {
            return;
        }

        final Player player = (Player) event.getEntity();
        event.setDamage(event.getDamage() * 0.5);
        
        Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
            public void run() {
                player.setNoDamageTicks(0);
            }
        }, 1L);
    }
}