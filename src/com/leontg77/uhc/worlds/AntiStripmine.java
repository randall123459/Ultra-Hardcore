package com.leontg77.uhc.worlds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.leontg77.uhc.Main;

@SuppressWarnings("deprecation")
public class AntiStripmine {
	private static AntiStripmine manager = new AntiStripmine();

	private EnumSet<Material> defaultOres = EnumSet.of(Material.DIAMOND_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.QUARTZ_ORE);
	private ArrayDeque<ChunkOreRemover> queue = new ArrayDeque<ChunkOreRemover>();
	private HashSet<ChunkOreRemover> checked = new HashSet<ChunkOreRemover>();
	private HashMap<UUID, WorldData> worlds = new HashMap<UUID, WorldData>();

	private BukkitTask tick = null;
	private Material oreReplacer;
	private int removalFactor;
	private int maxHeight;

	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static AntiStripmine getInstance() {
		return manager;
	}

	public void setup() {
		this.oreReplacer = Material.STONE;
		this.removalFactor = 100;
		this.maxHeight = 64;
	}

	public void queue(ChunkOreRemover remover) {
		this.queue.add(remover);
		
		if (this.tick == null) {
			this.tick = Bukkit.getScheduler().runTask(Main.plugin, new Runnable() {
				public void run() {
					long started = System.nanoTime();
					
					while ((!queue.isEmpty()) && (System.nanoTime() - started < 45000000L)) {
						queue.pop().run();
					}
					
					if (queue.isEmpty()) {
						tick = null;
					} else {
						tick = Bukkit.getScheduler().runTask(Main.plugin, this);
					}
				}
			});
		}
		
		this.checked.add(remover);
	}

	public int getDefaultMaxHeight() {
		return this.maxHeight;
	}

	public int getDefaultRemovalFactor() {
		return this.removalFactor;
	}

	public Material getDefaultOreReplacer() {
		return this.oreReplacer;
	}

	public EnumSet<Material> getDefaultOres() {
		return this.defaultOres;
	}

	public void displayStats(CommandSender sender) {
		for (WorldData worldData : this.worlds.values()) {
			worldData.displayStats(sender);
		}
	}

	public WorldData getWorldData(World world) {
		WorldData worldData = (WorldData) this.worlds.get(world.getUID());
		if (worldData == null) {
			worldData = registerWorld(world);
		}
		return worldData;
	}

	public boolean wasChecked(ChunkOreRemover remover) {
		return this.checked.contains(remover);
	}

	public WorldData registerWorld(World world) {
		WorldData worldData = new WorldData(world);
		this.worlds.put(world.getUID(), worldData);
		worldData.load();
		return worldData;
	}

	public class WorldData {
		private EnumMap<Material, Integer> remaining = new EnumMap<Material, Integer>(Material.class);
		private long total;
		private int chunks;
		private UUID worldId;
		private boolean enabled;
		private EnumSet<Material> excluded = EnumSet.noneOf(Material.class);
		private EnumSet<Material> ores = EnumSet.noneOf(Material.class);
		private int maxHeight;
		private int removalFactor;
		private Material oreReplacer;
		private final File queuedFile;
		private final File completedFile;
		private Set<ChunkOreRemover> noOresFound = new HashSet<ChunkOreRemover>();

		public WorldData(World world) {
			this.worldId = world.getUID();
			File worldFolder = new File(Main.plugin.getDataFolder(), "antistripmine" + File.separator + world.getName() + File.separator);

			if (!worldFolder.exists()) {
				worldFolder.mkdirs();
			}

			this.queuedFile = new File(worldFolder, "queued.log");
			this.completedFile = new File(worldFolder, "completed.log");

			if (world.getEnvironment() != World.Environment.NORMAL) {
				this.enabled = false;
			} else {
				this.enabled = true;
			}
			
			this.ores.addAll(getDefaultOres());
			this.maxHeight = getDefaultMaxHeight();
			this.removalFactor = getDefaultRemovalFactor();
			this.oreReplacer = getDefaultOreReplacer();
		}

		public EnumSet<Material> getExcluded() {
			return this.excluded;
		}

