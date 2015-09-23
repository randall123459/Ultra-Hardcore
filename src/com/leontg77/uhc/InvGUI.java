package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main.Border;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * The inventory managing class.
 * <p>
 * This class contains methods for opening the selector inventory, rules inventory and player inventories.
 * 
 * @author LeonTG77
 */
public class InvGUI {
	private static InvGUI manager = new InvGUI();
	
	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static InvGUI getManager() {
		return manager;
	}
	
	/**
	 * Opens an inventory of all the online players that is playing.
	 * 
	 * @param player the player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openSelector(Player player) {
		Inventory inv = Bukkit.createInventory(null, PlayerUtils.playerInvSize(), "Player Selector");
	
		for (Player online : PlayerUtils.getPlayers()) {
			if (!Spectator.getManager().isSpectating(online) && !online.getWorld().getName().equals("lobby")) {
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + online.getName());
				meta.setOwner(online.getName());
				meta.setLore(Arrays.asList(ChatColor.GRAY + "Click to teleport to §a" + online.getName() + "§f."));
				item.setItemMeta(meta);
				inv.addItem(item);
			}	
		}
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Opens the inventory of a target player.
	 * 
	 * @param player player to open for.
	 * @param target the players inv to use.
	 * @return The opened inventory.
	 */
	public Inventory openInv(final Player player, final Player target) {
		final Inventory inv = Bukkit.getServer().createInventory(target, 54, target.getName().substring(0, target.getName().length() > 52 ? 52 : target.getName().length()) + "'s Inventory");
	
		Main.invsee.put(inv, new BukkitRunnable() {
			public void run() {
				if (inv.getItem(0) != target.getInventory().getHelmet()) {
					inv.setItem(0, target.getInventory().getHelmet());
				}

				if (inv.getItem(1) != target.getInventory().getChestplate()) {
					inv.setItem(1, target.getInventory().getChestplate());
				}

				if (inv.getItem(2) != target.getInventory().getLeggings()) {
					inv.setItem(2, target.getInventory().getLeggings());
				}

				if (inv.getItem(3) != target.getInventory().getBoots()) {
					inv.setItem(3, target.getInventory().getBoots());
				}

				if (inv.getItem(5) != target.getItemInHand()) {
					inv.setItem(5, target.getItemInHand());
				}

				if (inv.getItem(6) != target.getItemOnCursor()) {
					inv.setItem(6, target.getItemOnCursor());
				}
				
				ItemStack info = new ItemStack (Material.BOOK);
				ItemMeta infoMeta = info.getItemMeta();
				infoMeta.setDisplayName("§8» §6Player Info §8«");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§8» §7Name: §a" + target.getName());
				lore.add(" ");
				int health = (int) target.getHealth();
				lore.add("§8» §7Hearts: §6" + (((double) health) / 2) + "§4♥");
				lore.add("§8» §7Percent: §6" + NumberUtils.makePercent(target.getHealth()) + "%");
				lore.add("§8» §7Hunger: §6" + (target.getFoodLevel() / 2));
				lore.add("§8» §7Xp level: §6" + target.getLevel());
				lore.add("§8» §7Location: §6x:" + target.getLocation().getBlockX() + ", y:" + target.getLocation().getBlockY() + ", z:" + target.getLocation().getBlockZ() + " (" + target.getWorld().getEnvironment().name().replaceAll("_", "").toLowerCase().replaceAll("normal", "overworld") + ")");
				lore.add(" ");
				lore.add("§8» §cPotion effects:");
				if (target.getActivePotionEffects().size() == 0) {
					lore.add("§8» §7None");
				}
				for (PotionEffect effects : target.getActivePotionEffects()) {
					if ((effects.getDuration() / 20) > 0) {
						lore.add("§8» §7P:§6" + NameUtils.getPotionName(effects.getType()) + " §7T:§6" + (effects.getAmplifier() + 1) + " §7D:§6" + DateUtils.ticksToString(effects.getDuration() / 20));
					}
				}
				infoMeta.setLore(lore);
				info.setItemMeta(infoMeta);
				inv.setItem(8, info);
				lore.clear();
				
				for (int i = 9; i < 18; i++) {
					ItemStack glass = new ItemStack (Material.STAINED_GLASS_PANE, 1, (short) 15);
					ItemMeta glassMeta = glass.getItemMeta();
					glassMeta.setDisplayName("§0:>");
					glass.setItemMeta(glassMeta);
					inv.setItem(8, info);
					
					if (inv.getItem(i) != glass) {
						inv.setItem(i, glass);
					}
				}
				
				int i = 18;
				for (ItemStack item : target.getInventory().getContents()) {
					if (inv.getItem(i) != item) {
						inv.setItem(i, item);
					}
					i++;
				}
				
				player.updateInventory();
			}
		});
		Main.invsee.get(inv).runTaskTimer(Main.plugin, 1, 1);
		
		player.openInventory(inv);
		return inv;
	}
	
