package com.leontg77.uhc.util;

import com.leontg77.uhc.Settings;

/**
 * Host utilities class.
 * @author LeonTG77
 */
public class HostUtils {
	
	/**
	 * Get the current host hof name.
	 * @return the hof name.
	 */
	public static String getCurrentHost() {
		String host = Settings.getInstance().getConfig().getString("game.host");
		
		if (host.equalsIgnoreCase("AxlurUHC")) {
			return "Axlur";
		} else if (host.equalsIgnoreCase("LeonTG77")) {
			return "Leon";
		} else if (host.equalsIgnoreCase("PolarBlunk")) {
			return "Polar";
		} else if (host.equalsIgnoreCase("Popcane")) {
			return "Popcane";
		} else if (host.equalsIgnoreCase("Itz_Isaac")) {
			return "Isaac";
		}
		
		return null;
	}

	/**
	 * Get the hof name for a host.
	 * @param host The host.
	 * @return The hof name.
	 */
	public static String getHost(String host) {
		if (host.equalsIgnoreCase("AxlurUHC") || host.equalsIgnoreCase("axlur")) {
			return "Axlur";
		} else if (host.equalsIgnoreCase("LeonTG77") || host.equalsIgnoreCase("leon")) {
			return "Leon";
		} else if (host.equalsIgnoreCase("polar") || host.equalsIgnoreCase("polarblunk")) {
			return "Polar";
		} else if (host.equalsIgnoreCase("Popcane") || host.equalsIgnoreCase("pop")) {
			return "Popcane";
		} else if (host.equalsIgnoreCase("Itz_Isaac") || host.equalsIgnoreCase("isaac")) {
			return "Isaac";
		}
		return host;
	}
}