package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

/**
 * @author Bergasms
 */
public class Pyrophobia extends Scenario implements Listener {
	private ArrayList<Location> locations;
	private boolean enabled = false;
	private int generateTaskID;
	private int totalChunks;

	public Pyrophobia() {
		super("Pyrophobia", "All water and ice is replaced with lava, redstone and lapis is replaced by obsidian and leaves drop sugar canes.");
	    this.generateTaskID = -1;
	    this.totalChunks = 0;
	    this.locations = new ArrayList<Location>();
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		IgniteCause cause = event.getCause();

		if (cause == IgniteCause.LAVA) {
			event.setCancelled(true);
			return;
		}

		if (cause == IgniteCause.SPREAD) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void CreatureSpawnEvent(CreatureSpawnEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 2));
	}

	@EventHandler(ignoreCancelled = true)
	public void LeavesDecayEvent(LeavesDecayEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Random r = new Random();
		if (r.nextInt(100) < 2) {
			Item item = event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.SUGAR_CANE, 1 + r.nextInt(1)));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().startsWith("/genpyro")) {
			if (event.getPlayer().hasPermission("uhc.pyro")) {
				convertToPyro(event.getPlayer().getWorld(), 1100);
			}
			event.setCancelled(true);
		}
	}

	private void completedPyro(final World w, int radius) {
		Bukkit.getServer().getScheduler().cancelTask(this.generateTaskID);
		this.generateTaskID = -1;
		Bukkit.getServer().broadcastMessage(Main.prefix() + "World Mid Converted");

		this.locations = new ArrayList<Location>();
		for (int i = -1 * radius; i < radius; i += 16) {
			for (int j = -1 * radius; j < radius; j += 16) {
				this.locations.add(new Location(w, i, 1.0D, j));
			}
		}
		this.totalChunks = this.locations.size();

		this.generateTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if (locations.size() > 0) {
					Location l = (Location) locations.remove(locations.size() - 1);
					postPyronChunk(w.getChunkAt(l));
				} else {
					completedForReal();
				}
			}
		}, 1L, 1L);
	}

	protected void completedForReal() {
		Bukkit.getServer().getScheduler().cancelTask(this.generateTaskID);
		this.generateTaskID = -1;
		Bukkit.getServer().broadcastMessage(Main.prefix() + "World Converted");
	}

	private void convertToPyro(final World w, final int radius) {
		if (this.generateTaskID != -1) {
			Bukkit.getServer().getScheduler().cancelTask(this.generateTaskID);
		}
		this.locations = new ArrayList<Location>();
		for (int i = -1 * radius; i < radius; i += 16) {
			for (int j = -1 * radius; j < radius; j += 16) {
				this.locations.add(new Location(w, i, 1.0D, j));
			}
		}
		this.totalChunks = this.locations.size();

		this.generateTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				if (locations.size() > 0) {
					Location l = (Location) locations.remove(locations.size() - 1);
					pyroChunk(w.getChunkAt(l));
				} else {
					completedPyro(w, radius);
				}
			}
		}, 1L, 1L);
	}

	protected void postPyronChunk(Chunk chunkAt) {
		Random r = new Random();
		for (int y = 0; y < 128; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 17; z++) {
					Block b = chunkAt.getBlock(x, y, z);
					if (b.getType() == Material.OBSIDIAN) {
						chunkAt.getBlock(x, y, z).setType(Material.STATIONARY_LAVA);
					}
					if ((b.getType() == Material.LAPIS_ORE) && (r.nextInt(50) < 4)) {
						chunkAt.getBlock(x, y, z).setType(Material.OBSIDIAN);
						Bukkit.getServer().broadcastMessage(Main.prefix() + "Coverted lapis.");
					}
					if ((b.getType() == Material.REDSTONE_ORE) && (r.nextInt(50) < 24)) {
						chunkAt.getBlock(x, y, z).setType(Material.OBSIDIAN);
						Bukkit.getServer().broadcastMessage(Main.prefix() + "Coverted redstone.");
					}
				}
			}
		}
		Bukkit.getServer().broadcastMessage(Main.prefix() + "Processed: §6" + (this.totalChunks - this.locations.size()) + "§7/§6" + this.totalChunks);
	}

	protected void pyroChunk(Chunk chunkAt) {
		for (int y = 0; y < 128; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 17; z++) {
					Block b = chunkAt.getBlock(x, y, z);
					if (b.getType() == Material.STATIONARY_WATER) {
						chunkAt.getBlock(x, y, z).setType(Material.OBSIDIAN);
					}
					if (b.getType() == Material.ICE) {
						chunkAt.getBlock(x, y, z).setType(Material.OBSIDIAN);
					}
					if (b.getType() == Material.PACKED_ICE) {
						chunkAt.getBlock(x, y, z).setType(Material.OBSIDIAN);
					}
					if (b.getType() == Material.WATER) {
						chunkAt.getBlock(x, y, z).setType(Material.OBSIDIAN);
					}
				}
			}
		}
		Bukkit.getServer().broadcastMessage(Main.prefix() + "Processed: §6" + (this.totalChunks - this.locations.size()) + "§7/§6" + this.totalChunks);
	}
}