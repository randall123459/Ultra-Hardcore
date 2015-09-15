package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * MysteryTeams scenario class
 * 
 * @author EXSolo, modified by LeonTG77
 */
public class MysteryTeams extends Scenario implements Listener, CommandExecutor {
	private boolean enabled = false;

	private HashMap<MysteryTeam, ArrayList<String>> orgTeams = new HashMap<MysteryTeam, ArrayList<String>>();
	private HashMap<MysteryTeam, ArrayList<String>> teams = new HashMap<MysteryTeam, ArrayList<String>>();

	public MysteryTeams() {
		super("MysteryTeams", "Teams are unknown until meeting and comparing banners.");
		Main main = Main.plugin;
		
		main.getCommand("mt").setExecutor(this);
	}

	public void setEnabled(boolean enable) {
		enabled = enable;

		orgTeams.clear();
		teams.clear();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public static String prefix() {
		return "§6[§cMysteryTeams§6] §f";
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mt")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.prefix() + "\"MysteryTeams\" is not enabled.");
				return true;
			}
			
			if (sender.hasPermission("mysteryteams.admin")) {
				if (args.length == 0) {
					printHelp(sender);
					return true;
				}
				
				if (args[0].equalsIgnoreCase("randomize")) {
					if (args.length > 1) {
						int teamSize = 1;
						
						try {
							teamSize = Integer.parseInt(args[1]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Invaild team size.");
							return true;
						}
						
						ArrayList<Player> players = new ArrayList<Player>();
						
						for (Player online : PlayerUtils.getPlayers()) {
							if (!isOnTeam(online)) {
								players.add(online);
							}
						}
						
						if (args.length > 2) {
							for (int i = 2; i < args.length; i++) {
								Player target = Bukkit.getServer().getPlayer(args[i]);
								
								if (target != null) {
									if (players.contains(target)) {
										players.remove(target);
									}
								}
							}
						}

						Collections.shuffle(players);
			    		MysteryTeam t = null;
						
						for (MysteryTeam mt : MysteryTeam.values()) {
			    			if (!this.teams.containsKey(mt)) {
			    				this.teams.put(mt, new ArrayList<String>());
			    				this.orgTeams.put(mt, new ArrayList<String>());
			    				t = mt;
			    				break;
			    			}
			    			if (this.teams.get(mt).size() < 1)  {
			    				t = mt;
			    				break;
			    			}
						}
						
						try {
							for (int i = 0; i < teamSize; i++) {
								if (players.size() < i) {
									sender.sendMessage(ChatColor.RED + "Could not add a player to team " + t.getName() + ".");
									continue;
								}
								
								Player p = players.get(i);
								
					    		orgTeams.get(t).add(p.getUniqueId().toString());
					    		this.teams.get(t).add(p.getUniqueId().toString());
					    		
					    		ItemStack item = new ItemStack(Material.BANNER);
					    		BannerMeta meta = (BannerMeta) item.getItemMeta();
					    		meta.setBaseColor(t.getDyeColor());
					    		item.setItemMeta(meta);
					    		
								p.sendMessage(Main.prefix() + "You were added to " + t.getChatColor() + t.getName() + " §7team.");
					    		PlayerUtils.giveItem(p, item);
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Not enough players for this team.");
						}

						if (this.teams.get(t).size() > 0) {
							sender.sendMessage(Main.prefix() + "Created a rTo" + teamSize + " using team " + t.getName() + ".");
						}
					} else {
						sender.sendMessage(prefix() + "§7Usage: /mt randomize <teamsize> [playersnotplaying]");
					}
				} 
				else if (args[0].equalsIgnoreCase("clear")) {
					sender.sendMessage(prefix() + "Teams cleared.");
					
					for (MysteryTeam mt : orgTeams.keySet()) {
						orgTeams.get(mt).clear();
					}
					
					for (MysteryTeam mt : teams.keySet()) {
						teams.get(mt).clear();
					}
				} 
				else if (args[0].equalsIgnoreCase("list")) {
					if (orgTeams.size() <= 0) {
						sender.sendMessage(prefix() + "There are no teams set.");
						return true;
					}

					sender.sendMessage(prefix() + "All teams: §o(Names in red means they are dead)");
					
					for (Entry<MysteryTeam, ArrayList<String>> team : orgTeams.entrySet()) { 
						StringBuilder members = new StringBuilder();
						int i = 1;
						
						for (String member : team.getValue()) {
							if (members.length() > 0) {
								if (i == team.getValue().size()) {
									members.append(" §8and §f");
								} else {
									members.append("§8, §f");
								}
							}
							
							members.append(orgTeams.get(team.getKey()).contains(member) ? ChatColor.GREEN + Bukkit.getOfflinePlayer(UUID.fromString(member)).getName() : ChatColor.RED + Bukkit.getOfflinePlayer(UUID.fromString(member)).getName());
							i++;
						}
						
						sender.sendMessage(team.getKey().getChatColor() + team.getKey().getName() + ": §f" + members.toString().trim());
					}
				} 
				else {
					if (args[0].equalsIgnoreCase("give")) {
						if (args.length > 1) {
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if (target == null) {
								sender.sendMessage(ChatColor.RED + "That player is not online.");
								return true;
							}
							
							MysteryTeam team = null;
							
							for (MysteryTeam t : teams.keySet()) {
								if (teams.get(t).contains(target.getUniqueId().toString())) {
									team = t;
									break;
								}
							}
							
							if (team == null) {
								sender.sendMessage(prefix() + "That player is not on a team.");
								return true;
							}
							
							sender.sendMessage(prefix() + "Gave item to player.");
							
							ItemStack item = new ItemStack(Material.BANNER);
				    		BannerMeta meta = (BannerMeta) item.getItemMeta();
				    		meta.setBaseColor(team.getDyeColor());
				    		item.setItemMeta(meta);
				    		PlayerUtils.giveItem(target, item);
						} else {
							for (Player online : PlayerUtils.getPlayers()) {
								MysteryTeam team = null;
								
								for (MysteryTeam t : teams.keySet()) {
									if (teams.get(t).contains(online.getUniqueId().toString())) {
										team = t;
										break;
									}
								}
								
								if (team == null) {
									continue;
								}
								
								sender.sendMessage(prefix() + "Gave item to player.");
								
								ItemStack item = new ItemStack(Material.BANNER);
					    		BannerMeta meta = (BannerMeta) item.getItemMeta();
					    		meta.setBaseColor(team.getDyeColor());
					    		item.setItemMeta(meta);
					    		PlayerUtils.giveItem(online, item);
							}
						}
					} else {
						printHelp(sender);
					}
				}
			} else {
				sender.sendMessage(prefix() + "You have no permission to execute this command!");
			}
		}
		return false;
	}
	
