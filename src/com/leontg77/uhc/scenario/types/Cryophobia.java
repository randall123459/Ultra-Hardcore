package com.leontg77.uhc.scenario.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.scenario.Scenario;

/**
 * @author Bergasms
 */
public class Cryophobia extends Scenario implements Listener {
	private boolean enabled = false;

	private World theworld;
	private int levelHeight;
	private int heightUpdateTimer;
	private int maxheight;
	private int processTimer;
	private Material replaceMaterial;
	private int[][] existingHeights;
	private boolean[][] biomeLookup;
	private int heightCountdown;
	private HashMap<String, ChunkProcess> chunkQueue;
	private LinkedList<String> priorityQueue;

	public Cryophobia() {
		super("Cryophobia", "A layer of ice will rise slowly from the bottom of the map, faster as the game goes on, filling caves and eventually reaching high up above the surface. Breaking ice causes damage and ill effects. Creepers explode into a ball of ice, while skeletons have geared up for the winter. The biome of the entire map has also been switched to a cold taiga, meaning snow falls and water freezes everywhere on the map.");
		this.maxheight = 80;
	}

	public void setEnabled(boolean enable) {
		enabled = enable;

		if (enable) {
			startCryo();
		} else {
			stopCryo();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	private class ChunkProcess {
		public int x;
		public int z;
		public int stopHeight;

		public ChunkProcess(int x, int z, int stopHeight) {
			this.x = x;
			this.z = z;
			this.stopHeight = stopHeight;
		}

		public String stringRep() {
			return this.x + ":" + this.z;
		}
	}

	public void startCryo() {
		this.chunkQueue = new HashMap<String, ChunkProcess>();
		this.priorityQueue = new LinkedList<String>();
		this.theworld = Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world"));
		this.replaceMaterial = Material.ICE;
		this.heightCountdown = 150;
		this.levelHeight = 0;
		this.biomeLookup = new boolean[500][500];
		this.existingHeights = new int[500][500];
		for (int i = 0; i < 500; i++) {
			for (int j = 0; j < 500; j++) {
				this.existingHeights[i][j] = this.levelHeight;
				this.biomeLookup[i][j] = false;
			}
		}
		Chunk[] arrayOfChunk;
		int i = (arrayOfChunk = theworld.getLoadedChunks()).length;
		for (int j = 0; j < i; j++) {
			Chunk c = arrayOfChunk[j];
			chunkLoaded(c);
		}
		this.processTimer = -1;
		this.heightUpdateTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				heightCountdown -= 1;
				if (heightCountdown <= 0) {
					if (levelHeight < maxheight) {
						levelHeight += 1;
					} else {
						endRaiseTimer();
					}
					didRaiseHeight();
					if (levelHeight <= 12) {
						heightCountdown = 150;
					} else if (levelHeight <= 20) {
						heightCountdown = 75;
					} else if (levelHeight <= 40) {
						heightCountdown = 60;
					} else {
						heightCountdown = 60;
					}
				}
			}
		}, 20L, 20L);

