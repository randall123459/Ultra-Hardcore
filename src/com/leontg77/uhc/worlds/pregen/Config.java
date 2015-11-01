package com.leontg77.uhc.worlds.pregen;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

public class Config {
	private static Main plugin = Main.plugin;
	private static Logger wbLog = null;
	public static volatile DecimalFormat coord = new DecimalFormat("0.0");
	private static int borderTask = -1;
	public static volatile WorldFillTask fillTask = null;
	private static Runtime rt = Runtime.getRuntime();

	private static boolean shapeRound = true;
	private static String message; 
	private static String messageFmt;
	private static String messageClean; 
	private static boolean DEBUG = false;
	private static double knockBack = 3.0;
	private static int timerTicks = 4;
	private static boolean whooshEffect = true;
	private static boolean portalRedirection = true;
	private static int remountDelayTicks = 0;
	private static boolean killPlayer = false;
	private static boolean denyEnderpearl = false;
	private static int fillAutosaveFrequency = 30;
	private static int fillMemoryTolerance = 500;
	private static boolean preventBlockPlace = false;
	private static boolean preventMobSpawn = false;

	// for monitoring plugin efficiency
	// public static long timeUsed = 0;

	public static long Now() {
		return System.currentTimeMillis();
	}

	public static void setMessage(String msg) {
		updateMessage(msg);
		save(true);
	}

	public static void updateMessage(String msg) {
		message = msg;
		messageFmt = replaceAmpColors(msg);
		messageClean = stripAmpColors(msg);
	}

	public static String Message() {
		return messageFmt;
	}

	public static String MessageRaw() {
		return message;
	}

	public static String MessageClean() {
		return messageClean;
	}

	public static void setShape(boolean round) {
		shapeRound = round;
		log("Set default border shape to " + (ShapeName()) + ".");
		save(true);
	}

	public static boolean ShapeRound() {
		return shapeRound;
	}

	public static String ShapeName() {
		return ShapeName(shapeRound);
	}

	public static String ShapeName(Boolean round) {
		if (round == null)
			return "default";
		return round ? "elliptic/round" : "rectangular/square";
	}

	public static void setDebug(boolean debugMode) {
		DEBUG = debugMode;
		log("Debug mode " + (DEBUG ? "enabled" : "disabled") + ".");
		save(true);
	}

	public static boolean Debug() {
		return DEBUG;
	}

	public static void setWhooshEffect(boolean enable) {
		whooshEffect = enable;
		log("\"Whoosh\" knockback effect " + (enable ? "enabled" : "disabled")
				+ ".");
		save(true);
	}

	public static boolean whooshEffect() {
		return whooshEffect;
	}

	public static void showWhooshEffect(Location loc) {
		if (!whooshEffect())
			return;

		World world = loc.getWorld();
		world.playEffect(loc, Effect.ENDER_SIGNAL, 0);
		world.playEffect(loc, Effect.ENDER_SIGNAL, 0);
		world.playEffect(loc, Effect.SMOKE, 4);
		world.playEffect(loc, Effect.SMOKE, 4);
		world.playEffect(loc, Effect.SMOKE, 4);
		world.playEffect(loc, Effect.GHAST_SHOOT, 0);
	}

	public static void setPreventBlockPlace(boolean enable) {
		preventBlockPlace = enable;
		log("prevent block place " + (enable ? "enabled" : "disabled") + ".");
		save(true);
	}

	public static void setPreventMobSpawn(boolean enable) {
		preventMobSpawn = enable;
		log("prevent mob spawn " + (enable ? "enabled" : "disabled") + ".");
		save(true);
	}

	public static boolean preventBlockPlace() {
		return preventBlockPlace;
	}

	public static boolean preventMobSpawn() {
		return preventMobSpawn;
	}

	public static boolean getIfPlayerKill() {
		return killPlayer;
	}

	public static boolean getDenyEnderpearl() {
		return denyEnderpearl;
	}

	public static void setDenyEnderpearl(boolean enable) {
		denyEnderpearl = enable;
		log("Direct cancellation of ender pearls thrown past the border "
				+ (enable ? "enabled" : "disabled") + ".");
		save(true);
	}

	public static void setPortalRedirection(boolean enable) {
		portalRedirection = enable;
		log("Portal redirection " + (enable ? "enabled" : "disabled") + ".");
		save(true);
	}

	public static boolean portalRedirection() {
		return portalRedirection;
	}

	public static void setKnockBack(double numBlocks) {
		knockBack = numBlocks;
		log("Knockback set to " + knockBack + " blocks inside the border.");
		save(true);
	}

	public static double KnockBack() {
		return knockBack;
	}

	public static void setTimerTicks(int ticks) {
		timerTicks = ticks;
		log("Timer delay set to " + timerTicks + " tick(s). That is roughly "
				+ (timerTicks * 50) + "ms / "
				+ (((double) timerTicks * 50.0) / 1000.0) + " seconds.");
		StartBorderTimer();
		save(true);
	}

	public static int TimerTicks() {
		return timerTicks;
	}

