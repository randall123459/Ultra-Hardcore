package com.leontg77.uhc.listeners;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Fireworks;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Parkour;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.UBL;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Rank;
import com.leontg77.uhc.cmds.SpreadCommand;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.BlockUtils;
import com.leontg77.uhc.utils.DateUtils;
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
	private Game game = Game.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);
		
		User user = User.get(player);
		user.getFile().set("username", player.getName());
		user.getFile().set("uuid", player.getUniqueId().toString());
		user.saveFile();
		
		// They think they can relog to have no damage ticks, HAHA!!! :D
		player.setNoDamageTicks(0);
		
		Spectator.getManager().hideSpectators(player);
		PlayerUtils.setTabList(player);
		
		if (!TeamCommand.invites.containsKey(player)) {
			TeamCommand.invites.put(player, new ArrayList<Player>());
		}
		
		if (Spectator.getManager().isSpectating(player)) {
			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			
			Spectator.getManager().enableSpecmode(player, true);
		}
		
		if (State.isState(State.INGAME) && !player.isWhitelisted() && !Spectator.getManager().isSpectating(player)) {
			player.sendMessage(Main.prefix() + "You joined a game that you didn't play from the start.");

			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			player.setExp(0);
			
			Spectator.getManager().enableSpecmode(player, true);
		}
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.canSee(player)) {
				online.sendMessage("§8[§a+§8] §7" + player.getName() + " has joined.");
			}
		}
		
		Bukkit.getLogger().info("§8[§a+§8] §7" + player.getName() + " has joined.");
		
		if (!Spectator.getManager().isSpectating(player)) {
			if (user.isNew()) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GOLD + player.getName() + " §7just joined for the first time.");
				
				File f = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
				PlayerUtils.broadcast(Main.prefix() + "The server has now §a" + f.listFiles().length + "§7 unique joins.");
			}
		}
		
		if (!game.isRR()) {
			player.sendMessage("§8» ----------[ §4§lArctic UHC §8]---------- «");
			if (GameUtils.getTeamSize().startsWith("No")) {
				player.sendMessage("§8» §c No games running");
			} 
			else if (GameUtils.getTeamSize().startsWith("Open")) {
				player.sendMessage("§8» §7 Open PvP, use §a/a §7to join.");
			} 
			else {
				player.sendMessage("§8» §7 Host: §6" + Settings.getInstance().getConfig().getString("game.host"));
				player.sendMessage("§8» §7 Gamemode: §6" + GameUtils.getTeamSize() + Settings.getInstance().getConfig().getString("game.scenarios"));
			}
			player.sendMessage("§8» --------------------------------- «");
		}
		
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
		Player player = event.getPlayer();
		event.setQuitMessage(null);
		
		PlayerUtils.handleLeavePermissions(player);
		
		if (Arena.getManager().isEnabled() && Arena.getManager().hasPlayer(player)) {
			Arena.getManager().removePlayer(player, false);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		}
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.canSee(player)) {
				online.sendMessage("§8[§c-§8] §7" + player.getName() + " has left.");
			}
		}
		
		Bukkit.getLogger().info("§8[§c-§8] §7" + player.getName() + " has left.");
		
		if (Main.msg.containsKey(player)) {
			Main.msg.remove(player);
		}
		
		HashSet<CommandSender> temp = new HashSet<CommandSender>();
		
		for (CommandSender key : Main.msg.keySet()) {
			temp.add(key);
		}
		
		for (CommandSender key : temp) {
			if (Main.msg.get(key).equals(player)) {
				Main.msg.remove(key);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity().getPlayer();
		
		new BukkitRunnable() {
			public void run() {
				player.spigot().respawn();
			}
		}.runTaskLater(Main.plugin, 20);
		
		if (Arena.getManager().isEnabled()) {
			Team team = Teams.getManager().getTeam(player);
			User user = User.get(player);
			
	    	event.setDeathMessage(null);
			event.setDroppedExp(0);
		
			Arena.getManager().removePlayer(player, true);
			
			if (!Bukkit.hasWhitelist()) {
				user.increaseStat("arenadeaths");
			}
	    	
			ItemStack skull = new ItemStack(Material.GOLDEN_APPLE);
			ItemMeta skullMeta = skull.getItemMeta();
			skullMeta.setDisplayName("§6Golden Head");
			skullMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood.", ChatColor.AQUA + "Made from the head of: " + player.getName()));
			skull.setItemMeta(skullMeta);
			
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
			event.getDrops().add(new ItemStack(Material.ARROW, 32));
			event.getDrops().add(skull);
			
			Player killer = player.getKiller();
			
			if (killer == null) {
				player.sendMessage(Main.prefix() + "You were killed by PvE.");
				Arena.getManager().killstreak.put(player, 0); 

				if (Arena.getManager().killstreak.containsKey(player) && Arena.getManager().killstreak.get(player) > 4) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getName() + "'s §7killstreak of " + Arena.getManager().killstreak.get(player) + " was shut down by PvE");
				}
				
				for (Player p : Arena.getManager().getPlayers()) {
					p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by PvE");
				}
				
				Arena.getManager().setScore("§8» §a§lPvE", Arena.getManager().getScore("§8» §a§lPvE") + 1);
				Arena.getManager().resetScore(player.getName());
				return;
			}
			
			if (Arena.getManager().killstreak.containsKey(player) && Arena.getManager().killstreak.get(player) > 4) {
				PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + player.getName() + "'s §7killstreak of " + Arena.getManager().killstreak.get(player) + " was shut down by §a" + player.getKiller().getName());
			}
			
			player.sendMessage(Main.prefix() + "You were killed by §a" + killer.getName() + "§7.");
			Arena.getManager().killstreak.put(player, 0);
			
			Team kTeam = Teams.getManager().getTeam(killer);
			killer.setLevel(killer.getLevel() + 1);
			User uKiller = User.get(killer);
			
			if (!Bukkit.hasWhitelist()) {
				uKiller.increaseStat("arenakills");
			}
			
			for (Player p : Arena.getManager().getPlayers()) {
				p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by " + (kTeam == null ? "§f" : kTeam.getPrefix()) + killer.getName());
			}   

			Arena.getManager().setScore(killer.getName(), Arena.getManager().getScore(killer.getName()) + 1);
		    Arena.getManager().resetScore(player.getName());
			
			if (Arena.getManager().killstreak.containsKey(killer)) {
				Arena.getManager().killstreak.put(killer, Arena.getManager().killstreak.get(killer) + 1);
			} else {
				Arena.getManager().killstreak.put(killer, 1);
			}
			
			if (Arena.getManager().killstreak.containsKey(killer)) {
				int killstreak = Arena.getManager().killstreak.get(killer);
				
				if (killstreak == 5) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 5 killstreak");
				}

				if (killstreak == 10) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 10 killstreak");
				}

				if (killstreak == 15) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 15 killstreak");
				}

				if (killstreak == 20) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 20 killstreak");
				}

				if (killstreak == 30) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 30 killstreak");
				}
				
				if (killstreak == 50) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 50 killstreak");
				}

				if (killstreak == 75) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 75 killstreak");
				}
				
				if (killstreak == 100) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GREEN + killer.getName() + " §7is now on a 100 killstreak");
				}
			}
		} else {
			User user = User.get(player);
			player.setWhitelisted(false);
			
			if (State.isState(State.INGAME) && !game.isRR()) {
				user.increaseStat("deaths");
			}
			
			if (game.deathLightning()) {
			    player.getWorld().strikeLightningEffect(player.getLocation());
			}

		    if (game.goldenHeads() && !(player.getWorld().getName().equals("lobby") || player.getWorld().getName().equals("arena"))) {
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
			
			final Player killer = player.getKiller();

			if (killer == null) {
				if (!game.isRR() && State.isState(State.INGAME)) {
			        Scoreboards.getManager().setScore("§8» §a§lPvE", Scoreboards.getManager().getScore("§8» §a§lPvE") + 1);
					Scoreboards.getManager().resetScore(player.getName());
				}
				
				event.setDeathMessage("§8» §f" + event.getDeathMessage());
				return;
			}
			
	        Scoreboards.getManager().setScore(killer.getName(), Scoreboards.getManager().getScore(killer.getName()) + 1);
			
			if (game.isRR()) {
				return;
			}
			
			if (event.getDeathMessage().contains(killer.getName()) && killer.getItemInHand() != null && killer.getItemInHand().hasItemMeta() && killer.getItemInHand().getItemMeta().hasDisplayName() && (event.getDeathMessage().contains("slain") || event.getDeathMessage().contains("shot"))) {
				ComponentBuilder builder = new ComponentBuilder("§8» §r" + event.getDeathMessage().replace("[" + killer.getItemInHand().getItemMeta().getDisplayName() + "]", ""));
				
				if (killer.getItemInHand().getEnchantments().isEmpty()) {
					builder.append("[" + killer.getItemInHand().getItemMeta().getDisplayName() + "]");
				} else {
					StringBuilder colored = new StringBuilder();
					
					for (String s : killer.getItemInHand().getItemMeta().getDisplayName().split(" ")) {
						colored.append(ChatColor.AQUA + s).append(" ");
					}
					
					builder.append("§b[" + colored.toString().trim() + "§b]");
				}
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] { new TextComponent(NameUtils.convertItemStackToJson(killer.getItemInHand())) }));
				
				final BaseComponent[] result = builder.create();
				
				new BukkitRunnable() {
					public void run() {
						for (Player online : PlayerUtils.getPlayers()) {
							online.spigot().sendMessage(result);
						}
					}
				}.runTaskLater(Main.plugin, 1);
				
				Bukkit.getLogger().info("§8» §f" + event.getDeathMessage());
				
				event.setDeathMessage(null);
			} else {
				event.setDeathMessage("§8» §f" + event.getDeathMessage());
			}

			if (State.isState(State.INGAME)) {
				User killerData = User.get(killer);
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
		
		if (!Arena.getManager().isEnabled() && !State.isState(State.LOBBY) && !game.isRR()) {
			player.sendMessage(Main.prefix() + "Thanks for playing our game, it really means a lot :)");
			player.sendMessage(Main.prefix() + "Follow us on twtter to know when our next games are: §a@ArcticUHC");
			
			if (player.hasPermission("uhc.prelist")) {
				player.sendMessage(Main.prefix() + "You will be put into spectator mode in 10 seconds.");
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						if (!State.isState(State.LOBBY) && player.isOnline() && !Spectator.getManager().isSpectating(player)) {
							for (Player online : PlayerUtils.getPlayers()) {
								online.showPlayer(player);
							}
							
							Spectator.getManager().enableSpecmode(player, true);
						}
					}
				}, 200);
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.hidePlayer(player);
				}
			} else {
				player.sendMessage(Main.prefix() + "You may stay as long as you want (You are vanished).");
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.hidePlayer(player);
				}
			}
			
			player.sendMessage(Main.prefix() + "Please do not spam, rage, spoil or be a bad sportsman.");
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		User user = User.get(player);

		event.setCancelled(true);
    	
    	if (game.isRR()) {
    		Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast((team != null ? team.getPrefix() + player.getName() : player.getName()) + "§8 » §f" + event.getMessage());
    		return;
    	}
		
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
    	
		if (user.getRank() == Rank.HOST) {
			if (user.isMuted()) {
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
					if (game.isMuted()) {
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
		else if (user.getRank() == Rank.TRIAL) {
			if (user.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§4§lTrial Host §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		}
		else if (user.getRank() == Rank.STAFF) {
			if (user.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§c§lStaff §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		}
		else if (user.getRank() == Rank.VIP) {
			if (game.isMuted()) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				return;
			}
			
			if (user.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§5§lVIP §8❘ §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		} 
		else {
			if (game.isMuted()) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				return;
			}
			
			if (user.isMuted()) {
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
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/fire")) {
			if (player.hasPermission("uhc.staff")) {
				Fireworks.getInstance().startFireworkShow();
			}
			event.setCancelled(true);
			return;
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
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		PlayerUtils.handlePermissions(player);
		
		if (player.getUniqueId().toString().equals("3be33527-be7e-4eb2-8b66-5b76d3d7ecdc")) {
			if (game.isRR()) {
				return;
			}
			
			if (Settings.getInstance().getConfig().getString("game.host").equalsIgnoreCase("PolarBlunk")) {
				event.disallow(Result.KICK_OTHER, "Connection timed out");
				return;
			}
		}
		
		if (event.getResult() == Result.KICK_BANNED) {
			if (Bukkit.getBanList(Type.NAME).getBanEntry(player.getName()) != null) {
				if (player.hasPermission("uhc.staff")) {
					Bukkit.getBanList(Type.NAME).pardon(player.getName());
					event.allow();
					return;
				}

				BanEntry ban = Bukkit.getBanList(Type.NAME).getBanEntry(player.getName());
				PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + player.getName() + " §7tried to join while being " + (ban.getExpiration() == null ? "banned" : "temp-banned") + " for:§c " + ban.getReason(), "uhc.staff");
				
				event.setKickMessage(
				"§8» §7You have been §4" + (ban.getExpiration() == null ? "banned" : "temp-banned") + " §7from §6Arctic UHC §8«" +
				"\n" + 
				"\n§cReason §8» §7" + ban.getReason() +
				"\n§cBanned by §8» §7" + ban.getSource() + (ban.getExpiration() == null ? "" : "" +
				"\n§cExpires in §8» §7" + DateUtils.formatDateDiff(ban.getExpiration().getTime())) +
				"\n" +
				"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				);
			}
			else if (Bukkit.getBanList(Type.IP).getBanEntry(player.getAddress().getAddress().getHostAddress()) != null) {
				if (player.hasPermission("uhc.staff")) {
					Bukkit.getBanList(Type.IP).pardon(player.getAddress().getAddress().getHostAddress());
					event.allow();
					return;
				}

				BanEntry ban = Bukkit.getBanList(Type.IP).getBanEntry(player.getAddress().getAddress().getHostAddress());
				PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + player.getName() + " §7tried to join while being IP-banned for:§c " + ban.getReason(), "uhc.staff");
				
				event.setKickMessage(
				"§8» §7You have been §4IP " + (ban.getExpiration() == null ? "banned" : "temp-banned") + " §7from §6Arctic UHC §8«" +
				"\n" + 
				"\n§cReason §8» §7" + ban.getReason() +
				"\n§cBanned by §8» §7" + ban.getSource() + (ban.getExpiration() == null ? "" : "" +
				"\n§cExpires in §8» §7" + DateUtils.formatDateDiff(ban.getExpiration().getTime())) +
				"\n" +
				"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				);
			}
			else {
				event.allow();
			}
			return;
		}
		
		if (event.getResult() == Result.KICK_WHITELIST) {
			if (game.isRR()) {
				event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThere are no games running");
				return;
			}
			
			if (player.hasPermission("uhc.prelist")) {
				event.allow();
				return;
			}
			
			if (GameUtils.getTeamSize().startsWith("No")) {
				event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThere are no games running");
			}
			else if (GameUtils.getTeamSize().startsWith("Open")) {
				Bukkit.setWhitelist(false);
				event.allow();
				return;
			} 
			else {
				if (State.isState(State.LOBBY)) {
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThe game has not opened yet,\n§ccheck the post for open time.\n\n§7Match post: §a" + settings.getConfig().getString("matchpost", "redd.it"));
				}
				else {
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThe game has already started");
				}
			}
			return;
		}
		
		if (PlayerUtils.getPlayers().size() >= settings.getConfig().getInt("maxplayers", 80)) {
			if (game.isRR()) {
				return;
			}
			
			if (player.isWhitelisted()) {
				event.allow();
				return;
			}
			
			if (player.hasPermission("uhc.staff")) {
				if (State.isState(State.INGAME)) {
					event.allow();
				} else {
					event.disallow(Result.KICK_FULL, "§8» §7The server is currently full, try again later §8«");
				}
				return;
			} 

			event.disallow(Result.KICK_FULL, "§8» §7The server is currently full, try again later §8«");
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
		String scenarios = ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("game.scenarios", "games scheduled"));
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
        
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && State.isState(State.INGAME) && Runnables.pvp > 0 && !game.isRR()) {
        	if (event.getItem() != null && (event.getItem().getType() == Material.LAVA_BUCKET || event.getItem().getType() == Material.FLINT_AND_STEEL || event.getItem().getType() == Material.CACTUS)) {
        		for (Entity nearby : PlayerUtils.getNearby(event.getClickedBlock().getLocation(), 5)) {
        			if (nearby instanceof Player) {
        				if (nearby == player) {
        					continue;
        				}
        				
        				if (Spectator.getManager().isSpectating((Player) nearby)) {
        					continue;
        				}
        				
        				PlayerUtils.broadcast(Main.prefix() + "§c" + player.getName() + " §7attempted to iPvP §c" + nearby.getName(), "uhc.staff");
        				player.sendMessage(Main.prefix() + "iPvP is not allowed before PvP.");
        				player.sendMessage(Main.prefix() + "Stop iPvPing now or staff will take action.");
        				event.setCancelled(true);
        			}
        		}
        	}
        	return;
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
		
		if (game.nether()) {
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
            if (BlockUtils.hasBlockNearby(Material.PORTAL, event.getFrom())) {
            	player.sendMessage(Main.prefix() + "The nether is disabled.");
            }
		}
		
		if (game.theEnd()) {
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
            if (BlockUtils.hasBlockNearby(Material.ENDER_PORTAL, event.getFrom())) {
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
				
				if (!game.goldenHeads()) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
			
			if (item.getDurability() == 1) {
				if (!game.notchApples()) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
		}
		
		if (item != null && item.getType() == Material.SPECKLED_MELON) {
			if (game.goldenMelonNeedsIngots()) {
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
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE && !game.absorption()) {
			player.removePotionEffect(PotionEffectType.ABSORPTION);
			
			Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
				public void run() {
					player.removePotionEffect(PotionEffectType.ABSORPTION);
				}
	        }, 1L);
		}
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equals("§6Golden Head")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25 * (game.goldenHeadsHeal() * 2), 1));
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