		didRaiseHeight();
	}

	protected void didRaiseHeight() {
		priorityQueue.remove("sentinel");
		priorityQueue.addLast("sentinel");
		if (processTimer == -1) {
			processTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
				public void run() {
					processChunkIfRequired();
				}
			}, 1L, 1L);
		}
	}

	protected void processChunkIfRequired() {
		int attempts = 10;
		for (int a = 0; a < attempts; a++) {
			String pQueue = (String) this.priorityQueue.getFirst();
			if (pQueue.equalsIgnoreCase("sentinel")) {
				this.priorityQueue.remove("sentinel");
				a = 100;
				if (this.processTimer != -1) {
					Bukkit.getServer().getScheduler().cancelTask(this.processTimer);
					this.processTimer = -1;
				}
				return;
			}
			ChunkProcess cp = (ChunkProcess) this.chunkQueue.get(pQueue);

			boolean addToMiddle = false;
			if ((cp != null) && (cp.x >= 0) && (cp.x < 500) && (cp.z >= 0) && (cp.z < 500) && (this.existingHeights[cp.x][cp.z] < this.levelHeight)) {
				World w = theworld;
				if (w != null) {
					Chunk c = w.getChunkAt(cp.x - 250, cp.z - 250);
					if (c != null) {
						int upToHeight = this.existingHeights[cp.x][cp.z];
						if ((cp.stopHeight < this.levelHeight) && (this.levelHeight - upToHeight < 8)) {
							cp.stopHeight = this.levelHeight;
						}
						for (int y = upToHeight; y <= cp.stopHeight; y++) {
							for (int i = 0; i < 16; i++) {
								for (int j = 0; j < 16; j++) {
									if (!c.getBlock(i, y, j).getType().isSolid()) {
										c.getBlock(i, y, j).setType(this.replaceMaterial);
									}
								}
							}
						}
						a = 100;
						this.existingHeights[cp.x][cp.z] = cp.stopHeight;
						int stopAt = cp.stopHeight + 8;
						if (stopAt > this.levelHeight) {
							stopAt = this.levelHeight;
						}
						cp.stopHeight = stopAt;
						if (cp.stopHeight < this.levelHeight) {
							addToMiddle = true;
						}
					}
				}
			}
			this.priorityQueue.removeFirst();
			if (addToMiddle) {
				int idx = this.priorityQueue.size() / 2;
				this.priorityQueue.add(idx, pQueue);
			} else {
				this.priorityQueue.addLast(pQueue);
			}
		}
	}

	protected void endRaiseTimer() {
		if (this.heightUpdateTimer != -1) {
			Bukkit.getServer().getScheduler().cancelTask(this.heightUpdateTimer);
			this.heightUpdateTimer = -1;
		}
		if (this.processTimer != -1) {
			Bukkit.getServer().getScheduler().cancelTask(this.processTimer);
			this.processTimer = -1;
		}
	}

	public void stopCryo() {
		endRaiseTimer();
	}

	public void chunkLoaded(Chunk chunk) {
		if (this.priorityQueue == null) {
			return;
		}
		if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) {
			return;
		}
		if (!chunk.getWorld().getName().equals(this.theworld.getName())) {
			return;
		}
		int xp = chunk.getX() + 250;
		int zp = chunk.getZ() + 250;
		if (!this.biomeLookup[xp][zp]) {
			this.biomeLookup[zp][zp] = true;
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					chunk.getBlock(i, 128, j).setBiome(Biome.COLD_TAIGA);
				}
			}
		}
		int stopAt = this.existingHeights[xp][zp] + 8;
		if (stopAt > this.levelHeight) {
			stopAt = this.levelHeight;
		}
		ChunkProcess cp = new ChunkProcess(xp, zp, stopAt);
		this.chunkQueue.put(cp.stringRep(), cp);
		this.priorityQueue.add(cp.stringRep());

		this.priorityQueue.remove("sentinel");
		this.priorityQueue.addLast("sentinel");
	}

	public void chunkUnloaded(Chunk chunk) {
		if (this.priorityQueue == null) {
			return;
		}
		if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) {
			return;
		}
		int xp = chunk.getX() + 250;
		int zp = chunk.getZ() + 250;
		ChunkProcess cp = new ChunkProcess(xp, zp, 0);
		this.chunkQueue.remove(cp.stringRep());
		this.priorityQueue.remove(cp.stringRep());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onChunkLoadEvent(ChunkLoadEvent event) {
		if (!isEnabled()) {
			return;
		}

		chunkLoaded(event.getChunk());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		if (!isEnabled()) {
			return;
		}

		chunkUnloaded(event.getChunk());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onEntityShootBowEvent(EntityShootBowEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}
		if (event.getEntity().getType() != EntityType.SKELETON) {
			return;
		}
		if (event.getProjectile().getType() == EntityType.ARROW) {
			event.setCancelled(true);
			event.getEntity().launchProjectile(Snowball.class, event.getProjectile().getVelocity());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		EntityType type = event.getEntity().getType();
		if ((type == EntityType.CHICKEN) || (type == EntityType.PIG) || (type == EntityType.COW) || (type == EntityType.SHEEP)) {
			if (new Random().nextInt(10) <= 1) {
				event.getEntity().getWorld().spawnEntity(event.getLocation(), EntityType.SNOWMAN);
			}
			event.setCancelled(new Random().nextBoolean());
			return;
		}

		if (type != EntityType.SKELETON) {
			return;
		}
		ItemStack entityhelm = new ItemStack(Material.LEATHER_HELMET);
		ItemStack entitychest = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack entityleg = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack entityboots = new ItemStack(Material.LEATHER_BOOTS);

		ItemStack[] items = { entityhelm, entitychest, entityleg, entityboots };
		String[] names = { "Beanie", "Parka", "Ski Pants", "Snowboard Boots" };
		Color[] colours = { Color.BLACK, Color.BLUE, Color.BLUE, Color.RED };
		int ind = 0;
		ItemStack[] arrayOfItemStack1;
		int j = (arrayOfItemStack1 = items).length;
		for (int i = 0; i < j; i++) {
			ItemStack i1 = arrayOfItemStack1[i];
			LeatherArmorMeta lma = (LeatherArmorMeta) i1.getItemMeta();
			lma.setColor(colours[ind]);
			lma.setDisplayName(names[ind]);
			i1.setItemMeta(lma);
			ind++;
		}
		event.getEntity().getEquipment().setArmorContents(items);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onExplodeDeath(EntityExplodeEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}
		if (event.getEntityType() != EntityType.CREEPER) {
			return;
		}
		final Location lc = event.getEntity().getLocation();
		final World w = lc.getWorld();

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				Location l = lc;
				w.getBlockAt(l).setType(Material.PACKED_ICE);
				w.getBlockAt(l.add(0.0D, 1.0D, 0.0D)).setType(Material.PACKED_ICE);
				w.getBlockAt(l.add(0.0D, -2.0D, 0.0D)).setType(Material.PACKED_ICE);
				w.getBlockAt(l.add(1.0D, 1.0D, 0.0D)).setType(Material.PACKED_ICE);
				w.getBlockAt(l.add(-2.0D, 0.0D, 0.0D)).setType(Material.PACKED_ICE);
				w.getBlockAt(l.add(1.0D, 0.0D, 1.0D)).setType(Material.PACKED_ICE);
				w.getBlockAt(l.add(0.0D, 0.0D, -2.0D)).setType(Material.PACKED_ICE);
				
				l = lc;
				w.getBlockAt(l.add(1.0D, -1.0D, 1.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(-1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(-1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(0.0D, 0.0D, -1.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(0.0D, 0.0D, -1.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(0.0D, 0.0D, 1.0D)).setType(Material.ICE);
				w.getBlockAt(l.add(0.0D, 0.0D, 1.0D)).setType(Material.ICE);
				
				Random r = new Random();
				int times = 3 + r.nextInt(2);
				for (int i = 0; i < times; i++) {
					l = lc;
					w.getBlockAt(l.add(r.nextInt(4) - 2, r.nextInt(2), r.nextInt(4) - 2)).setType(Material.ICE);
				}
			}
		}, 1L);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (!isEnabled()) {
			return;
		}

		Entity damageEntity = event.getDamager();

		if (damageEntity.getType() != EntityType.SNOWBALL) {
			return;
		}

		Entity damaged = event.getEntity();
		if (damaged.getType() == EntityType.PLAYER) {
			Snowball snowball = (Snowball) damageEntity;
			ProjectileSource entityThrower = snowball.getShooter();
			if ((entityThrower instanceof Skeleton)) {
				event.setDamage(1.0D + new Random().nextFloat());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onLeavesDecayEvent(LeavesDecayEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		if (event.getBlock().getType() == Material.LEAVES) {
			MaterialData data = event.getBlock().getState().getData();
			try {
				Tree t = (Tree) data;
				if (t.getSpecies() == TreeSpecies.REDWOOD) {
					Random r = new Random();
					if (r.nextInt(40) == 0) {
						Item item = event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.7, 0.5),new ItemStack(Material.APPLE, 1));
						item.setVelocity(new Vector(0, 0.2, 0));
					}
				}
			} catch (ClassCastException localClassCastException) {
			}
		}
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.getBlock().getType() == Material.ICE && event.getToBlock().getType() == Material.WATER) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		if ((event.getBlock().getType() == Material.ICE) || (event.getBlock().getType() == Material.PACKED_ICE)) {
			Random r = new Random();
			if (r.nextInt(4) == 0) {
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 1));
			}
			if (r.nextInt(8) == 0) {
				event.getPlayer().damage(1.0D);
			}
		}
	}
}