package com.leontg77.uhc.scenario.types;

import net.minecraft.server.v1_8_R3.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * PotentialPermanent scenario class
 * 
 * @author LeonTG77
 */
public class PotentialPermanent extends Scenario implements Listener {
	private boolean enabled = false;

	public PotentialPermanent() {
		super("PotentialPermanent", "You start the game with the normal 10(permanent) hearts and 10 absorption hearts. If you heal- when you have full health- the absorption hearts become the \"permanent\" hearts(potential). However, if you take damage, the absorption hearts will be gone forever. Say you took 1 heart of damage, you now have 10 normal hearts and 9 absorption hearts. Once you have enough to heal, you will only heal up to 19 hearts.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			PotionEffect effect = new PotionEffect(PotionEffectType.ABSORPTION, Short.MAX_VALUE, 4);
			
			for (Player online : PlayerUtils.getPlayers()) {
				online.addPotionEffect(effect);
				online.setMaxHealth(20);
			}
	
			Game game = Game.getInstance();

			game.setGoldenHeadsHeal(2);
			game.setAbsorption(true);
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.removePotionEffect(PotionEffectType.ABSORPTION);
				online.setMaxHealth(20);
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (item == null) {
			return;
		}
		
		if (item.getType() == Material.MILK_BUCKET) {
			player.sendMessage(Main.PREFIX + ChatColor.RED + "You cannot drink milk in PotentialPermanent.");
			event.setItem(new ItemStack (Material.AIR));
			event.setCancelled(true);
			return;
		}
		
		if (item.getType() != Material.GOLDEN_APPLE) {
			return;
		}
		
		CraftPlayer craft = (CraftPlayer) player;
		EntityPlayer handle = craft.getHandle();
		
		float absHearts = craft.getHandle().getAbsorptionHearts();
		
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        player.getWorld().playSound(player.getLocation(), Sound.BURP, 1, 1);
        player.setSaturation(player.getSaturation() + 9.6f);
        player.setFoodLevel(player.getFoodLevel() + 4);
		event.setCancelled(true);
        
		item.setAmount(1);
		player.getInventory().removeItem(item);
		
		if (absHearts != 0) {
			float toTake = Math.min(4, absHearts);
			
			player.setMaxHealth(player.getMaxHealth() + toTake);
			handle.setAbsorptionHearts(absHearts - toTake);
		}
	}
}