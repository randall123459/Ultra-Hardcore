package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * PeriodOfResistance scenario class
 * 
 * @author LeonTG77
 */
public class PeriodOfResistance extends Scenario implements Listener, CommandExecutor {
	private boolean enabled = false;

	private DamageType current;
	
	public PeriodOfResistance() {
		super("PeriodOfResistance", "Every 10 minutes the resistance type changes, during the next 10 minutes you cannot take damage from what the type was.");
		Main main = Main.plugin;
		
		main.getCommand("status").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			new BukkitRunnable() {
				public void run() {
					if (!isEnabled()) {
						PlayerUtils.broadcast(prefix() + "You are no longer resistant to anything!");
						cancel();
						return;
					}
					
					current = DamageType.values()[new Random().nextInt(DamageType.values().length)];
					
					PlayerUtils.broadcast(prefix() + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will no longer hurt you!");
					
					new BukkitRunnable() {
						public void run() {
							PlayerUtils.broadcast(prefix() + "Changing resistant period in 5 minutes!");
							
							new BukkitRunnable() {
								public void run() {
									PlayerUtils.broadcast(prefix() + "Changing resistant period in 1 minute!");
									
									new BukkitRunnable() {
										public void run() {
											PlayerUtils.broadcast(prefix() + "Changing resistant period in 30 seconds!");
											
											new BukkitRunnable() {
												public void run() {
													PlayerUtils.broadcast(prefix() + "Changing resistant period in 10 seconds!");
													
													new BukkitRunnable() {
														public void run() {
															PlayerUtils.broadcast(prefix() + "Changing resistant period in 5 seconds!");
															
															new BukkitRunnable() {
																public void run() {
																	PlayerUtils.broadcast(prefix() + "Changing resistant period in 4 seconds!");
																	
																	new BukkitRunnable() {
																		public void run() {
																			PlayerUtils.broadcast(prefix() + "Changing resistant period in 3 seconds!");
																			
																			new BukkitRunnable() {
																				public void run() {
																					PlayerUtils.broadcast(prefix() + "Changing resistant period in 2 seconds!");
																					
																					new BukkitRunnable() {
																						public void run() {
																							PlayerUtils.broadcast(prefix() + "Changing resistant period in 1 second!");
																						}
																					}.runTaskLater(Main.plugin, 20);
																				}
																			}.runTaskLater(Main.plugin, 20);
																		}
																	}.runTaskLater(Main.plugin, 20);
																}
															}.runTaskLater(Main.plugin, 20);
														}
													}.runTaskLater(Main.plugin, 100);
												}
											}.runTaskLater(Main.plugin, 400);
										}
									}.runTaskLater(Main.plugin, 600);
								}
							}.runTaskLater(Main.plugin, 4800);
						}
					}.runTaskLater(Main.plugin, 6000);
				}
			}.runTaskTimer(Main.plugin, 0, 12000);
		} else {
			current = null;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	private String prefix() {
		return "§7[§6Resistance§7] §7";
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		DamageCause cause = event.getCause();
		
		if (cause == DamageCause.FALL && current == DamageType.FALLING) {
			event.setCancelled(true);
		}
		
		if (cause == DamageCause.POISON && current == DamageType.POISON) {
			event.setCancelled(true);
		}
		
		if (cause == DamageCause.SUFFOCATION && current == DamageType.SUFFOCATION) {
			event.setCancelled(true);
		}
		
		if ((cause == DamageCause.LAVA || cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK) && current == DamageType.LAVA_AND_FIRE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Entity damager = event.getDamager();
		
		if (damager instanceof Zombie && current == DamageType.ZOMBIES) {
			event.setCancelled(true);
		}
		
		if (damager instanceof Creeper && current == DamageType.CREEPERS) {
			event.setCancelled(true);
		}
		
		if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Skeleton && current == DamageType.SKELETONS) {
			event.setCancelled(true);
		}
		
		if ((damager instanceof Spider || damager instanceof CaveSpider) && current == DamageType.SPIDERS) {
			event.setCancelled(true);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(Main.prefix() + "\"PeriodOfResistance\" is not enabled.");
			return true;
		}
		
		sender.sendMessage(prefix() + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will not hurt you!");
		return true;
	}
	
	public enum DamageType {
		ZOMBIES, SPIDERS, FALLING, SKELETONS, CREEPERS, LAVA_AND_FIRE, SUFFOCATION, POISON;
	}
}