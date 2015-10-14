package com.leontg77.uhc.listeners;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.HashSet;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.State;
import com.leontg77.uhc.UBL;
import com.leontg77.uhc.User;
import com.leontg77.uhc.cmds.SpreadCommand;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PermsUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Login listener class.
 * <p> 
 * Contains all eventhandlers for login releated events.
 * 
 * @author LeonTG77
 */
public class LoginListener implements Listener {
	private Settings settings = Settings.getInstance();
	private Game game = Game.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Spectator spec = Spectator.getManager();
		Player player = event.getPlayer();
		
		User user = User.get(player);
		user.getFile().set("username", player.getName());
		user.getFile().set("uuid", player.getUniqueId().toString());
		user.saveFile();
		
		PacketUtils.setTabList(player);
		player.setNoDamageTicks(0);

		spec.hideSpectators(player);
		event.setJoinMessage(null);
		
		if (player.isDead()) {
			player.spigot().respawn();
		}
		
		if (spec.isSpectating(player)) {
			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			player.setLevel(0);
			player.setExp(0);
			
			spec.enableSpecmode(player, true);
		} else {
			if (State.isState(State.INGAME) && !player.isWhitelisted() && !spec.isSpectating(player)) {
				player.sendMessage(Main.prefix() + "You joined a game that you didn't play from the start.");

				player.getInventory().setArmorContents(null);
				player.getInventory().clear();
				player.setLevel(0);
				player.setExp(0);
				
				spec.enableSpecmode(player, true);
			} else {
				PlayerUtils.broadcast("§8[§a+§8] §7" + player.getName() + " has joined.");
				
				if (user.hasntBeenWelcomed()) {
					PlayerUtils.broadcast(Main.prefix() + ChatColor.GOLD + player.getName() + " §7just joined for the first time.");
					
					File f = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
					PlayerUtils.broadcast(Main.prefix() + "The server has now §a" + f.listFiles().length + "§7 unique joins.");
				}
			}
		}
		
