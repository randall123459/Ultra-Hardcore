package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Moles scenario class
 * 
 * @author Bergasms, modified by LeonTG77
 */
@SuppressWarnings("deprecation")
public class Moles extends Scenario implements Listener, CommandExecutor {
	private ArrayList<String> moles = new ArrayList<String>();
	private boolean enabled = false;

	public Moles() {
		super("Moles", "There are a mole on each team, moles on each team work together to take out the normal teams.");
		Main main = Main.plugin;
		
		main.getCommand("moles").setExecutor(this);
		main.getCommand("molehelp").setExecutor(this);
		main.getCommand("mcc").setExecutor(this);
		main.getCommand("mcl").setExecutor(this);
		main.getCommand("mcp").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	
		if (enable) {
			ArrayList<String> pls = new ArrayList<String>();
				
			for (Team team : Teams.getManager().getTeams()) {
				if (team.getSize() < 1) {
					continue;
				}
				
				for (String t : team.getEntries()) {
					if (Bukkit.getPlayer(t) != null) {
						pls.add(t);
					}
				}
				
				if (pls.size() > 0) {
					moles.add(pls.get(new Random().nextInt(pls.size())));
				}
				pls.clear();
			}

			PlayerUtils.broadcast(Main.prefix() + "Moles has been set.");
			
			for (String m : moles) {
				Player mole = Bukkit.getServer().getPlayer(m);
				mole.sendMessage(Main.prefix() + "You are the mole.");
				
				ItemStack wool1 = new ItemStack (Material.WOOL, 1, (short) 8);
				ItemMeta wool1meta = wool1.getItemMeta();
				wool1meta.setDisplayName("§aThe Mobber");
				wool1meta.setLore(Arrays.asList("§7MONSTER_EGG x 1", "§7MONSTER_EGG x 2", "§7MONSTER_EGG x 1", "§7COBBLESTONE x 1", "§7TNT x 5", "§7ENDER_PEARL x 2"));
				wool1.setItemMeta(wool1meta);
				
				ItemStack wool2 = new ItemStack (Material.WOOL, 1, (short) 10);
				ItemMeta wool2meta = wool2.getItemMeta();
				wool2meta.setDisplayName("§aThe Potter");
				wool2meta.setLore(Arrays.asList("§7POTION x 1", "§7POTION x 1", "§7POTION x 1", "§7POTION x 1", "§7ENDER_PEARL x 1", "§7COBBLESTONE x 1"));
				wool2.setItemMeta(wool2meta);
				
				ItemStack wool3 = new ItemStack (Material.WOOL, 1, (short) 14);
				ItemMeta wool3meta = wool3.getItemMeta();
				wool3meta.setDisplayName("§aThe Pyro");
				wool3meta.setLore(Arrays.asList("§7LAVA_BUCKET x 1", "§7MONSTER_EGG x 5", "§7FLINT_AND_STEEL x 1", "§7POTION x 1", "§7TNT x 5", "§7COBBLESTONE x 1"));
				wool3.setItemMeta(wool3meta);
				
				ItemStack wool4 = new ItemStack (Material.WOOL, 1, (short) 12);
				ItemMeta wool4meta = wool4.getItemMeta();
				wool4meta.setDisplayName("§aThe Trapper");
				wool4meta.setLore(Arrays.asList("§7TNT x 3", "§7LAVA_BUCKET x 1", "§7POTION x 1", "§7COBBLESTONE x 1", "§7COBBLESTONE x 1", "§7COBBLESTONE x 2"));
				wool4.setItemMeta(wool4meta);
				
				ItemStack wool5 = new ItemStack (Material.WOOL, 1, (short) 1);
				ItemMeta wool5meta = wool5.getItemMeta();
				wool5meta.setDisplayName("§aThe Troll");
				wool5meta.setLore(Arrays.asList("§7FIREWORK x 64", "§7ENCHANTED_BOOK x 10", "§7EXPLOSIVE_MINECART x 8", "§7COBBLESTONE x 10", "§7WEB x 4", "§7ENDER_PORTAL x 1"));
				wool5.setItemMeta(wool5meta);
				
				ItemStack wool6 = new ItemStack (Material.WOOL, 1, (short) 13);
				ItemMeta wool6meta = wool6.getItemMeta();
				wool6meta.setDisplayName("§aThe Fighter");
				wool6meta.setLore(Arrays.asList("§7GOLDEN_APPLE x 1", "§7DIAMOND_SWORD x 1", "§7MONSTER_EGG x 1", "§7BOW x 1", "§7ARROW x 64", "§7POTION x 1"));
				wool6.setItemMeta(wool6meta);
				
				mole.getInventory().setItem(9, wool1);
				mole.getInventory().setItem(10, wool2);
				mole.getInventory().setItem(11, wool3);
				mole.getInventory().setItem(12, wool4);
				mole.getInventory().setItem(13, wool5);
				mole.getInventory().setItem(14, wool6);
			}
		} else {
			moles.clear();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getCurrentItem() == null) {
			return;
		}
		
		if (event.getCurrentItem().getType() != Material.WOOL) {
			return;
		}
		
		if (!moles.contains(event.getWhoClicked().getName())) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		
		if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aThe Mobber")) {
			ItemStack wool1 = new ItemStack (Material.MONSTER_EGG, 1, (short) 50);
			
			ItemStack wool2 = new ItemStack (Material.MONSTER_EGG, 2, (short) 51);
			
			ItemStack wool3 = new ItemStack (Material.MONSTER_EGG, 1, (short) 57);
			
			ItemStack wool4 = new ItemStack (Material.COBBLESTONE, 1);
			ItemMeta wool4meta = wool4.getItemMeta();
			wool4meta.setDisplayName("§bTrap");
			wool4meta.setLore(Arrays.asList("§5§oEscape Hatch"));
			wool4.setItemMeta(wool4meta);
			
			ItemStack wool5 = new ItemStack (Material.TNT, 5);
			
			ItemStack wool6 = new ItemStack (Material.ENDER_PEARL, 2);
			
			player.getInventory().setItem(9, wool1);
			player.getInventory().setItem(10, wool2);
			player.getInventory().setItem(11, wool3);
			player.getInventory().setItem(12, wool4);
			player.getInventory().setItem(13, wool5);
			player.getInventory().setItem(14, wool6);
			event.setCancelled(true);
		}
		else if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aThe Potter")) {
			ItemStack wool1 = new ItemStack (Material.POTION, 1, (short) 16388);
			