		public int getMaxHeight() {
			return this.maxHeight;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public int getRemovalFactor() {
			return this.removalFactor;
		}

		public Material getOreReplacer() {
			return this.oreReplacer;
		}

		public EnumSet<Material> getOres() {
			return this.ores;
		}

		public World getWorld() {
			return Bukkit.getServer().getWorld(this.worldId);
		}

		public UUID getWorldId() {
			return this.worldId;
		}

		public void displayStats(CommandSender sender) {
			if (this.chunks == 0) {
				sender.sendMessage("AntiBranchMining has not run yet on " + getWorld().getName());
				return;
			}
			
			sender.sendMessage(getWorld().getName() + ": Average " + this.total / this.chunks + " ns per chunk (" + this.total / this.chunks / 1.0E9D + "s)");
			
			for (Material material : this.remaining.keySet()) {
				int amount = ((Integer) this.remaining.get(material)).intValue();
				sender.sendMessage(material.name() + ": " + amount + " remaining in " + this.chunks + " chunks (average " + amount / this.chunks + " per chunk)");
			}
		}

		public void load() {
			final WorldData worldData = this;
			
			new BukkitRunnable() {
				public void run() {
					Set<String> toQueue = new TreeSet<String>();
					Throwable localThrowable3;
					
					try {
						BufferedReader in = new BufferedReader(new FileReader(WorldData.this.queuedFile));
						localThrowable3 = null;
						try {
							String line;
							while ((line = in.readLine()) != null) {
								toQueue.add(line);
							}
						} catch (Throwable localThrowable1) {
							localThrowable3 = localThrowable1;
							throw localThrowable1;
						} finally {
							if (in != null) {
								if (localThrowable3 != null) {
									try {
										in.close();
									} catch (Throwable x2) {
										localThrowable3.addSuppressed(x2);
									}
								} else {
									in.close();
								}
							}
						}
					} catch (FileNotFoundException ex) {
						
					} catch (IOException ex) {
						Main.plugin.getLogger().log(Level.SEVERE, "Cannot read " + WorldData.this.queuedFile.getPath(), ex);
					}
					try {
						BufferedReader in = new BufferedReader(new FileReader(WorldData.this.completedFile));
						localThrowable3 = null;
						try {
							String line;
							while ((line = in.readLine()) != null) {
								String[] parts = line.split(" ");
								toQueue.remove(parts[0]);
								
								for (int i = 2; i < parts.length; i++) {
									String[] kvp = parts[i].split(":");
									int materialId = Integer.parseInt(kvp[0]);
									int amount = Integer.parseInt(kvp[1]);
									Material material = Material.getMaterial(materialId);
									
									if (WorldData.this.remaining.containsKey(material)) {
										WorldData.this.remaining.put(material, Integer.valueOf(((Integer) WorldData.this.remaining.get(material)).intValue() + amount));
									} else {
										WorldData.this.remaining.put(material, Integer.valueOf(amount));
									}
								}
							}
						} catch (Throwable localThrowable2) {
							localThrowable3 = localThrowable2;
							throw localThrowable2;
						} finally {
							if (in != null) {
								if (localThrowable3 != null) {
									try {
										in.close();
									} catch (Throwable x2) {
										localThrowable3.addSuppressed(x2);
									}
								} else {
									in.close();
								}
							}
						}
					} catch (FileNotFoundException ex) {
						
					} catch (IOException ex) {
						Main.plugin.getLogger().log(Level.SEVERE, "Cannot read " + WorldData.this.completedFile.getPath(), ex);
					}
					
					int i = 1;
					for (String record : toQueue) {
						String[] parts = record.split(",");
						final int x = Integer.parseInt(parts[0]);
						final int z = Integer.parseInt(parts[1]);
						new BukkitRunnable() {
							public void run() {
								queue(new ChunkOreRemover(worldData, getWorld().getChunkAt(x, z)));
							}
						}.runTaskLater(Main.plugin, i++);
					}
					
					final int queued = toQueue.size();
					
					new BukkitRunnable() {
						public void run() {
							Main.plugin.getLogger().info("Loaded data for " + WorldData.this.getWorld().getName() + ", checking " + queued + " chunks");
						}
					}.runTask(Main.plugin);
				}
			}.runTaskAsynchronously(Main.plugin);
		}

		public void logQueued(ChunkOreRemover remover) {
			final String record = remover.toString() + "\n";
			new BukkitRunnable() {
				public void run() {
					synchronized (WorldData.this.queuedFile) {
						try {
							BufferedWriter out = new BufferedWriter(new FileWriter(WorldData.this.queuedFile, true));
							Throwable localThrowable2 = null;
							
							try {
								out.write(record);
							} catch (Throwable localThrowable1) {
								localThrowable2 = localThrowable1;
								throw localThrowable1;
							} finally {
								if (out != null) {
									if (localThrowable2 != null) {
										try {
											out.close();
										} catch (Throwable x2) {
											localThrowable2.addSuppressed(x2);
										}
									} else {
										out.close();
									}
								}
							}
						} catch (IOException ex) {
							Main.plugin.getLogger().log(Level.SEVERE, "Failed to save to " + WorldData.this.queuedFile.getPath() + ": " + record, ex);
						}
					}
				}
			}.runTaskAsynchronously(Main.plugin);
		}

		public void logCompleted(ChunkOreRemover remover, long duration, EnumMap<Material, Integer> remaining) {
			this.chunks += 1;
			this.total += duration;
			StringBuilder sb = new StringBuilder();
			sb.append(remover.toString());
			sb.append(" ").append(Long.toString(duration));
			
			for (Map.Entry<Material, Integer> entry : remaining.entrySet()) {
				Material material = (Material) entry.getKey();
				int amount = ((Integer) entry.getValue()).intValue();
				sb.append(" ").append(material.getId()).append(":").append(amount);
				if (this.remaining.containsKey(material)) {
					this.remaining.put(material, Integer.valueOf(((Integer) this.remaining.get(material)).intValue() + amount));
				} else {
					this.remaining.put(material, Integer.valueOf(amount));
				}
			}
			
			sb.append("\n");
			
			final String record = sb.toString();
			
			new BukkitRunnable() {
				public void run() {
					synchronized (WorldData.this.completedFile) {
						try {
							BufferedWriter out = new BufferedWriter(new FileWriter(WorldData.this.completedFile, true));
							Throwable localThrowable2 = null;
							
							try {
								out.write(record);
							} catch (Throwable localThrowable1) {
								localThrowable2 = localThrowable1;
								throw localThrowable1;
							} finally {
								if (out != null) {
									if (localThrowable2 != null) {
										try {
											out.close();
										} catch (Throwable x2) {
											localThrowable2.addSuppressed(x2);
										}
									} else {
										out.close();
									}
								}
							}
						} catch (IOException ex) {
							Main.plugin.getLogger().log(Level.SEVERE, "Failed to save to " + WorldData.this.completedFile.getPath() + ": " + record, ex);
						}
					}
				}
			}.runTaskAsynchronously(Main.plugin);
		}

		public boolean hasNoOres(ChunkOreRemover remover) {
			if (this.noOresFound.contains(remover)) {
				Main.plugin.getLogger().info("Confirmed that " + getWorld().getName() + " " + remover.toString() + " still has no ores on the second pass");
				return true;
			}
			return false;
		}

		public void notifyNoOres(final ChunkOreRemover remover) {
			this.noOresFound.add(remover);
			Main.plugin.getLogger().info("No ores were found in " + getWorld().getName() + " " + remover.toString() + ": Scheduling for a second check in 5 seconds");
			
			new BukkitRunnable() {
				public void run() {
					queue(remover);
				}
			}.runTaskLater(Main.plugin, 100L);
		}

		public boolean equals(Object obj) {
			if ((obj instanceof WorldData)) {
				WorldData other = (WorldData) obj;
				return other.worldId.equals(this.worldId);
			}
			return false;
		}

		public int hashCode() {
			return Objects.hashCode(this.worldId);
		}
	}

