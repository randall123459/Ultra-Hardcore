package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Announcer class.
 * <p>
 * This class contains methods for sending announcements to the server every minute.
 * 
 * @author LeonTG77
 */
public class Announcer {
	private static Announcer instance = new Announcer();
	
	private ArrayList<String> messages = new ArrayList<String>();
	private BukkitRunnable task;
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Announcer getInstance() {
		return instance;
	}
	
	/**
	 * Setup the arena class.
	 */
	public void setup() {
		task = new BukkitRunnable() {
			public void run() {
				if (Game.getInstance().isRecordedRound() || State.isState(State.SCATTER)) {
					return;
				}
				
				PlayerUtils.broadcast("§8§l[§6§l!§8§l]§7 " + randomAnnouncement());
			}
		};
		
		task.runTaskTimer(Main.plugin, 5000, 5000);
		
		messages.add("Remember to use §a/uhc §7for all game information.");
		messages.add("You can view the hall of fame with §a/hof§7.");
		messages.add("If you have an questions, use §a/helpop§7.");
		messages.add("You can find the match post by doing §a/post§7.");
		messages.add("This server runs 95% custom plugins made by LeonTG77.");
		messages.add("Wonder if you are lagging? Use §a/ms §7or §a/tps§7.");
		messages.add("Follow our twitter for games and updates, §a@ArcticUHC§7!");
		
		Main.plugin.getLogger().info("The announcer has been setup.");
	}

	private String randomAnnouncement() {
		Random rand = new Random();
		
		return messages.get(rand.nextInt(messages.size()));
	}
}