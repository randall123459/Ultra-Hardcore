package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.uhc.utils.ScatterUtils;

/**
 * PvP Arena class.
 * <p>
 * This class contains methods for enabling/disabling the arena, removing and adding players, giving the kit, scores (see  class) and getting the players in the arena.
 * 
 * @see {@link Scoreboards}
 * @author LeonTG77
 */
public class Arena {
	public HashMap<Player, Integer> killstreak = new HashMap<Player, Integer>();
	private Settings settings = Settings.getInstance();
	private static Arena instance = new Arena();
	private ArrayList<Player> players;
	private boolean enabled = false;	
	
	public Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	public Objective arenaKills = board.getObjective("arenaKills");
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Arena getManager() {
		return instance;
	}
	
	/**
	 * Setup the arena class.
	 */
	public void setup() {
		players = new ArrayList<Player>();
		enabled = false;

		if (board.getObjective("arenaKills") == null) {
			arenaKills = board.registerNewObjective("arenaKills", "dummy");
		}
		arenaKills.setDisplayName("§4Open PvP §8- §7Join with /a");
		
		Bukkit.getLogger().info("[UHC] The arena has been setup.");
	}
	
	/**
	 * Enable or disable the arena
	 * 
	 * @param enable <code>true</code> to enable, <code>false</code> to disable.
	 */
	public void setEnabled(boolean enable) {
		this.enabled = enable;
		
		if (enable) {
			if (Main.board) {
				Scoreboards.getManager().setScore("§a ", 10);
				Scoreboards.getManager().setScore("§8» §cArena:", 9);
				Scoreboards.getManager().setScore("§8» §7/a ", 8);
			}
		} else {
			for (Player p : Bukkit.getWorld("arena").getPlayers()) {
				this.removePlayer(p, false);
			}
			
			if (Main.board) {
				Scoreboards.getManager().resetScore("§a ");
				Scoreboards.getManager().resetScore("§8» §cArena:");
				Scoreboards.getManager().resetScore("§8» §7/a ");
			}
		}
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
		players.add(player);
		Location loc = ScatterUtils.getScatterLocations(Bukkit.getWorld("arena"), 100, 1).get(0);
		loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
		player.teleport(loc);
		giveKit(player);
	}
	
	/**
	 * Removes the given player from the arena.
	 * 
	 * @param player the player.
	 */
	public void removePlayer(Player player, boolean death) {
		players.remove(player);
		
		if (death) {
			return;
		}
		
		player.setItemOnCursor(new ItemStack (Material.AIR));
		player.getInventory().setArmorContents(null);
		player.getInventory().clear();
		player.setMaxHealth(20.0);
		player.setSaturation(20);
		player.setFoodLevel(20);
		player.setHealth(20.0);
		player.setLevel(0);
		player.setExp(0);
		
		World world = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
		double x = settings.getData().getDouble("spawn.x");
		double y = settings.getData().getDouble("spawn.y");
		double z = settings.getData().getDouble("spawn.z");
		float yaw = (float) settings.getData().getDouble("spawn.yaw");
		float pitch = (float) settings.getData().getDouble("spawn.pitch");
		
		Location loc = new Location(world, x, y, z, yaw, pitch);
		player.teleport(loc);
	}
	
	/**
	 * Gives the arena kit to the given player.
	 * 
	 * @param player the player.
	 */
	private void giveKit(Player player) {
		ItemStack Sword = new ItemStack(Material.IRON_SWORD);
		ItemStack Bow = new ItemStack(Material.BOW);
		ItemStack Cobble = new ItemStack(Material.COBBLESTONE, 64);
		ItemStack Bucket = new ItemStack(Material.WATER_BUCKET);
		ItemStack Pick = new ItemStack(Material.IRON_PICKAXE);
		Pick.addEnchantment(Enchantment.DIG_SPEED, 2);
		ItemStack Axe = new ItemStack(Material.IRON_AXE);
		Axe.addEnchantment(Enchantment.DIG_SPEED, 2);
		ItemStack Shovel = new ItemStack(Material.IRON_SPADE);
		Shovel.addEnchantment(Enchantment.DIG_SPEED, 2);
		ItemStack Steel = new ItemStack(Material.GOLDEN_APPLE);
		ItemStack Food = new ItemStack(Material.COOKED_BEEF, 32);
		
		ItemStack Helmet = new ItemStack(Material.IRON_HELMET);
		
		ItemStack Chestplate = new ItemStack(Material.IRON_CHESTPLATE);
		
		ItemStack Leggings = new ItemStack(Material.IRON_LEGGINGS);
		
		ItemStack Boots = new ItemStack(Material.IRON_BOOTS);
		
		player.getInventory().setItem(0, Sword);
		player.getInventory().setItem(1, Bow);
		player.getInventory().setItem(2, Cobble);
		player.getInventory().setItem(3, Bucket);
		player.getInventory().setItem(4, Pick);
		player.getInventory().setItem(5, Axe);
		player.getInventory().setItem(6, Shovel);
		player.getInventory().setItem(7, Steel);
		player.getInventory().setItem(8, Food);
		player.getInventory().setItem(27, new ItemStack (Material.ARROW, 64));
		player.getInventory().addItem(new ItemStack (Material.WORKBENCH, 16));
		player.getInventory().addItem(new ItemStack (Material.ENCHANTMENT_TABLE, 4));
		player.getInventory().addItem(new ItemStack (Material.INK_SACK, 64, (short) 4));
		player.getInventory().setHelmet(Helmet);
		player.getInventory().setChestplate(Chestplate);
		player.getInventory().setLeggings(Leggings);
		player.getInventory().setBoots(Boots);
	}
}