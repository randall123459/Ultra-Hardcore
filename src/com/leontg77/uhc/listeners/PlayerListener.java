package com.leontg77.uhc.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
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
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
import com.leontg77.uhc.Game;
import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Runnables;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Rank;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.managers.Fireworks;
import com.leontg77.uhc.managers.Parkour;
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
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity().getPlayer();
		
		new BukkitRunnable() {
			public void run() {
				player.spigot().respawn();
			}
		}.runTaskLater(Main.plugin, 18);
		
		if (Arena.getInstance().isEnabled()) {
			Team team = Teams.getManager().getTeam(player);
			User user = User.get(player);
			
	    	event.setDeathMessage(null);
			event.setDroppedExp(0);
		
			Arena.getInstance().removePlayer(player, true);
			
			if (!Bukkit.hasWhitelist()) {
				user.increaseStat(Stat.ARENADEATHS);
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
				Arena.getInstance().killstreak.put(player, 0); 

				if (Arena.getInstance().killstreak.containsKey(player) && Arena.getInstance().killstreak.get(player) > 4) {
					PlayerUtils.broadcast(Main.prefix() + "§6" + player.getName() + "'s §7killstreak of §a" + Arena.getInstance().killstreak.get(player) + " §7was shut down by PvE");
				}
				
				for (Player p : Arena.getInstance().getPlayers()) {
					p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by PvE");
				}
				
				Arena.getInstance().setScore("§8» §a§lPvE", Arena.getInstance().getScore("§8» §a§lPvE") + 1);
				Arena.getInstance().resetScore(player.getName());
				return;
			}
			
			if (Arena.getInstance().killstreak.containsKey(player) && Arena.getInstance().killstreak.get(player) > 4) {
				PlayerUtils.broadcast(Main.prefix() + "§6" + player.getName() + "'s §7killstreak of §a" + Arena.getInstance().killstreak.get(player) + " §7was shut down by §6" + player.getKiller().getName());
			}
			
			player.sendMessage(Main.prefix() + "You were killed by §a" + killer.getName() + "§7.");
			Arena.getInstance().killstreak.put(player, 0);
			
			Team kTeam = Teams.getManager().getTeam(killer);
			killer.setLevel(killer.getLevel() + 1);
			User uKiller = User.get(killer);
			
			if (!Bukkit.hasWhitelist()) {
				uKiller.increaseStat(Stat.ARENAKILLS);
			}
			
			for (Player p : Arena.getInstance().getPlayers()) {
				p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas killed by " + (kTeam == null ? "§f" : kTeam.getPrefix()) + killer.getName());
			}   

			Arena.getInstance().setScore(killer.getName(), Arena.getInstance().getScore(killer.getName()) + 1);
		    Arena.getInstance().resetScore(player.getName());
			
			if (Arena.getInstance().killstreak.containsKey(killer)) {
				Arena.getInstance().killstreak.put(killer, Arena.getInstance().killstreak.get(killer) + 1);
			} else {
				Arena.getInstance().killstreak.put(killer, 1);
			}
			
			if (Arena.getInstance().killstreak.containsKey(killer)) {
				String killstreak = String.valueOf(Arena.getInstance().killstreak.get(killer));
				
				if (killstreak.endsWith("0") || killstreak.endsWith("5")) {
					PlayerUtils.broadcast(Main.prefix() + "§6" + killer.getName() + " §7is now on a §a" + killstreak + " §7killstreak!");
				}
			}
		} else {
			User user = User.get(player);
			player.setWhitelisted(false);
			
			if (State.isState(State.INGAME) && !game.isRR()) {
				user.increaseStat(Stat.DEATHS);
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
							Bukkit.getLogger().warning("Could not place player skull.");
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
			
			if (game.isRR()) {
				return;
			}
			
			if (State.isState(State.INGAME)) {
				User killerData = User.get(killer);
				killerData.increaseStat(Stat.KILLS);
				
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
		
		if (!Arena.getInstance().isEnabled() && !State.isState(State.LOBBY) && !game.isRR()) {
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
    	
		if (user.isMuted()) {
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date date = new Date();
			
			if (user.getUnmuteTime() == -1 || user.getUnmuteTime() > date.getTime()) {
				player.sendMessage(Main.prefix() + "You have been muted for: §a" + user.getMutedReason());
				
				if (user.getUnmuteTime() < 0) {
					player.sendMessage(Main.prefix() + "Your mute is permanent.");
				} else {
					player.sendMessage(Main.prefix() + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getUnmuteTime()));
				}
				return;
			} 
			else {
				user.setMuted(false, "N", null, "N");
			}
		}
		
		if (user.getRank() == Rank.HOST) {
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			
			if (player.getUniqueId().toString().equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				if (settings.getConfig().getString("game.host").equals(player.getName())) {
					PlayerUtils.broadcast("§3§lHost §8| §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				} 
				else {
					PlayerUtils.broadcast("§3§lCo Host §8| §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				}	
			}
			else {
				if (settings.getConfig().getString("game.host").equals(player.getName())) {
					PlayerUtils.broadcast("§4§lHost §8| §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				} 
				else {
					PlayerUtils.broadcast("§4§lCo Host §8| §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
				}	
			}
		}
		else if (user.getRank() == Rank.TRIAL) {
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§4§lTrial Host §8| §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		}
		else if (user.getRank() == Rank.STAFF) {
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§c§lStaff §8| §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + event.getMessage());
		}
		else if (user.getRank() == Rank.VIP) {
			if (game.isMuted()) {
				player.sendMessage(Main.prefix() + "The chat is currently muted.");
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("§5§lVIP §8| §f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "§8 » §f" + event.getMessage());
		} 
		else {
			if (game.isMuted()) {
				player.sendMessage(Main.prefix() + "The chat is currently muted.");
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
	public void onServerListPing(ServerListPingEvent event) {
		String scenarios = ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("game.scenarios", "games scheduled"));
		String host = Settings.getInstance().getConfig().getString("game.host", "None");
		String teamSize = GameUtils.getTeamSize();
		String state = GameUtils.getState();
		
		event.setMotd("§4§lArctic UHC §8- §71.8 §8- §a" + state + "§r \n" + 
		ChatColor.GOLD + teamSize + scenarios + (teamSize.startsWith("Open") || teamSize.startsWith("No") ? "" : "§8 - §4Host: §7" + host));

		int max = settings.getConfig().getInt("maxplayers", 80);
		event.setMaxPlayers(max);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (event.getTo().getWorld().getName().equals("lobby") && event.getTo().getY() <= 20) {
			Parkour parkour = Parkour.getManager();
			
			if (parkour.isParkouring(player)) {
				if (parkour.getCheckpoint(player) != null) {
					int checkpoint = parkour.getCheckpoint(player);
					player.teleport(parkour.getLocation(checkpoint));
					return;
				}
				
				player.teleport(parkour.getLocation(0));
				return;
			}
			
			player.teleport(Main.getSpawn());
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getReason().equals("disconnect.spam")) {
			event.setReason("§8» §7Kicked for spamming §8«");
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        
        if (player.getWorld().getName().equals("lobby") && !player.hasPermission("uhc.build") && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
        	event.setCancelled(true);
        }
        
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && State.isState(State.INGAME) && Runnables.pvp > 0 && !game.isRR()) {
        	if (event.getItem() != null && (event.getItem().getType() == Material.LAVA_BUCKET || event.getItem().getType() == Material.FLINT_AND_STEEL || event.getItem().getType() == Material.CACTUS || event.getItem().getType() == Material.SAND || event.getItem().getType() == Material.GRAVEL)) {
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
                    break;
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