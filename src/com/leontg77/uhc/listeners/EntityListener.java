package com.leontg77.uhc.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.State;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Entity listener class.
 * <p> 
 * Contains all eventhandlers for entity releated events.
 * 
 * @author LeonTG77
 */
public class EntityListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		Item item = event.getEntity();
		ItemStack itemStack = item.getItemStack();
		Material itemType = itemStack.getType();
		
		if (Main.toReplace.containsKey(itemType)) {
			item.setItemStack(Main.toReplace.get(itemType));
			Main.toReplace.remove(itemType);
	    }
	}
	 
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		SpawnReason reason = event.getSpawnReason();
		Entity entity = event.getEntity();
		
		Location loc = event.getLocation();
		World world = loc.getWorld();
		
		if (reason == SpawnReason.NETHER_PORTAL || (State.isState(State.INGAME) && reason == SpawnReason.SPAWNER_EGG)) {
			event.setCancelled(true);
			return;
		}
		
		if (world.getName().equals("lobby") || world.getName().equals("arena")) {
			if (reason != SpawnReason.CUSTOM) {
				event.setCancelled(true);
			}
			return;
		}
		
		if (world.getGameRuleValue("doMobSpawning").equals("false")) {
			event.setCancelled(true);
			return;
		}
		
		if (entity instanceof Wolf) {
			entity.setCustomName("Wolf");
			return;
		}
		
		if (entity instanceof Rabbit || entity instanceof Sheep) {
			Biome biome = loc.getBlock().getBiome();
			
			if (biome.equals(Biome.FOREST) || biome.equals(Biome.FOREST_HILLS)) {
				Wolf wolf = loc.getWorld().spawn(loc, Wolf.class);
				wolf.setCustomName("Wolf");
				
				event.setCancelled(true);
			}
			return;
		}
	}
	
	@EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
    	Entity entity = event.getEntity();
    	
    	if (entity instanceof Ghast && game.ghastDropGold()) {
    		for (ItemStack drop : event.getDrops()) {
    			if (drop.getType() == Material.GHAST_TEAR) {
    				drop.setType(Material.GOLD_INGOT);
    			}
    		}
    		return;
        }
    	
    	if (entity instanceof Creeper) {
    		Creeper creeper = (Creeper) entity;
    		
    		if (creeper.isPowered()) {
    			event.setDroppedExp(0);
    		}
    		return;
    	}
    	
    	if (game.isRecordedRound()) {
    		return;
    	}
        	
    	ItemStack potion = new ItemStack (Material.POTION, 1, (short) 8261);
    	List<ItemStack> drops = event.getDrops();
    	
    	if (!(entity instanceof Witch)) {
    		return;
        }
    	
    	Witch witch = (Witch) entity;
		Player killer = witch.getKiller();
		
		if (killer == null) {
			return;
		}
		
		drops.remove(potion);
		
		if (killer.hasPotionEffect(PotionEffectType.POISON)) {
			drops.add(potion);
		} 
		else {
			Random rand = new Random();
			
			if (rand.nextInt(99) < 30) {
				drops.add(potion);
			}
		}
	}
	
	@EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
		RegainReason reason = event.getRegainReason();
		Entity entity = event.getEntity();
		
        if (entity instanceof Player) {
            if (reason == RegainReason.REGEN || reason == RegainReason.SATIATED) {
                event.setCancelled(true);
            }
        }
    }
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		
		if (State.isState(State.SCATTER)) {
			event.setCancelled(true);
			return;
		}
    	
		if (entity instanceof Player || entity instanceof Minecart || entity instanceof ArmorStand || entity instanceof Painting || entity instanceof ItemFrame) {
			if (entity.getWorld().getName().equals("lobby")) {
	    		event.setCancelled(true);
	    	}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity attacker = event.getDamager();
		
		if (attacker instanceof EnderPearl) {
			if (!game.pearlDamage()) {
				event.setCancelled(true);
			}
		}
	}
	
	/**
	 * @author D4mnX
	 */
	@EventHandler
    public void onStrengthDamage(EntityDamageByEntityEvent event) {
		Entity attacker = event.getDamager();
		
        if (attacker instanceof Player) {
        	Player damager = (Player) attacker;
        	
        	if (game.nerfedStrength()) {
		        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			        int amplifier = 0;
			        
		        	for (PotionEffect effect : damager.getActivePotionEffects()) {
			            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
			                amplifier = effect.getAmplifier() + 1;
			                break;
			            }
			        }

			        double damageWOS = event.getDamage() / (1 + (amplifier * 1.3));
			        double damageWNS = damageWOS + amplifier * 3;

			        event.setDamage(damageWNS);
		        }
	        }
		}
    }
	
	@EventHandler(ignoreCancelled = true)
	public void onShot(EntityDamageByEntityEvent event) {
		Entity attacked = event.getEntity();
		Entity attacker = event.getDamager();
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
    	if (game.isRecordedRound() || scen.getScenario("TeamHealth").isEnabled() || scen.getScenario("Paranoia").isEnabled()) {
			return;
		}
    	
		if (attacked instanceof Player && attacker instanceof Arrow) {
			final Player player = (Player) attacked;
			final Arrow arrow = (Arrow) attacker;
			
			if (arrow.getShooter() instanceof Player) {
				new BukkitRunnable() {
					public void run() {
						Player killer = (Player) arrow.getShooter();
						
						double health = player.getHealth();
						String percent = NumberUtils.makePercent(health);
						
						if (health > 0.0000) {
							killer.sendMessage(Main.PREFIX + "§6" + player.getName() + " §7is now at §a" + percent + "%");
						}
					}
				}.runTaskLater(Main.plugin, 1);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLongshot(EntityDamageByEntityEvent event) {
		Entity attacked = event.getEntity();
		Entity attacker = event.getDamager();
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
    	if (game.isRecordedRound() || scen.getScenario("RewardingLongshots").isEnabled()) {
			return;
		}
    	
		if (attacked instanceof Player && attacker instanceof Arrow) {
			Player player = (Player) attacked;
			Arrow arrow = (Arrow) attacker;
			
			if (arrow.getShooter() instanceof Player) {
				Player killer = (Player) arrow.getShooter();
				double distance = killer.getLocation().distance(player.getLocation());
				
				if (distance >= 50) {
					PlayerUtils.broadcast(Main.PREFIX + "§6" + killer.getName() + " §7got a longshot of §6" + NumberUtils.convertDouble(distance) + " §7blocks.");
				}
			}
		}
	}
}