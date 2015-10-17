package com.leontg77.uhc.scenario.types;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * DragonRush scenario class
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class DragonRush extends Scenario implements Listener {
	private boolean enabled = false;
	private int placed = 0;
	
	public DragonRush() {
		super("DragonRush", "The first team to kill the dragon wins the game.");
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (Spectator.getInstance().isSpectating(player)) {
        	return;
        }

        if (event.getClickedBlock() == null) {
        	return;
        }
        
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        	return;
        }
        
        if (!event.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME)) {
        	return;
        }
        
        if (event.getClickedBlock().getLocation().distance(new Location(event.getClickedBlock().getWorld(), 0, event.getClickedBlock().getLocation().getY(), 0)) > 15) {
        	return;
        }
        
        if (event.getClickedBlock().getState().getRawData() > 3) {
        	return;
        }
        
        if (event.getItem() == null) {
        	return;
        }
        
        if (event.getItem().getType() == Material.EYE_OF_ENDER) {
        	placed++;
        	
        	if (placed == 3) {
            	PlayerUtils.broadcast(Main.prefix() + "§d§l§oThe portal has been activated.");
            	
            	for (Player online : PlayerUtils.getPlayers()) {
    				online.playSound(online.getLocation(), Sound.PORTAL_TRAVEL, 1, 1);
    			}
            	
            	Location loc = new Location(event.getClickedBlock().getWorld(), 0, event.getClickedBlock().getLocation().getY(), 0);
            	
            	for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
					for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
						event.getClickedBlock().getWorld().getBlockAt(x, loc.getBlockY(), z).setType(Material.ENDER_PORTAL);
						event.getClickedBlock().getWorld().getBlockAt(x, loc.getBlockY(), z).getState().update();
					}
            	}
        	} else if (placed < 3) {
            	PlayerUtils.broadcast(Main.prefix() + "An eye has been placed (§a" + placed + "§7/§a3§7)");
            	
            	for (Player online : PlayerUtils.getPlayers()) {
    				online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
    			}
        	}
        }
    }
	
	@EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof EnderDragon) {
        	Player killer = entity.getKiller();
        	
        	if (killer != null) {
        		PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
				PlayerUtils.broadcast(ChatColor.RED + " ");
				PlayerUtils.broadcast(ChatColor.RED + " " + killer.getName() + " killed the dragon and their team won the game.");
				PlayerUtils.broadcast(ChatColor.RED + " ");
				PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (online.getWorld() != killer.getWorld()) {
						online.playSound(online.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
					}
				}
        	}
        }
    }
}