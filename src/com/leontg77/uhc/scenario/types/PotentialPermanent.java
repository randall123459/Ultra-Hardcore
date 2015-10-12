package com.leontg77.uhc.scenario.types;

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
			for (Player online : PlayerUtils.getPlayers()) {
				online.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1726272000, 4));
				online.setMaxHealth(20.0);
			}
	
			Game.getInstance().setAbsorption(true);
			Game.getInstance().setGoldenHeadsHeal(2);
		} else {
			for (Player online : PlayerUtils.getPlayers()) {
				online.removePotionEffect(PotionEffectType.ABSORPTION);
				online.setMaxHealth(20.0);
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
		
		if (item.getType() != Material.GOLDEN_APPLE) {
			if (item.getType() == Material.MILK_BUCKET) {
				player.sendMessage(ChatColor.RED + "You cannot drink milk in PotentialPermanent.");
				event.setItem(new ItemStack (Material.AIR));
				event.setCancelled(true);
			}
			return;
		}
		
		CraftPlayer cplayer = (CraftPlayer) player;
		
		float absHearts = cplayer.getHandle().getAbsorptionHearts();
		
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        player.getWorld().playSound(player.getLocation(), Sound.BURP, 1, 1);
        player.setSaturation(player.getSaturation() + 9.6f);
        player.setFoodLevel(player.getFoodLevel() + 4);
		event.setCancelled(true);
        
		if (player.getItemInHand().getAmount() == 1) {
			player.setItemInHand(new ItemStack (Material.AIR));
		} else {
			ItemStack itemInHand = player.getItemInHand();
			itemInHand.setAmount(itemInHand.getAmount() - 1);
			player.setItemInHand(itemInHand);
		}
		
		if (absHearts != 0) {
			player.setMaxHealth(absHearts >= 4 ? player.getMaxHealth() + 4 : player.getMaxHealth() + absHearts);
			cplayer.getHandle().setAbsorptionHearts(absHearts >= 4 ? absHearts - 4 : 0);
		}
	}
}