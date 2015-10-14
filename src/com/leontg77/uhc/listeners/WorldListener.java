package com.leontg77.uhc.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldInitEvent;

import com.leontg77.uhc.AntiStripmine;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.State;
import com.leontg77.uhc.AntiStripmine.ChunkOreRemover;
import com.leontg77.uhc.AntiStripmine.WorldData;

/**
 * Weather listener class.
 * <p>
 * Contains all eventhandlers for weather releated events.
 * 
 * @author LeonTG77
 */
public class WorldListener implements Listener {

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
    	if (event.toWeatherState()) {
			if (event.getWorld().getName().equals("lobby") || event.getWorld().getName().equalsIgnoreCase("arena")) {
				event.setCancelled(true);
				return;
			}

			if (!State.isState(State.INGAME)) {
				event.setCancelled(true);
				return;
			}

			if (Timers.meetup <= 0) {
				event.setCancelled(true);
				return;
			}

			if (Timers.pvp > 0) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onThunderChange(ThunderChangeEvent event) {
    	if (event.toThunderState()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkPopulate(ChunkPopulateEvent event) {
		AntiStripmine strip = AntiStripmine.getManager();
		World world = event.getWorld();
		
		WorldData worldData = strip.getWorldData(world);
		
		if (!worldData.isEnabled()) {
			return;
		}
		
		ChunkOreRemover remover = new ChunkOreRemover(worldData, event.getChunk());
		
		if (AntiStripmine.getManager().wasChecked(remover)) {
			Main.plugin.getLogger().warning("Populated " + worldData.getWorld().getName() + " " + remover.getChunkX() + "," + remover.getChunkZ() + " again");
			AntiStripmine.getManager().queue(remover);
		} else {
			worldData.logQueued(remover);
			AntiStripmine.getManager().queue(remover);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldInit(WorldInitEvent event) {
		AntiStripmine strip = AntiStripmine.getManager();
		World world = event.getWorld();
		
		strip.registerWorld(world);
	}
}