	public static class ChunkOreRemover implements Runnable {
		private static final Random random = new Random();
		private WorldData worldData;
		private int chunkX;
		private int chunkZ;

		public ChunkOreRemover(WorldData worldData, Chunk chunk) {
			this.worldData = worldData;
			this.chunkX = chunk.getX();
			this.chunkZ = chunk.getZ();
		}

		private Block getBlock(Chunk chunk, int dx, int y, int dz) {
			if ((y < 0) || (y > 255)) {
				return null;
			}
			
			if ((dx >= 0) && (dx <= 15) && (dz >= 0) && (dz <= 15)) {
				return chunk.getBlock(dx, y, dz);
			}
			
			int x = chunk.getX() * 16 + dx;
			int z = chunk.getZ() * 16 + dz;
			
			if (chunk.getWorld().isChunkLoaded(x >> 4, z >> 4)) {
				return chunk.getWorld().getBlockAt(x, y, z);
			}
			return null;
		}

		private void allowLinked(Block block, HashMap<Block, HashSet<Block>> toRemove, HashSet<Block> allowed) {
			if (toRemove.containsKey(block)) {
				HashSet<Block> linked = toRemove.get(block);
				toRemove.remove(block);
				allowed.add(block);
				
				for (Block link : linked) {
					allowLinked(link, toRemove, allowed);
				}
			}
		}

