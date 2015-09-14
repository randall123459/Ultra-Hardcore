package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.scenario.Scenario;

/**
 * Compensation scenario class
 * 
 * @author TheRCPanda
 */
public class Compensation extends Scenario implements Listener {
	private boolean enabled = false;
	
	public Compensation() {
		super("Compensation", "When a player on a team dies, the player's max health is divided up and added to the max health of the player's teammates. The extra health received will regenerate in 30 seconds.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
		if (event.getRecipe().getResult().getType() == Material.ARROW) {
			event.getInventory().getResult().setAmount(event.getInventory().getResult().getAmount() * 4);
		}
    }
	
	@EventHandler
    public void onDeath(PlayerDeathEvent event) {
		Player victim = event.getEntity();
        double victimMaxHealth = victim.getMaxHealth();

        Team victimTeam = victim.getScoreboard().getEntryTeam(victim.getName());

        if (victimTeam != null) {
            victimTeam.removeEntry(victim.getName());

            double healthPerPerson = victimMaxHealth / victimTeam.getSize();
            int healthPerPersonRounded = (int) healthPerPerson;

			double excessHealth = healthPerPerson - healthPerPersonRounded;

            int ticksRegen = healthPerPersonRounded * 50;

            for (String s : victimTeam.getEntries()) {
            	Player p = Bukkit.getServer().getPlayer(s);
            	
            	if (p == null) {
            		continue;
            	}
            	
                p.setMaxHealth(p.getMaxHealth() + healthPerPerson);
                p.removePotionEffect(PotionEffectType.REGENERATION);
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticksRegen, 0));
                p.setHealth(p.getHealth() + excessHealth);
            }
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
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
}