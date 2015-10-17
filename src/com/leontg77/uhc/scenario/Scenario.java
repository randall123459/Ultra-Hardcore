package com.leontg77.uhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.leontg77.uhc.Main;

/**
 * Scenario super class
 * @author LeonTG77
 */
public abstract class Scenario {
	private String name, desc;
	
	/**
	 * Scenario constructor
	 * @param name scenario name
	 * @param desc description of the scenario
	 */
	protected Scenario(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	/**
	 * Get the name of the scenario
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the description of the scenario.
	 * @return the description.
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * Enable the scenario.
	 * <p>
	 * Registers listeners if needed.
	 */
	public void enable() {
		if (this instanceof Listener) {
			Bukkit.getServer().getPluginManager().registerEvents((Listener) this, Main.plugin);
		}
		
		setEnabled(true);
	}
	
	/**
	 * Disable the scenario.
	 * <p>
	 * Unregisters listeners if needed.
	 */
	public void disable() {
		if (this instanceof Listener) {
			HandlerList.unregisterAll((Listener) this);
		}
		
		setEnabled(false);
	}
	
	/**
	 * Sets the scenario to enable or disable
	 * @param enable true to enable, false to disable.
	 */
	protected abstract void setEnabled(boolean enable);
	
	/**
	 * Check if the scenario is enabled
	 * @return True if enabled, false otherwise.
	 */
	public abstract boolean isEnabled();
}