package com.leontg77.uhc.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.NumberUtils;

public class EntityListener implements Listener {
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {		
		if (State.isState(State.SCATTER)) {
			event.setCancelled(true);
			return;
		}
    	
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
	    	if (player.getWorld() == Bukkit.getWorld("lobby")) {
	    		event.setCancelled(true);
	    	}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
	
		if (!Main.pearldamage && event.getDamager() instanceof EnderPearl) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Location loc = event.getLocation();
		
		if (loc.getWorld().getName().equals("lobby") || loc.getWorld().getName().equals("arena")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
    	Entity entity = event.getEntity();
    	
    	if (Main.ghastdrops) {
        	if (entity instanceof Ghast) {
        		event.getDrops().remove(new ItemStack (Material.GHAST_TEAR));
        		event.getDrops().add(new ItemStack (Material.GOLD_INGOT));
        		return;
            }
    	}
    	
    	ItemStack potion = new ItemStack (Material.POTION, 1, (short) 8261);
    	
    	if (entity instanceof Witch) {
    		if (((LivingEntity) entity).getKiller() != null) {
    			if (((LivingEntity) entity).getKiller().hasPotionEffect(PotionEffectType.POISON)) {
    				if ((new Random().nextInt(99) + 1) <= 80) {
    		    		event.getDrops().add(potion);
    				}
    			} else {
    				if ((new Random().nextInt(99) + 1) <= 30) {
    		    		event.getDrops().add(potion);
    				}
    			}
    		}
        }
	}
	
	@EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        boolean regainByFood = event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED);
        boolean regainByPeaceful = event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN);

        if (!regainByFood && !regainByPeaceful) {
            return;
        }

        event.setCancelled(true);
        if (player.getFoodLevel() >= 18 && player.getHealth() > 0 && player.getHealth() < player.getMaxHealth()) {
            player.setExhaustion(player.getExhaustion() - 3);
        }
    }
	
	@EventHandler
    public void onStrengthDamage(EntityDamageByEntityEvent event) {
		if (Main.nerfedStrength) {
	        if (!(event.getDamager() instanceof Player)) {
	            return;
	        }

	        final Player attacker = (Player) event.getDamager();

	        if (!attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
	            return;
	        }

	        int strengthAmplifier = 0;
	        for (PotionEffect potionEffect : attacker.getActivePotionEffects()) {
	            if (potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
	                strengthAmplifier = potionEffect.getAmplifier() + 1;
	                break;
	            }
	        }

	        double damageWithoutStrength = event.getDamage() / (1 + (strengthAmplifier * 1.3));
	        double damageWithNerfedStrength = damageWithoutStrength + strengthAmplifier * 3;

	        event.setDamage(damageWithNerfedStrength);
		}
    }
	
	@EventHandler
	public void onShot(final EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Projectile) || !(event.getEntity() instanceof Player)) {
			return;
		}
	
		final Player player = (Player) event.getEntity();
		final Projectile damager = (Projectile) event.getDamager();

		if (Main.spectating.contains(player.getName())) {
			return;
		}
		
		if (damager instanceof EnderPearl) {
			return;
		}
		
		if (!(damager.getShooter() instanceof Player)) {
			return;
		}
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				Player killer = (Player) damager.getShooter();
				Damageable damage = player;
				double health = damage.getHealth();
				double hearts = health / 2;
				double precent = hearts * 10;
				
				if (health > 0.0000) {
					killer.sendMessage(Main.prefix() + "§6" + player.getName() + " §7is now at §6" + ((int) precent) + "%");
				}
			}
		}, 1);
	}
	
	@EventHandler
	public void onLongShot(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Projectile) || !(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (ScenarioManager.getManager().getScenario("RewardingLongshots").isEnabled()) {
			return;
		}
	
		Player player = (Player) event.getEntity();
		Projectile damager = (Projectile) event.getDamager();

		if (Main.spectating.contains(player.getName())) {
			return;
		}
		
		if (!(damager.getShooter() instanceof Player)) {
			return;
		}
		
		Player killer = (Player) damager.getShooter();
		double distance = killer.getLocation().distance(player.getLocation());
		
		if (distance < 50) {
			return;
		}
		
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			online.sendMessage(Main.prefix() + killer.getName() + " got a longshot of §6" + NumberUtils.convertDouble(distance) + " §7blocks.");
		}
	}
}