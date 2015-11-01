package com.leontg77.uhc;

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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.User.Rank;
import com.leontg77.uhc.User.Stat;
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
	
	public Inventory openStats(Player player, User user) {
		Inventory inv = Bukkit.createInventory(user.getPlayer(), InventoryType.HOPPER, "» §7" + user.getPlayer().getName() + "'s Stats");
		ArrayList<String> lore = new ArrayList<String>(); 
		
		ItemStack general = new ItemStack (Material.SIGN);
		ItemMeta generalMeta = general.getItemMeta();
		generalMeta.setDisplayName("§8» §6General Stats §8«");
		lore.add(" ");
		lore.add("§8» §7Games played: §a" + user.getStat(Stat.GAMESPLAYED));
		lore.add("§8» §7Wins: §a" + user.getStat(Stat.WINS));
		lore.add(" ");
		lore.add("§8» §7Hostile kills: §a" + user.getStat(Stat.HOSTILEMOBKILLS));
		lore.add("§8» §7Animal kills: §a" + user.getStat(Stat.ANIMALKILLS));
		lore.add("§8» §7Damage taken: §a" + (((double) user.getStat(Stat.DAMAGETAKEN)) / 2));
		lore.add(" ");
		generalMeta.setLore(lore);
		general.setItemMeta(generalMeta);
		inv.setItem(0, general);
		lore.clear();
		
		ItemStack pvpmining = new ItemStack (Material.DIAMOND_AXE);
		ItemMeta pvpminingMeta = pvpmining.getItemMeta();
		pvpminingMeta.setDisplayName("§8» §6PvP & Mining Stats §8«");
		lore.add(" ");
		lore.add("§8» §7Highest Arena Killstreak: §a" + user.getStat(Stat.ARENAKILLSTREAK));
		lore.add("§8» §7Highest Killstreak: §a" + user.getStat(Stat.KILLSTREAK));
		lore.add(" ");
		lore.add("§8» §7Kills: §a" + user.getStat(Stat.KILLS));
		lore.add("§8» §7Deaths: §a" + user.getStat(Stat.DEATHS));
		if (user.getStat(Stat.DEATHS) == 0) {
			lore.add("§8» §7KDR: §a" + user.getStat(Stat.KILLS));
		} else {
			lore.add("§8» §7KDR: §a" + user.getStat(Stat.KILLS) / user.getStat(Stat.DEATHS));
		}
		lore.add(" ");
		lore.add("§8» §7Diamonds mined: §a" + user.getStat(Stat.DIAMONDS));
		lore.add("§8» §7Gold mined: §a" + user.getStat(Stat.GOLD));
		lore.add(" ");
		lore.add("§8» §7Arena Kills: §a" + user.getStat(Stat.ARENAKILLS));
		lore.add("§8» §7Arena Deaths: §a" + user.getStat(Stat.ARENADEATHS));
		if (user.getStat(Stat.ARENADEATHS) == 0) {
			lore.add("§8» §7Arena KDR: §a" + user.getStat(Stat.ARENAKILLS));
		} else {
			lore.add("§8» §7Arena KDR: §a" + user.getStat(Stat.ARENAKILLS) / user.getStat(Stat.ARENADEATHS));
		}
		lore.add(" ");
		pvpminingMeta.setLore(lore); 
		pvpminingMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		pvpminingMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		pvpmining.setItemMeta(pvpminingMeta);
		inv.setItem(2, pvpmining);
		lore.clear();
		
		ItemStack misc = new ItemStack (Material.NETHER_STALK);
		ItemMeta miscMeta = misc.getItemMeta();
		miscMeta.setDisplayName("§8» §6Misc Stats §8«");
		lore.add(" ");
		lore.add("§8» §7Golden Apples eaten: §a" + user.getStat(Stat.GOLDENAPPLESEATEN));
		lore.add("§8» §7Golden Heads eaten: §a" + user.getStat(Stat.GOLDENHEADSEATEN));
		lore.add("§8» §7Potions drunk: §a" + user.getStat(Stat.POTIONS));
		lore.add(" ");
		lore.add("§8» §7Nethers entered: §a" + user.getStat(Stat.NETHER));
		lore.add("§8» §7Ends entered: §a" + user.getStat(Stat.END));
		lore.add(" ");
		lore.add("§8» §7Horses tamed: §a" + user.getStat(Stat.HORSESTAMED));
		lore.add("§8» §7Wolves tamed: §a" + user.getStat(Stat.WOLVESTAMED));
		lore.add(" ");
		miscMeta.setLore(lore);
		misc.setItemMeta(miscMeta);
		inv.setItem(4, misc);
		lore.clear();
		
		player.openInventory(inv);
		return inv;
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

		for (Player online : PlayerUtils.getPlayers()) {
			if (Spectator.getInstance().isSpectating(online) || !GameUtils.getGameWorlds().contains(online.getWorld())) {
				list.remove(online);
			}
		}
		
		int pages = ((list.size() / 28) + 1);
		
		pagesForPlayer.put(player, new HashMap<Integer, Inventory>());
		
		for (int current = 1; current <= pages; current++) {
			inv = Bukkit.createInventory(null, 54, "» §7Player Selector");
			
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
		final Inventory inv = Bukkit.getServer().createInventory(target, 54, "» §7" + target.getName() + "'s Inventory");
	
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
		Set<String> keys = settings.getHOF().getConfigurationSection(host).getKeys(false);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		ArrayList<String> list = new ArrayList<String>(keys);
		Inventory inv = null;
		
		int pages = ((list.size() / 28) + 1);
		
		pagesForPlayer.put(player, new HashMap<Integer, Inventory>());
		
		for (int current = 1; current <= pages; current++) {
			inv = Bukkit.createInventory(null, 54, "» §7" + host + "'s HoF, Page " + current);
			
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
			hlore.add("§8» §7Total games hosted: §6" + settings.getHOF().getConfigurationSection(host).getKeys(false).size());
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
	 * Open the config option inventory for the given player.
	 * 
	 * @param player The player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openConfigOptions(Player player) {
		Inventory inv = Bukkit.getServer().createInventory(null, 45, "» §7Game config");
		Game game = Game.getInstance();
		
		ItemStack absorption = new ItemStack (Material.GOLDEN_APPLE);
		ItemMeta absorptionMeta = absorption.getItemMeta();
		absorptionMeta.setDisplayName((game.absorption() ? "§a" : "§c") + "Absorption");
		absorption.setItemMeta(absorptionMeta);
		inv.setItem(0, absorption);
		
		ItemStack heads = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headsMeta = heads.getItemMeta();
		headsMeta.setDisplayName((game.goldenHeads() ? "§a" : "§c") + "Golden Heads");
		heads.setItemMeta(headsMeta);
		inv.setItem(1, heads);
		
		ItemStack pearl = new ItemStack (Material.ENDER_PEARL);
		ItemMeta peralMeta = pearl.getItemMeta();
		peralMeta.setDisplayName((game.goldenHeads() ? "§a" : "§c") + "Pearl Damage");
		pearl.setItemMeta(peralMeta);
		inv.setItem(2, pearl);
		
		ItemStack notchApples = new ItemStack (Material.GOLDEN_APPLE, 1, (short) 1);
		ItemMeta notchMeta = notchApples.getItemMeta();
		notchMeta.setDisplayName((game.notchApples() ? "§a" : "§c") + "Notch Apples");
		notchApples.setItemMeta(notchMeta);
		inv.setItem(3, notchApples);
		
		ItemStack hearts = new ItemStack (Material.INK_SACK, 1, (short) 1);
		ItemMeta heartsMeta = hearts.getItemMeta();
		heartsMeta.setDisplayName((game.heartsOnTab() ? "§a" : "§c") + "Hearts on tab");
		hearts.setItemMeta(heartsMeta);
		inv.setItem(5, hearts);
		
		ItemStack hardcore = new ItemStack (Material.REDSTONE);
		ItemMeta hardcoreMeta = hardcore.getItemMeta();
		hardcoreMeta.setDisplayName((game.hardcoreHearts() ? "§a" : "§c") + "Hardcore Hearts");
		hardcore.setItemMeta(hardcoreMeta);
		inv.setItem(6, hardcore);
		
		ItemStack tab = new ItemStack (Material.SIGN);
		ItemMeta tabMeta = tab.getItemMeta();
		tabMeta.setDisplayName((game.tabShowsHealthColor() ? "§a" : "§c") + "Tab health color");
		tab.setItemMeta(tabMeta);
		inv.setItem(7, tab);
		
		ItemStack rr = new ItemStack (Material.PAINTING);
		ItemMeta rrMeta = rr.getItemMeta();
		rrMeta.setDisplayName((game.isRecordedRound() ? "§a" : "§c") + "Recorded Round");
		rr.setItemMeta(rrMeta);
		inv.setItem(8, rr);
		
		ItemStack nether = new ItemStack (Material.NETHER_STALK);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName((game.nether() ? "§a" : "§c") + "Nether");
		nether.setItemMeta(netherMeta);
		inv.setItem(18, nether);
		
		ItemStack end = new ItemStack (Material.ENDER_PORTAL_FRAME);
		ItemMeta endMeta = end.getItemMeta();
		endMeta.setDisplayName((game.theEnd() ? "§a" : "§c") + "The End");
		end.setItemMeta(endMeta);
		inv.setItem(19, end);
		
		ItemStack strip = new ItemStack (Material.DIAMOND_PICKAXE);
		ItemMeta stripMeta = strip.getItemMeta();
		stripMeta.setDisplayName((game.antiStripmine() ? "§a" : "§c") + "Anti Stripmine");
		strip.setItemMeta(stripMeta);
		inv.setItem(21, strip);
		
		ItemStack death = new ItemStack (Material.BLAZE_ROD);
		ItemMeta deathMeta = death.getItemMeta();
		deathMeta.setDisplayName((game.deathLightning() ? "§a" : "§c") + "Death Lightning");
		death.setItemMeta(deathMeta);
		inv.setItem(22, death);
		
		ItemStack horse = new ItemStack (Material.SADDLE);
		ItemMeta horseMeta = horse.getItemMeta();
		horseMeta.setDisplayName((game.horses() ? "§a" : "§c") + "Horses");
		horse.setItemMeta(horseMeta);
		inv.setItem(24, horse);
		
		ItemStack armor = new ItemStack (Material.IRON_BARDING);
		ItemMeta armorMeta = armor.getItemMeta();
		armorMeta.setDisplayName((game.horseArmor() ? "§a" : "§c") + "Horse Armor");
		armor.setItemMeta(armorMeta);
		inv.setItem(25, armor);
		
		ItemStack healing = new ItemStack (Material.BREAD);
		ItemMeta healingMeta = healing.getItemMeta();
		healingMeta.setDisplayName((game.horseHealing() ? "§a" : "§c") + "Horse Healing");
		healing.setItemMeta(healingMeta);
		inv.setItem(26, healing);
		
		ItemStack shears = new ItemStack (Material.SHEARS);
		ItemMeta shearsMeta = shears.getItemMeta();
		shearsMeta.setDisplayName((game.shears() ? "§a" : "§c") + "Shears");
		shears.setItemMeta(shearsMeta);
		inv.setItem(36, shears);
		
		ItemStack ghast = new ItemStack (Material.GHAST_TEAR);
		ItemMeta ghastMeta = ghast.getItemMeta();
		ghastMeta.setDisplayName((game.ghastDropGold() ? "§a" : "§c") + "Ghast drop gold");
		ghast.setItemMeta(ghastMeta);
		inv.setItem(37, ghast);
		
		ItemStack melon = new ItemStack (Material.SPECKLED_MELON);
		ItemMeta melonMeta = melon.getItemMeta();
		melonMeta.setDisplayName((game.goldenMelonNeedsIngots() ? "§a" : "§c") + "Golden Melon needs ingots");
		melon.setItemMeta(melonMeta);
		inv.setItem(38, melon);
		
		ItemStack tier2 = new ItemStack (Material.GLOWSTONE_DUST);
		ItemMeta tier2Meta = tier2.getItemMeta();
		tier2Meta.setDisplayName((game.tier2() ? "§a" : "§c") + "Tier 2");
		tier2.setItemMeta(tier2Meta);
		inv.setItem(41, tier2);
		
		ItemStack splash = new ItemStack (Material.POTION, 1, (short) 16424);
		ItemMeta splashMeta = splash.getItemMeta();
		splashMeta.setDisplayName((game.splash() ? "§a" : "§c") + "Splash");
		splash.setItemMeta(splashMeta);
		inv.setItem(42, splash);
		
		ItemStack str = new ItemStack (Material.BLAZE_POWDER);
		ItemMeta strMeta = str.getItemMeta();
		strMeta.setDisplayName((game.strength() ? "§a" : "§c") + "Strength");
		str.setItemMeta(strMeta);
		inv.setItem(43, str);
		
		ItemStack nerf = new ItemStack (Material.POTION, 1, (short) 8233);
		ItemMeta nerfMeta = nerf.getItemMeta();
		nerfMeta.setDisplayName((game.nerfedStrength() ? "§a" : "§c") + "Nerfed Strength");
		nerf.setItemMeta(nerfMeta);
		inv.setItem(44, nerf);
		
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
		final Inventory inv = Bukkit.getServer().createInventory(null, 45, "» §7Game Information");
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
		lore.add("§8» §7Final heal is 20 seconds after start, ");
		lore.add(" §7no more are given after that.");
		lore.add(" ");
		lore.add("§8» §7Our UHC plugin is custom coded by LeonTG77.");
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
		lore.add("§8» §7Team Killing: " + ((GameUtils.getTeamSize().startsWith("r") || GameUtils.getTeamSize().isEmpty()) && !ScenarioManager.getInstance().getScenario("Moles").isEnabled() ? "§cNot Allowed." : "§aAllowed."));
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
		if (game.antiStripmine()) {
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
		} else {
			lore.add("§8» §4Info:");
			lore.add("§8» §7AntiStripmine is disabled.");
			lore.add(" ");
			lore.add("§8» §7Stripmining: §cNot Allowed.");
			lore.add("§8» §7Branchmining: §cNot Allowed.");
			lore.add("§8» §7Pokeholing: §cNot Allowed.");
			lore.add("§8» §7Blastmining: §aAllowed.");
			lore.add("§8» §7Staircasing: §aAllowed.");
			lore.add("§8» §7Rollercoastering: §aAllowed.");
			lore.add("§8» §7Digging to sounds: §aAllowed.");
			lore.add("§8» §7Digging to entities: §aAllowed.");
			lore.add("§8» §7Digging to players: §cOnly if you see them.");
			lore.add(" ");
		}
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
		lore.add("§a/uhc §8» §7§oView this menu :o");
		lore.add("§a/helpop §8» §7§oAsk for help by the staff.");
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
		
		ItemStack nether = new ItemStack (Material.NETHER_STALK);
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
			lore.add("§8» §7Strength: " + (game.strength() ? (game.nerfedStrength() ? "§cNerfed" : "§aVanilla") : "§cDisabled"));
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
		lore.add("§8» §7Absorption: " + (game.absorption() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7Golden Heads: " + (game.goldenHeads() ? "§aEnabled." : "§cDisabled."));
		if (game.goldenHeads()) {
			lore.add("§8» §7Heads Heal: §6" + game.goldenHeadsHeal() + " hearts.");
		}
		lore.add("§8» §7Notch Apples: " + (game.notchApples() ? "§aEnabled." : "§cDisabled."));
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
		lore.add("§8» §7Horses: " + (game.horses() ? "§aEnabled." : "§cDisabled."));
		if (game.horses()) {
			lore.add("§8» §7Horse Healing: " + (game.horseHealing() ? "§aEnabled." : "§cDisabled."));
			lore.add("§8» §7Horse Armor: " + (game.horseArmor() ? "§aEnabled." : "§cDisabled."));
		}
		lore.add(" ");
		horseMeta.setLore(lore);
		horse.setItemMeta(horseMeta);
		inv.setItem(42, horse);
		lore.clear();
		
		ItemStack miscI = new ItemStack (Material.ENDER_PEARL);
		ItemMeta miscIMeta = miscI.getItemMeta();
		miscIMeta.setDisplayName("§8» §6Misc. Info §8«");
		lore.add(" ");
		lore.add("§8» §7Enderpearl Damage: " + (game.pearlDamage() ? "§aEnabled, deals 1 heart." : "§cDisabled."));
		lore.add("§8» §7Death Lightning: " + (game.deathLightning() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7Saturation Fix: §aEnabled.");
		lore.add(" ");
		lore.add("§8» §7Border shrinks: §6" + NameUtils.fixString(game.getBorderShrink().getPreText(), false) + game.getBorderShrink().name().toLowerCase() + ".");
		lore.add("§8» §7The border will kill you if you go outside!");
		lore.add(" ");
		lore.add("§8» §7At meetup you can do everything you want");
		lore.add("§8» §7as long as you are inside the border and");
		lore.add("§8» §7on the surface, border can shrink to 100x100.");
		lore.add(" ");
		miscIMeta.setLore(lore);
		miscI.setItemMeta(miscIMeta);
		inv.setItem(44, miscI);
		lore.clear();
		
		File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
		
		StringBuilder staffs = new StringBuilder();
		StringBuilder owners = new StringBuilder();
		StringBuilder hosts = new StringBuilder();
		StringBuilder specs = new StringBuilder();
		
		ArrayList<String> hostL = new ArrayList<String>();
		ArrayList<String> staffL = new ArrayList<String>();
		ArrayList<String> specL = new ArrayList<String>();
		
		int i = 1;
		int j = 0;
		
		for (File file : folder.listFiles()) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			if (config.getString("rank").equals(Rank.ADMIN.name())) {
				hostL.add(config.getString("username"));
			}

			if (config.getString("rank").equals(Rank.HOST.name())) {
				hostL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.TRIAL.name())) {
				hostL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.STAFF.name())) {
				staffL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.SPEC.name())) {
				specL.add(config.getString("username"));
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
			
			if (j == 2) {
				hosts.append("-");
				j = 0;
			} else {
				j++;
			}
			
			hosts.append(sL);
			i++;
		}
		
		i = 1;
		j = 0;
		
		for (String sL : staffL) {
			if (staffs.length() > 0) {
				if (staffL.size() == i) {
					staffs.append(" and ");
				} else {
					staffs.append(", ");
				}
			}
			
			if (j == 2) {
				staffs.append("-");
				j = 0;
			} else {
				j++;
			}
			
			staffs.append(sL);
			i++;
		}
		
		i = 1;
		j = 0;
		
		for (String pL : specL) {
			if (specs.length() > 0) {
				if (specL.size() == i) {
					specs.append(" and ");
				} else {
					specs.append(", ");
				}
			}
			
			if (j == 2) {
				specs.append("-");
				j = 0;
			} else {
				j++;
			}
			
			specs.append(pL);
			i++;
		}
		
		i = 1;
		j = 0;
		
		for (OfflinePlayer ops : Bukkit.getServer().getOperators()) {
			if (owners.length() > 0) {
				if (Bukkit.getOperators().size() == i) {
					owners.append(" and ");
				} else {
					owners.append(", ");
				}
			}
			
			if (j == 2) {
				hosts.append("-");
				j = 0;
			} else {
				j++;
			}
			
			owners.append(ops.getName());
			i++;
		}
		
		lore.add(" ");
		lore.add("§8» §4Owners:");
		for (String split : owners.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
		lore.add(" ");
		lore.add("§8» §4Hosts:");
		for (String split : hosts.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
		lore.add(" ");
		lore.add("§8» §cStaff:");
		for (String split : staffs.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
		lore.add(" ");
		lore.add("§8» §7Specs:");
		for (String split : specs.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
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
					lore.add("§8» §7Current Episode: §a" + Timers.meetup);
					lore.add("§8» §7Time to next episode: §a" + Timers.time + " mins");
				}
				else if (GameUtils.getTeamSize().startsWith("No") || GameUtils.getTeamSize().startsWith("Open")) {
					lore.add("§8» §7There are no matches running.");
				}
				else if (!State.isState(State.INGAME)) {
					lore.add("§8» §7The game has not started yet.");
				}
				else {
					lore.add("§8» §7Time since start: §a" + DateUtils.ticksToString(Timers.timeSeconds));
					lore.add(Timers.pvpSeconds <= 0 ? "§8» §aPvP is enabled." : "§8» §7PvP in: §a" + DateUtils.ticksToString(Timers.pvpSeconds));
					lore.add(Timers.meetupSeconds <= 0 ? "§8» §6Meetup is now!" : "§8» §7Meetup in: §a" + DateUtils.ticksToString(Timers.meetupSeconds));
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