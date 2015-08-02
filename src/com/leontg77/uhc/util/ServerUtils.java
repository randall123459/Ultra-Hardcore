package com.leontg77.uhc.util;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import org.bukkit.Bukkit;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;

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
			} else {
				return "rTo" + Main.teamSize;
			}
		}
		return "To" + Main.teamSize;
	}
}