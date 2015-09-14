package com.leontg77.uhc.scenario.types;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Bigcrack scenario class
 * 
 * @author dans1988
 */
public class BigCrack extends Scenario implements Listener, CommandExecutor {
	private static final int CHUNK_HEIGHT_LIMIT = 128;
    private static final int BLOCKS_PER_CHUNK = 16;

	private boolean generation = false;
	private boolean enabled = false;

	public BigCrack() {
		super("BigCrack", "A Chunk Error running on the Z axis splits the world in half.");
		Main main = Main.plugin;
		
		main.getCommand("bigcrack").setExecutor(this);
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (generation) {
            event.setCancelled(true);
        }
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can have generate cracks.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("bigcrack")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"BigCrack\" is not enabled.");
				return true;
			}
			
			if (player.hasPermission("uhc.bigcrack.generate")) {
				if (args.length < 3) {
					player.sendMessage(ChatColor.RED + "Usage: /bigcrack <width> <length> <speed>");
	                return true;
	            }

	            int width;
	            int length;
	            int speed;
	            
	            try {
	                width = Integer.parseInt(args[0]);
	                length = Integer.parseInt(args[1]);
	                speed = Integer.parseInt(args[2]);
	            } catch (NumberFormatException ex) {
	            	player.sendMessage(ChatColor.RED + "Invaild number!");
	                return true;
	            }
	            
	            generate(player.getWorld(), length, width, speed);
			} else {
				player.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
	
	public void generate(final World world, final int length, final int width, int speed) {
		generation = true;
        
        int xChunk;
        if (length % BLOCKS_PER_CHUNK == 0) {
            xChunk = length / BLOCKS_PER_CHUNK;
        } else {
            xChunk = (length / BLOCKS_PER_CHUNK) + 1;
        }

        int xMaxChunk = xChunk;
        xChunk = xChunk * -1;
        
        int zChunk;
        if (width % BLOCKS_PER_CHUNK == 0) {
            zChunk = (width) / BLOCKS_PER_CHUNK;
        } else {
            zChunk = ((width) / BLOCKS_PER_CHUNK) + 1;
        }
        
        int zMaxChunk = zChunk;
        zChunk = zChunk * -1;
        
        int delayMultiplier = 0;
        for (int x = xChunk; x <= xMaxChunk; x++) {
            for (int z = zChunk; z <= zMaxChunk; z++) {
                final Chunk chunk = world.getChunkAt(x, z);
                new BukkitRunnable() {
                    public void run() {
                        populate(world, chunk, width, length);
                        PlayerUtils.broadcast(Main.prefix().replaceAll("UHC", "Bigcrack") + "Populated chunk at x = §a" + chunk.getX() + "§7, z = §a" + chunk.getZ() + "§7.");
                    }
                }.runTaskLater(Main.plugin, delayMultiplier * speed);
                delayMultiplier++;
            }
        }
        
        new BukkitRunnable() {
            public void run() {
            	generation = false;
                PlayerUtils.broadcast(Main.prefix().replaceAll("UHC", "Bigcrack") + "Bigcrack generation finished!");
            }
        }.runTaskLater(Main.plugin, delayMultiplier * speed);
    }

    public void populate(World world, Chunk chunk, int width, int length) {
        chunk.load();
        for (int x = 0; x < BLOCKS_PER_CHUNK; x++) {
            for (int z = 0; z < BLOCKS_PER_CHUNK; z++) {
                for (int y = CHUNK_HEIGHT_LIMIT - 1; y >= 0; y--) {
                	Block block = chunk.getBlock(x, y, z);
                    Location location = block.getLocation();
                    
                    int xLocation = location.getBlockX();
                    int zLocation = location.getBlockZ();
                    
                    if (zLocation >= (width * -1) && zLocation <= width && xLocation <= length && xLocation >= (length * -1)) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}