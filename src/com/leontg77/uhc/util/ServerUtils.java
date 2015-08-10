package com.leontg77.uhc.util;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import org.bukkit.Bukkit;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Settings;

/**
 * Server utilities class.
 * @author LeonTG77
 */
public class ServerUtils {
	
	/**
	 * Gets the servers tps.
	 * @return The servers tps.
	 */
	public static double getTps() {
		return MinecraftServer.getServer().recentTps[0];
	}
	
	/**
	 * Gets a string version of the current state.
	 * @return the string version.
	 */
	public static String getState() {
		State current = State.getState();
		
		switch (current) {
		case INGAME:
			return "Started.";
		case LOBBY:
			if (Bukkit.getServer().hasWhitelist()) {
				return "Not running.";
			} else {
				return "Waiting for players...";
			}
		case SCATTER:
			return "Starting...";
		default:
			return "Not running.";
		}
	}
	
	/**
	 * Get the teamsize in a string format.
	 * @return the string format.
	 */
	public static String getTeamSize() {
		if (Main.ffa) {
			if (Main.teamSize == 1) {
				return "FFA";
			} else if (Main.teamSize == 0) {
				return "No";
			} else if (Main.teamSize < 0) {
				return "Open";
			} else {
				return "rTo" + Main.teamSize;
			}
		} else {
			return "To" + Main.teamSize;
		}
	}

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