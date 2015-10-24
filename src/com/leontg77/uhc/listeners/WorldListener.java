package com.leontg77.uhc.listeners;

import java.util.List;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.worlds.terrain.AntiStripmine;
import com.leontg77.uhc.worlds.terrain.AntiStripmine.ChunkOreRemover;
import com.leontg77.uhc.worlds.terrain.AntiStripmine.WorldData;

/**
 * World listener class.
 * <p>
 * Contains all eventhandlers for world releated events.
 * 
 * @author LeonTG77
 */
public class WorldListener implements Listener {

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
    	if (!event.toWeatherState()) {
    		return;
    	}
    	
    	List<World> worlds = GameUtils.getGameWorlds();
    	World world = event.getWorld();
    	
    	if (!worlds.contains(world)) {
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

	@EventHandler
	public void onThunderChange(ThunderChangeEvent event) {
    	if (!event.toThunderState()) {
    		return;
		}
    	
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		if (State.isState(State.SCATTER)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onChunkPopulate(ChunkPopulateEvent event) {
		AntiStripmine strip = AntiStripmine.getInstance();
		World world = event.getWorld();
		
		WorldData worldData = strip.getWorldData(world);
		
		if (!worldData.isEnabled()) {
			return;
		}
		
		ChunkOreRemover remover = new ChunkOreRemover(worldData, event.getChunk());
		
		if (AntiStripmine.getInstance().wasChecked(remover)) {
			Main.plugin.getLogger().warning("Populated " + worldData.getWorld().getName() + " " + remover.getChunkX() + "," + remover.getChunkZ() + " again");
			AntiStripmine.getInstance().queue(remover);
		} else {
			worldData.logQueued(remover);
			AntiStripmine.getInstance().queue(remover);
		}
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		AntiStripmine strip = AntiStripmine.getInstance();
		World world = event.getWorld();
		
		strip.registerWorld(world);
	}
}