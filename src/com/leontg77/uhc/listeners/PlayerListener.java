package com.leontg77.uhc.listeners;

import java.util.ArrayList;
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
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
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
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Parkour;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Rank;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.inventory.InvGUI;
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
		final Player player = event.getEntity();
		Arena arena = Arena.getInstance();
		
		new BukkitRunnable() {
			public void run() {
				player.spigot().respawn();
			}
		}.runTaskLater(Main.plugin, 18);
		
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 

		Scoreboards board = Scoreboards.getInstance();
		User user = User.get(player);
		
		user.increaseStat(Stat.DEATHS);
		player.setWhitelisted(false);
		
		if (game.deathLightning()) {
		    player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		List<World> worlds = GameUtils.getGameWorlds();

	    if (game.goldenHeads() && worlds.contains(player.getWorld())) {
			new BukkitRunnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					player.getLocation().getBlock().setType(Material.NETHER_FENCE);
					player.getLocation().add(0, 1, 0).getBlock().setType(Material.SKULL);
				    
					Skull skull;
					
					try {
				        skull = (Skull) player.getLocation().add(0, 1, 0).getBlock().getState();
					} catch (Exception e) {
						Bukkit.getLogger().warning("Could not place player skull.");
						return;
					}
					
				    skull.setSkullType(SkullType.PLAYER);
				    skull.setOwner(player.getName());
				    skull.setRotation(BlockUtils.getBlockDirection(player.getLocation()));
				    skull.update();
				    
				    Block b = player.getLocation().add(0, 1, 0).getBlock();
				    b.setData((byte) 0x1, true);
				}
			}.runTaskLater(Main.plugin, 1);
	    }

		final String deathMessage = event.getDeathMessage();
		final Player killer = player.getKiller();

		if (killer == null) {
			if (worlds.contains(player.getWorld())) {
				board.setScore("§8» §a§lPvE", board.getScore("§8» §a§lPvE") + 1);
		        board.resetScore(player.getName());
			}
			
			if (deathMessage == null) {
				return;
			}
			
			new BukkitRunnable() {
				public void run() {
					for (Player online : PlayerUtils.getPlayers()) {
						online.sendMessage("§8» §f" + deathMessage);
					}
				}
			}.runTaskLater(Main.plugin, 1);
			
			Bukkit.getLogger().info("§8» §f" + deathMessage);
			event.setDeathMessage(null);
			return;
		}
		
		if (worlds.contains(killer.getWorld())) {
			board.setScore(killer.getName(), board.getScore(killer.getName()) + 1);
		}
		
		if (deathMessage != null) {
			if (deathMessage.contains(killer.getName()) && (deathMessage.contains("slain") || deathMessage.contains("shot"))) {
				ItemStack item = killer.getItemInHand();
				
				if (!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
					return;
				}
				
				String name = item.getItemMeta().getDisplayName();
				
				ComponentBuilder builder = new ComponentBuilder("§8» §r" + deathMessage.replace("[" + name + "]", ""));
				StringBuilder colored = new StringBuilder();
				
				if (killer.getItemInHand().getEnchantments().isEmpty()) {
					for (String entry : name.split(" ")) {
						colored.append("§o" + entry).append(" ");
					}
					
					builder.append("§f[" + colored.toString().trim() + "§f]");
				} else {
					for (String entry : name.split(" ")) {
						colored.append("§b§o" + entry).append(" ");
					}
					
					builder.append("§b[" + colored.toString().trim() + "§b]");
				}
				
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent(NameUtils.convertToJson(item))}));
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
				new BukkitRunnable() {
					public void run() {
						for (Player online : PlayerUtils.getPlayers()) {
							online.sendMessage("§8» §f" + deathMessage);
						}
					}
				}.runTaskLater(Main.plugin, 1);
				
				Bukkit.getLogger().info("§8» §f" + deathMessage);
				event.setDeathMessage(null);
			}
		}
		
		if (game.isRecordedRound()) {
			return;
		}

		User killerData = User.get(killer);
		killerData.increaseStat(Stat.KILLS);
		
		if (State.isState(State.INGAME)) {
			if (Main.kills.containsKey(killer.getName())) {
				Main.kills.put(killer.getName(), Main.kills.get(killer.getName()) + 1);
			} else {
				Main.kills.put(killer.getName(), 1);
			}
			
			Team team = Teams.getInstance().getTeam(killer);
			
			if (team != null) {
				if (Main.teamKills.containsKey(team.getName())) {
					Main.teamKills.put(team.getName(), Main.teamKills.get(team.getName()) + 1);
				} else {
					Main.teamKills.put(team.getName(), 1);
				}
			}
		}

		board.resetScore(player.getName());
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();

		Scoreboards board = Scoreboards.getInstance();
		Arena arena = Arena.getInstance();
		
		event.setRespawnLocation(Main.getSpawn());
		board.resetScore(player.getName());
		player.setMaxHealth(20);
		
		if (arena.isEnabled() || State.isState(State.LOBBY) || game.isRecordedRound()) {
			return;
		}
		
		player.sendMessage(Main.PREFIX + "Thanks for playing our game, it really means a lot :)");
		player.sendMessage(Main.PREFIX + "Follow us on twtter to know when our next games are: §a@ArcticUHC");
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.hidePlayer(player);
		}
		
		if (!player.hasPermission("uhc.prelist")) {
			player.sendMessage(Main.PREFIX + "You may stay as long as you want (You are vanished).");
			player.sendMessage(Main.PREFIX + "Please do not spam, rage, spoil or be a bad sportsman.");
		}
		
		player.sendMessage(Main.PREFIX + "You will be put into spectator mode in 10 seconds.");
		player.sendMessage(Main.PREFIX + "Please do not spam, rage, spoil or be a bad sportsman.");
		
		new BukkitRunnable() {
			public void run() {
				Spectator spec = Spectator.getInstance();
				
				if (State.isState(State.LOBBY) || !player.isOnline() || spec.isSpectating(player)) {
					return;
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.showPlayer(player);
				}
				
				Spectator.getInstance().enableSpecmode(player, true);
			}
		}.runTaskLater(Main.plugin, 200);
	}
	
	@EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		User user = User.get(player);

		Teams teams = Teams.getInstance();
		Team team = teams.getTeam(player);
		
		String message = event.getMessage();
		String name = (team == null || team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName());
		
		event.setCancelled(true);
    	
    	if (game.isRecordedRound()) {
    		PlayerUtils.broadcast(name + "§8 » §f" + message);
    		return;
    	}
		
		if (VoteCommand.running && (message.equalsIgnoreCase("y") || message.equalsIgnoreCase("n"))) {
			World world = player.getWorld();
			
			if (!State.isState(State.LOBBY) && world.getName().equals("lobby")) {
				player.sendMessage(ChatColor.RED + "You cannot vote when you are dead.");
				return;
			}
			
			Spectator spec = Spectator.getInstance();
			
			if (spec.isSpectating(player)) {
				player.sendMessage(ChatColor.RED + "You cannot vote as a spectator.");
				return;
			}
			
			if (VoteCommand.voted.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "You have already voted.");
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("y")) {
				player.sendMessage(Main.PREFIX + "You voted yes.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.yes++;
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("n")) {
				player.sendMessage(Main.PREFIX + "You voted no.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.no++;
			}
			return;
		}
    	
		if (user.isMuted()) {
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date date = new Date();
			
			if (user.getUnmuteTime() == -1 || user.getUnmuteTime() > date.getTime()) {
				player.sendMessage(Main.PREFIX + "You have been muted for: §a" + user.getMutedReason());
				
				if (user.getUnmuteTime() < 0) {
					player.sendMessage(Main.PREFIX + "Your mute is permanent.");
				} else {
					player.sendMessage(Main.PREFIX + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getUnmuteTime()));
				}
				return;
			} 
			else {
				user.unmute();
			}
		}
		
		if (user.getRank() == Rank.HOST) {
			String host = settings.getConfig().getString("game.host");
			String uuid = player.getUniqueId().toString();
			
			String prefix;
			
			if (uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				if (host.equals(player.getName())) {
					prefix = "§3§lHost";
				} else {
					prefix = "§3§lCo Host";
				}	
			} else {
				if (host.equals(player.getName())) {
					prefix = "§4§lHost";
				} else {
					prefix = "§4§lCo Host";
				}	
			}
			
			PlayerUtils.broadcast(prefix + " §8| §f" + name + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.TRIAL) {
			PlayerUtils.broadcast("§4§lTrial Host §8| §f" + name + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.STAFF) {
			PlayerUtils.broadcast("§c§lStaff §8| §f" + name + "§8 » §f" + message);
			return;
		}
		
		if (user.getRank() == Rank.VIP) {
			if (game.isMuted()) {
				player.sendMessage(Main.PREFIX + "The chat is currently muted.");
				return;
			}

			PlayerUtils.broadcast("§5§lVIP §8| §f" + name + "§8 » §f" + message);
			return;
		} 
			
		if (game.isMuted()) {
			player.sendMessage(Main.PREFIX + "The chat is currently muted.");
			return;
		}

		PlayerUtils.broadcast(name + "§8 » §f" + message);
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();
		
		Spectator spec = Spectator.getInstance();
		Fireworks fire = Fireworks.getInstance();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online == player) {
				continue;
			}
			
			if (!online.hasPermission("uhc.cmdspy")) {
				continue;
			}
			
			if (!spec.hasCommandSpy(online)) {
				continue;
			}
			
			if (online.getGameMode() != GameMode.CREATIVE && !spec.isSpectating(online)) {
				continue;
			}
			
			online.sendMessage("§8" + player.getName() + ": §7" + message);
		}
		
		String command = message.split(" ")[0].substring(1);
		
		if (command.equalsIgnoreCase("fire")) {
			if (player.hasPermission("uhc.staff")) {
				fire.startFireworkShow();
				event.setCancelled(true);
			}
			return;
		}
		
		if (command.equalsIgnoreCase("me") || command.equalsIgnoreCase("kill")) {
			player.sendMessage(Main.NO_PERM_MSG);
			event.setCancelled(true);
			return;
		}
		
		if (command.startsWith("bukkit:") && command.startsWith("minecraft:")) {
			if (player.hasPermission("uhc.admin")) {
				return;
			}
			
			player.sendMessage(Main.NO_PERM_MSG);
			event.setCancelled(true);
			return;
		}
		
		if (command.equalsIgnoreCase("rl") || command.equalsIgnoreCase("reload") || command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("restart")) {
			if (State.isState(State.LOBBY) || State.isState(State.SCATTER)) {
				return;
			}
			
			String done = command.replace("rl", "reload");
			
			if (!player.hasPermission("bukkit.command." + done)) {
				return;
			}
			
			player.sendMessage(ChatColor.RED + "You may not want to " + done + " when the game is running.");
			player.sendMessage(ChatColor.RED + "If you still want to " + done + ", do it in the console.");
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		String scenarios = ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("game.scenarios", "games scheduled"));
		String host = Settings.getInstance().getConfig().getString("game.host", "None");
		String teamSize = GameUtils.getTeamSize();
		String state = GameUtils.getState();
		
		event.setMotd("§4§lArctic UHC §8- §71.8 §8- §a" + state + "§r \n§6" + 
		teamSize + scenarios + (teamSize.startsWith("Open") || teamSize.startsWith("No") ? "" : "§8 - §4Host: §7" + host));

		int max = settings.getConfig().getInt("maxplayers", 80);
		event.setMaxPlayers(max);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (event.getTo().getWorld().getName().equals("lobby") && event.getTo().getY() <= 20) {
			Parkour parkour = Parkour.getInstance();
			
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
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        Action action = event.getAction();
        ItemStack item = event.getItem();
        
        Spectator spec = Spectator.getInstance();
        InvGUI inv = InvGUI.getInstance();
        
        if (world.getName().equals("lobby")) {
        	if (action == Action.PHYSICAL) {
            	event.setCancelled(true);
        	} else {
        		if (!player.hasPermission("uhc.build")) {
        			event.setCancelled(true);
        		}
        	}
        }
        
        if (item == null) {
        	return;
        }
        
        if (action == Action.RIGHT_CLICK_BLOCK && State.isState(State.INGAME) && Timers.pvp > 0 && !game.isRecordedRound()) {
        	if (item.getType() != Material.LAVA_BUCKET && item.getType() != Material.FLINT_AND_STEEL && item.getType() != Material.CACTUS) {
            	return;
        	}
        	
        	for (Entity nearby : PlayerUtils.getNearby(event.getClickedBlock().getLocation(), 5)) {
    			if (!(nearby instanceof Player)) {
    				continue;
    			}
    			
    			Player near = (Player) nearby;
				
				if (near == player) {
					continue;
				}
				
				if (spec.isSpectating(near)) {
					continue;
				}
				
				Team pTeam = Teams.getInstance().getTeam(player);
				Team nearTeam = Teams.getInstance().getTeam(near);
				
				if (pTeam != null && nearTeam != null) {
					if (pTeam == nearTeam) {
						continue;
					}
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "§c" + player.getName() + " §7attempted to iPvP §c" + near.getName(), "uhc.staff");
				
				player.sendMessage(Main.PREFIX + "iPvP is not allowed before PvP.");
				player.sendMessage(Main.PREFIX + "Stop iPvPing now or staff will take action.");
				
				item.setType(Material.AIR);
				event.setCancelled(true);
				break;
    		}
        	return;
        }
        
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			inv.openSelector(player);
		} 
		else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			ArrayList<Player> players = new ArrayList<Player>();
			
			for (Player online : PlayerUtils.getPlayers()) {
				World oWorld = online.getWorld();
				
				if (!spec.isSpectating(online) && !GameUtils.getGameWorlds().contains(oWorld)) {
					players.add(online);
				}
			}
			
			if (players.size() <= 0) {
				player.sendMessage(Main.PREFIX + "No players to teleport to.");
				return;
			}
			
			Random rand = new Random();
			Player target = players.get(rand.nextInt(players.size()));
			
			player.sendMessage(Main.PREFIX + "You teleported to §a" + target.getName() + "§7.");
			player.teleport(target.getLocation());
		}
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity clicked = event.getRightClicked();
		
		List<World> worlds = GameUtils.getGameWorlds();
		World world = clicked.getWorld();
		
		if (clicked instanceof ArmorStand && !worlds.contains(world)) {
			event.setCancelled(true);
			return;
		}
		
		if (!(clicked instanceof Player)) {
			return;
		}
	    	
		Player interacted = (Player) event.getRightClicked();
		Player player = event.getPlayer();
				
		Spectator spec = Spectator.getInstance();
		InvGUI inv = InvGUI.getInstance();
		
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (spec.isSpectating(interacted)) {
			return;
		}
		
		inv.openPlayerInventory(player, interacted);
    }
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		Recipe recipe = event.getRecipe();
		
		CraftingInventory inv = event.getInventory();
		ItemStack item = recipe.getResult();
		
		if (item == null) {
			return;
		}
		
		/**
		 * @author Ghowden
		 */
        if (RecipeUtils.areSimilar(event.getRecipe(), Main.headRecipe)) {
            ItemMeta meta = item.getItemMeta();
            String name = "N/A";
          
            for (ItemStack content : inv.getContents()) {
                if (content.getType() == Material.SKULL_ITEM) {
                    SkullMeta skullMeta = (SkullMeta) content.getItemMeta();
                    name = skullMeta.getOwner();
                    break;
                }
            }

            List<String> list = meta.getLore();
            list.add(ChatColor.AQUA + "Made from the head of: " + (name == null ? "N/A" : name));
            meta.setLore(list);
            item.setItemMeta(meta);
            
            inv.setResult(item);
        }
		
		if (item.getType() == Material.GOLDEN_APPLE) {
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
				ScenarioManager scen = ScenarioManager.getInstance();
				
				if (scen.getScenario("VengefulSpirits").isEnabled()) {
					return;
				}
				
				if (!game.goldenHeads()) {
					inv.setResult(new ItemStack(Material.AIR));
				}
				return;
			}
			
			if (item.getDurability() == 1) {
				if (!game.notchApples()) {
					inv.setResult(new ItemStack(Material.AIR));
				}
			}
			return;
		}
		
		if (item.getType() == Material.SPECKLED_MELON) {
			if (recipe instanceof ShapedRecipe) {
				ShapedRecipe shaped = (ShapedRecipe) event.getRecipe();
				
				if (game.goldenMelonNeedsIngots()) {
					if (shaped.getIngredientMap().values().contains(new ItemStack (Material.GOLD_NUGGET))) {
						inv.setResult(new ItemStack(Material.AIR));
					}
				} else {
					if (shaped.getIngredientMap().values().contains(new ItemStack (Material.GOLD_INGOT))) {
						inv.setResult(new ItemStack(Material.AIR));
					}
				}
			}
		}
    }
	
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		Spectator spec = Spectator.getInstance();
		Player player = event.getPlayer();
		
		if (!spec.isSpectating(player) && State.isState(State.INGAME)) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final ItemStack item = event.getItem();
		
		final float before = player.getSaturation();

		new BukkitRunnable() {
			public void run() {
				float change = player.getSaturation() - before;
				player.setSaturation((float) (before + change * 2.5D));
			}
        }.runTaskLater(Main.plugin, 1);
		
		if (item.getType() == Material.GOLDEN_APPLE) {
			if (!game.absorption()) {
				player.removePotionEffect(PotionEffectType.ABSORPTION);
				
				new BukkitRunnable() {
					public void run() {
						player.removePotionEffect(PotionEffectType.ABSORPTION);
					}
		        }.runTaskLater(Main.plugin, 1);
			}
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25 * (game.goldenHeadsHeal() * 2), 1));
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		World world = player.getWorld();
		
		if (world.getName().equals("lobby")) {
			event.setCancelled(true);
			event.setFoodLevel(20);
			return;
		}
		
		if (event.getFoodLevel() >= player.getFoodLevel()) {
			return;
	    }
		
		event.setCancelled(new Random().nextInt(100) < 66);
	}
}