	public Inventory openGameInfo(final Player player) {
		final Inventory inv = Bukkit.getServer().createInventory(null, 45, "§4ArcticUHC Game Information");
		final ArrayList<String> lore = new ArrayList<String>();
		Game game = Game.getInstance();
		
		ItemStack general = new ItemStack (Material.SIGN);
		ItemMeta generalMeta = general.getItemMeta();
		generalMeta.setDisplayName("§8» §6General Info §8«");
		lore.add(" ");
		lore.add("§8» §7Starter food: §65 minutes of starter saturation.");
		lore.add("§8» §7Towering: §aAllowed, but come down at meetup.");
		lore.add("§8» §7Forting: §aAllowed before meetup.");
		lore.add(" ");
		lore.add("§8» §7You can follow our twitter @ArcticUHC to find");
		lore.add(" §7out when our next games are.");
		lore.add(" ");
		lore.add("§8» §7Final heal is 1 minute after start, ");
		lore.add(" §7no more are given after that.");
		lore.add(" ");
		lore.add("§8» §7Our UHC plugin is custom coded by LeonTG77.");
		lore.add(" ");
		lore.add("§8» §7VIP's are trusted players, they can spectate");
		lore.add(" §7and join before whitelist is off");
		lore.add(" ");
		generalMeta.setLore(lore);
		general.setItemMeta(generalMeta);
		inv.setItem(0, general);
		lore.clear();
		
		ItemStack chat = new ItemStack (Material.PAPER);
		ItemMeta chatMeta = chat.getItemMeta();
		chatMeta.setDisplayName("§8» §6Chat Rules §8«");
		lore.add(" ");
		lore.add("§8» §7Spoiling: §cNot allowed after you died.");
		lore.add("§8» §7Spamming: §cMute, ban if excessive.");
		lore.add("§8» §7Swearing: §aAllowed unless it's done excessively.");
		lore.add(" ");
		chatMeta.setLore(lore);
		chat.setItemMeta(chatMeta);
		inv.setItem(2, chat);
		lore.clear();
		
		ItemStack pvp = new ItemStack (Material.IRON_SWORD);
		ItemMeta pvpMeta = pvp.getItemMeta();
		pvpMeta.setDisplayName("§8» §6PvP Rules §8«");
		lore.add(" ");
		lore.add("§8» §7iPvP: §cNot Allowed before pvp.");
		lore.add("§8» §7Team Killing: " + (GameUtils.getTeamSize().startsWith("r") && !ScenarioManager.getInstance().getScenario("Moles").isEnabled() ? "§cNot Allowed." : "§aAllowed."));
		lore.add("§8» §7Stalking: §aAllowed. §c(Not excessive)");
		lore.add("§8» §7Stealing: §aAllowed.");
		lore.add("§8» §7Crossteaming: §cNot Allowed.");
		lore.add(" ");
		pvpMeta.setLore(lore);
		pvpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		pvp.setItemMeta(pvpMeta);
		inv.setItem(4, pvp);
		lore.clear();
		
		ItemStack mining = new ItemStack (Material.DIAMOND_PICKAXE);
		ItemMeta miningMeta = mining.getItemMeta();
		miningMeta.setDisplayName("§8» §6Mining Rules §8«");
		lore.add(" ");
		lore.add("§8» §4IMPORTANT:");
		lore.add("§8» §7Gold and Diamonds only spawn in caves.");
		lore.add(" ");
		lore.add("§8» §7Stripmining: §aAllowed.");
		lore.add("§8» §7Branchmining: §aAllowed.");
		lore.add("§8» §7Pokeholing: §aAllowed.");
		lore.add("§8» §7Blastmining: §aAllowed.");
		lore.add("§8» §7Staircasing: §aAllowed.");
		lore.add("§8» §7Rollercoastering: §aAllowed.");
		lore.add("§8» §7Digging to sounds: §aAllowed.");
		lore.add("§8» §7Digging to entities: §aAllowed.");
		lore.add("§8» §7Digging to players: §aAllowed.");
		lore.add(" ");
		miningMeta.setLore(lore);
		miningMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		mining.setItemMeta(miningMeta);
		inv.setItem(6, mining);
		lore.clear();
		
		ItemStack misc = new ItemStack (Material.NETHER_STAR);
		ItemMeta miscMeta = misc.getItemMeta();
		miscMeta.setDisplayName("§8» §6Misc. Rules §8«");
		lore.add(" ");
		lore.add("§8» §7Suiciding in random team games: §cNot Allowed.");
		lore.add("§8» §7TS in random team games: §cRequired.");
		lore.add(" ");
		lore.add("§8» §7Xray/Cavefinder: §cNot Allowed.");
		lore.add("§8» §7Hacked Client: §cNot Allowed.");
		lore.add("§8» §7Fast Break: §cNot Allowed.");
		lore.add(" ");
		lore.add("§8» §7F3+A Spam: §cNot Allowed.");
		lore.add("§8» §7Full Bright: §aAllowed.");
		lore.add(" ");
		miscMeta.setLore(lore);
		misc.setItemMeta(miscMeta);
		inv.setItem(8, misc);
		lore.clear();
		
		ItemStack staff = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta staffMeta = (SkullMeta) staff.getItemMeta();
		staffMeta.setDisplayName("§8» §6Staff §8«");
		lore.add(" ");
		lore.add("§8» §4Owner/Dev:");
		lore.add("§8» §7LeonTG77");
		lore.add(" ");
		lore.add("§8» §4Hosts:");
		lore.add("§8» §7LeonTG77, PolarBlunk and Itz_Isaac");
		lore.add(" ");
		lore.add("§8» §cStaff:");
		lore.add("§8» §7D4mnX, Vael, TitanUHC, BLA2K14 and Zephey");
		lore.add(" ");
		staffMeta.setLore(lore);
		staffMeta.setOwner("LeonTG77");
		staff.setItemMeta(staffMeta);
		inv.setItem(19, staff);
		lore.clear();
		
		ItemStack commands = new ItemStack (Material.BANNER, 1, (short) 1);
		ItemMeta commandsMeta = commands.getItemMeta();
		commandsMeta.setDisplayName("§8» §6Useful commands §8«");
		lore.add(" ");
		lore.add("§a/helpop §8» §7§oAsk for help by the staff.");
		lore.add("§a/rules §8» §7§oView this menu :o");
		lore.add("§a/post §8» §7§oGet a link to the matchpost.");
		lore.add("§a/team §8» §7§oView the team help menu.");
		lore.add("§a/scen §8» §7§oView the enabled scenarios.");
		lore.add("§a/timeleft §8» §7§oView the timer.");
		lore.add("§a/border §8» §7§oView the current border size.");
		lore.add("§a/hof §8» §7§oView the hall of fame.");
		lore.add("§a/lag §8» §7§oView the server performance.");
		lore.add("§a/ms §8» §7§oView your or someones ping.");
		lore.add("§a/list §8» §7§oView online players.");
		lore.add("§a/pm §8» §7§oTalk in team chat.");
		lore.add("§a/tl §8» §7§oTell your team your coords.");
		lore.add(" ");
		commandsMeta.setLore(lore);
		commands.setItemMeta(commandsMeta);
		inv.setItem(21, commands);
		lore.clear();
		
		ItemStack scenario = new ItemStack (Material.BANNER, 1, (short) 14);
		ItemMeta scenarioMeta = scenario.getItemMeta();
		scenarioMeta.setDisplayName("§8» §6Enabled Scenarios §8«");
		lore.add(" ");
		lore.add("§8» §cScenarios:");
		if (ScenarioManager.getInstance().getEnabledScenarios().isEmpty()) {
			lore.add("§8» §7None!");
		}
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			lore.add("§8» §7" + scen.getName());
		}
		lore.add(" ");
		lore.add("§8» §7Use §a/scen §7to view info about the scenarios.");
		lore.add(" ");
		scenarioMeta.setLore(lore);
		scenario.setItemMeta(scenarioMeta);
		inv.setItem(23, scenario);
		lore.clear();
		