		private void addLinked(Block block, HashMap<Block, HashSet<Block>> toRemove, HashSet<Block> vein) {
			if ((toRemove.containsKey(block)) && (!vein.contains(block))) {
				vein.add(block);
				HashSet<Block> linked = toRemove.get(block);
				
				for (Block link : linked) {
					if (link.getType().equals(block.getType())) {
						addLinked(link, toRemove, vein);
					}
				}
			}
		}

		private void addRemainingOres(EnumMap<Material, Integer> remaining, Material type, int size) {
			if (!remaining.containsKey(type)) {
				remaining.put(type, Integer.valueOf(size));
			} else {
				remaining.put(type, Integer.valueOf(((Integer) remaining.get(type)).intValue() + size));
			}
		}

		public void run() {
			long started = System.nanoTime();

			HashMap<Block, HashSet<Block>> toRemove = new HashMap<Block, HashSet<Block>>();
			HashSet<Block> allowed = new HashSet<Block>();
			World world = this.worldData.getWorld();
			Chunk chunk = world.getChunkAt(this.chunkX, this.chunkZ);
			int maxHeight = this.worldData.getMaxHeight();
			int removalFactor = this.worldData.getRemovalFactor();
			EnumSet<Material> excludedOres = this.worldData.getExcluded();
			Material oreReplacer = this.worldData.getOreReplacer();
			EnumSet<Material> ores = this.worldData.getOres();
			boolean hasNoOres = true;
			
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y <= maxHeight; y++) {
						Block block = getBlock(chunk, x, y, z);
						if (block != null) {
							if (ores.contains(block.getType())) {
								hasNoOres = false;
								HashSet<Block> nearOres = new HashSet<Block>();
								
								for (int dx = -1; dx <= 1; dx++) {
									for (int dy = -1; dy <= 1; dy++) {
										for (int dz = -1; dz <= 1; dz++) {
											if ((dx != 0) || (dy != 0) || (dz != 0)) {
												Block near = getBlock(chunk, x + dx, y + dy, z + dz);
												
												if (near != null) {
													if (ores.contains(near.getType())) {
														nearOres.add(near);
													}
													if ((allowed.contains(near)) || (near.isEmpty()) || (near.isLiquid())) {
														allowed.add(block);
													}
												}
											}
										}
									}
								}
								if (allowed.contains(block)) {
									for (Block near : nearOres) {
										allowLinked(near, toRemove, allowed);
									}
									toRemove.remove(block);
								} else {
									toRemove.put(block, nearOres);
								}
							}
						}
					}
				}
			}
			
			EnumMap<Material, Integer> remaining = new EnumMap<Material, Integer>(Material.class);
			
			while (!toRemove.isEmpty()) {
				HashSet<Block> vein = new HashSet<Block>();
				Block next = (Block) toRemove.keySet().iterator().next();
				addLinked(next, toRemove, vein);
				Material type = vein.iterator().next().getType();
				if ((excludedOres.contains(type)) || ((removalFactor < 100) && (random.nextInt(100) >= removalFactor))) {
					addRemainingOres(remaining, type, vein.size());
				} else {
					for (Block block : vein) {
						block.setType(oreReplacer);
					}
				}
				toRemove.keySet().removeAll(vein);
			}
			
			for (Block block : allowed) {
				addRemainingOres(remaining, block.getType(), 1);
			}
			
			long duration = System.nanoTime() - started;
			
			if ((hasNoOres) && (!this.worldData.hasNoOres(this))) {
				worldData.notifyNoOres(this);
			} else {
				worldData.logCompleted(this, duration, remaining);
			}
		}

		public UUID getWorldName() {
			return this.worldData.getWorldId();
		}

		public int getChunkX() {
			return this.chunkX;
		}

		public int getChunkZ() {
			return this.chunkZ;
		}

		public boolean equals(Object obj) {
			if ((obj instanceof ChunkOreRemover)) {
				ChunkOreRemover other = (ChunkOreRemover) obj;
				return (other.chunkX == this.chunkX) && (other.chunkZ == this.chunkZ) && (other.worldData.equals(this.worldData));
			}
			return false;
		}

		public int hashCode() {
			int hash = 5;
			hash = 13 * hash + Objects.hashCode(this.worldData);
			hash = 13 * hash + this.chunkX;
			hash = 13 * hash + this.chunkZ;
			return hash;
		}

		public String toString() {
			return this.chunkX + "," + this.chunkZ;
		}
	}
}