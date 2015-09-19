package com.leontg77.uhc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldInitEvent;

import com.leontg77.uhc.AntiStripmine;
import com.leontg77.uhc.AntiStripmine.ChunkOreRemover;
import com.leontg77.uhc.AntiStripmine.WorldData;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Runnables;

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

			if (Runnables.meetup <= 0) {
				event.setCancelled(true);
				return;
			}

			if (Runnables.pvp > 0) {
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
		WorldData worldData = AntiStripmine.getManager().getWorldData(event.getWorld());
		
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
		AntiStripmine.getManager().registerWorld(event.getWorld());
	}
}