package com.leontg77.uhc.scenario.types;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

public class Backpacks extends Scenario implements Listener {
	private boolean enabled = false;

	public Backpacks() {
		super("Backpacks", "Players can type /bp to open up a backpack inventory.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getEntity();
		Block block = player.getLocation().add(0, -1, 0).getBlock();
		
		block.setType(Material.CHEST);
		block.getState().update();
		
		Chest chest = (Chest) block.getState();
		
		for (ItemStack item : player.getEnderChest().getContents()) {
			if (item == null) {
				continue;
			}
			
			chest.getInventory().addItem(item);
		}
		
		player.getEnderChest().clear();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use Backpack commands.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("bp")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Backbacks\" is not enabled.");
				return true;
			}

			player.openInventory(player.getEnderChest());
		}
		return true;
	}
}