package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class Superheroes extends Scenario implements Listener {
	private HashMap<String, HeroType> type = new HashMap<String, HeroType>();
	private boolean enabled = false;
	
	public Superheroes() {
		super("Superheroes", "Each player on the team receives a special \"super\" power such as jump boost, health boost, strength, speed, invis, or resistance.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				HeroType type = getRandom();
				online.sendMessage(Main.prefix() + "You are the §a" + type.name().toLowerCase() + " §7type.");
				
				switch (type) {
				case HEALTH:
					online.setMaxHealth(40.0);
					online.setHealth(40.0);
					this.type.put(online.getName(), type);
					break;
				case INVIS:
					online.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1726272000, 0));
					online.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1726272000, 0));
					this.type.put(online.getName(), type);
					break;
				case JUMP:
					this.type.put(online.getName(), type);
					online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 3));
					online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
					online.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1726272000, 9));
					break;
				case RESISTANCE:
					online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 1));
					online.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 0));
					this.type.put(online.getName(), type);
					break;
				case SPEED:
					online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 1));
					online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
					this.type.put(online.getName(), type);
					break;
				case STRENGTH:
					online.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0));
					this.type.put(online.getName(), type);
					break;
				default:
					break;
				}
			}
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.setMaxHealth(20.0);
				for (PotionEffect effect : online.getActivePotionEffects()) {
					online.removePotionEffect(effect.getType());
				}
			}
			type.clear();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.getItem().getType() == Material.MILK_BUCKET) {
			event.getPlayer().sendMessage(Main.prefix() + "You cannot drink milk in superheros.");
			event.setCancelled(true);
			event.setItem(new ItemStack (Material.AIR));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.getCause() != DamageCause.FALL) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player sender = event.getPlayer();
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/slist")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Superheroes is not enabled.");
				return;
			}
			
			if (sender.hasPermission("uhc.superheroes.admin")) {
				
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/super")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Superheroes is not enabled.");
				return;
			}
			
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			String[] args = ar.toArray(new String[ar.size()]);
			
			if (sender.hasPermission("uhc.superheroes.admin")) {
				if (args.length == 0) {
					sender.sendMessage(Main.prefix() + "Help for superheroes:");
					sender.sendMessage(ChatColor.GRAY + "- §f/super set <player> - Add a random effect to a player.");
					sender.sendMessage(ChatColor.GRAY + "- §f/super clear <player> - Clears the players effects.");
					sender.sendMessage(ChatColor.GRAY + "- §f/super apply - Reapply the effects.");
					return;
				}
				
				if (args[0].equalsIgnoreCase("apply")) {
					sender.sendMessage(Main.prefix() + "Effects reapplied.");
					for (Player online : PlayerUtils.getPlayers()) {
						for (PotionEffect effect : online.getActivePotionEffects()) {
							online.removePotionEffect(effect.getType());
						}
						online.setMaxHealth(20.0);
						
						if (type.get(online.getName()) == HeroType.RESISTANCE) {
							if (online.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
								online.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
							}
							if (online.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
								online.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
							}
							online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 1));
							online.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 0));
							continue;
						}
						
						if (type.get(online.getName()) == HeroType.STRENGTH) {
							if (online.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
								online.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
							}
							online.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0));
							continue;
						}
						
						if (type.get(online.getName()) == HeroType.JUMP) {
							if (online.hasPotionEffect(PotionEffectType.JUMP)) {
								online.removePotionEffect(PotionEffectType.JUMP);
							}
							if (online.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
								online.removePotionEffect(PotionEffectType.FAST_DIGGING);
							}
							if (online.hasPotionEffect(PotionEffectType.SATURATION)) {
								online.removePotionEffect(PotionEffectType.SATURATION);
							}
							online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 3));
							online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
							online.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1726272000, 9));
							continue;
						}
						
						if (type.get(online.getName()) == HeroType.INVIS) {
							if (online.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
								online.removePotionEffect(PotionEffectType.INVISIBILITY);
							}
							if (online.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
								online.removePotionEffect(PotionEffectType.WATER_BREATHING);
							}
							online.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1726272000, 0));
							online.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1726272000, 0));
							continue;
						}
						
						if (type.get(online.getName()) == HeroType.HEALTH) {
							online.setMaxHealth(40.0);
							online.setHealth(40.0);
							continue;
						}
						
						if (type.get(online.getName()) == HeroType.SPEED) {
							if (online.hasPotionEffect(PotionEffectType.SPEED)) {
								online.removePotionEffect(PotionEffectType.SPEED);
							}
							if (online.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
								online.removePotionEffect(PotionEffectType.FAST_DIGGING);
							}
							online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 1));
							online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
							continue;
						}
					}
				} else if (args[0].equalsIgnoreCase("set")) {
					if (args.length == 1) {
						sender.sendMessage(ChatColor.RED + "Usage: /super set <player>");
						return;
					}
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return;
					}
					
					HeroType type = getRandom();
					
					switch (type) {
					case HEALTH:
						target.setMaxHealth(40.0);
						target.setHealth(40.0);
						this.type.put(target.getName(), type);
						break;
					case INVIS:
						if (target.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							target.removePotionEffect(PotionEffectType.INVISIBILITY);
						}
						if (target.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
							target.removePotionEffect(PotionEffectType.WATER_BREATHING);
						}
						target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1726272000, 0));
						target.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1726272000, 0));
						this.type.put(target.getName(), type);
						break;
					case JUMP:
						if (target.hasPotionEffect(PotionEffectType.JUMP)) {
							target.removePotionEffect(PotionEffectType.JUMP);
						}
						if (target.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
							target.removePotionEffect(PotionEffectType.FAST_DIGGING);
						}
						if (target.hasPotionEffect(PotionEffectType.SATURATION)) {
							target.removePotionEffect(PotionEffectType.SATURATION);
						}
						this.type.put(target.getName(), type);
						target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 3));
						target.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
						target.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1726272000, 9));
						break;
					case RESISTANCE:
						if (target.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
							target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
						}
						if (target.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
							target.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
						}
						target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 1));
						target.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 0));
						this.type.put(target.getName(), type);
						break;
					case SPEED:
						if (target.hasPotionEffect(PotionEffectType.SPEED)) {
							target.removePotionEffect(PotionEffectType.SPEED);
						}
						if (target.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
							target.removePotionEffect(PotionEffectType.FAST_DIGGING);
						}
						target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 1));
						target.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
						this.type.put(target.getName(), type);
						break;
					case STRENGTH:
						if (target.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
							target.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
						}
						target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0));
						this.type.put(target.getName(), type);
						break;
					default:
						break;
					}
					sender.sendMessage(Main.prefix() + "Given §a" + target.getName() + " §7an random effect.");
					target.sendMessage(Main.prefix() + "You are the §a" + type.name().toLowerCase() + " §7type.");
				} else if (args[0].equalsIgnoreCase("clear")) {
					if (args.length == 1) {
						sender.sendMessage(ChatColor.RED + "Usage: /super clear <player>");
						return;
					}
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online.");
						return;
					}
					
					target.setMaxHealth(20.0);
					for (PotionEffect effect : target.getActivePotionEffects()) {
						target.removePotionEffect(effect.getType());
					}
					sender.sendMessage(Main.prefix() + "Effects of §a" + target.getName() + " §7has been cleared.");
					target.sendMessage(Main.prefix() + "Your effects has been cleared.");
				} else {
					sender.sendMessage(Main.prefix() + "Help for superheroes:");
					sender.sendMessage(ChatColor.GRAY + "- §f/super set <player> - Add a random effect to a player.");
					sender.sendMessage(ChatColor.GRAY + "- §f/super clear <player> - Clears the players effects.");
					sender.sendMessage(ChatColor.GRAY + "- §f/super apply - Reapply the effects.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
	}
	
	private HeroType getRandom() {
		ArrayList<HeroType> list = new ArrayList<HeroType>();
		for (HeroType type : HeroType.values()) {
			if (!Main.ffa && type == HeroType.INVIS) {
				continue;
			}
			
			list.add(type);
		}
		
		return list.get(new Random().nextInt(list.size()));
	}
	
	private enum HeroType {
		SPEED, STRENGTH, HEALTH, JUMP, INVIS, RESISTANCE;
	}
}