		ItemStack nether = new ItemStack (Material.NETHERRACK);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName("§8» §6Nether Info §8«");
		lore.add(" ");
		lore.add("§8» §7Nether: " + (game.nether() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7The End: " + (game.theEnd() ? "§aEnabled." : "§cDisabled."));
		lore.add(" ");
		if (game.nether()) {
			lore.add("§8» §7Trapping: " + (game.theEnd() ? "§cNot allowed." : "§aAllowed."));
			lore.add("§8» §7Camping: §aAllowed.");
			lore.add(" ");
			lore.add("§8» §7Strength: " + (game.nerfedStrength() ? "§cNerfed" : "§aVanilla"));
			lore.add("§8» §7Tier 2: §aEnabled.");
			lore.add("§8» §7Splash: §aEnabled.");
			lore.add(" ");
			lore.add("§8» §7Golden Melon: §6" + (game.goldenMelonNeedsIngots() ? "Gold Ingots." : "Golden Nuggets."));
			lore.add("§8» §7Ghast Drop: §6" + (game.ghastDropGold() ? "Gold Ingot." : "Ghast Tear."));
			lore.add(" ");
		}
		netherMeta.setLore(lore);
		nether.setItemMeta(netherMeta);
		inv.setItem(36, nether);
		lore.clear();
		
		ItemStack healing = new ItemStack (Material.GOLDEN_APPLE);
		ItemMeta healingMeta = healing.getItemMeta();
		healingMeta.setDisplayName("§8» §6Healing Info §8«");
		lore.add(" ");
		lore.add("§8» §7Absorption: " + (game.absorption() ? "§aEnabled." : "§cDisabled.") + "");
		lore.add("§8» §7Golden Heads: " + (game.goldenHeads() ? "§aEnabled." : "§cDisabled.") + "");
		if (game.goldenHeads()) {
			lore.add("§8» §7Heads Heal: §6" + game.goldenHeadsHeal() + " hearts.");
		}
		lore.add("§8» §7Notch Apples: " + (game.notchApples() ? "§aEnabled." : "§cDisabled.") + "");
		lore.add(" ");
		healingMeta.setLore(lore);
		healing.setItemMeta(healingMeta);
		inv.setItem(38, healing);
		lore.clear();
		
		ItemStack rates = new ItemStack (Material.FLINT);
		ItemMeta ratesMeta = rates.getItemMeta();
		ratesMeta.setDisplayName("§8» §6Rates Info §8«");
		lore.add(" ");
		lore.add("§8» §7Apple Rates: §6Vanilla (0.55%)");
		lore.add("§8» §7Shears: " + (game.shears() ? "§aEnabled." : "§cDisabled.") + "");
		lore.add("§8» §7Flint Rates: §6" + game.getFlintRates() + "%");
		lore.add(" ");
		lore.add("§8» §7Mob Rates: §6Vanilla.");
		lore.add("§8» §7Ore Rates: §6Vanilla.");
		lore.add("§8» §7Cave Rates: §6Vanilla.");
		lore.add(" ");
		lore.add("§8» §7Witch Potion: §6If poisoned 100%, if not 30%");
		lore.add(" ");
		ratesMeta.setLore(lore);
		rates.setItemMeta(ratesMeta);
		inv.setItem(40, rates);
		lore.clear();
		
		ItemStack horse = new ItemStack (Material.SADDLE);
		ItemMeta horseMeta = horse.getItemMeta();
		horseMeta.setDisplayName("§8» §6Horse Info §8«");
		lore.add(" ");
		lore.add("§8» §7Horses: §aEnabled.");
		lore.add("§8» §7Horse Healing: §aEnabled.");
		lore.add("§8» §7Horse Armor: §aEnabled.");
		lore.add(" ");
		horseMeta.setLore(lore);
		horse.setItemMeta(horseMeta);
		inv.setItem(42, horse);
		lore.clear();
		
		ItemStack miscI = new ItemStack (Material.ENDER_PEARL);
		ItemMeta miscIMeta = miscI.getItemMeta();
		miscIMeta.setDisplayName("§8» §6Misc. Info §8«");
		lore.add(" ");
		lore.add("§8» §7Enderpearl Damage: " + (game.pearlDamage() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7Death Lightning: " + (game.deathLightning() ? "§aEnabled." : "§cDisabled."));
		lore.add(" ");
		lore.add("§8» §7Border shrinks: §6" + ((game.getBorderShrinkTime() == Border.NEVER ? "Never" : (game.getBorderShrinkTime() == Border.START ? "From " : "At ") + game.getBorderShrinkTime().name().toLowerCase())) + ".");
		lore.add("§8» §7Saturation Fix: §aEnabled.");
		lore.add(" ");
		miscIMeta.setLore(lore);
		miscI.setItemMeta(miscIMeta);
		inv.setItem(44, miscI);
		lore.clear();
		
		Main.rules.put(inv, new BukkitRunnable() {
			public void run() {
				ItemStack timer = new ItemStack (Material.WATCH);
				ItemMeta timerMeta = timer.getItemMeta();
				timerMeta.setDisplayName("§8» §6Timers §8«");
				lore.add(" ");
				if (Game.getInstance().isRR()) {
					lore.add("§8» §7Current Episode: §a" + Runnables.meetup + " mins");
					lore.add("§8» §7Time to next episode: §a" + Runnables.heal + " mins");
				}
				else if (GameUtils.getTeamSize().startsWith("No") || GameUtils.getTeamSize().startsWith("Open")) {
					lore.add("§8» §7There are no matches running.");
				}
				else if (!State.isState(State.INGAME)) {
					lore.add("§8» §7The game has not started yet.");
				}
				else {
					lore.add(Runnables.healSeconds <= 0 ? "§8» §eFinal heal has passed." : "§8» §7Final heal in: §a" + DateUtils.ticksToString(Runnables.healSeconds));
					lore.add(Runnables.pvpSeconds <= 0 ? "§8» §aPvP is enabled." : "§8» §7PvP in: §a" + DateUtils.ticksToString(Runnables.pvpSeconds));
					lore.add(Runnables.meetupSeconds <= 0 ? "§8» §cMeetup is now!" : "§8» §7Meetup in: §a" + DateUtils.ticksToString(Runnables.meetupSeconds));
				}
				lore.add(" ");
				timerMeta.setLore(lore);
				timer.setItemMeta(timerMeta);
				inv.setItem(25, timer);
				lore.clear();
				player.updateInventory();
			}
		});
		
		Main.rules.get(inv).runTaskTimer(Main.plugin, 1, 1);
		player.openInventory(inv);
		
		return inv;
	}
}