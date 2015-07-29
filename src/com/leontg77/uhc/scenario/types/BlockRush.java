package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

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
import com.leontg77.uhc.util.PlayerUtils;

public class BlockRush extends Scenario implements Listener {
	private ArrayList<Material> crafted = new ArrayList<Material>();
	private boolean enabled = false;

	public BlockRush() {
		super("BlockRush", "Mining a specific block type for the first time gives you a reward, usually 1 gold ingot.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (!crafted.contains(block.getType())) {
			crafted.add(block.getType());
			PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN).replaceAll("UHC", "Inventors") + player.getName() + " §7broke " + block.getType().name().toLowerCase().replaceAll("_", "") + " first.");
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GOLD_INGOT));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
	}
}