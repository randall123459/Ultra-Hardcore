package com.leontg77.uhc.worlds;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * World pregenner.
 * 
 * @author LeonTG77
 */
public class Pregenner {
	private static Pregenner instance = new Pregenner();
	
	private ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	private BukkitRunnable task;
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Pregenner getInstance() {
		return instance;
	}
	
	/**
	 * Start pregenning. 
	 * 
	 * @param world The world to pregen in.
	 * @param radius The radius to pregen with.
	 */
	public void start(final World world, int radius) {
		int radiusX = ((radius + 208) / 16);
		int radiusZ = ((radergius + 208) / 16);
		
		for (int cx = (0 - radiusX); cx < radiusX; cx++) {
			for (int cz = (0 - radiusZ); cz < radiusZ; cz++) {
				Chunk chunk = world.getChunkAt(cx, cz);

				chunks.add(chunk);
			}
		}

		PlayerUtils.broadcast(Main.PREFIX + "Pregen of world '§a" + world.getName() + "§7' has started.");
		final int totalChunks = chunks.size();
		
		task = new BukkitRunnable() {
			public void run() {
				if (chunks.size() == 0) {
					PlayerUtils.broadcast(Main.PREFIX + "Pregen of world '§a" + world.getName() + "§7' has finished.");
					cancel();
					return;
				}
				
				Chunk chunk = chunks.remove(0);
				chunk.load(true);

				int percentCompleted = ((totalChunks - chunks.size())*100 / totalChunks);
				
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendAction(online, Main.PREFIX + "Pregenning: §a" + percentCompleted + "% §7finished");
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 2, 2);
	}
	
	/**
	 * Stop the pregenning.
	 * 
	 * @param world The world of the pregen.
	 */
	public void stop() {
		PlayerUtils.broadcast(Main.PREFIX + "Pregen has been cancelled.");
		
		task.cancel();
		task = null;
	}
}