	public static void setRemountTicks(int ticks) {
		remountDelayTicks = ticks;
		if (remountDelayTicks == 0)
			log("Remount delay set to 0. Players will be left dismounted when knocked back from the border while on a vehicle.");
		else {
			log("Remount delay set to " + remountDelayTicks
					+ " tick(s). That is roughly " + (remountDelayTicks * 50)
					+ "ms / " + (((double) remountDelayTicks * 50.0) / 1000.0)
					+ " seconds.");
			if (ticks < 10)
				logWarn("setting the remount delay to less than 10 (and greater than 0) is not recommended. This can lead to nasty client glitches.");
		}
		save(true);
	}

	public static int RemountTicks() {
		return remountDelayTicks;
	}

	public static void setFillAutosaveFrequency(int seconds) {
		fillAutosaveFrequency = seconds;
		if (fillAutosaveFrequency == 0)
			log("World autosave frequency during Fill process set to 0, disabling it. Note that much progress can be lost this way if there is a bug or crash in the world generation process from Bukkit or any world generation plugin you use.");
		else
			log("World autosave frequency during Fill process set to "
					+ fillAutosaveFrequency
					+ " seconds (rounded to a multiple of 5). New chunks generated by the Fill process will be forcibly saved to disk this often to prevent loss of progress due to bugs or crashes in the world generation process.");
		save(true);
	}

	public static int FillAutosaveFrequency() {
		return fillAutosaveFrequency;
	}

	public static boolean isBorderTimerRunning() {
		if (borderTask == -1)
			return false;
		return (plugin.getServer().getScheduler().isQueued(borderTask) || plugin
				.getServer().getScheduler().isCurrentlyRunning(borderTask));
	}

	public static void StartBorderTimer() {
		StopBorderTimer();

		if (borderTask == -1)
			logWarn("Failed to start timed border-checking task! This will prevent the plugin from working. Try restarting Bukkit.");

		logConfig("Border-checking timed task started.");
	}

	public static void StopBorderTimer() {
		if (borderTask == -1)
			return;

		plugin.getServer().getScheduler().cancelTask(borderTask);
		borderTask = -1;
		logConfig("Border-checking timed task stopped.");
	}

	public static void StopFillTask() {
		if (fillTask != null && fillTask.valid())
			fillTask.cancel();
	}

	public static void StoreFillTask() {
		save(false, true);
	}

	public static void UnStoreFillTask() {
		save(false);
	}

	public static void RestoreFillTask(String world, int fillDistance,
			int chunksPerRun, int tickFrequency, int x, int z, int length,
			int total, boolean forceLoad) {
		fillTask = new WorldFillTask(plugin.getServer(), null, world,
				fillDistance, chunksPerRun, tickFrequency, forceLoad);
		if (fillTask.valid()) {
			fillTask.continueProgress(x, z, length, total);
			int task = plugin
					.getServer()
					.getScheduler()
					.scheduleSyncRepeatingTask(plugin, fillTask, 20,
							tickFrequency);
			fillTask.setTaskID(task);
		}
	}

	// for backwards compatibility
	public static void RestoreFillTask(String world, int fillDistance,
			int chunksPerRun, int tickFrequency, int x, int z, int length,
			int total) {
		RestoreFillTask(world, fillDistance, chunksPerRun, tickFrequency, x, z,
				length, total, false);
	}

	public static int AvailableMemory() {
		return (int) ((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576);
	}

	public static boolean AvailableMemoryTooLow() {
		return AvailableMemory() < fillMemoryTolerance;
	}

	public static boolean HasPermission(Player player, String request) {
		return HasPermission(player, request, true);
	}

	public static boolean HasPermission(Player player, String request,
			boolean notify) {
		if (player == null) // console, always permitted
			return true;

		if (player.hasPermission("worldborder." + request)) // built-in Bukkit
															// superperms
			return true;

		if (notify)
			player.sendMessage(Main.NO_PERM_MSG);

		return false;
	}

	public static String replaceAmpColors(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	// adapted from code posted by Sleaker
	public static String stripAmpColors(String message) {
		return message.replaceAll("(?i)&([a-fk-or0-9])", "");
	}

	public static void log(Level lvl, String text) {
		wbLog.log(lvl, text);
	}

	public static void log(String text) {
		log(Level.INFO, text);
	}

	public static void logWarn(String text) {
		log(Level.WARNING, text);
	}

	public static void logConfig(String text) {
		log(Level.INFO, "[CONFIG] " + text);
	}

	public static void save(boolean logIt) {
		save(logIt, false);
	}

	public static void save(boolean logIt, boolean storeFillTask) {
		Settings setting = Settings.getInstance();
		FileConfiguration cfg = setting.getWorlds();
		
		if (storeFillTask && fillTask != null && fillTask.valid()) {
			cfg.set("fillTask.world", fillTask.refWorld());
			cfg.set("fillTask.fillDistance", fillTask.refFillDistance());
			cfg.set("fillTask.chunksPerRun", fillTask.refChunksPerRun());
			cfg.set("fillTask.tickFrequency", fillTask.refTickFrequency());
			cfg.set("fillTask.x", fillTask.refX());
			cfg.set("fillTask.z", fillTask.refZ());
			cfg.set("fillTask.length", fillTask.refLength());
			cfg.set("fillTask.total", fillTask.refTotal());
			cfg.set("fillTask.forceLoad", fillTask.refForceLoad());
		} else {
			cfg.set("fillTask", null);
		}

		setting.saveWorlds();
	}
}