package com.leontg77.uhc.scenario.types;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * BlockRush scenario class
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class BlockRush extends Scenario implements Listener {
	private HashSet<String> mined = new HashSet<String>();
	private boolean enabled = false;

	public BlockRush() {
		super("BlockRush", "Mining a specific block type for the first time gives you 1 gold ingot.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (!mined.contains(block.getType().name() + block.getState().getRawData())) {
			mined.add(block.getType().name() + block.getState().getRawData());
			PlayerUtils.broadcast(Main.prefix().replaceFirst("UHC", "BlockRush") + ChatColor.GREEN + player.getName() + " §7broke §6" + block.getType().name().toLowerCase().replaceAll("_", " ") + (block.getState().getRawData() > 0 ? ":" + block.getState().getRawData() : "") + "§7 first.");
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GOLD_INGOT));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
	}
}