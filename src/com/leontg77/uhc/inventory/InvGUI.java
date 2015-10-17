package com.leontg77.uhc.inventory;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Rank;
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
 * This class contains methods for opening the selector inventory, game info inventory, hall of fame inventory and player inventories.
 * 
 * @author LeonTG77
 */
public class InvGUI {
	private Settings settings = Settings.getInstance();
	private static InvGUI manager = new InvGUI();
	
	public HashMap<Player, HashMap<Integer, Inventory>> pagesForPlayer = new HashMap<Player, HashMap<Integer, Inventory>>();
	public HashMap<Player, Integer> currentPage = new HashMap<Player, Integer>();
	
	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static InvGUI getInstance() {
		return manager;
	}
	
	/**
	 * Opens an inventory of all the online players that is playing.
	 * 
	 * @param player the player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openSelector(Player player) {
		ArrayList<Player> list = new ArrayList<Player>(PlayerUtils.getPlayers());
		Inventory inv = null;

		int index = 0;
		
		for (Player online : list) {
			if (Spectator.getInstance().isSpectating(online) || !GameUtils.getGameWorlds().contains(player.getWorld())) {
				list.remove(index);
			}	
			index++;
		}
		
		int pages = ((list.size() / 28) + 1);
		
		pagesForPlayer.put(player, new HashMap<Integer, Inventory>());
		
		for (int current = 1; current <= pages; current++) {
			inv = Bukkit.createInventory(null, 54, "§8» §cPlayer Selector §8«");
			
			for (int i = 0; i < 35; i++) {
				if (list.size() < 1) {
					continue;
				}
				
				if (noItem(i)) {
					continue;
				}
				
				Player target = list.remove(0);
				
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName("§a" + target.getName());
				meta.setLore(Arrays.asList("§7Click to teleport."));
				meta.setOwner(target.getName());
				item.setItemMeta(meta);
				inv.setItem(i, item);
			}
			
			ItemStack nextpage = new ItemStack (Material.ARROW);
			ItemMeta pagemeta = nextpage.getItemMeta();
			pagemeta.setDisplayName(ChatColor.GREEN + "Next page");
			pagemeta.setLore(Arrays.asList("§7Switch to the next page."));
			nextpage.setItemMeta(pagemeta);
			
			ItemStack prevpage = new ItemStack (Material.ARROW);
			ItemMeta prevmeta = prevpage.getItemMeta();
			prevmeta.setDisplayName(ChatColor.GREEN + "Previous page");
			prevmeta.setLore(Arrays.asList("§7Switch to the previous page."));
			prevpage.setItemMeta(prevmeta);
			
			if (current != 1) {
				inv.setItem(47, prevpage);
			}
			
			if (current != pages) {
				inv.setItem(51, nextpage);
			}
			
			pagesForPlayer.get(player).put(current, inv);
		}
		
		inv = pagesForPlayer.get(player).get(1);
		currentPage.put(player, 1);
		
		player.openInventory(inv);
		return inv;
	}

	/**
	 * Opens the inventory of the given target for the given player.
	 * 
	 * @param player player to open for.
	 * @param target the players inv to use.
	 * 
	 * @return The opened inventory.
	 */
	public Inventory openPlayerInventory(final Player player, final Player target) {
		final Inventory inv = Bukkit.getServer().createInventory(target, 54, target.getName() + "'s Inventory");
	
		Main.invsee.put(inv, new BukkitRunnable() {
			public void run() {
				inv.setItem(0, target.getInventory().getHelmet());
				inv.setItem(1, target.getInventory().getChestplate());
				inv.setItem(2, target.getInventory().getLeggings());
				inv.setItem(3, target.getInventory().getBoots());
				inv.setItem(5, target.getItemInHand());
				inv.setItem(6, target.getItemOnCursor());
				
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
					lore.add("§8» §7P:§6" + NameUtils.getPotionName(effects.getType()) + " §7T:§6" + (effects.getAmplifier() + 1) + " §7D:§6" + DateUtils.ticksToString(effects.getDuration() / 20));
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
					inv.setItem(i, glass);
				}
				
				int i = 18;
				
				for (ItemStack item : target.getInventory().getContents()) {
					inv.setItem(i, item);
					i++;
				}
				
				player.updateInventory();
			}
		});
		
