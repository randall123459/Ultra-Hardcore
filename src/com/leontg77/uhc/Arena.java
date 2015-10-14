package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.utils.ScatterUtils;

/**
 * PvP Arena class.
 * <p>
 * This class contains methods for enabling/disabling the arena, removing and adding players, giving the kit, scores and getting the players in the arena.
 * 
 * @see {@link Scoreboards}
 * @author LeonTG77
 */
public class Arena {
	private static Arena instance = new Arena();
	private Game game = Game.getInstance();
	private boolean enabled = false;
	
	public Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	public Objective arenaKills = board.getObjective("arenaKills");
	
	public HashMap<Player, Integer> killstreak = new HashMap<Player, Integer>();
	private ArrayList<Player> players = new ArrayList<Player>();
	
	private ArrayList<Long> seeds = new ArrayList<Long>();

	@SuppressWarnings("unused")
	private BukkitRunnable kitcycle;
	
	@SuppressWarnings("unused")
	private BukkitRunnable resetwarner;
	
	@SuppressWarnings("unused")
	private BukkitRunnable reset;
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Arena getInstance() {
		return instance;
	}
	
	/**
	 * Setup the arena class.
	 */
	public void setup() {
		if (board.getObjective("arenaKills") == null) {
			arenaKills = board.registerNewObjective("arenaKills", "dummy");
		}

		seeds.add(-281289493347827785l);
		seeds.add(-3703739705758069691l);
		seeds.add(-4092363856954762791l);
		seeds.add(8563798513411452931l);
		seeds.add(3543701468968612620l);
		seeds.add(8171481896432811161l);
		seeds.add(-2686161525319484628l);
		seeds.add(6333008698316655937l);
		
		arenaKills.setDisplayName("§4Arena §8- §7Use /a to join");
		if (Game.getInstance().arenaBoard()) {
			arenaKills.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		Main.plugin.getLogger().info("The arena has been setup.");
	}
	
	/**
	 * Enable the arena
	 */
	public void enable() {
		this.enabled = true;
		
		if (game.pregameBoard()) {
			Scoreboards.getManager().setScore("§a ", 10);
			Scoreboards.getManager().setScore("§8» §cArena:", 9);
			Scoreboards.getManager().setScore("§8» §7/a ", 8);
		}

		/*resetwarner = new BukkitRunnable() {
			public void run() {
			}
		};
		
		kitcycle = new BukkitRunnable() {
			public void run() {
			}
		};
		
		reset = new BukkitRunnable() {
			public void run() {
			}
		};
		
		resetwarner.runTaskTimer(Main.plugin, 1, 1);
		kitcycle.runTaskTimer(Main.plugin, 1, 1);
		reset.runTaskTimer(Main.plugin, 1, 1);*/
	}
	
	/**
	 * Disable the arena
	 */
	public void disable() {
		this.enabled = false;
		
		for (Player player : getPlayers()) {
			player.setItemOnCursor(new ItemStack (Material.AIR));
			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			player.setMaxHealth(20.0);
			player.setSaturation(20);
			player.setFoodLevel(20);
			player.setHealth(20);
			player.setLevel(0);
			player.setExp(0);
			
			if (player.isDead()) {
				player.spigot().respawn();
			}
			
			board.resetScores(player.getName());
			player.teleport(Main.getSpawn());
		}
		
		if (game.pregameBoard()) {
			Scoreboards.getManager().resetScore("§a ");
			Scoreboards.getManager().resetScore("§8» §cArena:");
			Scoreboards.getManager().resetScore("§8» §7/a ");
		}
		
		killstreak.clear();
		players.clear();

		/*resetwarner.cancel();
		kitcycle.cancel();
		reset.cancel();

		resetwarner = null;
		kitcycle = null;
		reset = null;*/
	}

	public void reset() {
		final boolean wasEnabled = isEnabled();
		
		if (wasEnabled) {
			disable();
		}
		
		PlayerUtils.broadcast(Main.prefix() + "The arena is resetting, lag incoming.");
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv delete arena");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvconfirm");
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvcreate arena normal -s " + seeds.get(new Random().nextInt(seeds.size())));
		
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				PlayerUtils.broadcast(Main.prefix() + "World reset done, setting up borders.");
				
				World world = Bukkit.getServer().getWorld("arena");
				
				world.getWorldBorder().setSize(400);
				world.getWorldBorder().setCenter(0.0, 0.0);
				world.getWorldBorder().setWarningDistance(0);
				world.getWorldBorder().setWarningTime(60);
				world.getWorldBorder().setDamageAmount(0.1);
				world.getWorldBorder().setDamageBuffer(50);
				world.setGameRuleValue("doDaylightCycle", "false");
				world.setTime(6000);
				
				PlayerUtils.broadcast(Main.prefix() + "Borders setup, pregenning arena world.");
				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb arena fill 420");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
			}
		}, 200);
		
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				PlayerUtils.broadcast(Main.prefix() + "Arena reset complete.");
				if (wasEnabled) {
					enable();
				}
			}
		}, 620);
	}

	/**
	 * Check if the arena is enabled.
	 * 
	 * @return True if the arena is enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Get a list of players in the arena.
	 * 
	 * @return A list of players in the arena.
	 */
	public List<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Check if the arena contains a player.
	 * 
	 * @param player the player.
	 * @return <code>True</code> if the player is in the arena, <code>false</code> otherwise.
	 */
	public boolean hasPlayer(Player player) {
		return players.contains(player);
	}
	
	/**
	 * Sets the score of the given player.
	 * 
	 * @param player the player setting it for.
	 * @param newScore the new score.
	 */
	public void setScore(String player, int newScore) {
		Score score = arenaKills.getScore(player);
		
		score.setScore(newScore);
	}

	/**
	 * Gets the given score for the given string.
	 * 
	 * @param string the wanted string.
	 * @return The score of the string.
	 */
	public int getScore(String string) {
		return arenaKills.getScore(string).getScore();
	}

	/**
	 * Reset the score of the given string.
	 * 
	 * @param string the string resetting.
	 */
	public void resetScore(String string) {
		board.resetScores(string);
	}
	
	/**
	 * Adds the given player to the arena.
	 * 
	 * @param player the player.
	 */
	public void addPlayer(Player player) {
		Location loc;
		
		try {
			loc = ScatterUtils.getScatterLocations(Bukkit.getWorld("arena"), (int) Bukkit.getWorld("arena").getWorldBorder().getSize() / 3, 1).get(0);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Could not teleport you to the arena.");
			return;
		}
		
		killstreak.put(player, 0);
		players.add(player);
		giveKit(player);
		
		loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
		
		player.teleport(loc);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 7));
	}
	
	/**
	 * Removes the given player from the arena.
	 * 
	 * @param player the player.
	 * @param death True if the removal was caused by dying, false otherwise.
	 */
	public void removePlayer(Player player, boolean death) {
		players.remove(player);
		
		if (!death) {
			Team team = Teams.getManager().getTeam(player);

			if (Arena.getInstance().killstreak.containsKey(player) && Arena.getInstance().killstreak.get(player) > 4) {
				PlayerUtils.broadcast(Main.prefix() + "§6" + player.getName() + "'s §7killstreak of §a" + Arena.getInstance().killstreak.get(player) + " §7was shut down from leaving");
			}
			
			for (Player p : Arena.getInstance().getPlayers()) {
				p.sendMessage("§8» " + (team == null ? "§f" : team.getPrefix()) + player.getName() + " §fwas died from leaving");
			}

			Arena.getInstance().killstreak.put(player, 0); 
			player.setItemOnCursor(new ItemStack (Material.AIR));
			player.getInventory().setArmorContents(null);
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.setMaxHealth(20.0);
			player.setSaturation(20);
			player.setFoodLevel(20);
			player.setHealth(20.0);
			player.setLevel(0);
			player.setExp(0);
			
			if (player.isDead()) {
				player.spigot().respawn();
			}
			
			board.resetScores(player.getName());
			player.teleport(Main.getSpawn());
		}
	}
	
	/**
	 * Gives the arena kit to the given player.
	 * 
	 * @param player the player.
	 */
	private void giveKit(Player player) {
		User user = User.get(player);
		
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemStack bow = new ItemStack(Material.BOW);
		
		ItemStack cobble = new ItemStack(Material.COBBLESTONE, 64);
		ItemStack bucket = new ItemStack(Material.WATER_BUCKET);
		
		ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
		pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);
		
		ItemStack axe = new ItemStack(Material.IRON_AXE);
		axe.addEnchantment(Enchantment.DIG_SPEED, 2);
		
		ItemStack shovel = new ItemStack(Material.IRON_SPADE);
		shovel.addEnchantment(Enchantment.DIG_SPEED, 2);
		
		ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE);
		ItemStack food = new ItemStack(Material.COOKED_BEEF, 32);
		
		ItemStack helmet = new ItemStack(Material.IRON_HELMET);
		ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
		ItemStack boots = new ItemStack(Material.IRON_BOOTS);
		
		player.getInventory().setItem(user.getFile().getInt("hotbar.sword", 0), sword);
		player.getInventory().setItem(user.getFile().getInt("hotbar.bow", 1), bow);
		player.getInventory().setItem(user.getFile().getInt("hotbar.bucket", 2), bucket);
		player.getInventory().setItem(user.getFile().getInt("hotbar.pickaxe", 3), pickaxe);
		player.getInventory().setItem(user.getFile().getInt("hotbar.cobble", 4), cobble);
		player.getInventory().setItem(user.getFile().getInt("hotbar.gapple", 5), gapple);
		player.getInventory().setItem(user.getFile().getInt("hotbar.shovel", 6), shovel);
		player.getInventory().setItem(user.getFile().getInt("hotbar.axe", 7), axe);
		player.getInventory().setItem(user.getFile().getInt("hotbar.food", 8), food);
		
		player.getInventory().setItem(27, new ItemStack (Material.ARROW, 64));
		player.getInventory().addItem(new ItemStack (Material.WORKBENCH, 16));
		player.getInventory().addItem(new ItemStack (Material.ENCHANTMENT_TABLE, 4));
		
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
	}
}