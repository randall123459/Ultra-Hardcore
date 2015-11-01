package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * ChunkApocalypse scenario class
 * 
 * @author LeonTG77
 */
public class ChunkApocalypse extends Scenario implements Listener, CommandExecutor {
	private boolean enabled = false;
	
	private ArrayList<Location> locations = new ArrayList<Location>();
	private int totalChunks;
	
	public static BukkitRunnable task = null;
	
	public ChunkApocalypse() {
		super("ChunkApocalypse", "Every chunk has a 30% chance of being replaced with air");
		Main main = Main.plugin;
		
		main.getCommand("chunkapo").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
    public void onFlow(BlockFromToEvent event) {
        event.setCancelled(true);
    }
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can generate chunk apocalypse.");
			return true;
		}
		
		final Player player = (Player) sender;
		
		if (!isEnabled()) {
			sender.sendMessage(Main.PREFIX + "\"ChunkApocalypse\" is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc.chunkapo")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			player.sendMessage(Main.PREFIX + "Usage: /chunkapo <radius>");
			return true;
		}
		
		int radius;
		
		try {
			radius = Integer.parseInt(args[0]);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + args[0] + " is not an vaild radius.");
			return true;
		}
		
		locations = new ArrayList<Location>();
		
		for (int x = -1 * radius; x < radius; x += 16) {
			for (int z = -1 * radius; z < radius; z += 16) {
				if (new Random().nextInt(99) < 30) {
					locations.add(new Location(player.getWorld(), x, 1, z));
				}
			}
		}
		
		totalChunks = locations.size();

		PlayerUtils.broadcast(Main.PREFIX + "ChunkApocalypse generation started.");
		
		task = new BukkitRunnable() {
			public void run() {
				if (locations.size() == 0) {
					PlayerUtils.broadcast(Main.PREFIX + "ChunkApocalypse generation finished.");
					cancel();
					task = null;
					return;
				}

				Location loc = locations.remove(locations.size() - 1);
				Chunk chunk = player.getWorld().getChunkAt(loc);
				
				for (int y = 0; y < 128; y++) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 17; z++) {
							Block block = chunk.getBlock(x, y, z);
							
							block.setType(Material.AIR);
						}
					}
				}

				int percentCompleted = ((totalChunks - locations.size())*100 / totalChunks);
				
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendAction(online, Main.PREFIX + "Removed chunk at x:" + chunk.getX() + " z:" + chunk.getZ() + ", §6" + percentCompleted + "% §7finished");
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 1, 1);
		return true;
	}
}