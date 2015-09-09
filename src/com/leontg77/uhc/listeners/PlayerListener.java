package com.leontg77.uhc.listeners;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Data;
import com.leontg77.uhc.Data.Rank;
import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Parkour;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.UBL;
import com.leontg77.uhc.cmds.SpreadCommand;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.BlockUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.utils.RecipeUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class PlayerListener implements Listener {
	private Settings settings = Settings.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);
		
		Data data = Data.getFor(player);
		data.getFile().set("username", player.getName());
		data.saveFile();
		
		player.setNoDamageTicks(0);
		
		Spectator.getManager().hideSpectators(player);
		PlayerUtils.setTabList(player);
		
		if (Main.relog.containsKey(player.getName())) {
			Main.relog.get(player.getName()).cancel();
			Main.relog.remove(player.getName());
		}
		
		if (!TeamCommand.invites.containsKey(player)) {
			TeamCommand.invites.put(player, new ArrayList<Player>());
		}
		
		if (Spectator.getManager().isSpectating(player)) {
			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			
			Spectator.getManager().enableSpecmode(player, true);
		}
		
		if (State.isState(State.INGAME) && !player.isWhitelisted() && !Spectator.getManager().isSpectating(player)) {
			player.sendMessage(Main.prefix() + "You joined a game that you didn't play from the start (or you was idle for too long).");

			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			player.setExp(0);
			
			Spectator.getManager().enableSpecmode(player, true);
		}
		
		if (!Spectator.getManager().isSpectating(player)) {
			PlayerUtils.broadcast("§8[§a+§8] §7" + player.getName() + " has joined.");
			if (data.isNew()) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getName() + " §7just joined for the first time.");
				
				File f = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
				PlayerUtils.broadcast(Main.prefix() + "The server has now §a" + f.listFiles().length + "§7 unique joins.");
			}
		}
		
		player.sendMessage("§8---------------------------");
		player.sendMessage(" §8» §6Welcome to Arctic UHC");
		player.sendMessage("§8---------------------------");
		if (GameUtils.getTeamSize().startsWith("No")) {
			player.sendMessage(" §8» §cNo games scheduled");
		} else if (GameUtils.getTeamSize().startsWith("Open")) {
			player.sendMessage(" §8» §7Open PvP, use §a/a §7to join.");
		} else {
			player.sendMessage(" §8» §aHost: §7" + Settings.getInstance().getConfig().getString("game.host"));
			player.sendMessage(" §8» §aTeamsize: §7" + GameUtils.getTeamSize());
			player.sendMessage(" §8» §aGamemode: §7" + Settings.getInstance().getConfig().getString("game.scenarios"));
		}
		player.sendMessage("§8---------------------------");
		
		if (SpreadCommand.scatterLocs.containsKey(player.getName()) && SpreadCommand.isReady) {
			if (State.isState(State.SCATTER)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
			}
			player.teleport(SpreadCommand.scatterLocs.get(player.getName()));
			PlayerUtils.broadcast(Main.prefix() + "- §a" + player.getName() + " §7scheduled scatter.");
			SpreadCommand.scatterLocs.remove(player.getName());
		}
		
		if (!State.isState(State.SCATTER) && player.hasPotionEffect(PotionEffectType.JUMP) && player.hasPotionEffect(PotionEffectType.BLINDNESS) && player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && player.hasPotionEffect(PotionEffectType.SLOW)) {
			player.removePotionEffect(PotionEffectType.JUMP);
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			player.removePotionEffect(PotionEffectType.SLOW);	
		}
		
		if (State.isState(State.LOBBY)) {
			World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
			double x = settings.getData().getDouble("spawn.x");
			double y = settings.getData().getDouble("spawn.y");
			double z = settings.getData().getDouble("spawn.z");
			float yaw = (float) settings.getData().getDouble("spawn.yaw");
			float pitch = (float) settings.getData().getDouble("spawn.pitch");
			final Location loc = new Location(w, x, y, z, yaw, pitch);
			player.teleport(loc);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		event.setQuitMessage(null);
		
		PlayerUtils.handleLeavePermissions(player);
		
		if (Arena.getManager().isEnabled() && Arena.getManager().hasPlayer(player)) {
			Arena.getManager().removePlayer(player, false);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		}
		
		if (!Spectator.getManager().isSpectating(player)) {
			PlayerUtils.broadcast("§8[§c-§8] §7" + player.getName() + " has left.");
			
			if (State.isState(State.INGAME)) {
				Main.relog.put(player.getName(), new BukkitRunnable() {
					public void run() {
						if (!player.isOnline()) {
							if (player.isWhitelisted()) {
								player.setWhitelisted(false);
								Scoreboards.getManager().resetScore(player.getName());
								Main.relog.remove(player.getName());
								
								for (ItemStack content : player.getInventory().getContents()) {
									if (content != null) {
										Item item = player.getWorld().dropItem(player.getLocation().add(0.5, 0.7, 0.5), content);
										item.setVelocity(new Vector(0, 0.2, 0));
									}
								}

								for (ItemStack armorContent : player.getInventory().getArmorContents()) {
									if (armorContent != null && armorContent.getType() != Material.AIR) {
										Item item = player.getWorld().dropItem(player.getLocation().add(0.5, 0.7, 0.5), armorContent);
										item.setVelocity(new Vector(0, 0.2, 0));
									}
								}
								
								ExperienceOrb exp = player.getWorld().spawn(player.getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
								exp.setExperience((int) player.getExp());
								exp.setVelocity(new Vector(0, 0.2, 0));

								player.getInventory().setArmorContents(null);
								player.getInventory().clear();
								player.setExp(0);
								
								Team team = Teams.getManager().getTeam(player);
								
								PlayerUtils.broadcast("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §ftook too long to come back");
								PlayerDeathEvent event = new PlayerDeathEvent(player, new ArrayList<ItemStack>(), 0, null);
								Bukkit.getServer().getPluginManager().callEvent(event);
							}
						}
					}
				});
				
				Main.relog.get(player.getName()).runTaskLater(Main.plugin, 12000);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity().getPlayer();
		
		if (Arena.getManager().isEnabled()) {
	    	event.setDeathMessage(null);
			Data data = Data.getFor(player);
			data.increaseStat("arenadeaths");
	 
	    	ItemStack skull = new ItemStack(Material.GOLDEN_APPLE);
			ItemMeta skullMeta = skull.getItemMeta();
			skullMeta.setDisplayName("§6Golden Head");
			skullMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood.", ChatColor.AQUA + "Made from the head of: " + player.getName()));
			skull.setItemMeta(skullMeta);
	    	
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
			event.getDrops().add(new ItemStack(Material.ARROW, 32));
			event.getDrops().add(skull);
			
			Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
				public void run() {
					Arena.getManager().removePlayer(player, true);
				}
			}, 20);
			
			if (player.getKiller() == null) {
				Arena.getManager().setScore("§8» §a§lPvE", Arena.getManager().getScore("§8» §a§lPvE") + 1);
				Arena.getManager().resetScore(player.getName());
				player.sendMessage(Main.prefix() + "You were killed by PvE.");
				
				Team team = Teams.getManager().getTeam(player);
				
				for (Player p : Arena.getManager().getPlayers()) {
					p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by PvE");
				}

				if (Arena.getManager().killstreak.containsKey(player) && Arena.getManager().killstreak.get(player) > 4) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getName() + "'s §7killstreak of " + Arena.getManager().killstreak.get(player) + " was shut down by PvE");
				}
				
				Arena.getManager().killstreak.put(player, 0);   
				return;
			}
			
			if (Arena.getManager().killstreak.containsKey(player) && Arena.getManager().killstreak.get(player) > 4) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getName() + "'s §7killstreak of " + Arena.getManager().killstreak.get(player) + " was shut down by §a" + player.getKiller().getName());
			}
			
			Arena.getManager().killstreak.put(player, 0);
			
			player.getKiller().setLevel(player.getKiller().getLevel() + 1);
			Data killerData = Data.getFor(player.getKiller());
			killerData.increaseStat("arenakills");
			player.sendMessage(Main.prefix() + "You were killed by §a" + player.getKiller().getName() + "§7.");
			
			Team team = Teams.getManager().getTeam(player);
			Team kTeam = Teams.getManager().getTeam(player.getKiller());
			
			for (Player p : Arena.getManager().getPlayers()) {
				p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by " + (kTeam == null ? "§f" : kTeam.getPrefix()) + player.getKiller().getName());
			}   

			Arena.getManager().setScore(player.getKiller().getName(), Arena.getManager().getScore(player.getKiller().getName()) + 1);
		    Arena.getManager().resetScore(player.getName());
			
			if (Arena.getManager().killstreak.containsKey(player.getKiller())) {
				Arena.getManager().killstreak.put(player.getKiller(), Arena.getManager().killstreak.get(player.getKiller()) + 1);
			} else {
				Arena.getManager().killstreak.put(player.getKiller(), 1);
			}
			
			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 5) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 5 killstreak");
			}

			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 10) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 10 killstreak");
			}

			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 15) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 15 killstreak");
			}

			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 20) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 20 killstreak");
			}

			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 30) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 30 killstreak");
			}
			
			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 50) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 50 killstreak");
			}

			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 75) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 75 killstreak");
			}
			
			if (Arena.getManager().killstreak.containsKey(player.getKiller()) && Arena.getManager().killstreak.get(player.getKiller()) == 100) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getKiller().getName() + " §7is now on a 100 killstreak");
			}
		} else {
			player.setWhitelisted(false);
			
			if (State.isState(State.INGAME)) {
				Data data = Data.getFor(player);
				data.increaseStat("deaths");
			}
			
			if (Main.deathlightning) {
			    player.getWorld().strikeLightningEffect(player.getLocation());
			}

		    if (Main.goldenheads) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						player.getLocation().getBlock().setType(Material.NETHER_FENCE);
						player.getLocation().add(0, 1, 0).getBlock().setType(Material.SKULL);
					        
						try {
					        Skull skull = (Skull) player.getLocation().add(0, 1, 0).getBlock().getState();
						    skull.setSkullType(SkullType.PLAYER);
						    skull.setOwner(player.getName());
						    skull.setRotation(BlockUtils.getBlockDirection(player.getLocation()));
						    skull.update();
						    
						    Block b = player.getLocation().add(0, 1, 0).getBlock();
						    b.setData((byte) 0x1, true);
						} catch (Exception e) {
							Bukkit.getLogger().warning(ChatColor.RED + "Could not place player skull.");
						}
					}
				}, 1L);
		    }

			if (player.getKiller() == null) {
		        Scoreboards.getManager().setScore("§8» §a§lPvE", Scoreboards.getManager().getScore("§8» §a§lPvE") + 1);
				Scoreboards.getManager().resetScore(player.getName());
				
				event.setDeathMessage("§8» §f" + event.getDeathMessage());
				return;
			}
			
			Player killer = player.getKiller();
			
			if (event.getDeathMessage().contains(killer.getName()) && killer.getItemInHand() != null && killer.getItemInHand().hasItemMeta() && killer.getItemInHand().getItemMeta().hasDisplayName()) {
				ComponentBuilder builder = new ComponentBuilder("§8» §r" + event.getDeathMessage().replace("[" + killer.getItemInHand().getItemMeta().getDisplayName() + "]", ""));
				
				if (killer.getItemInHand().getEnchantments().isEmpty()) {
					builder.append("[" + killer.getItemInHand().getItemMeta().getDisplayName() + "]");
				} else {
					builder.append("§b[" + killer.getItemInHand().getItemMeta().getDisplayName() + "§b]");
				}
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] { new TextComponent(NameUtils.convertItemStackToJson(killer.getItemInHand())) }));
				
				BaseComponent[] result = builder.create();
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.spigot().sendMessage(result);
				}
				
				Bukkit.getLogger().info("§8» §f" + event.getDeathMessage());
				
				event.setDeathMessage(null);
			} else {
				event.setDeathMessage("§8» §f" + event.getDeathMessage());
			}

			if (State.isState(State.INGAME)) {
				Data killerData = Data.getFor(killer);
				killerData.increaseStat("kills");
				
				if (Main.kills.containsKey(killer.getName())) {
					Main.kills.put(killer.getName(), Main.kills.get(killer.getName()) + 1);
				} else {
					Main.kills.put(killer.getName(), 1);
				}
			}
			
			Team team = Teams.getManager().getTeam(killer);
			
			if (team != null) {
				if (Main.teamKills.containsKey(team.getName())) {
					Main.teamKills.put(team.getName(), Main.teamKills.get(team.getName()) + 1);
				} else {
					Main.teamKills.put(team.getName(), 1);
				}
			}

	        Scoreboards.getManager().setScore(killer.getName(), Scoreboards.getManager().getScore(killer.getName()) + 1);
			Scoreboards.getManager().resetScore(player.getName());
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		
		World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
		double x = settings.getData().getDouble("spawn.x");
		double y = settings.getData().getDouble("spawn.y");
		double z = settings.getData().getDouble("spawn.z");
		float yaw = (float) settings.getData().getDouble("spawn.yaw");
		float pitch = (float) settings.getData().getDouble("spawn.pitch");
		
		Location loc = new Location(w, x, y, z, yaw, pitch);
		event.setRespawnLocation(loc);
		
		Scoreboards.getManager().resetScore(player.getName());
		player.setMaxHealth(20);
		
		if (!Arena.getManager().isEnabled() && !State.isState(State.LOBBY)) {
			player.sendMessage(Main.prefix() + "Thanks for playing our game, it really means a lot :)");
			player.sendMessage(Main.prefix() + "Follow us on twtter to know when our next games are: §a@ArcticUHC");
			
			if (player.hasPermission("uhc.prelist")) {
				player.sendMessage(Main.prefix() + "You will be put into spectator mode in 15 seconds.");
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						if (!State.isState(State.LOBBY) && player.isOnline() && !Spectator.getManager().isSpectating(player)) {
							Spectator.getManager().enableSpecmode(player, true);
						}
					}
				}, 300);
			} else {
				player.sendMessage(Main.prefix() + "You have 45 seconds to say your goodbyes.");
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						if (!State.isState(State.LOBBY) && player.isOnline() && !Spectator.getManager().isSpectating(player)) {
							player.kickPlayer("§8» §7Thanks for playing! §8«");
						}
					}
				}, 900);
			}
			
			player.sendMessage(Main.prefix() + "Please do not spam, rage, spoil or be a bad sportsman.");
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Data data = Data.getFor(player);

		event.setCancelled(true);
		
		if (VoteCommand.running && (event.getMessage().equalsIgnoreCase("y") || event.getMessage().equalsIgnoreCase("n"))) {
			if (!State.isState(State.LOBBY) && player.getWorld().getName().equals("lobby")) {
				player.sendMessage(ChatColor.RED + "You cannot vote when you are dead.");
				return;
			}
			
			if (Spectator.getManager().isSpectating(player)) {
				player.sendMessage(ChatColor.RED + "You cannot vote as a spectator.");
				return;
			}
			
			if (VoteCommand.voted.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "You have already voted.");
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("y")) {
				player.sendMessage(Main.prefix() + "You voted yes.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.yes++;
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("n")) {
				player.sendMessage(Main.prefix() + "You voted no.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.no++;
			}
			return;
		}
    	
		if (data.getRank() == Rank.HOST) {
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			
			if (player.getUniqueId().toString().equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				if (settings.getConfig().getString("game.host").equals(player.getName())) {
					PlayerUtils.broadcast("§3§lHost §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				} else {
					PlayerUtils.broadcast("§3§lCo Host §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				}	
			} 
			else if (player.getUniqueId().toString().equals("3be33527-be7e-4eb2-8b66-5b76d3d7ecdc")) {
				if (settings.getConfig().getString("game.host").equals(player.getName())) {
					PlayerUtils.broadcast("§4§lHost §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				} else {
					if (Main.muted) {
						player.sendMessage(Main.prefix() + "All players are muted.");
						return;
					}
					
					PlayerUtils.broadcast("§4§lCo Host §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				}	
			}
			else {
				if (settings.getConfig().getString("game.host").equals(player.getName())) {
					PlayerUtils.broadcast("§4§lHost §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				} else {
					PlayerUtils.broadcast("§4§lCo Host §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				}	
			}
		}
		else if (data.getRank() == Rank.TRIAL) {
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§4§lTrial Host §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		}
		else if (data.getRank() == Rank.STAFF) {
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§c§lStaff §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		}
		else if (data.getRank() == Rank.VIP) {
			if (Main.muted) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				return;
			}
			
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§5§lVIP §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		} 
		else {
			if (Main.muted) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				return;
			}
			
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}
			
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast((team != null ? team.getPrefix() + player.getName() : player.getName()) + "§8 » §f" + event.getMessage());
		} 
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.hasPermission("uhc.commandspy") && (online.getGameMode() == GameMode.CREATIVE || Spectator.getManager().isSpectating(online)) && Spectator.getManager().hasCommandSpy(online) && online != player) {
				online.sendMessage(ChatColor.YELLOW + player.getName() + ": §7" + event.getMessage());
			}
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/me")) {
			player.sendMessage(Main.prefix() + "You can't use that command.");
			event.setCancelled(true);
			return;
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/bukkit:") && !player.hasPermission("uhc.admin")) {
			player.sendMessage(Main.prefix() + "You can't use that command.");
			event.setCancelled(true);
			return;
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/minecraft:") && !player.hasPermission("uhc.admin")) {
			player.sendMessage(Main.prefix() + "You can't use that command.");
			event.setCancelled(true);
			return;
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/kill")) {
			player.sendMessage(Main.prefix() + "You can't use that command.");
			event.setCancelled(true);
			return;
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/rl")) {
			if (!State.isState(State.LOBBY) && player.hasPermission("bukkit.command.reload")) {
				player.sendMessage(ChatColor.RED + "You might not want to reload when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to reload, do it in the console.");
				event.setCancelled(true);
			}
			return;
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/reload")) {
			if (!State.isState(State.LOBBY) && player.hasPermission("bukkit.command.reload")) {
				player.sendMessage(ChatColor.RED + "You might not want to reload when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to reload, do it in the console.");
				event.setCancelled(true);
			}
			return;
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/stop")) {
			if (!State.isState(State.LOBBY) && player.hasPermission("bukkit.command.stop")) {
				player.sendMessage(ChatColor.RED + "You might not want to stop when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to stop, do it in the console.");
				event.setCancelled(true);
			}
			return;
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/restart")) {
			if (!State.isState(State.LOBBY) && player.hasPermission("spigot.command.restart")) {
				player.sendMessage(ChatColor.RED + "You might not want to restart when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to restart, do it in the console.");
				event.setCancelled(true);
			}
			return;
		}
		
		// Tempoarly here until /setup is done.
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/border")) {
			event.setCancelled(true);
			
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			String[] args = ar.toArray(new String[ar.size()]);
			
			if (args.length == 0) {
				player.sendMessage(Main.prefix() + "The border is currently: §a" + (((int) player.getWorld().getWorldBorder().getSize()) + 1) + "x" + (((int) player.getWorld().getWorldBorder().getSize()) + 1));
				return;
			}
			
			if (player.hasPermission("uhc.border")) {
				int radius;
				
				try {
					radius = Integer.parseInt(args[0]);
				} catch (Exception e) {
					player.sendMessage(Main.prefix() + ChatColor.RED + "Invaild radius.");
					return;
				}

				player.getWorld().getWorldBorder().setWarningDistance(0);
				player.getWorld().getWorldBorder().setDamageAmount(0.1);
				player.getWorld().getWorldBorder().setSize(radius - 1);
				player.getWorld().getWorldBorder().setDamageBuffer(30);
				player.getWorld().getWorldBorder().setWarningTime(60);
				
				if (player.getWorld().getEnvironment() == Environment.NETHER) {
					player.getWorld().getWorldBorder().setCenter(0, 0);
				} else {
					player.getWorld().getWorldBorder().setCenter(0.5, 0.5);
				}
				
				PlayerUtils.broadcast(Main.prefix() + "Border setup with radius of " + radius + "x" + radius + ".");
			} else {
				player.sendMessage(Main.prefix() + "You can't use that command.");
			}
			return;
		}
		
		// Tempoarly here for testing.
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/sound")) {
			event.setCancelled(true);
			
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			String[] args = ar.toArray(new String[ar.size()]);
			
			if (player.hasPermission("uhc.sound")) {
				if (args.length == 0) {
					player.sendMessage(Main.prefix() + "Usage: /sound <sound> <pitch>");
					return;
				}
				
				int i = 1;
				
				try {
					i = Integer.parseInt(args[1]);
				} catch (Exception e) {
				} 
				
				player.playSound(player.getLocation(), args[0], 1, i);
			} else {
				player.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		
		// Tempoarly here for testing.
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/fire")) {
			event.setCancelled(true);
			
			if (player.hasPermission("uhc.sound")) {
				PlayerUtils.playWinnerFireworks();
			} else {
				player.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		
		// Tempoarly here for testing.
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/s")) {
			event.setCancelled(true);
			
			for (Scenario scen : ScenarioManager.getInstance().getScenarios()) {
				PlayerUtils.broadcast(scen.getName() + ": " + scen.isEnabled());
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		PlayerUtils.handlePermissions(player);
		
		if (event.getResult() == Result.KICK_BANNED) {
			if (player.hasPermission("uhc.staff")) {
				event.allow();
				return;
			}

			BanEntry ban = Bukkit.getBanList(Type.NAME).getBanEntry(player.getName());
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

			PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + player.getName() + " §7tried to join while being banned for:§c " + ban.getReason(), "uhc.staff");
			event.setKickMessage("§8» §7You have been " + (ban.getExpiration() == null ? "banned" : "temp-banned") + " from Arctic UHC\n\n§8» §cReason: §7" + ban.getReason() + (ban.getExpiration() == null ? "" : "\n§8» §cExpires: §7" + ban.getExpiration()) + "\n§8» §cBanned by: §7" + ban.getSource() + "\n\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC");
			return;
		}
		
		if (event.getResult() == Result.KICK_WHITELIST) {
			if (player.hasPermission("uhc.prelist")) {
				event.allow();
				return;
			}
			
			String game;
			
			if (GameUtils.getTeamSize().startsWith("No")) {
				game = "There are no games running.";
			} else if (GameUtils.getTeamSize().startsWith("Open")) {
				event.allow();
				return;
			} else {
				if (State.isState(State.LOBBY)) {
					game = "The game has not opened yet, check the post for open time!";
				} else {
					game = "The game has already started, you were too late!";
				}
			}
			
			event.setKickMessage("§8» §7You are not whitelisted.\n\n§c" + game);
			return;
		}
		
		if (PlayerUtils.getPlayers().size() >= settings.getConfig().getInt("maxplayers", 80)) {
			if (player.isWhitelisted()) {
				event.allow();
				return;
			}
			
			if (player.hasPermission("uhc.staff")) {
				if (State.isState(State.INGAME)) {
					event.allow();
				} else {
					event.disallow(Result.KICK_FULL, "§8» §7The server is currently full\n\n§cTry again later.");
				}
				return;
			} 
			
			event.disallow(Result.KICK_FULL, "§8» §7The server is currently full\n\n§cTry again later.");
		} else {
			event.allow();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (UBL.getManager().isBanned(event.getName(), event.getUniqueId())) {
            UBL.BanEntry banEntry = UBL.getManager().banlistByIGN.get(event.getName().toLowerCase());
        	PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + event.getName() + " §7tried to join while being UBL'ed for:§c " + banEntry.getData("Reason"), "uhc.staff");
        	
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, UBL.getManager().getBanMessage(event.getUniqueId()));
            return;
        }
		
        if (UBL.getManager().isBanned(event.getName())) {
            UBL.BanEntry banEntry = UBL.getManager().banlistByIGN.get(event.getName().toLowerCase());
        	PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + event.getName() + " §7tried to join while being UBL'ed for:§c " + banEntry.getData("Reason"), "uhc.staff");
        	
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, UBL.getManager().getBanMessage(event.getName()));
        }
    }
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		String state = GameUtils.getState();
		String teamSize = GameUtils.getTeamSize();
		String scenarios = ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("game.scenarios", "games scheduled."));
		String host = Settings.getInstance().getConfig().getString("game.host", "None");
		
		event.setMotd("§4§lArctic UHC §8- §71.8 §8- §a" + state + "§r \n" + 
		ChatColor.GOLD + teamSize + scenarios + (teamSize.startsWith("Open") || teamSize.startsWith("No") ? "" : "§8 - §4Host: §7" + host));

		int max = settings.getConfig().getInt("maxplayers", 80);
		event.setMaxPlayers(max);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		World world = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
		double x = settings.getData().getDouble("spawn.x");
		double y = settings.getData().getDouble("spawn.y");
		double z = settings.getData().getDouble("spawn.z");
		float yaw = (float) settings.getData().getDouble("spawn.yaw");
		float pitch = (float) settings.getData().getDouble("spawn.pitch");
		
		Location loc = new Location(world, x, y, z, yaw, pitch);
		
		if (event.getTo().getWorld().getName().equals("lobby") && event.getTo().getY() <= 20) {
			if (Parkour.getManager().parkourPlayers.contains(player)) {
				if (Parkour.getManager().checkpoint.containsKey(player) && Parkour.getManager().checkpoint.get(player) == 1) {
					player.teleport(Parkour.getManager().point1);
					return;
				}
				else if (Parkour.getManager().checkpoint.containsKey(player) && Parkour.getManager().checkpoint.get(player) == 2) {
					player.teleport(Parkour.getManager().point2);
					return;
				}
				else if (Parkour.getManager().checkpoint.containsKey(player) && Parkour.getManager().checkpoint.get(player) == 3) {
					player.teleport(Parkour.getManager().point3);
					return;
				}
				
				player.teleport(Parkour.getManager().spawn);
				return;
			}
			
			player.teleport(loc);
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getReason().equals("disconnect.spam")) {
			event.setReason("§cStop spamming, please.");
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        
        if (player.getWorld().getName().equals("lobby") && !player.hasPermission("uhc.build") && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
        	event.setCancelled(true);
        }
        
        if (event.getAction() == Action.PHYSICAL && player.getWorld().getName().equals("lobby")) {
        	event.setCancelled(true);
        	return;
        }
        
		if (Spectator.getManager().isSpectating(player)) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				InvGUI.getManager().openSelector(player);
			} else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				ArrayList<Player> players = new ArrayList<Player>();
				for (Player online : PlayerUtils.getPlayers()) {
					if (!Spectator.getManager().isSpectating(online) && !online.getWorld().getName().equals("lobby")) {
						players.add(online);
					}
				}
				
				if (players.size() > 0) {
					Player target = players.get(new Random().nextInt(players.size()));
					player.teleport(target.getLocation());
					player.sendMessage(Main.prefix() + "You teleported to §a" + target.getName() + "§7.");
				} else {
					player.sendMessage(Main.prefix() + "No players to teleport to.");
				}
			}
		}
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ArmorStand) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getRightClicked() instanceof Player) {
			Player player = (Player) event.getPlayer();
	    	Player clicked = (Player) event.getRightClicked();
					
			if (Spectator.getManager().isSpectating(player)) {
				if (Spectator.getManager().isSpectating(clicked)) {
					return;
				}
				
				InvGUI.getManager().openInv(player, clicked);
			}
		}
    }

	@EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
		TravelAgent travel = event.getPortalTravelAgent();
		Player player = event.getPlayer();
		Location from = event.getFrom();
		
		if (Main.nether) {
			String fromName = event.getFrom().getWorld().getName();
	        String targetName;
	        
	        if (event.getFrom().getWorld().getEnvironment().equals(Environment.NETHER)) {
	            if (!fromName.endsWith("_nether")) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the overworld, contact the staff now.");
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 7);
	        } else if (event.getFrom().getWorld().getEnvironment().equals(Environment.NORMAL)) {
	            if (!BlockUtils.hasBlockNearby(Material.PORTAL, from)) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the nether, contact the staff now.");
	                return;
	            }

	            targetName = fromName + "_nether";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.prefix() + "The nether has not been created.");
	            return;
	        }

	        Location to = (world.getName().endsWith("_nether") ? new Location(world, (from.getX() / 8), (from.getY() / 8), (from.getZ() / 8), from.getYaw(), from.getPitch()) : new Location(world, (from.getX() * 8), (from.getY() * 8), (from.getZ() * 8), from.getYaw(), from.getPitch()));
	        to = travel.findOrCreate(to);

	        if (to != null) {
	            event.setTo(to);
	        } else {
            	player.sendMessage(Main.prefix() + "Could not teleport you, contact the staff now.");
	        }
		} else {
            if (!BlockUtils.hasBlockNearby(Material.PORTAL, event.getFrom())) {
            	player.sendMessage(Main.prefix() + "The nether is disabled.");
            }
		}
		
		if (Main.theend) {
			String fromName = event.getFrom().getWorld().getName();
	        String targetName;
	        
	        if (event.getFrom().getWorld().getEnvironment().equals(Environment.THE_END)) {
	        	World world = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
	    		double x = settings.getData().getDouble("spawn.x");
	    		double y = settings.getData().getDouble("spawn.y");
	    		double z = settings.getData().getDouble("spawn.z");
	    		float yaw = (float) settings.getData().getDouble("spawn.yaw");
	    		float pitch = (float) settings.getData().getDouble("spawn.pitch");
	    		
	            event.setTo(new Location(world, x, y, z, yaw, pitch));
	            return;
	        } else if (event.getFrom().getWorld().getEnvironment().equals(Environment.NORMAL)) {
	            if (!BlockUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the end, contact the staff now.");
	                return;
	            }
	            
	            targetName = fromName + "_end";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.prefix() + "The end has not been created.");
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
		} else {
            if (!BlockUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
            	player.sendMessage(Main.prefix() + "The end is disabled.");
            }
		}
    }
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		ItemStack item = event.getRecipe().getResult();
		
		/**
		 * @author Ghowden
		 */
        if (RecipeUtils.areSimilar(event.getRecipe(), Main.headRecipe)) {
            ItemMeta meta = item.getItemMeta();
            String name = "N/A";
          
            for (ItemStack items : event.getInventory().getContents()) {
                if (items.getType() == Material.SKULL_ITEM) {
                    SkullMeta skullMeta = (SkullMeta) items.getItemMeta();
                    name = skullMeta.getOwner();
                }
            }

            List<String> list = meta.getLore();
            list.add(ChatColor.AQUA + "Made from the head of: " + name);
            meta.setLore(list);
            item.setItemMeta(meta);
            event.getInventory().setResult(item);
        }
		
		if (item != null && item.getType() == Material.GOLDEN_APPLE) {
			if (item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
				if (ScenarioManager.getInstance().getScenario("VengefulSpirits").isEnabled()) {
					return;
				}
				
				if (!Main.goldenheads) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
			
			if (item.getDurability() == 1) {
				if (!Main.notchapples) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
		}
		
		if (item != null && item.getType() == Material.SPECKLED_MELON) {
			if (Main.harderCrafting) {
				if (event.getRecipe() instanceof ShapedRecipe) {
					if (((ShapedRecipe) event.getRecipe()).getIngredientMap().values().contains(new ItemStack (Material.GOLD_NUGGET))) {
						event.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}
			} else {
				if (event.getRecipe() instanceof ShapedRecipe) {
					if (((ShapedRecipe) event.getRecipe()).getIngredientMap().values().contains(new ItemStack (Material.GOLD_INGOT))) {
						event.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}
			}
		}
    }
	
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		Player player = event.getPlayer();
		
		if (Spectator.getManager().isSpectating(player)) {
			event.setCancelled(true);
			return;
		}
		
		if (!State.isState(State.INGAME)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final float before = player.getSaturation();

		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				float change = player.getSaturation() - before;
				player.setSaturation((float) (before + change * 2.5D));
			}
	    }, 1L);
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE && !Main.absorption) {
			player.removePotionEffect(PotionEffectType.ABSORPTION);
			
			Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
				public void run() {
					player.removePotionEffect(PotionEffectType.ABSORPTION);
				}
	        }, 1L);
		}
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equals("§6Golden Head")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25 * (Settings.getInstance().getConfig().getInt("feature.goldenheads.heal") * 2), 1));
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		
		if (player.getWorld().getName().equals("lobby")) {
			event.setCancelled(true);
			event.setFoodLevel(20);
			return;
		}
		
		if (event.getFoodLevel() < player.getFoodLevel()) {
			event.setCancelled(new Random().nextInt(100) < 66);
	    }
	}
}