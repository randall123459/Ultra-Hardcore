package com.leontg77.uhc.scenario.types;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.scenario.Scenario;

public class Astrophobia extends Scenario implements Listener {
	private boolean enabled = false;

	public int astroTask;
	public long tickInterval;
	public World runOnWorld;
	public int frequency;
	public int fuse;
	float chancePerDiamond = 10.0F;
	float chancePerGold = 30.0F;
	float chancePerIron = 60.0F;
	boolean debug;

	public Astrophobia() {
		super("Astrophobia", "The sun is gone, and the world is in eternal night. Deadly meteors impact the surface at random, leaving craters and flames, and sometimes ores. Aliens arrive from the sky wearing protective armor and shooting powerful weapons. Their tracking bombs (charged creepers) are fired onto the world to target any players that come near them. After defeating one, players can spawn a charged creeper of their own.");

		this.tickInterval = 100L;
		this.frequency = 5;
		this.fuse = 150;
		this.astroTask = -1;
		this.runOnWorld = null;
		this.debug = false;
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setGameRuleValue("doDaylightCycle", "false");
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setTime(18000);
			startForPlayer();
		} else {
			Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world")).setGameRuleValue("doDaylightCycle", "true");
			stopAstroTask();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	private void startForPlayer() {
		stopAstroTask();
		this.runOnWorld = Bukkit.getWorld(Settings.getInstance().getConfig().getString("game.world"));
		startAstroTask(runOnWorld);
	}

	public void stopAstroTask() {
		if (this.astroTask != -1) {
			Bukkit.getServer().getScheduler().cancelTask(astroTask);
		}
		this.astroTask = -1;
		this.runOnWorld = null;
	}

	private void startAstroTask(World world) {
		this.astroTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for (int i = 0; i < frequency; i++) {
					dropAstroItem();
				}
			}
		}, tickInterval, tickInterval);
	}

	protected void dropAstroItem() {
		Chunk[] chunkscandidates = null;
		chunkscandidates = this.runOnWorld.getLoadedChunks();

		Random r = new Random();
		if (chunkscandidates.length > 0) {
			Chunk dropChunk = chunkscandidates[r.nextInt(chunkscandidates.length)];
			Location dropFrom = new Location(dropChunk.getWorld(), dropChunk.getX() * 16 + r.nextInt(16), 255.0D, dropChunk.getZ() * 16 + r.nextInt(15));

			int chance = r.nextInt(100);

			if (chance < 70) {
				TNTPrimed tnt = (TNTPrimed) this.runOnWorld.spawn(dropFrom, TNTPrimed.class);
				tnt.setVelocity(new Vector((r.nextFloat() * 10.0F - 5.0F) / 2.0F, -1.0F * r.nextFloat(), (r.nextFloat() * 10.0F - 5.0F) / 2.0F));
				tnt.setFallDistance(150.0F);
				tnt.setFuseTicks(this.fuse);
				tnt.setMetadata("meteor", new FixedMetadataValue(Main.plugin, "isMeteor"));
				tnt.setIsIncendiary(r.nextBoolean());
				tnt.setYield(Math.max((float) ((1000.0D - dropFrom.distance(new Location(this.runOnWorld, 0.0D, 63.0D, 0.0D))) / 100.0D), 7.0F));
			} else if (chance < 85) {
				Creeper creep = (Creeper) this.runOnWorld.spawnEntity(dropFrom.subtract(0.0D, 155.0D, 0.0D), EntityType.CREEPER);
				creep.setPowered(true);
				creep.setFallDistance(-500.0F);
				creep.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 10), true);
				creep.setVelocity(new Vector(r.nextFloat() * 10.0F - 5.0F, -1.0F * r.nextFloat(), r.nextFloat() * 10.0F - 5.0F));

				creep.getEquipment().setItemInHand(new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
				creep.setCanPickupItems(false);
				creep.getEquipment().setItemInHandDropChance(100.0F);
			} else if (chance < 100) {
				Vector cel = new Vector(r.nextFloat() * 10.0F - 5.0F, -1.0F * r.nextFloat(), r.nextFloat() * 10.0F - 5.0F);
				for (int i = 2; i < 8; i++) {
					Skeleton creep = (Skeleton) this.runOnWorld.spawnEntity(dropFrom.subtract(0.0D, 155.0D, 0.0D), EntityType.SKELETON);
					creep.setFallDistance(-500.0F);
					creep.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999, 10), true);
					creep.setCanPickupItems(false);

					ItemStack entitychest = new ItemStack(Material.LEATHER_CHESTPLATE);
					ItemStack entityleg = new ItemStack(Material.LEATHER_LEGGINGS);
					ItemStack entityboots = new ItemStack(Material.LEATHER_BOOTS);

					ItemStack[] items = { entitychest, entityleg, entityboots };
					String[] names = { "Space Suit", "Space Pants", "Space Boots" };
					Color[] colours = { Color.GRAY, Color.GRAY, Color.GRAY };
					int ind = 0;
					ItemStack[] arrayOfItemStack1;
					int j = (arrayOfItemStack1 = items).length;
					
					for (int i1 = 0; i1 < j; i1++) {
						ItemStack it = arrayOfItemStack1[i1];
						LeatherArmorMeta lma = (LeatherArmorMeta) it.getItemMeta();
						lma.setColor(colours[ind]);
						lma.setDisplayName(names[ind]);
						it.setItemMeta(lma);
						ind++;
					}
					
					creep.getEquipment().setArmorContents(items);

					ItemStack entityhelm = new ItemStack(Material.GLASS);
					creep.getEquipment().setHelmet(entityhelm);
					creep.setVelocity(cel);

					creep.getEquipment().setItemInHand(new ItemStack(Material.BOW));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onEntityShootBowEvent(EntityShootBowEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}
		
		if (this.runOnWorld == null) {
			return;
		}
		
		if (event.getEntity().getType() != EntityType.SKELETON) {
			return;
		}
		
		if ((event.getProjectile().getType() == EntityType.ARROW) && (event.getEntity().getEquipment().getHelmet().getType() == Material.GLASS)) {
			Firework f = (Firework) this.runOnWorld.spawnEntity(event.getEntity().getEyeLocation(), EntityType.FIREWORK);
			FireworkMeta fwm = f.getFireworkMeta();
			FireworkEffect effect = FireworkEffect.builder().withTrail().with(FireworkEffect.Type.STAR).withColor(new Color[] { Color.GREEN, Color.GREEN }).with(FireworkEffect.Type.BURST).build();
			fwm.addEffect(effect);
			f.setFireworkMeta(fwm);
			event.getProjectile().setPassenger(f);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Entity damageEntity = event.getDamager();
		
		if (damageEntity.getType() != EntityType.ARROW) {
			return;
		}
		
		if (damageEntity.getPassenger() == null) {
			return;
		}
		
		Firework f = (Firework) damageEntity.getPassenger();
		FireworkMeta fwm = f.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder().withTrail().with(FireworkEffect.Type.BALL_LARGE).withColor(new Color[] { Color.RED, Color.RED }).with(FireworkEffect.Type.BURST).build();
		fwm.addEffect(effect);
		f.setFireworkMeta(fwm);
		f.detonate();
		event.setDamage(6.0D);
	}

	public static Entity[] getNearbyEntities(Location l, int radius) {
		int chunkRadius = radius < 16 ? 1 : (radius - radius % 16) / 16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();
		for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) l.getX();
				int y = (int) l.getY();
				int z = (int) l.getZ();
				Entity[] arrayOfEntity;
				int j = (arrayOfEntity = new Location(l.getWorld(), x + chX
						* 16, y, z + chZ * 16).getChunk().getEntities()).length;
				for (int i = 0; i < j; i++) {
					Entity e = arrayOfEntity[i];
					if ((e.getLocation().distance(l) <= radius)
							&& (e.getLocation().getBlock() != l.getBlock())) {
						radiusEntities.add(e);
					}
				}
			}
		}
		return (Entity[]) radiusEntities.toArray(new Entity[radiusEntities
				.size()]);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onEntityExplode(EntityExplodeEvent e) {
		if (!isEnabled()) {
			return;
		}
		
		if (e.getEntity().hasMetadata("meteor")) {
			final Location loc = e.getLocation();

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					Entity[] entities = getNearbyEntities(loc, 10);
					Entity[] arrayOfEntity1;
					int j = (arrayOfEntity1 = entities).length;
					for (int i = 0; i < j; i++) {
						Entity e = arrayOfEntity1[i];
						if (e.getType() == EntityType.DROPPED_ITEM) {
							Item i1 = (Item) e;
							if ((i1.getItemStack().getType() == Material.COBBLESTONE)
									|| (i1.getItemStack().getType() == Material.SAND)
									|| (i1.getItemStack().getType() == Material.SANDSTONE)
									|| (i1.getItemStack().getType() == Material.DIRT)
									|| (i1.getItemStack().getType() == Material.GRAVEL)
									|| (i1.getItemStack().getType() == Material.SAPLING)
									|| (i1.getItemStack().getType() == Material.LOG)
									|| (i1.getItemStack().getType() == Material.LOG_2)
									|| (i1.getItemStack().getType() == Material.SEEDS)) {
								e.remove();
							}
						}
					}
					Random r = new Random();
					if (r.nextBoolean()) {
						float fr = r.nextFloat() * 100.0F;
						if (fr < chancePerDiamond) {
							loc.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND, 1));
						} 
						else if (fr < chancePerDiamond + chancePerGold) {
							loc.getWorld().dropItem(loc, new ItemStack(Material.GOLD_ORE, 2));
						} 
						else if (fr < chancePerDiamond + chancePerGold	+ chancePerIron) {
							loc.getWorld().dropItem(loc, new ItemStack(Material.IRON_ORE, 2));
						}
					}
				}
			}, 2L);
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if ((event.getEntity().getType() == EntityType.CREEPER) && (event.getSpawnReason() == SpawnReason.SPAWNER_EGG)) {
			Creeper creep = (Creeper) event.getEntity();
			creep.setPowered(true);
			creep.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 10), true);

			creep.getEquipment().setItemInHand(new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
			creep.setCanPickupItems(false);
			creep.getEquipment().setItemInHandDropChance(100.0F);
		}
	}
}