package com.leontg77.uhc;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.utils.LocationUtils;

/**
 * Firework randomizer class.
 * <p>
 * This class contains methods for launching random fireworks, getting a random type and a random color.
 * 
 * @author LeonTG77
 */
public class Fireworks {
	private static Fireworks instance = new Fireworks();
	private Random ran = new Random();

	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Fireworks getInstance() {
		return instance;
	}

	/**
	 * Launch an random firework at the given location.
	 * 
	 * @param loc the location launching at.
	 */
	public void launchRandomFirework(Location loc) {
		Firework item = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = item.getFireworkMeta();

		if (ran.nextBoolean()) {
			meta.addEffect(FireworkEffect.builder().flicker(ran.nextBoolean()).trail(ran.nextBoolean()).with(randomType()).withColor(randomColor()).withFade(randomColor()).build());
		} else {
			meta.addEffect(FireworkEffect.builder().flicker(ran.nextBoolean()).trail(ran.nextBoolean()).with(randomType()).withColor(randomColor()).build());
		}

		meta.setPower(1);
		item.setFireworkMeta(meta);
	}
	
	/**
	 * Launch fireworks around the spawn.
	 */
	public void startFireworkShow() {
		new BukkitRunnable() {
			int i = 0;
			
			public void run() {
				int x = ran.nextInt(50 * 2) - 50;
				int z = ran.nextInt(50 * 2) - 50;

				Location loc = Main.getSpawn().clone().add(x, 0, z);
				loc.setY(LocationUtils.getHighestBlock(loc).getY());
				
				launchRandomFirework(loc.add(0, 1, 0));
				
				i++;
				
				if (i == 200) {
					cancel();
				}
			}
		}.runTaskTimer(Main.plugin, 20, 5);
	}
	
	/**
	 * Gets an random color.
	 * 
	 * @return A random color.
	 */
	private Color randomColor() {
		return Color.fromBGR(ran.nextInt(255), ran.nextInt(255), ran.nextInt(255));
	}

	/**
	 * Gets an random firework type.
	 * 
	 * @return A random firework type.
	 */
	private Type randomType() {
		return Type.values()[ran.nextInt(Type.values().length)];
	}
}