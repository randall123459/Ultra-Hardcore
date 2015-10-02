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
 * SlimyCrack scenario class
 * 
 * @author dans1988
 */
public class SlimyCrack extends Scenario implements Listener, CommandExecutor {
	private static final int CHUNK_HEIGHT_LIMIT = 128;
    private static final int BLOCKS_PER_CHUNK = 16;
    private static final int STAIRCASE_START = 16;

	private boolean generation = false;
	private boolean enabled = false;

	public SlimyCrack() {
		super("SlimyCrack", "There is a giant fissure generated through natural terrain which exposes ores, caves mineshafts and the like but at the bottom there are slime blocks except at the sides where there are gaps that players are still able to fall down. The crack goes through 0,0 and is parallel to the x axis.");
		Main main = Main.plugin;
		
		main.getCommand("slimecrack").setExecutor(this);
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
		
		if (cmd.getName().equalsIgnoreCase("slimecrack")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"SlimyCrack\" is not enabled.");
				return true;
			}
			
			if (player.hasPermission("uhc.slimycrack.generate")) {
				if (args.length < 4) {
					player.sendMessage(ChatColor.RED + "Usage: /slimecrack <width> <length> <speed> <z>");
	                return true;
	            }

	            int width;
	            int length;
	            int speed;
	            boolean z;
	            
	            try {
	                width = Integer.parseInt(args[0]);
	                length = Integer.parseInt(args[1]);
	                speed = Integer.parseInt(args[2]);
	                z = (args[3].equalsIgnoreCase("true") ? true : false);
	            } catch (NumberFormatException ex) {
	            	player.sendMessage(ChatColor.RED + "Invaild number!");
	                return true;
	            }
	            
	            generate(player.getWorld(), length, width, speed, z);
			} else {
				sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		return true;
	}
	
	public void generate(final World world, final int length, final int width, int speed, final boolean z2) {
		generation = true;
        
        int xChunk;
        if (z2) {
            if (length % BLOCKS_PER_CHUNK == 0) {
                xChunk = length / BLOCKS_PER_CHUNK;
            } else {
                xChunk = (length / BLOCKS_PER_CHUNK) + 1;
            }
        } else {
            if (width % BLOCKS_PER_CHUNK == 0) {
                xChunk = (width + STAIRCASE_START) / BLOCKS_PER_CHUNK;
            } else {
                xChunk = ((width + STAIRCASE_START) / BLOCKS_PER_CHUNK) + 1;
            }
        }

        int xMaxChunk = xChunk;
        xChunk = xChunk * -1;
        
        int zChunk;
        if (z2) {
            if (width % BLOCKS_PER_CHUNK == 0) {
                zChunk = (width + STAIRCASE_START) / BLOCKS_PER_CHUNK;
            } else {
                zChunk = ((width + STAIRCASE_START) / BLOCKS_PER_CHUNK) + 1;
            }
        } else {
            if (length % BLOCKS_PER_CHUNK == 0) {
                zChunk = length / BLOCKS_PER_CHUNK;
            } else {
                zChunk = (length / BLOCKS_PER_CHUNK) + 1;
            }
        }
        
        int zMaxChunk = zChunk;
        zChunk = zChunk * -1;
        
        int delayMultiplier = 0;
        
        if (z2) {
            for (int x = xChunk; x <= xMaxChunk; x++) {
                for (int z = zChunk; z <= zMaxChunk; z++) {
                    final Chunk chunk = world.getChunkAt(x, z);
                    new BukkitRunnable() {
                        public void run() {
                            populate(world, chunk, width, length, z2);
    						
    						for (Player online : PlayerUtils.getPlayers()) {
    							PlayerUtils.sendAction(online, Main.prefix().replaceAll("UHC", "SlimyCrack") + "Populated chunk at x = §a" + chunk.getX() + "§7, z = §a" + chunk.getZ() + "§7.");
    						}
                        }
                    }.runTaskLater(Main.plugin, delayMultiplier * speed);
                    delayMultiplier++;
                }
            }
        } else {
            for (int z = zChunk; z <= zMaxChunk; z++) {
            	for (int x = xChunk; x <= xMaxChunk; x++) {
                    final Chunk chunk = world.getChunkAt(x, z);
                    new BukkitRunnable() {
                        public void run() {
                            populate(world, chunk, width, length, z2);
    						
    						for (Player online : PlayerUtils.getPlayers()) {
    							PlayerUtils.sendAction(online, Main.prefix().replaceAll("UHC", "SlimyCrack") + "Populated chunk at x = §a" + chunk.getX() + "§7, z = §a" + chunk.getZ() + "§7.");
    						}
                        }
                    }.runTaskLater(Main.plugin, delayMultiplier * speed);
                    delayMultiplier++;
                }
            }
        }
        
        new BukkitRunnable() {
            public void run() {
            	generation = false;
                PlayerUtils.broadcast(Main.prefix().replaceAll("UHC", "SlimyCrack") + "SlimyCrack generation finished!");
            }
        }.runTaskLater(Main.plugin, delayMultiplier * speed);
    }

    public void populate(World world, Chunk chunk, int width, int length, boolean z2) {
        chunk.load();
        for (int x = 0; x < BLOCKS_PER_CHUNK; x++) {
            for (int z = 0; z < BLOCKS_PER_CHUNK; z++) {
                for (int y = CHUNK_HEIGHT_LIMIT - 1; y >= 0; y--) {
                	Block block = chunk.getBlock(x, y, z);
                    Location location = block.getLocation();
                    
                    int xLocation = location.getBlockX();
                    int yLocation = location.getBlockY();
                    int zLocation = location.getBlockZ();
                    
                    int stairWidth = width + STAIRCASE_START - y;
                    
                    if (z2) {
                        if (zLocation >= (width * -1) && zLocation <= width && xLocation <= length && xLocation >= (length * -1) && yLocation > STAIRCASE_START) {
                            block.setType(Material.AIR);
                        } else if (y <= STAIRCASE_START && y != 1) {
                            if (zLocation >= (stairWidth * -1) && zLocation <= stairWidth && xLocation <= length && xLocation >= (length * -1)) {
                                block.setType(Material.AIR);
                            }
                        } else if (y == 1) {
                            if (zLocation >= (width * -1) && zLocation <= width && xLocation <= length && xLocation >= (length * -1)) {
                                block.setType(Material.SLIME_BLOCK);
                            } else if (zLocation >= (stairWidth * -1) && zLocation <= stairWidth && xLocation <= length && xLocation >= (length * -1)) {
                                block.setType(Material.AIR);
                            }
                        }
                    } else {
                        if (xLocation >= (width * -1) && xLocation <= width && zLocation <= length && zLocation >= (length * -1) && yLocation > STAIRCASE_START) {
                            block.setType(Material.AIR);
                        } else if (y <= STAIRCASE_START && y != 1) {
                            if (xLocation >= (stairWidth * -1) && xLocation <= stairWidth && zLocation <= length && zLocation >= (length * -1)) {
                                block.setType(Material.AIR);
                            }
                        } else if (y == 1) {
                            if (xLocation >= (width * -1) && xLocation <= width && zLocation <= length && zLocation >= (length * -1)) {
                                block.setType(Material.SLIME_BLOCK);
                            } else if (xLocation >= (stairWidth * -1) && xLocation <= stairWidth && zLocation <= length && zLocation >= (length * -1)) {
                            	if (block.getType() != Material.SLIME_BLOCK) {
                                    block.setType(Material.AIR);
                            	}
                            }
                        }
                    }
                }
            }
        }
    }
}