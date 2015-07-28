package com.leontg77.uhc.scenario;

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
	public Scenario(String name, String desc) {
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
	 * Sets the scenario to enable or disable
	 * @param enable true to enable, false to disable.
	 */
	public abstract void setEnabled(boolean enable);
	
	/**
	 * Check if the scenario is enabled
	 * @return True if enabled, false otherwise.
	 */
	public abstract boolean isEnabled();
}