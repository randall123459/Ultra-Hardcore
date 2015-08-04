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

/**
 * PvP Arena class.
 * @author LeonTG77
 */
public class Arena {
	public HashMap<Player, Integer> killstreak = new HashMap<Player, Integer>();
	private Settings settings = Settings.getInstance();
	private ArrayList<Player> players;
	private boolean enabled = false;	
	private static Arena instance;
	
	/**
	 * Gets the instance of the class.
	 * @return the instance.
	 */
	public static Arena getManager() {
		return (instance == null ? new Arena() : instance);
	}
	
	/**
	 * Setup the arena class.
	 */
	public void setup() {
		players = new ArrayList<Player>();
		enabled = false;
		instance = this;
		Bukkit.getLogger().info("§a[UHC] The arena has been setup.");
	}
	
	/**
	 * Enable or disable the arena
	 * @param enabled enable?
	 */
	public void setEnabled(boolean enabled) {
		if (!enabled) {
			for (Player p : Bukkit.getWorld("arena").getPlayers()) {
				this.removePlayer(p, false);
			}
		}
		
		this.enabled = enabled;
	}

	/**
	 * Check if the arena is enabled.
	 * @return True if the arena is enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Get a list of players in the arena.
	 * @return list of players in the arena.
	 */
	public List<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Check if the arena contains a player.
	 * @param player the player.
	 * @return True if the player is in the arena, false otherwise.
	 */
	public boolean hasPlayer(Player player) {
		return players.contains(player);
	}
	
	/**
	 * Adds a player to the arena.
	 * @param player the player.
	 */
	public void addPlayer(Player player) {
		players.add(player);
		double areaRadius = 200;
		double minRadius = 5; 
		double t = Math.random() * Math.PI;
		double radius = Math.random()*(areaRadius - minRadius) + minRadius;
		double x = Math.cos(t) * radius;
		double z = Math.sin(t) * radius;
		Location loc = new Location (Bukkit.getWorld("arena"), x, 200, z);
		loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
		player.teleport(loc);
		giveKit(player);
	}
	
	/**
	 * Removes a player from the arena.
	 * @param player the player.
	 */
	public void removePlayer(Player player, boolean death) {
		players.remove(player);
		
		if (death) {
			return;
		}
		
		player.setItemOnCursor(new ItemStack (Material.AIR));
		player.setHealth(20.0);
		player.setMaxHealth(20.0);
		player.setSaturation(20);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
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
	 * Gives the arena kit to a player.
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