	private boolean isOnTeam(Player player) {
		for (MysteryTeam t : teams.keySet()) {
			if (teams.get(t).contains(player.getUniqueId().toString())) {
				return true;
			}
		}
		return false;
	}

	public void printHelp(CommandSender sender) {
		sender.sendMessage(prefix() + "MysteryTeams Command");
		sender.sendMessage(" - " + ChatColor.BLUE + "/mt randomize <teamsize> [playersnotplaying]");
	    sender.sendMessage(" - " + ChatColor.BLUE + "/mt give [player]");
	    sender.sendMessage(" - " + ChatColor.BLUE + "/mt clear");
	    sender.sendMessage(" - " + ChatColor.BLUE + "/mt list");
	}
	
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		if (event.getInventory().getResult() != null && event.getInventory().getResult().getType() == Material.BANNER) {
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		MysteryTeam team = null;
		
		for (MysteryTeam t : teams.keySet()) {
			if (teams.get(t).contains(player.getUniqueId().toString())) {
				team = t;
				break;
			}
		}

		if (team != null) {
			teams.get(team).remove(player.getUniqueId().toString());

			PlayerUtils.broadcast(prefix() + team.getChatColor() + "Player from team " + team.getName() + " died.");
			
			if (teams.get(team).size() > 0) {
				PlayerUtils.broadcast(prefix() + team.getChatColor() + "Team " + team.getName() + " has " + ChatColor.RESET + teams.get(team).size() + team.getChatColor() + (teams.get(team).size() > 1 ? " players" : " player") + " left");
			} else {
				teams.remove(team);
				
				if (teams.size() > 1) {
					PlayerUtils.broadcast(prefix() + team.getChatColor() + "Team " + team.getName() + " eliminated. There are " + ChatColor.RESET + teams.size() + team.getChatColor() + " teams left.");
				} else {
					MysteryTeam m = null;
					
					for (MysteryTeam mt : teams.keySet()) {
						m = mt;
						break;
					}
					
					PlayerUtils.broadcast(prefix() + team.getChatColor() + "Team " + team.getName() + " eliminated. " + m.getChatColor() + "The " + m.getName() + " team won!");
				}
			}
		}
	}

	public enum MysteryTeam {
		WHITE(ChatColor.WHITE, DyeColor.WHITE, "White"), 
		ORANGE(ChatColor.GOLD, DyeColor.ORANGE, "Orange"), 
		LIGHT_BLUE(ChatColor.AQUA, DyeColor.LIGHT_BLUE, "Light blue"), 
		YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, "Yellow"), 
		LIME(ChatColor.GREEN, DyeColor.LIME, "Light Green"), 
		PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, "Pink"), 
		GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, "Gray"), 
		SILVER(ChatColor.GRAY, DyeColor.SILVER, "Light Gray"), 
		CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN, "Cyan"), 
		PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, "Purple"), 
		BLUE(ChatColor.BLUE, DyeColor.BLUE, "Blue"), 
		GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, "Green"), 
		RED(ChatColor.RED, DyeColor.RED, "Red"), 
		BLACK(ChatColor.BLACK, DyeColor.BLACK, "Black");

		private ChatColor cColor;
		private DyeColor dColor;
		private String name;

		private MysteryTeam(ChatColor cColor, DyeColor dColor, String name) {
			this.cColor = cColor;
			this.dColor = dColor;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public ChatColor getChatColor() {
			return cColor;
		}

		public DyeColor getDyeColor() {
			return dColor;
		}
	}
}