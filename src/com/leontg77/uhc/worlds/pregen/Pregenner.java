package com.leontg77.uhc.worlds.pregen;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;

/**
 * World pregenner.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("unused")
public class Pregenner {
	private static Pregenner instance = new Pregenner();

	public BukkitRunnable task = null;

	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Pregenner getInstance() {
		return instance;
	}

	/**
	 * Start pregenning.
	 * 
	 * @param world
	 *            The world to pregen in.
	 * @param check wether to cancel, pause or start the task.
	 *
	public void manage(Player sender, World world, String check) {
		if (check.equals("cancel") || check.equals("stop")) {
			if (!makeSureFillIsRunning(sender))
				return;
			sender.sendMessage(C_HEAD
					+ "Cancelling the world map generation task.");
			fillDefaults();
			Config.StopFillTask();
			return;
		} else if (check.equals("pause")) {
			if (!makeSureFillIsRunning(sender))
				return;
			Config.fillTask.pause();
			sender.sendMessage(C_HEAD
					+ "The world map generation task is now "
					+ (Config.fillTask.isPaused() ? "" : "un") + "paused.");
			return;
		}

		// if not just confirming, make sure a world name is available
		if (worldName == null && !confirm) {
			if (player != null)
				worldName = player.getWorld().getName();
			else {
				sendErrorAndHelp(sender, "You must specify a world!");
				return;
			}
		}

		// colorized "/wb fill "
		String cmd = cmd(sender) + nameEmphasized() + C_CMD;

		// make sure Fill isn't already running
		if (Config.fillTask != null && Config.fillTask.valid()) {
			sender.sendMessage(C_ERR
					+ "The world map generation task is already running.");
			sender.sendMessage(C_DESC + "You can cancel at any time with "
					+ cmd + "cancel" + C_DESC + ", or pause/unpause with "
					+ cmd + "pause" + C_DESC + ".");
			return;
		}

		// set frequency and/or padding if those were specified
		try {
			if (params.size() >= 1 && !confirm)
				fillFrequency = Math.abs(Integer.parseInt(params.get(0)));
			if (params.size() >= 2 && !confirm)
				fillPadding = Math.abs(Integer.parseInt(params.get(1)));
		} catch (NumberFormatException ex) {
			sendErrorAndHelp(sender,
					"The frequency and padding values must be integers.");
			fillDefaults();
			return;
		}
		if (fillFrequency <= 0) {
			sendErrorAndHelp(sender,
					"The frequency value must be greater than zero.");
			fillDefaults();
			return;
		}

		// see if the command specifies to load even chunks which should already
		// be fully generated
		if (params.size() == 3)
			fillForceLoad = strAsBool(params.get(2));

		// set world if it was specified
		if (worldName != null)
			fillWorld = worldName;

		if (fillWorld.isEmpty()) {
			sendErrorAndHelp(sender,
					"You must first use this command successfully without confirming.");
			return;
		}

		if (player != null)
			Config.log("Filling out world to border at the command of player \""
					+ player.getName() + "\".");

		int ticks = 1, repeats = 1;
		if (fillFrequency > 20)
			repeats = fillFrequency / 20;
		else
			ticks = 20 / fillFrequency;

		/*	*Config.log("world: " + fillWorld + "  padding: " + fillPadding
				+ "  repeats: " + repeats + "  ticks: " + ticks);
		Config.fillTask = new WorldFillTask(Bukkit.getServer(), sender,
				fillWorld, fillPadding, repeats, ticks, fillForceLoad);
		if (Config.fillTask.valid()) {
			int task = Bukkit
					.getServer()
					.getScheduler()
					.scheduleSyncRepeatingTask(Main.plugin,
							Config.fillTask, ticks, ticks);
			Config.fillTask.setTaskID(task);
			sender.sendMessage("WorldBorder map generation task for world \""
					+ fillWorld + "\" started.");
		} else
			sender.sendMessage(C_ERR
					+ "The world map generation task failed to start.");

		fillDefaults();
	}*/

	/*
	 * with "view-distance=10" in server.properties on a fast VM test server and
	 * "Render Distance: Far" in client, hitting border during testing was
	 * loading 11+ chunks beyond the border in a couple of directions (10 chunks
	 * in the other two directions). This could be worse on a more loaded or
	 * worse server, so:
	 */
	private final int defaultPadding = CoordXZ.chunkToBlock(13);

	private String fillWorld = "";
	private int fillFrequency = 420;
	private int fillPadding = defaultPadding;
	private boolean fillForceLoad = false;

	private void fillDefaults() {
		fillWorld = "";
		fillFrequency = 20;
		fillPadding = defaultPadding;
		fillForceLoad = false;
	}

	private boolean makeSureFillIsRunning(CommandSender sender) {
		if (Config.fillTask != null && Config.fillTask.valid()) {
			return true;
		}
		
		sender.sendMessage(Main.PREFIX + "There are no tasks running");
		return false;
	}
}