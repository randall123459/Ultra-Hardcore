package com.leontg77.uhc.scenario.types;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Genie scenario class
 * 
 * @author LeonTG77
 */
public class Genie extends Scenario implements Listener, CommandExecutor {
	private boolean enabled = false;
	
	private HashMap<String, Integer> wishes = new HashMap<String, Integer>();
	private HashMap<String, Integer> kills = new HashMap<String, Integer>();
	private HashSet<String> dead = new HashSet<String>();

	public Genie() {
		super("Genie", "You have three wishes throughout the whole game, but what you can wish for depends on the amount of kills you have at the time. So basically, you can't wish for something from a lower kill list if you've gotten more kills than that. Ex: If you wanted a golden apple from the 0 kill wishlist, but since you have 1 kill to your name, you can't. You can only wish for things from the 1 kill wishlist.");
		Main main = Main.plugin;
		
		main.getCommand("genie").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			for (Player online : PlayerUtils.getPlayers()) {
				wishes.put(online.getName(), 3);
				kills.put(online.getName(), 0);
			}
			
			dead.clear();
		} else {
			wishes.clear();
			kills.clear();
			dead.clear();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public String prefix() {
		return "§8[§9Genie§8] §f";
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (dead.contains(player.getName())) {
			return;
		}
		
		if (!wishes.containsKey(player.getName())) {
			wishes.put(player.getName(), 3);
		}
		
		if (!kills.containsKey(player.getName())) {
			kills.put(player.getName(), 0);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		wishes.remove(player.getName());
		kills.remove(player.getName());
		dead.add(player.getName());
		
		Player killer = player.getKiller();
		
		if (killer != null) {
			if (!kills.containsKey(killer.getName())) {
				kills.put(killer.getName(), 0);
			}
			
			if (kills.get(killer.getName()) == 5) {
				killer.sendMessage("§7------------------------------------------------");
				killer.sendMessage(prefix() + "Your killstreak §ecannot§r go any higher");
				killer.sendMessage("§7------------------------------------------------");
				killer.playSound(killer.getLocation(), "random.break", 1, 1);
			}
			else {
				kills.put(killer.getName(), kills.get(killer.getName()) + 1);
				killer.sendMessage("§7------------------------------------------------");
				killer.sendMessage(prefix() + "Your killstreak is now §e" + kills.get(killer.getName()));
				killer.sendMessage("§7------------------------------------------------");
				killer.playSound(killer.getLocation(), "note.harp", 1, 1);
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can wish for genie items.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("genie")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Genie\" is not enabled.");
				return true;
			}
			
			if (args.length == 0) {
				player.sendMessage("§7------------------------------------------------");
				player.sendMessage(prefix() + "§eCommands:");
				player.sendMessage(prefix() + "/genie wishes §7- How many wishes you have");
				player.sendMessage(prefix() + "/genie wish §7- Wish for a item here");
				player.sendMessage("§7------------------------------------------------");
				return true;
			}
			
			if (!wishes.containsKey(player.getName())) {
				wishes.put(player.getName(), 3);
			}
			
			if (!kills.containsKey(player.getName())) {
				kills.put(player.getName(), 0);
			}
			
			if (args[0].equalsIgnoreCase("wishes")) {
				if (args.length > 1 && args[1].equalsIgnoreCase("list")) {
					if (args.length < 3) {
						player.sendMessage("§7------------------------------------------------");
						player.sendMessage(prefix() + "You are at §e" + kills.get(player.getName()) + "§r kills");
						player.sendMessage(prefix() + "/genie wishes list 0 §7- 0 Kills");
						player.sendMessage(prefix() + "/genie wishes list 1 §7- 1 Kill");
						player.sendMessage(prefix() + "/genie wishes list 2 §7- 2 Kills");
						player.sendMessage(prefix() + "/genie wishes list 3 §7- 3 Kills");
						player.sendMessage(prefix() + "/genie wishes list 4 §7- 4 Kills");
						player.sendMessage(prefix() + "/genie wishes list 5 §7- 5+ Kills");
						player.sendMessage("§7------------------------------------------------");
					} 
					else {
						if (args[2].equalsIgnoreCase("0")) {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§eTier 0:");
							player.sendMessage(prefix() + "Golden Apple §7- /genie wish gapple");
							player.sendMessage(prefix() + "Diamond Sword §7- /genie wish dsword");
							player.sendMessage(prefix() + "Anvil §7- /genie wish anvil");
							player.sendMessage("§7------------------------------------------------");
						} 
						else if (args[2].equalsIgnoreCase("1")) {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§eTier 1:");
							player.sendMessage(prefix() + "Player Head §7- /genie wish head");
							player.sendMessage(prefix() + "Tier I Speed Pot §7- /genie wish speed1");
							player.sendMessage(prefix() + "Tier I Strength Pot §7- /genie wish strength");
							player.sendMessage("§7------------------------------------------------");
						} 
						else if (args[2].equalsIgnoreCase("2")) {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§eTier 2:");
							player.sendMessage(prefix() + "Enchanting Table §7- /genie wish etable");
							player.sendMessage(prefix() + "Brewing Stand §7- /genie wish bstand");
							player.sendMessage(prefix() + "Fortune III Book §7- /genie wish fortune");		
							player.sendMessage("§7------------------------------------------------");
						} 
						else if (args[2].equalsIgnoreCase("3")) {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§eTier 3:");
							player.sendMessage(prefix() + "15 Bookshelves §7- /genie wish bookshelf");
							player.sendMessage(prefix() + "5 Diamond Ore §7- /genie wish dore");
							player.sendMessage(prefix() + "Tier II Speed Pot §7- /genie wish speed2");
							player.sendMessage(prefix() + "8 Nether Warts §7- /genie wish netherwart");
							player.sendMessage("§7------------------------------------------------");
						} 
						else if (args[2].equalsIgnoreCase("4")) {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§eTier 4:");
							player.sendMessage(prefix() + "Tier II Health Pot §7- /genie wish health");
							player.sendMessage(prefix() + "128 Bottles of Enchanting §7- /genie wish ebottle");
							player.sendMessage(prefix() + "Glowstone Block §7- /genie wish glowstone");
							player.sendMessage(prefix() + "Blaze Rod §7- /genie wish blazerod");			
							player.sendMessage("§7------------------------------------------------");
						} 
						else if (args[2].equalsIgnoreCase("5")) {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§eHighest Tier(5+):");
							player.sendMessage(prefix() + "64 Obsidian §7- /genie wish obsidian");
							player.sendMessage(prefix() + "3 Wither Skulls §7- /genie wish skull");
							player.sendMessage(prefix() + "5 Soul Sand §7- /genie wish sand");
							player.sendMessage(prefix() + "8 Gold Ingots §7- /genie wish gold");
							player.sendMessage("§7------------------------------------------------");
						} 
						else {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§cWrong Syntax - Use /genie wishes list");
							player.sendMessage("§7------------------------------------------------");
							player.playSound(player.getLocation(), "random.break", 1, 1);
						}
					}
				} 
				else {
					if (wishes.get(player.getName()) == 0) {
						player.sendMessage("§7------------------------------------------------");
						player.sendMessage(prefix() + "§cYou have no more wishes.");
						player.sendMessage("§7------------------------------------------------");
						player.playSound(player.getLocation(), "random.break", 1, 1);
					} 
					else {
						player.sendMessage("§7------------------------------------------------");
						player.sendMessage(prefix() + "You have §e" + wishes.get(player.getName()) + "§r wishes left");
						player.sendMessage(prefix() + "You are at §e" + kills.get(player.getName()) + "§r kills");
						player.sendMessage(prefix() + "Here is your list:");
						player.sendMessage("§7------------------------------------------------");
						
						if (kills.get(player.getName()) == 0) {
							player.sendMessage(prefix() + "§eTier 0:");
							player.sendMessage(prefix() + "Golden Apple §7- /genie wish gapple");
							player.sendMessage(prefix() + "Diamond Sword §7- /genie wish dsword");
							player.sendMessage(prefix() + "Anvil §7- /genie wish anvil");
						} 
						else if (kills.get(player.getName()) == 1) {
							player.sendMessage(prefix() + "§eTier 1:");
							player.sendMessage(prefix() + "Player Head §7- /genie wish head");
							player.sendMessage(prefix() + "Tier I Speed Pot §7- /genie wish speed1");
							player.sendMessage(prefix() + "Tier I Strength Pot §7- /genie wish strength");
						} 
						else if (kills.get(player.getName()) == 2) {
							player.sendMessage(prefix() + "§eTier 2:");
							player.sendMessage(prefix() + "Enchanting Table §7- /genie wish etable");
							player.sendMessage(prefix() + "Brewing Stand §7- /genie wish bstand");
							player.sendMessage(prefix() + "Fortune III Book §7- /genie wish fortune");
						} 
						else if (kills.get(player.getName()) == 3) {
							player.sendMessage(prefix() + "§eTier 3:");
							player.sendMessage(prefix() + "15 Bookshelves §7- /genie wish bookshelf");
							player.sendMessage(prefix() + "5 Diamond Ore §7- /genie wish dore");
							player.sendMessage(prefix() + "Tier II Speed Pot §7- /genie wish speed2");
							player.sendMessage(prefix() + "8 Nether Warts §7- /genie wish netherwart");
						} 
						else if (kills.get(player.getName()) == 4) {
							player.sendMessage(prefix() + "§eTier 4:");
							player.sendMessage(prefix() + "Tier II Health Pot §7- /genie wish health");
							player.sendMessage(prefix() + "128 Bottles of Enchanting §7- /genie wish ebottle");
							player.sendMessage(prefix() + "Glowstone Block §7- /genie wish glowstone");
							player.sendMessage(prefix() + "Blaze Rod §7- /genie wish blazerod");		
						} 
						else {
							player.sendMessage(prefix() + "§eHighest Tier(5+):");
							player.sendMessage(prefix() + "64 Obsidian §7- /genie wish obsidian");
							player.sendMessage(prefix() + "3 Wither Skulls §7- /genie wish skull");
							player.sendMessage(prefix() + "5 Soul Sand §7- /genie wish sand");
							player.sendMessage(prefix() + "8 Gold Ingots §7- /genie wish gold");
						}
						
						player.sendMessage(prefix() + "§e/genie wishes list §7- To learn all the other wishes");
						player.sendMessage("§7------------------------------------------------");
					}
				}
			}
			else if (args[0].equalsIgnoreCase("wish")) {
				if (dead.contains(player.getName())) {
					player.sendMessage("§7------------------------------------------------");
					player.sendMessage(prefix() + "§cYou cannot wish when you are dead.");
					player.sendMessage("§7------------------------------------------------");
					player.playSound(player.getLocation(), "random.break", 1, 1);
					return true;
				}
				
				if (wishes.get(player.getName()) > 0) {
					if (args.length < 2) {
						player.sendMessage("§7------------------------------------------------");
						player.sendMessage(prefix() + "§cError: Select a wish §7- /genie wishes");
						player.sendMessage("§7------------------------------------------------");
					}
					else {
						ItemStack item;
						
						// Killstreak 0
						
						if (args[1].equalsIgnoreCase("gapple")) {
							if (kills.get(player.getName()) == 0) {
								item = new ItemStack(Material.GOLDEN_APPLE);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("dsword")) {
							if (kills.get(player.getName()) == 0) {
								item = new ItemStack(Material.DIAMOND_SWORD);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("anvil")) {
							if (kills.get(player.getName()) == 0) {
								item = new ItemStack(Material.ANVIL);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						
						// Killstreak 1
						
						else if (args[1].equalsIgnoreCase("head")) {
							if (kills.get(player.getName()) == 1) {
								item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
								SkullMeta meta = (SkullMeta) item.getItemMeta();
								meta.setOwner(player.getName());
								item.setItemMeta(meta);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("speed1")) {
							if (kills.get(player.getName()) == 1) {
								item = new ItemStack(Material.POTION, 1, (short) 8194);
								
								if (PlayerUtils.hasSpaceFor(player, new ItemStack(Material.WOOD_SWORD))) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("strength")) {
							if (kills.get(player.getName()) == 1) {
								item = new ItemStack(Material.POTION, 1, (short) 8201);
								
								if (PlayerUtils.hasSpaceFor(player, new ItemStack(Material.WOOD_SWORD))) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						
						// Killstreak 2
						
						else if (args[1].equalsIgnoreCase("etable")) {
							if (kills.get(player.getName()) == 2) {
								item = new ItemStack(Material.ENCHANTMENT_TABLE);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("bstand")) {
							if (kills.get(player.getName()) == 2) {
								item = new ItemStack(Material.BREWING_STAND_ITEM);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("fortune")) {
							if (kills.get(player.getName()) == 2) {
								item = new ItemStack(Material.ENCHANTED_BOOK);
								EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
						        meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
						        item.setItemMeta(meta);
								
								if (PlayerUtils.hasSpaceFor(player, new ItemStack(Material.WOOD_SWORD))) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						
						// Killstreak 3

						else if (args[1].equalsIgnoreCase("bookshelf")) {
							if (kills.get(player.getName()) == 3) {
								item = new ItemStack(Material.BOOKSHELF, 15);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("dore")) {
							if (kills.get(player.getName()) == 3) {
								item = new ItemStack(Material.DIAMOND_ORE, 5);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("speed2")) {
							if (kills.get(player.getName()) == 3) {
								item = new ItemStack(Material.POTION, 1, (short) 8226);
								
								if (PlayerUtils.hasSpaceFor(player, new ItemStack(Material.WOOD_SWORD))) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("netherwart")) {
							if (kills.get(player.getName()) == 3) {
								item = new ItemStack(Material.NETHER_WARTS, 8);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						
						// Killstreak 4

						else if (args[1].equalsIgnoreCase("health")) {
							if (kills.get(player.getName()) == 4) {
								item = new ItemStack(Material.POTION, 1, (short) 8229);
								
								if (PlayerUtils.hasSpaceFor(player, new ItemStack(Material.WOOD_SWORD))) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("ebottle")) {
							if (kills.get(player.getName()) == 4) {
								item = new ItemStack(Material.EXP_BOTTLE, 128);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("glowstone")) {
							if (kills.get(player.getName()) == 4) {
								item = new ItemStack(Material.GLOWSTONE);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("blazerod")) {
							if (kills.get(player.getName()) == 4) {
								item = new ItemStack(Material.BLAZE_ROD);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						
						// Killstreak 5+

						else if (args[1].equalsIgnoreCase("obsidian")) {
							if (kills.get(player.getName()) == 5) {
								item = new ItemStack(Material.OBSIDIAN, 64);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("skull")) {
							if (kills.get(player.getName()) == 5) {
								item = new ItemStack(Material.SKULL_ITEM, 3, (short) 1);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("sand")) {
							if (kills.get(player.getName()) == 5) {
								item = new ItemStack(Material.SOUL_SAND, 5);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else if (args[1].equalsIgnoreCase("gold")) {
							if (kills.get(player.getName()) == 5) {
								item = new ItemStack(Material.GOLD_INGOT, 8);
								
								if (PlayerUtils.hasSpaceFor(player, item)) {
									player.getInventory().addItem(item);
									wishes.put(player.getName(), wishes.get(player.getName()) - 1);
									
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "You now have §e" + wishes.get(player.getName()) + "§r wishes.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "note.harp", 1, 1);
								} 
								else {
									player.sendMessage("§7------------------------------------------------");
									player.sendMessage(prefix() + "§cError: You don't have room.");
									player.sendMessage("§7------------------------------------------------");
									player.playSound(player.getLocation(), "random.break", 1, 1);
								}
							}
							else {
								player.sendMessage("§7------------------------------------------------");
								player.sendMessage(prefix() + "§cError: Incorrect Killstreak §7- /genie wishes");
								player.sendMessage("§7------------------------------------------------");
								player.playSound(player.getLocation(), "random.break", 1, 1);
							}
						}
						else {
							player.sendMessage("§7------------------------------------------------");
							player.sendMessage(prefix() + "§cError: Cannot find wish §7- /genie wishes");
							player.sendMessage("§7------------------------------------------------");
							player.playSound(player.getLocation(), "random.break", 1, 1);
						}
					}
				} 
				else {
					player.sendMessage("§7------------------------------------------------");
					player.sendMessage(prefix() + "§cError: You have §e0§c wishes left");
					player.sendMessage("§7------------------------------------------------");
					player.playSound(player.getLocation(), "random.break", 1, 1);
				}
			} else {
				player.sendMessage("§7------------------------------------------------");
				player.sendMessage(prefix() + "§eCommands:");
				player.sendMessage(prefix() + "/genie wishes §7- How many wishes you have");
				player.sendMessage(prefix() + "/genie wish §7- Wish for a item here");
				player.sendMessage("§7------------------------------------------------");
			}
		}
		return true;
	}
}