		if (SpreadCommand.scatterLocs.containsKey(player.getName()) && SpreadCommand.isReady) {
			if (State.isState(State.SCATTER)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 2));
			}
			
			PlayerUtils.broadcast(Main.prefix() + "- §a" + player.getName() + " §7scheduled scatter.");
			player.teleport(SpreadCommand.scatterLocs.get(player.getName()));
			SpreadCommand.scatterLocs.remove(player.getName());
		}
		
		if (!State.isState(State.SCATTER) && player.hasPotionEffect(PotionEffectType.JUMP) && 
			player.hasPotionEffect(PotionEffectType.BLINDNESS) && 
			player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && 
			player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && 
			player.hasPotionEffect(PotionEffectType.SLOW) && 
			player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			
			player.removePotionEffect(PotionEffectType.JUMP);
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			player.removePotionEffect(PotionEffectType.SLOW);	
			player.removePotionEffect(PotionEffectType.INVISIBILITY);	
		}
		
		if (State.isState(State.LOBBY)) {
			player.teleport(Main.getSpawn());
		}
		
		if (!game.isRecordedRound()) {
			player.sendMessage("§8» ----------[ §4§lArctic UHC §8]---------- «");
			
			if (GameUtils.getTeamSize().startsWith("No")) {
				player.sendMessage("§8» §c No games running");
			} 
			else if (GameUtils.getTeamSize().startsWith("Open")) {
				player.sendMessage("§8» §7 Open PvP, use §a/a §7to join.");
			} 
			else {
				player.sendMessage("§8» §7 Host: §a" + Settings.getInstance().getConfig().getString("game.host"));
				player.sendMessage("§8» §7 Gamemode: §a" + GameUtils.getTeamSize() + Settings.getInstance().getConfig().getString("game.scenarios"));
			}
			
			player.sendMessage("§8» --------------------------------- «");
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		Spectator spec = Spectator.getManager();
		Arena arena = Arena.getInstance();
		
		PermsUtils.removePermissions(player);
		event.setQuitMessage(null);
		
		if (!spec.isSpectating(player)) {
			PlayerUtils.broadcast("§8[§c-§8] §7" + player.getName() + " has left.");
		}
		
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			player.getInventory().setArmorContents(null);
			player.getInventory().clear();
			
			arena.removePlayer(player, false);
		}
		
		if (Main.msg.containsKey(player)) {
			Main.msg.remove(player);
		}
		
		HashSet<CommandSender> temp = new HashSet<CommandSender>();
		
		for (CommandSender key : Main.msg.keySet()) {
			temp.add(key);
		}
		
		for (CommandSender key : temp) {
			if (Main.msg.get(key).equals(player)) {
				Main.msg.remove(key);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		PermsUtils.addPermissions(player);
		
		if (event.getResult() == Result.KICK_BANNED) {
			BanList name = Bukkit.getBanList(Type.NAME);
			BanList ip = Bukkit.getBanList(Type.IP);
			
			String adress = event.getAddress().getHostAddress();
			
			if (name.getBanEntry(player.getName()) != null) {
				if (player.hasPermission("uhc.staff")) {
					name.pardon(player.getName());
					event.allow();
					return;
				}

				BanEntry ban = name.getBanEntry(player.getName());
				PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + player.getName() + " §7tried to join while being " + (ban.getExpiration() == null ? "banned" : "temp-banned") + " for:§c " + ban.getReason(), "uhc.staff");
				
				event.setKickMessage(
				"§8» §7You have been §4" + (ban.getExpiration() == null ? "banned" : "temp-banned") + " §7from §6Arctic UHC §8«" +
				"\n" + 
				"\n§cReason §8» §7" + ban.getReason() +
				"\n§cBanned by §8» §7" + ban.getSource() + (ban.getExpiration() == null ? "" : "" +
				"\n§cExpires in §8» §7" + DateUtils.formatDateDiff(ban.getExpiration().getTime())) +
				"\n" +
				"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				);
			}
			else if (ip.getBanEntry(adress) != null) {
				if (player.hasPermission("uhc.staff")) {
					ip.pardon(adress);
					event.allow();
					return;
				}

				BanEntry ban = ip.getBanEntry(adress);
				PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + player.getName() + " §7tried to join while being IP-banned for:§c " + ban.getReason(), "uhc.staff");
				
				event.setKickMessage(
				"§8» §7You have been §4IP banned §7from §6Arctic UHC §8«" +
				"\n" + 
				"\n§cReason §8» §7" + ban.getReason() +
				"\n§cBanned by §8» §7" +
				"\n" +
				"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				);
			}
			else {
				event.allow();
			}
			return;
		}
		
		if (event.getResult() == Result.KICK_WHITELIST) {
			if (game.isRecordedRound()) {
				event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThere are no games running");
				return;
			}
			
			if (player.isOp()) {
				event.allow();
				return;
			}
			
			String teamSize = GameUtils.getTeamSize();
			
			if (teamSize.startsWith("No")) {
				event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThere are no games running");
			}
			else if (teamSize.startsWith("Open")) {
				Bukkit.setWhitelist(false);
				event.allow();
				return;
			} 
			else {
				if (State.isState(State.LOBBY)) {
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThe game has not opened yet,\n§ccheck the post for open time.\n\n§7Match post: §a" + settings.getConfig().getString("matchpost", "redd.it"));
				}
				else {
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThe game has already started");
				}
			}
			
			if (player.hasPermission("uhc.prelist")) {
				String scenario = game.getScenarios();
				
				boolean moles = false;
				
				for (String scen : scenario.split(" ")) {
					if (scen.equalsIgnoreCase("moles")) {
						moles = true;
					}
				}
				
				if (moles && State.isState(State.LOBBY)) {
					String kickMsg = event.getKickMessage();
					
					event.disallow(Result.KICK_WHITELIST, "§4§lVIP's are not pre-whitelisted for Mole games\n\n" + kickMsg);
					return;
				}
				
				event.allow();
			}
			return;
		}
		
		if (PlayerUtils.getPlayers().size() >= settings.getConfig().getInt("maxplayers", 150)) {
			if (game.isRecordedRound()) {
				return;
			}
			
			if (player.isWhitelisted()) {
				event.allow();
				return;
			}
			
			if (player.hasPermission("uhc.staff")) {
				if (State.isState(State.INGAME)) {
					event.allow();
					return;
				}
			} 

			event.disallow(Result.KICK_FULL, "§8» §7The server is currently full, try again later §8«");
		} else {
			event.allow();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (UBL.getManager().isBanned(event.getName(), event.getUniqueId())) {
            UBL.BanEntry banEntry = UBL.getManager().banlistByUUID.get(event.getUniqueId());
        	PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + event.getName() + " §7tried to join while being UBL'ed for:§c " + banEntry.getData("Reason"), "uhc.staff");
        	
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, UBL.getManager().getBanMessage(event.getUniqueId()));
            return;
        }
		
        if (UBL.getManager().isBanned(event.getName())) {
            UBL.BanEntry banEntry = UBL.getManager().banlistByIGN.get(event.getName().toLowerCase());
        	PlayerUtils.broadcast(Main.prefix() + ChatColor.RED + event.getName() + " §7tried to join while being UBL'ed for:§c " + banEntry.getData("Reason"), "uhc.staff");
        	
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, UBL.getManager().getBanMessage(event.getName()));
        }
    }
}