package com.leontg77.uhc.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.BlockUtils;
import com.leontg77.uhc.utils.NumberUtils;

/**
 * Entity listener class.
 * <p> 
 * Contains all eventhandlers for entity releated events.
 * 
 * @author LeonTG77
 */
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
		if (!Main.pearldamage && event.getDamager() instanceof EnderPearl) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Location loc = event.getLocation();
		
		if (loc.getWorld().getName().equals("lobby") || loc.getWorld().getName().equals("arena")) {
			if (event.getEntityType() != EntityType.ARMOR_STAND) {
				event.setCancelled(true);
			}
			return;
		}
		
		if (loc.getWorld().getGameRuleValue("doMobSpawning") != null && loc.getWorld().getGameRuleValue("doMobSpawning").equals("false")) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getEntity() instanceof Wolf) {
			event.getEntity().setCustomName("Wolf");
		}
		
		if (event.getEntity() instanceof Sheep) {
			if (loc.getBlock().getBiome().equals(Biome.FOREST) || loc.getBlock().getBiome().equals(Biome.FOREST_HILLS)) {
				event.setCancelled(true);
				
				Wolf wolf = loc.getWorld().spawn(loc, Wolf.class);
				wolf.setCustomName("Wolf");
			}
		}
		
		if (event.getEntity() instanceof Rabbit) {
			if (loc.getBlock().getBiome().equals(Biome.FOREST) || loc.getBlock().getBiome().equals(Biome.FOREST_HILLS)) {
				event.setCancelled(true);
				
				Wolf wolf = loc.getWorld().spawn(loc, Wolf.class);
				wolf.setCustomName("Wolf");
			}
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
    	
    	if (entity instanceof Creeper) {
    		if (((Creeper) entity).isPowered()) {
    			event.setDroppedExp(0);
    		}
    	}
    	
    	ItemStack potion = new ItemStack (Material.POTION, 1, (short) 8261);
    	
    	if (entity instanceof Witch) {
			Player killer = ((Witch) entity).getKiller();
			
    		if (killer != null) {
        		event.getDrops().remove(new ItemStack (Material.POTION, 1, (short) 8261));
        		
    			if (killer.hasPotionEffect(PotionEffectType.POISON)) {
		    		event.getDrops().add(potion);
    			} else {
    				if ((new Random().nextInt(99) + 1) <= 30) {
    		    		event.getDrops().add(potion);
    				}
    			}
    		}
        }
	}
	
	/**
	 * @author Ghowden
	 */
	@EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getRegainReason() == RegainReason.REGEN || event.getRegainReason() == RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }
	
	/**
	 * @author D4mnX
	 */
	@EventHandler
    public void onStrengthDamage(EntityDamageByEntityEvent event) {
		if (Main.nerfedStrength) {
	        if (!(event.getDamager() instanceof Player)) {
	            return;
	        }

	        Player attacker = (Player) event.getDamager();
	        int strengthAmplifier = 0;

	        if (!attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
	            return;
	        }
	        
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
	public void onShot(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Projectile) || !(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (ScenarioManager.getInstance().getScenario("Paranoia").isEnabled()) {
			return;
		}
	
		final Player player = (Player) event.getEntity();
		final Projectile damager = (Projectile) event.getDamager();

		if (Spectator.getManager().isSpectating(player)) {
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
	public void onLongshot(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Projectile) || !(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (ScenarioManager.getInstance().getScenario("RewardingLongshots").isEnabled()) {
			return;
		}
	
		Player player = (Player) event.getEntity();
		Projectile damager = (Projectile) event.getDamager();

		if (Spectator.getManager().isSpectating(player)) {
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

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
		TravelAgent travel = event.getPortalTravelAgent();
		Location from = event.getFrom();
		
		if (Main.nether) {
			String fromName = event.getFrom().getWorld().getName();

	        String targetName;
	        if (from.getWorld().getEnvironment() == Environment.NETHER) {
	            if (!fromName.endsWith("_nether")) {
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 7);
	        } else if (from.getWorld().getEnvironment() == Environment.NORMAL) {
	            if (!BlockUtils.hasBlockNearby(Material.PORTAL, from)) {
	                return;
	            }

	            targetName = fromName + "_nether";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
	            return;
	        }

	        Location to = new Location(world, from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch());
	        to = travel.findOrCreate(to);

	        if (to != null) {
	            event.setTo(to);
	        }
		}
		
		if (Main.theend) {
			String fromName = event.getFrom().getWorld().getName();

	        String targetName;
	        if (event.getFrom().getWorld().getEnvironment() == Environment.THE_END) {
	            if (!fromName.endsWith("_end")) {
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 4);
	        } else if (event.getFrom().getWorld().getEnvironment() == Environment.NORMAL) {
	            if (!BlockUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
	                return;
	            }
	            
	            targetName = fromName + "_end";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
	            return;
	        }

	        Location to = new Location(world, 100.0, 49, 0, 90f, 0f);

			for (int y = to.getBlockY() - 1; y <= to.getBlockY() + 2; y++) {
				for (int x = to.getBlockX() - 2; x <= to.getBlockX() + 2; x++) {
					for (int z = to.getBlockZ() - 2; z <= to.getBlockZ() + 2; z++) {
						if (y == 48) {
							to.getWorld().getBlockAt(x, y, z).setType(Material.OBSIDIAN);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						} else {
							to.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						}
					}
				}
			}
			
			event.setTo(to);
		}
    }
}