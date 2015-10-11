package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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
public class Voidscape extends Scenario implements Listener, CommandExecutor {
	private ArrayList<Chunk> finished = new ArrayList<Chunk>();
	private ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	private boolean enabled = false;
	
	public Voidscape() {
		super("Voidscape", "All stone and bedrock is replaced with air");
		Main main = Main.plugin;
		
		main.getCommand("void").setExecutor(this);
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
			sender.sendMessage(ChatColor.RED + "Only players can generate voidscape.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("void")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"Voidscape\" is not enabled.");
				return true;
			}
			
			if (sender.hasPermission("uhc.void")) {
				if (args.length == 0) {
					player.sendMessage(Main.prefix() + "Usage: /void <radius>");
					return true;
				}
				
				int radius;
				
				try {
					radius = Integer.parseInt(args[0]);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED + "That is not an vaild radius.");
					return true;
				}
				
				int radiusX = (radius / 16);
				int radiusZ = (radius / 16);
				
				chunks.clear();
				PlayerUtils.broadcast(Main.prefix() + "Voidscape generation started.");
				
				for (int cx = (0 - radiusX); cx < radiusX; cx++) {
					for (int cz = (0 - radiusZ); cz < radiusZ; cz++) {
						Chunk chunk = player.getWorld().getChunkAt(cx, cz);

						finished.add(chunk);
						chunks.add(chunk);
					}
				}
			
				new BukkitRunnable() {
					int i = 0;
					
					public void run() {
						if (i >= chunks.size()) {
							cancel();
							PlayerUtils.broadcast(Main.prefix() + "Voidscape generation finished.");
							return;
						}
						
						Chunk chunk = chunks.get(i);
						
						for (int y = 0; y < 128; y++) {
							for (int x = 0; x < 16; x++) {
								for (int z = 0; z < 17; z++) {
									Block block = chunk.getBlock(x, y, z);
									
									if (block.getType() == Material.STONE || block.getType() == Material.BEDROCK) {
										block.setType(Material.AIR);
									}
								}
							}
						}

						finished.remove(chunk);

						int one = ((chunks.size() - finished.size())*100 / chunks.size());
						
						for (Player online : PlayerUtils.getPlayers()) {
							PacketUtils.sendAction(online, Main.prefix() + "Voidscape generation §6" + one + "% §7finished");
						}
						
						i++;
					}
				}.runTaskTimer(Main.plugin, 2, 2);
			} else {
				sender.sendMessage(Main.prefix() + "You can't use that command.");
			}
		}
		return true;
	}
}