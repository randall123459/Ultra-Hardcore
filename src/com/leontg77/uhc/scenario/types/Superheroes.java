package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Superheroes scenario class
 * 
 * @author LeonTG77
 */
public class Superheroes extends Scenario implements Listener, CommandExecutor {
	private HashMap<String, HeroType> type = new HashMap<String, HeroType>();
	private boolean enabled = false;
	
	public Superheroes() {
		super("Superheroes", "Each player on the team receives a special \"super\" power such as jump boost, health boost, strength, speed, invis, or resistance.");
		Main main = Main.plugin;
		
		main.getCommand("slist").setExecutor(this);
		main.getCommand("super").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				HeroType type = getRandom(online);
				online.sendMessage(Main.prefix() + "You are the §a" + type.name().toLowerCase() + " §7type.");
				
				switch (type) {
				case HEALTH:
					online.setMaxHealth(online.getMaxHealth() + 20);
					online.setHealth(online.getMaxHealth());
					this.type.put(online.getName(), type);
					break;
				case INVIS:
					if (online.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						online.removePotionEffect(PotionEffectType.INVISIBILITY);
					}
					if (online.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
						online.removePotionEffect(PotionEffectType.WATER_BREATHING);
					}
					online.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1726272000, 0));
					online.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1726272000, 0));
					this.type.put(online.getName(), type);
					break;
				case JUMP:
					if (online.hasPotionEffect(PotionEffectType.JUMP)) {
						online.removePotionEffect(PotionEffectType.JUMP);
					}
					if (online.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
						online.removePotionEffect(PotionEffectType.FAST_DIGGING);
					}
					if (online.hasPotionEffect(PotionEffectType.SATURATION)) {
						online.removePotionEffect(PotionEffectType.SATURATION);
					}
					this.type.put(online.getName(), type);
					online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 3));
					online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
					online.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1726272000, 9));
					break;
				case RESISTANCE:
					if (online.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
						online.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					}
					if (online.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
						online.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
					}
					
					online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 1));
					online.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 0));
					this.type.put(online.getName(), type);
					break;
				case SPEED:
					if (online.hasPotionEffect(PotionEffectType.SPEED)) {
						online.removePotionEffect(PotionEffectType.SPEED);
					}
					if (online.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
						online.removePotionEffect(PotionEffectType.FAST_DIGGING);
					}
					online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 1));
					online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 1));
					this.type.put(online.getName(), type);
					break;
				case STRENGTH:
					if (online.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
						online.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					}
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
		if (event.getItem().getType() == Material.MILK_BUCKET) {
			event.getPlayer().sendMessage(Main.prefix() + "You cannot drink milk in superheros.");
			event.setCancelled(true);
			event.setItem(new ItemStack (Material.AIR));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.FALL) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players have superheroes effects.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("slist")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Superheroes\" is not enabled.");
				return true;
			}
			
			if (Spectator.getInstance().isSpectating(player)) {
				StringBuilder health = new StringBuilder("");
				StringBuilder invis = new StringBuilder("");
				StringBuilder jump = new StringBuilder("");
				StringBuilder resistance = new StringBuilder("");
				StringBuilder speed = new StringBuilder("");
				StringBuilder strength = new StringBuilder("");
				
				for (String key : type.keySet()) {
					if (type.get(key) == HeroType.HEALTH) {
						if (health.length() > 0) {
							health.append("§7, §a");
						}
						
						health.append(ChatColor.GREEN + key);
					} 
					else if (type.get(key) == HeroType.INVIS) {
						if (invis.length() > 0) {
							invis.append("§7, §a");
						}
						
						invis.append(ChatColor.GREEN + key);
					}
					else if (type.get(key) == HeroType.JUMP) {
						if (jump.length() > 0) {
							jump.append("§7, §a");
						}
						
						jump.append(ChatColor.GREEN + key);
					}
					else if (type.get(key) == HeroType.RESISTANCE) {
						if (resistance.length() > 0) {
							resistance.append("§7, §a");
						}
						
						resistance.append(ChatColor.GREEN + key);
					}
					else if (type.get(key) == HeroType.SPEED) {
						if (speed.length() > 0) {
							speed.append("§7, §a");
						}
						
						speed.append(ChatColor.GREEN + key);
					}
					else if (type.get(key) == HeroType.STRENGTH) {
						if (strength.length() > 0) {
							strength.append("§7, §a");
						}
						
						strength.append(ChatColor.GREEN + key);
					}
				}
				
				player.sendMessage(Main.prefix() + "List of types:");
				player.sendMessage("§8» §7Health: " + health.toString().trim());
				player.sendMessage("§8» §7Invis: " + invis.toString().trim());
				player.sendMessage("§8» §7Jump: " + jump.toString().trim());
				player.sendMessage("§8» §7Resistance: " + resistance.toString().trim());
				player.sendMessage("§8» §7Speed: " + speed.toString().trim());
				player.sendMessage("§8» §7Strength: " + strength.toString().trim());
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("super")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Superheroes\" is not enabled.");
				return true;
			}
			
			if (player.hasPermission("uhc.superheroes.admin")) {
				if (args.length == 0) {
					player.sendMessage(Main.prefix() + "Help for superheroes:");
					player.sendMessage(ChatColor.GRAY + "- §f/super set <player> - Add a random effect to a player.");
					player.sendMessage(ChatColor.GRAY + "- §f/super clear <player> - Clears the players effects.");
					player.sendMessage(ChatColor.GRAY + "- §f/super apply - Reapply the effects.");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("apply")) {
					player.sendMessage(Main.prefix() + "Effects reapplied.");
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
							online.setHealth(online.getMaxHealth());
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
						player.sendMessage(ChatColor.RED + "Usage: /super set <player>");
						return true;
					}
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					
					if (target == null) {
						player.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}
					
					HeroType type = getRandom(target);
					
					switch (type) {
					case HEALTH:
						target.setMaxHealth(target.getMaxHealth() + 20);
						target.setHealth(target.getMaxHealth());
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
					player.sendMessage(Main.prefix() + "Given §a" + target.getName() + " §7an random effect.");
					target.sendMessage(Main.prefix() + "You are the §a" + type.name().toLowerCase() + " §7type.");
				} else if (args[0].equalsIgnoreCase("clear")) {
					if (args.length == 1) {
						player.sendMessage(ChatColor.RED + "Usage: /super clear <player>");
						return true;
					}
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					
					if (target == null) {
						player.sendMessage(ChatColor.RED + "That player is not online.");
						return true;
					}
					
					target.setMaxHealth(20.0);
					for (PotionEffect effect : target.getActivePotionEffects()) {
						target.removePotionEffect(effect.getType());
					}
					player.sendMessage(Main.prefix() + "Effects of §a" + target.getName() + " §7has been cleared.");
					target.sendMessage(Main.prefix() + "Your effects has been cleared.");
				} else {
					player.sendMessage(Main.prefix() + "Help for superheroes:");
					player.sendMessage(ChatColor.GRAY + "- §f/super set <player> - Add a random effect to a player.");
					player.sendMessage(ChatColor.GRAY + "- §f/super clear <player> - Clears the players effects.");
					player.sendMessage(ChatColor.GRAY + "- §f/super apply - Reapply the effects.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
	
	private HeroType getRandom(Player player) {
		ArrayList<HeroType> list = new ArrayList<HeroType>();
		for (HeroType type : HeroType.values()) {
			if (player.getScoreboard().getEntryTeam(player.getName()) != null && type == HeroType.INVIS) {
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