		Main.invsee.get(inv).runTaskTimer(Main.plugin, 1, 1);
		player.openInventory(inv);
		
		return inv;
	}
	
	/**
	 * Opens an inventory the given hosts hall of fame.
	 * 
	 * @param player the player opening for.
	 * @param host The owner of the hall of fame.
	 * @return The opened inventory.
	 */
	public Inventory openHOF(Player player, String host) {
		Set<String> keys = Settings.getInstance().getHOF().getConfigurationSection(host).getKeys(false);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		ArrayList<String> list = new ArrayList<String>(keys);
		Inventory inv = null;
		
		int pages = ((list.size() / 28) + 1);
		
		pagesForPlayer.put(player, new HashMap<Integer, Inventory>());
		
		for (int current = 1; current <= pages; current++) {
			inv = Bukkit.createInventory(null, 54, "§8» §c" + host + "'s HoF, Page " + current + " §8«");
			
			for (int i = 0; i < 35; i++) {
				if (list.size() < 1) {
					continue;
				}
				
				if (noItem(i)) {
					continue;
				}
				
				String target = list.remove(0);
				boolean isSpecial = target.endsWith("50") || target.endsWith("00") || target.endsWith("25") || target.endsWith("75");
				
				ItemStack item = new ItemStack (Material.GOLDEN_APPLE, 1, isSpecial ? (short) 1 : (short) 0);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§8» §6" + host + "'s #" + target + " §8«");
				
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§7" + settings.getHOF().getString(host + "." + target + ".date", "N/A"));
				lore.add(" ");
				lore.add("§8» §cWinners:");
				
				for (String winners : settings.getHOF().getStringList(host + "." + target + ".winners")) {
					lore.add("§8» §7" + winners);
				}
				
				lore.add(" ");
				lore.add("§8» §cKills:");
				lore.add("§8» §7" + settings.getHOF().getString(host + "." + target + ".kills", "-1"));
				
				if (!settings.getHOF().getString(host + "." + target + ".teamsize", "FFA").isEmpty()) {
					lore.add(" ");
					lore.add("§8» §cTeamsize:");
					lore.add("§8» §7" + settings.getHOF().getString(host + "." + target + ".teamsize", "FFA"));
				}
				
				lore.add(" ");
				lore.add("§8» §cScenario:");
				
				for (String scenario : settings.getHOF().getString(host + "." + target + ".scenarios", "Vanilla+").split(" ")) {
					lore.add("§8» §7" + scenario);
				}
				
				lore.add(" ");
				meta.setLore(lore);
				item.setItemMeta(meta);
				inv.setItem(i, item);
			}
			
			ItemStack nextpage = new ItemStack (Material.ARROW);
			ItemMeta pagemeta = nextpage.getItemMeta();
			pagemeta.setDisplayName(ChatColor.GREEN + "Next page");
			pagemeta.setLore(Arrays.asList("§7Switch to the next page."));
			nextpage.setItemMeta(pagemeta);
			
			ItemStack prevpage = new ItemStack (Material.ARROW);
			ItemMeta prevmeta = prevpage.getItemMeta();
			prevmeta.setDisplayName(ChatColor.GREEN + "Previous page");
			prevmeta.setLore(Arrays.asList("§7Switch to the previous page."));
			prevpage.setItemMeta(prevmeta);
			
			String name = GameUtils.getHostName(host);
			
			ItemStack head = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta headMeta = (SkullMeta) head.getItemMeta();
			headMeta.setDisplayName("§8» §6Host Info §8«");
			headMeta.setOwner(name);
			ArrayList<String> hlore = new ArrayList<String>();
			hlore.add(" ");
			hlore.add("§8» §7Total games hosted: §6" + Settings.getInstance().getHOF().getConfigurationSection(host).getKeys(false).size());
			hlore.add("§8» §7Rank: §6" + NameUtils.fixString(User.get(PlayerUtils.getOfflinePlayer(name)).getRank().name(), false));
			hlore.add(" ");
			hlore.add("§8» §7Host name: §6" + host);
			hlore.add("§8» §7IGN: §6" + name);
			hlore.add(" ");
			headMeta.setLore(hlore);
			head.setItemMeta(headMeta);
			
			inv.setItem(49, head);
			
			if (current != 1) {
				inv.setItem(47, prevpage);
			}
			
			if (current != pages) {
				inv.setItem(51, nextpage);
			}
			
			pagesForPlayer.get(player).put(current, inv);
		}
		
		inv = pagesForPlayer.get(player).get(1);
		currentPage.put(player, 1);
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Opens the game information inventory.
	 * 
	 * @param player player to open for.
	 * @return The opened inventory.
	 */
	public Inventory openGameInfo(final Player player) {
		final Inventory inv = Bukkit.getServer().createInventory(null, 45, "§8» §cGame Information §8«");
		final ArrayList<String> lore = new ArrayList<String>();
		final Game game = Game.getInstance();
		
		ItemStack general = new ItemStack (Material.SIGN);
		ItemMeta generalMeta = general.getItemMeta();
		generalMeta.setDisplayName("§8» §6General Info §8«");
		lore.add(" ");;
		lore.add("§8» §7Teaming in the arena: §cNot Allowed.");
		lore.add("§8» §7Starter food: §cNone.");
		lore.add(" ");
		lore.add("§8» §7Towering: §aAllowed, but come down at meetup.");
		lore.add("§8» §7Forting: §aAllowed before meetup.");
		lore.add(" ");
		lore.add("§8» §7You can follow our twitter @ArcticUHC to find");
		lore.add(" §7out when our next games are.");
		lore.add(" ");
		lore.add("§8» §7Final heal is 30 seconds after start, ");
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
		lore.add("§8» §7Excessive rage: §eKick.");
		lore.add(" ");
		lore.add("§8» §7Talking other languages in chat: §cMute.");
		lore.add("§8» §7Excessive Swearing: §cMute.");
		lore.add("§8» §7Homophobic: §cMute.");
		lore.add("§8» §7Spamming: §cMute.");
		lore.add("§8» §7Insults: §cMute.");
		lore.add("§8» §7Racism: §cMute.");
		lore.add(" ");
		lore.add("§8» §7Helpop abuse: §4Ban.");
		lore.add("§8» §7Disrespect: §4Ban.");
		lore.add(" ");
		lore.add("§8» §7Spoiling when alive: §aAllowed.");
		lore.add("§8» §7Spoiling when dead: §cNot allowed.");
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
		lore.add("§8» §7TS in random teams games: §cRequired.");
		lore.add(" ");
		lore.add("§8» §7Xray/Cavefinder: §cNot Allowed.");
		lore.add("§8» §7Hacked Client: §cNot Allowed.");
		lore.add("§8» §7Fast Break: §cNot Allowed.");
		lore.add(" ");
		lore.add("§8» §7F3+A Spam: §cNot Allowed.");
		lore.add("§8» §7Full Bright: §aAllowed.");
		lore.add(" ");
		lore.add("§8» §7Benefitting: §4Ban.");
		lore.add("§8» §7Bug abuse: §4Ban.");
		lore.add("§8» §7PvP Log: §4Ban.");
		lore.add("§8» §7PvE Log: §4Ban.");
		lore.add(" ");
		miscMeta.setLore(lore);
		misc.setItemMeta(miscMeta);
		inv.setItem(8, misc);
		lore.clear();
		
		ItemStack staff = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta staffMeta = (SkullMeta) staff.getItemMeta();
		staffMeta.setDisplayName("§8» §6Staff §8«");
		
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
			lore.add("§8» §7Tier 2: " + (game.tier2() ? "§aEnabled." : "§cDisabled."));
			lore.add("§8» §7Splash: " + (game.splash() ? "§aEnabled." : "§cDisabled."));
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
		lore.add("§8» §7Apple Rates: §6" + game.getAppleRates() + "%");
		lore.add("§8» §7Shears: " + (game.shears() ? "§aWork." : "§cDoes not work.") + "");
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
		lore.add("§8» §7Saturation Fix: §aEnabled.");
		lore.add(" ");
		lore.add("§8» §7Border shrinks: §6" + NameUtils.fixString(game.getBorderShrink().getPreText(), false) + game.getBorderShrink().name().toLowerCase() + ".");
		lore.add("§8» §7The border will kill you if you go outside!");
		lore.add(" ");
		miscIMeta.setLore(lore);
		miscI.setItemMeta(miscIMeta);
		inv.setItem(44, miscI);
		lore.clear();
		
		File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
		
		StringBuilder staffs = new StringBuilder();
		StringBuilder owners = new StringBuilder();
		StringBuilder hosts = new StringBuilder();
		
		ArrayList<String> hostL = new ArrayList<String>();
		ArrayList<String> staffL = new ArrayList<String>();
		
		int i = 1;
		
		for (File file : folder.listFiles()) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			if (config.getString("rank").equals(Rank.HOST.name())) {
				hostL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.TRIAL.name())) {
				hostL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.STAFF.name())) {
				staffL.add(config.getString("username"));
			}
		}
		
		for (String sL : hostL) {
			if (hosts.length() > 0) {
				if (hostL.size() == i) {
					hosts.append(" and ");
				} else {
					hosts.append(", ");
				}
			}
			
			hosts.append(sL);
			i++;
		}
		
		i = 1;
		
		for (String sL : staffL) {
			if (staffs.length() > 0) {
				if (staffL.size() == i) {
					staffs.append(" and ");
				} else {
					staffs.append(", ");
				}
			}
			
			staffs.append(sL);
			i++;
		}
		
		i = 1;
		
		for (OfflinePlayer ops : Bukkit.getServer().getOperators()) {
			if (owners.length() > 0) {
				if (Bukkit.getOperators().size() == i) {
					owners.append(" and ");
				} else {
					owners.append(", ");
				}
			}
			
			owners.append(ops.getName());
			i++;
		}
		
		lore.add(" ");
		lore.add("§8» §4Owners:");
		lore.add("§8» §7" + owners.toString());
		lore.add(" ");
		lore.add("§8» §4Hosts:");
		lore.add("§8» §7" + hosts.toString());
		lore.add(" ");
		lore.add("§8» §cStaff:");
		lore.add("§8» §7" + staffs.toString());
		lore.add(" ");
		staffMeta.setLore(lore);
		staffMeta.setOwner("LeonTG77");
		staff.setItemMeta(staffMeta);
		inv.setItem(19, staff);
		lore.clear();
		
		Main.gameInfo.put(inv, new BukkitRunnable() {
			public void run() {
				ItemStack timer = new ItemStack (Material.WATCH);
				ItemMeta timerMeta = timer.getItemMeta();
				timerMeta.setDisplayName("§8» §6Timers §8«");
				lore.add(" ");
				
				if (Game.getInstance().isRecordedRound()) {
					lore.add("§8» §7Current Episode: §a" + Timers.meetup + " mins");
					lore.add("§8» §7Time to next episode: §a" + Timers.heal + " mins");
				}
				else if (GameUtils.getTeamSize().startsWith("No") || GameUtils.getTeamSize().startsWith("Open")) {
					lore.add("§8» §7There are no matches running.");
				}
				else if (!State.isState(State.INGAME)) {
					lore.add("§8» §7The game has not started yet.");
				}
				else {
					lore.add(Timers.healSeconds <= 0 ? "§8» §eFinal heal has passed." : "§8» §7Final heal in: §a" + DateUtils.ticksToString(Timers.healSeconds));
					lore.add(Timers.pvpSeconds <= 0 ? "§8» §aPvP is enabled." : "§8» §7PvP in: §a" + DateUtils.ticksToString(Timers.pvpSeconds));
					lore.add(Timers.meetupSeconds <= 0 ? "§8» §cMeetup is now!" : "§8» §7Meetup in: §a" + DateUtils.ticksToString(Timers.meetupSeconds));
				}
				
				lore.add(" ");
				timerMeta.setLore(lore);
				timer.setItemMeta(timerMeta);
				inv.setItem(25, timer);
				lore.clear();
				player.updateInventory();
			}
		});
		
		Main.gameInfo.get(inv).runTaskTimer(Main.plugin, 1, 1);
		player.openInventory(inv);
		
		return inv;
	}
	
	/**
	 * Check if this slot shall have no items.
	 * 
	 * @param slot The slot.
	 * @return True if it shouldn't, false otherwise.
	 */
	private boolean noItem(int slot) {
		switch (slot) {
		case 0:
		case 8:
		case 9:
		case 17:
		case 18:
		case 26:
		case 27:
		case 35:
		case 36:
			return true;
		default:
			return false;
		}
	}
}