			ItemStack wool2 = new ItemStack (Material.POTION, 1, (short) 16392);
			
			ItemStack wool3 = new ItemStack (Material.POTION, 1, (short) 16394);
			
			ItemStack wool4 = new ItemStack (Material.POTION, 1, (short) 2);
		
			ItemStack wool5 = new ItemStack (Material.ENDER_PEARL, 1);
			
			ItemStack wool6 = new ItemStack (Material.COBBLESTONE, 1);
			ItemMeta wool6meta = wool6.getItemMeta();
			wool6meta.setDisplayName("§bTrap");
			wool6meta.setLore(Arrays.asList("§5§oStaircase"));
			wool6.setItemMeta(wool6meta);
			
			player.getInventory().setItem(9, wool1);
			player.getInventory().setItem(10, wool2);
			player.getInventory().setItem(11, wool3);
			player.getInventory().setItem(12, wool4);
			player.getInventory().setItem(13, wool5);
			player.getInventory().setItem(14, wool6);
			event.setCancelled(true);
		}
		else if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aThe Pyro")) {
			ItemStack wool1 = new ItemStack (Material.LAVA_BUCKET, 1);
			
			ItemStack wool2 = new ItemStack (Material.MONSTER_EGG, 5, (short) 61);
			
			ItemStack wool3 = new ItemStack (Material.FLINT_AND_STEEL, 1);
			
			ItemStack wool4 = new ItemStack (Material.POTION, 1, (short) 3);
			
			ItemStack wool5 = new ItemStack (Material.TNT, 5);
			
			ItemStack wool6 = new ItemStack (Material.COBBLESTONE, 1);
			ItemMeta wool6meta = wool6.getItemMeta();
			wool6meta.setDisplayName("§bTrap");
			wool6meta.setLore(Arrays.asList("§5§oHole"));
			wool6.setItemMeta(wool6meta);
			
			player.getInventory().setItem(9, wool1);
			player.getInventory().setItem(10, wool2);
			player.getInventory().setItem(11, wool3);
			player.getInventory().setItem(12, wool4);
			player.getInventory().setItem(13, wool5);
			player.getInventory().setItem(14, wool6);
			event.setCancelled(true);
		}
		else if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aThe Trapper")) {
			ItemStack wool1 = new ItemStack (Material.TNT, 3);
			
			ItemStack wool2 = new ItemStack (Material.LAVA_BUCKET, 1);
			
			ItemStack wool3 = new ItemStack (Material.POTION, 1, (short) 16398);
			
			ItemStack wool4 = new ItemStack (Material.COBBLESTONE, 1);
			ItemMeta wool4meta = wool4.getItemMeta();
			wool4meta.setDisplayName("§bTrap");
			wool4meta.setLore(Arrays.asList("§5§oDrop Trap"));
			wool4.setItemMeta(wool4meta);
			
			ItemStack wool5 = new ItemStack (Material.COBBLESTONE, 1);
			ItemMeta wool5meta = wool5.getItemMeta();
			wool5meta.setDisplayName("§bTrap");
			wool5meta.setLore(Arrays.asList("§5§oLava Trap"));
			wool5.setItemMeta(wool5meta);
			
			ItemStack wool6 = new ItemStack (Material.COBBLESTONE, 2);
			ItemMeta wool6meta = wool6.getItemMeta();
			wool6meta.setDisplayName("§bTrap");
			wool6meta.setLore(Arrays.asList("§5§oTNT Trap"));
			wool6.setItemMeta(wool6meta);
			
			player.getInventory().setItem(9, wool1);
			player.getInventory().setItem(10, wool2);
			player.getInventory().setItem(11, wool3);
			player.getInventory().setItem(12, wool4);
			player.getInventory().setItem(13, wool5);
			player.getInventory().setItem(14, wool6);
			event.setCancelled(true);
		}
		else if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aThe Troll")) {
			ItemStack wool1 = new ItemStack (Material.FIREWORK, 64);
			
			ItemStack wool2 = new ItemStack (Material.ENCHANTED_BOOK, 10);
			
			ItemStack wool3 = new ItemStack (Material.EXPLOSIVE_MINECART, 8);
			
			ItemStack wool4 = new ItemStack (Material.COBBLESTONE, 10);
			ItemMeta wool4meta = wool4.getItemMeta();
			wool4meta.setDisplayName("§bTrap");
			wool4meta.setLore(Arrays.asList("§5§oHole"));
			wool4.setItemMeta(wool4meta);
			
			ItemStack wool5 = new ItemStack (Material.WEB, 4);
			
			ItemStack wool6 = new ItemStack (Material.ENDER_PORTAL, 1);
			
			player.getInventory().setItem(9, wool1);
			player.getInventory().setItem(10, wool2);
			player.getInventory().setItem(11, wool3);
			player.getInventory().setItem(12, wool4);
			player.getInventory().setItem(13, wool5);
			player.getInventory().setItem(14, wool6);
			event.setCancelled(true);
		}
		else if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aThe Fighter")) {
			ItemStack wool1 = new ItemStack (Material.GOLDEN_APPLE);
			
			ItemStack wool2 = new ItemStack (Material.DIAMOND_SWORD);
			
			ItemStack wool3 = new ItemStack (Material.BOW);
			
			ItemStack wool4 = new ItemStack (Material.ARROW, 64);
			
			ItemStack wool5 = new ItemStack (Material.COBBLESTONE, 1);
			ItemMeta wool4meta = wool5.getItemMeta();
			wool4meta.setDisplayName("§bTrap");
			wool4meta.setLore(Arrays.asList("§5§oStaircase"));
			wool5.setItemMeta(wool4meta);
			
			ItemStack wool6 = new ItemStack (Material.POTION, 1, (short) 16396);
			
			player.getInventory().setItem(9, wool1);
			player.getInventory().setItem(10, wool2);
			player.getInventory().setItem(11, wool3);
			player.getInventory().setItem(12, wool4);
			player.getInventory().setItem(13, wool5);
			player.getInventory().setItem(14, wool6);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}

		ItemStack is = event.getItemInHand();
		
		if ((is.hasItemMeta()) && (is.getItemMeta().getLore() != null)) {
			float yaw = event.getPlayer().getLocation().getYaw();
			Location l = event.getBlock().getLocation();

			World w = event.getBlock().getWorld();
			event.getBlock().setType(Material.AIR);
			event.getBlock().getState().update(true);
			
			if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains("§5§oDrop Trap")) {
				createDropTrap(yaw, w, l);
			} 
			else if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains("§5§oLava Trap")) {
				createLavaTrap(yaw, w, l);
			} 
			else if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains("§5§oTNT Trap")) {
				createTntTrap(yaw, w, l);
			} 
			else if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains("§5§oEscape Hatch")) {
				createEscapeHatch(yaw, w, l);
			} 
			else if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains("§5§oHole")) {
				createHole(yaw, w, l);
			} 
			else if (is.hasItemMeta() && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains("§5§oStaircase")) {
				createStaircase(yaw, w, l);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use mole commands.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("moles")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Moles\" is not enabled.");
				return true;
			}
			
			if (Spectator.getManager().isSpectating(player)) {
				StringBuilder moleList = new StringBuilder("");
				int i = 1;
				
				for (String mole : moles) {
					if (moleList.length() > 0) {
						if (i == moles.size()) {
							moleList.append(" §7and §a");
						} else {
							moleList.append("§7, §a");
						}
					}
					
					moleList.append(ChatColor.GREEN + mole);
					i++;
				}
				
				player.sendMessage(Main.prefix() + "The moles are: §a" + (moleList.length() > 0 ? moleList.toString().trim() : "None") + "§7.");
			} else {
				player.sendMessage(Main.NO_PERMISSION_MESSAGE);
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("molehelp")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Moles\" is not enabled.");
				return true;
			}
			
			if (!moles.contains(player.getName())) {
				player.sendMessage(Main.prefix().replaceAll("UHC", "Moles") + "You are not a mole.");
				return true;
			}
	
			player.sendMessage(Main.prefix().replaceAll("UHC", "Moles") + "Mole help:");
			player.sendMessage("§7- §f/mcl §7- §o(Tell your location to the moles)");
			player.sendMessage("§7- §f/mcc §7- §o(Chat with the other moles)");
			player.sendMessage("§7- §f/mcp §7- §o(Display the other moles)");
		}
		
		if (cmd.getName().equalsIgnoreCase("mcl")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Moles\" is not enabled.");
				return true;
			}
			
			if (!moles.contains(player.getName())) {
				player.sendMessage(Main.prefix().replaceAll("UHC", "Moles") + "You are not a mole.");
				return true;
			}
			
			for (String mole : moles) {
				Player moleP = Bukkit.getServer().getPlayer(mole);
				
				if (moleP != null) {
					moleP.sendMessage(Main.prefix().replaceAll("UHC", "MoleLoc") + player.getName() + ": §fx:" + player.getLocation().getBlockX() + ", y:" + player.getLocation().getBlockY() + ", z:" + player.getLocation().getBlockZ() + " (" + player.getWorld().getEnvironment().name().replaceAll("_", " ").replaceAll("NORMAL", "overworld").toLowerCase().replaceAll("normal", "overworld") + ")");
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("mcc")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Moles\" is not enabled.");
				return true;
			}
			
			if (!moles.contains(player.getName())) {
				player.sendMessage(Main.prefix().replaceAll("UHC", "Moles") + "You are not a mole.");
				return true;
			}
			
			if (args.length == 0) {
				player.sendMessage(Main.prefix() + "Usage: /mcc <message>");
				return true;
			}
			
			StringBuilder message = new StringBuilder();
			
			for (int i = 0; i < args.length; i++) {
				message.append(args[i]).append(" ");
			}
			
			for (String mole : moles) {
				Player moleP = Bukkit.getServer().getPlayer(mole);
				
				if (moleP != null) {
					moleP.sendMessage(Main.prefix().replaceAll("UHC", "MoleChat") + player.getName() + ": §f" + message.toString().trim());
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("mcp")) {
			if (!isEnabled()) {
				player.sendMessage(Main.prefix() + "\"Moles\" is not enabled.");
				return true;
			}
			
			if (!moles.contains(player.getName())) {
				player.sendMessage(Main.prefix().replaceAll("UHC", "Moles") + "You are not a mole.");
				return true;
			}
			
			StringBuilder moleList = new StringBuilder("");
			int i = 1;
			
			for (String mole : moles) {
				if (moleList.length() > 0) {
					if (i == moles.size()) {
						moleList.append(" §7and §a");
					} else {
						moleList.append("§7, §a");
					}
				}
				
				moleList.append(ChatColor.GREEN + mole);
				i++;
			}
			
			player.sendMessage(Main.prefix() + "The moles are: §a" + (moleList.length() > 0 ? moleList.toString().trim() : "None") + "§7.");
		}
		return true;
	}

	private void createHole(float yaw, World world, Location location) {
		for (int x = location.getBlockX() - 4; x < location.getBlockX() + 4; x++) {
			for (int z = location.getBlockZ() - 4; z < location.getBlockZ() + 4; z++) {
				for (int y = location.getBlockY() + 2; y > location.getBlockY() - 3; y--) {
					double xdist = location.getX() - x;
					double zdist = location.getZ() - z;
					if (xdist * xdist + zdist * zdist < 16.0D) {
						world.getBlockAt(x, y - 1, z).setType(Material.AIR);
					}
				}
			}
		}
		world.getBlockAt(location).setType(Material.STONE);
	}

	private void createStaircase(float yaw, World world, Location location) {
		float rot = yaw % 360.0F;
		int xoff = 0;
		int zoff = 0;

		rot = Math.abs(rot);
		if ((0.0F <= rot) && (rot < 45.0F)) {
			zoff = 1;
			xoff = 0;
		} else if ((45.0F <= rot) && (rot < 135.0F)) {
			xoff = -1;
			zoff = 0;
		} else if ((135.0F <= rot) && (rot < 225.0F)) {
			zoff = -1;
			xoff = 0;
		} else if ((225.0F <= rot) && (rot < 315.0F)) {
			xoff = 1;
			zoff = 0;
		} else {
			zoff = 1;
			xoff = 0;
		}
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				world.getBlockAt(location.getBlockX(), location.getBlockY() + j, location.getBlockZ()).setType(Material.AIR);
			}
			location.add(xoff, 1.0D, zoff);
		}
	}

	private void createEscapeHatch(float yaw, World world, Location location) {
		for (int i = location.getBlockY() - 3; i < location.getBlockY() + 15; i++) {
			world.getBlockAt(new Location(world, location.getX() + 2.0D, i, location.getZ())).setType(Material.OBSIDIAN);
			world.getBlockAt(new Location(world, location.getX(), i, location.getZ())).setType(Material.AIR);
			world.getBlockAt(new Location(world, location.getX() - 1.0D, i, location.getZ())).setType(Material.OBSIDIAN);
			world.getBlockAt(new Location(world, location.getX(), i, location.getZ() + 1.0D)).setType(Material.OBSIDIAN);
			world.getBlockAt(new Location(world, location.getX(), i, location.getZ() - 1.0D)).setType(Material.OBSIDIAN);
			world.getBlockAt(new Location(world, location.getX() + 1.0D, i, location.getZ() + 1.0D)).setType(Material.OBSIDIAN);
			world.getBlockAt(new Location(world, location.getX() + 1.0D, i, location.getZ() - 1.0D)).setType(Material.OBSIDIAN);
		}
	}

	private void createTrapTop(float f, World w, Location location) {
		float rot = (f - 90.0F) % 360.0F;
		int xoff = 0;
		int zoff = 0;

		rot = Math.abs(rot);

		byte face;
		if ((0.0F <= rot) && (rot < 45.0F)) {
			zoff = -1;
			xoff = 0;
			face = 3;
		} else {
			if ((45.0F <= rot) && (rot < 135.0F)) {
				xoff = 1;
				zoff = 0;
				face = 4;
			} else {
				if ((135.0F <= rot) && (rot < 225.0F)) {
					zoff = 1;
					xoff = 0;
					face = 2;
				} else {
					if ((225.0F <= rot) && (rot < 315.0F)) {
						xoff = -1;
						zoff = 0;
						face = 5;
					} else {
						zoff = -1;
						xoff = 0;
						face = 3;
					}
				}
			}
		}
		w.getBlockAt(new Location(w, location.getX() + xoff, location.getY() - 1.0D,location.getZ() + zoff)).setType(Material.PISTON_BASE);
		w.getBlockAt(new Location(w, location.getX() + xoff, location.getY() - 1.0D,location.getZ() + zoff)).setData(face);
		w.getBlockAt(location).setType(Material.STONE_PLATE);
		w.getBlockAt(new Location(w, location.getX() - xoff, location.getY() - 1.0D,location.getZ() - zoff)).setType(Material.AIR);
	}

	private void createDropTrap(float f, World w, Location location) {
		w.getBlockAt(location).setType(Material.STONE_PLATE);
		w.getBlockAt(location).getState().update(true);
		for (int i = location.getBlockY(); i > (location.getBlockY() - 25 > 3 ? location.getBlockY() - 25 : 3); i--) {
			if (i != location.getBlockY() - 1) {
				w.getBlockAt(location.getBlockX(), i, location.getBlockZ()).setType(Material.AIR);
			}
		}
		createTrapTop(f, w, location);
	}

	private void createLavaTrap(float f, World w, Location location) {
		w.getBlockAt(location).setType(Material.STONE_PLATE);
		w.getBlockAt(location).getState().update(true);
		for (int i = location.getBlockY(); i > (location.getBlockY() - 3 > 3 ? location.getBlockY() - 3 : 3); i--) {
			if (i != location.getBlockY() - 1) {
				w.getBlockAt(location.getBlockX(), i, location.getBlockZ()).setType(Material.LAVA);
			}
		}
		createTrapTop(f, w, location);
	}

	private void createTntTrap(float f, World w, Location location) {
		w.getBlockAt(location).setType(Material.STONE_PLATE);
		w.getBlockAt(location).getState().update(true);
		w.getBlockAt(new Location(w, location.getX(), location.getY() - 2.0D, location.getZ())).setType(Material.TNT);
	}
}