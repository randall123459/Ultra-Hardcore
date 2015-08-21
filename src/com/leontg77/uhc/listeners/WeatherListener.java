package com.leontg77.uhc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.leontg77.uhc.Runnables;

/**
 * Weather listener class.
 * <p> 
 * Contains all eventhandlers for weather releated events.
 * 
 * @author LeonTG77
 */
public class WeatherListener implements Listener {

	@EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
        	if (event.getWorld().getName().equals("lobby") || event.getWorld().getName().equalsIgnoreCase("arena")) {
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
}