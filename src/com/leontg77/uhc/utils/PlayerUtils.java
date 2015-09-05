package com.leontg77.uhc.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Data;
import com.leontg77.uhc.Data.Rank;
import com.leontg77.uhc.Fireworks;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;

/**
 * Player utilities class.
 * <p>
 * Contains player related methods.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class PlayerUtils {
	
	/**
	 * Get a list of players online.
	 * 
	 * @return A list of online players.
	 */
	public static Set<Player> getPlayers() {
		HashSet<Player> list = new HashSet<Player>();
		
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			list.add(online);
		}
		
		return list;
	}
	
	/**
	 * Gets an offline player by a name.
	 * <p>
	 * This is just because of the deprecation on <code>Bukkit.getOfflinePlayer(String)</code> 
	 * 
	 * @param name The name.
	 * @return the offline player.
	 */
	public static OfflinePlayer getOfflinePlayer(String name) {
		return Bukkit.getServer().getOfflinePlayer(name);
	}
	
	/**
	 * Broadcasts a message to everyone online with a specific permission.
	 * 
	 * @param message the message.
	 * @param permission the permission.
	 */
	public static void broadcast(String message, String permission) {
		for (Player online : getPlayers()) {
			if (online.hasPermission(permission)) {
				online.sendMessage(message);
			}
		}
		
		Bukkit.getLogger().info(message.replaceAll("§l", ""));
	}
	
	/**
	 * Broadcasts a message to everyone online.
	 * 
	 * @param message the message.
	 */
	public static void broadcast(String message) {
		for (Player online : getPlayers()) {
			online.sendMessage(message);
		}
		
		Bukkit.getLogger().info(message.replaceAll("§l", ""));
	}

	/**
	 * Get the inv size of # players online.
	 * 
	 * @return the inv size.
	 */
	public static int playerInvSize() {
		int length = getPlayers().size();
		length = (length - Spectator.getManager().spectators.size());
		
		if (length <= 9) {
			return 9;
		} else if (length > 9 && length <= 18) {
			return 18;
		} else if (length > 18 && length <= 27) {
			return 27;
		} else if (length > 27 && length <= 36) {
			return 36;
		} else if (length > 36 && length <= 45) {
			return 45;
		} else if (length > 45 && length <= 54) {
			return 54;
		}
		
		return 54;
	}
	
	/**
	 * Get a list of entites within a distance of a location.
	 * 
	 * @param loc the location.
	 * @param distance the distance.
	 * @return A list of entites nearby.
	 */
	public static List<Entity> getNearby(Location loc, int distance) {
		List<Entity> list = new ArrayList<Entity>();
		
		for (Entity e : loc.getWorld().getEntities()) {
			if (e instanceof Player) {
				continue;
			}
			
			if (!e.getType().isAlive()) {
				continue;
			}
			
			if (loc.distance(e.getLocation()) <= distance) {
				list.add(e);
			}
		}
		
		for (Player online : getPlayers()) {
			if (online.getWorld() == loc.getWorld()) {
				if (loc.distance(online.getLocation()) <= distance) {
					list.add(online);
				}
			}
		}
		
		return list;
	}
	
	public static void playWinnerFireworks() {
		new BukkitRunnable() {
			Location center = new Location(Bukkit.getWorld("lobby"), 0.5, 34, 0.5);
			
			int i = 0;
			
			public void run() {
				Random rand = new Random();
				int x = rand.nextInt(50 * 2) - 50;
				int z = rand.nextInt(50 * 2) - 50;

				Location loc = new Location(Bukkit.getWorld("lobby"), x + 0.5, 34, z + 0.5);
				loc.setY(BlockUtils.highestBlock(loc).getY());
				Fireworks.getRandomizer().launchRandomFirework(loc.clone().add(0, 1, 0));
				
				if (new Random().nextDouble() < 0.1) {
					Fireworks.getRandomizer().launchRandomFirework(center);
				}
				
				i++;
				
				if (i == 200) {
					cancel();
				}
			}
		}.runTaskTimer(Main.plugin, 20, 5);
	}
	
	/**
	 * Get the given player's ping.
	 * 
	 * @param player the player
	 * @return the players ping
	 */
	public static int getPing(Player player) {
		CraftPlayer craft = (CraftPlayer) player;
		return craft.getHandle().ping;
	}
	
	/**
	 * Sets a tablist for the given player.
	 * 
	 * @param player the player.
	 */
	public static void setTabList(Player player) {
		CraftPlayer craft = (CraftPlayer) player;

        IChatBaseComponent headerJSON = ChatSerializer.a("{text:'§8=-=-= §4Arctic UHC §8=-=-=\n§a/rules §8| §a/post §8| §a/lag §8| §a/ms §8| §a/hof\n'}");
        IChatBaseComponent footerJSON = ChatSerializer.a("{text:'\n§7" + GameUtils.getTeamSize() + Settings.getInstance().getConfig().getString("game.scenarios") + (Main.teamSize > 0 || Main.teamSize == -2 ? "\n§4Host: §a" + Settings.getInstance().getConfig().getString("game.host") : "") + "'}");

        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(headerJSON);
 
        try {
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, footerJSON);
        }
        catch (Exception e) {
            Bukkit.getServer().getLogger().severe("§cCould not send tab list packets to " + player.getName());
        }
        finally {
            craft.getHandle().playerConnection.sendPacket(headerPacket);
        }
	}
	
	/**
	 * Sends a message in action bar to the given player.
	 * @param player the player.
	 * @param msg the message.
	 */
	public static void sendAction(Player player, String msg) {
		CraftPlayer craft = (CraftPlayer) player;

        IChatBaseComponent actionJSON = ChatSerializer.a("{text:'" + msg + "'}");
        PacketPlayOutChat actionPacket = new PacketPlayOutChat(actionJSON, (byte) 2);
        
        craft.getHandle().playerConnection.sendPacket(actionPacket);
	}
	
	/**
	 * Sends a title message to the given player.
	 * 
	 * @param player the player displaying to.
	 * @param title the title.
	 * @param subtitle the subtitle.
	 * @param in how long it uses to fade in.
	 * @param out how long it uses to fade out.
	 * @param stay how long it stays.
	 */
	public static void sendTitle(Player player, String title, String subtitle, int in, int stay, int out) {
        CraftPlayer craft = (CraftPlayer) player;
        
        IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
        IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");
        
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, in, stay, out);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        
        craft.getHandle().playerConnection.sendPacket(titlePacket);
        craft.getHandle().playerConnection.sendPacket(subtitlePacket);
    }

	/**
	 * Handle the permissions for the given player.
	 * 
	 * @param player the player.
	 */
	public static void handlePermissions(Player player) {
		if (Main.permissions.get(player.getName()) == null) {
			Main.permissions.put(player.getName(), player.addAttachment(Main.plugin));
		}

		PermissionAttachment perm = Main.permissions.get(player.getName());
		Data data = Data.getFor(player);
	
		String uuid = player.getUniqueId().toString();
		
		if (uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f") || uuid.equals("8b2b2e07-b694-4bd0-8f1b-ba99a267be41")) {
			perm.setPermission("bukkit.command.timings", true);
			perm.setPermission("spigot.commands.timings", true);
			perm.setPermission("minecraft.command.scoreboard", true);
			perm.setPermission("minecraft.command.tp", true);
			perm.setPermission("minecraft.command.time", true);
			perm.setPermission("minecraft.command.gamerule", true);
			perm.setPermission("minecraft.command.scoreboard", true);
			perm.setPermission("minecraft.command.worldborder", true);
			perm.setPermission("minecraft.command.effect", true);
			perm.setPermission("bukkit.command.ban.ip", true);
			perm.setPermission("bukkit.command.ban.name", true);
			perm.setPermission("bukkit.command.unban.ip", true);
			perm.setPermission("bukkit.command.unban.name", true);
			perm.setPermission("bukkit.command.reload", true);
			perm.setPermission("uhc.gamemode.other", true);
			perm.setPermission("worldedit.*", true);
			perm.setPermission("uhc.aboard", true);
			perm.setPermission("uhc.build", true);
			perm.setPermission("uhc.perma", true);
			perm.setPermission("uhc.sound", true);
			perm.setPermission("uhc.text", true);
			perm.setPermission("uhc.setspawn", true);
			perm.setPermission("uhc.*", true);
			player.setOp(true);
		} 
		else if (uuid.equals("679021a8-67c1-4317-8323-4b2b839a01f6")) {
			perm.setPermission("bukkit.command.timings", true);
			perm.setPermission("spigot.commands.timings", true);
		}
		
		perm.setPermission("mv.bypass.gamemode.*", true);
		
		if (data.getRank() == Rank.USER) {
			return;
		}

		perm.setPermission("uhc.prelist", true);
		
		if (data.getRank() == Rank.STAFF || data.getRank() == Rank.TRIAL || data.getRank() == Rank.HOST) {
			perm.setPermission("uhc.commandspy", true);
			perm.setPermission("bukkit.command.whitelist.list", true);
			perm.setPermission("uhc.whitelist", true);
			perm.setPermission("uhc.spectate", true);
			perm.setPermission("uhc.tempban", true);
			perm.setPermission("uhc.seemsg", true);
			perm.setPermission("uhc.admin", true);
			perm.setPermission("uhc.staff", true);
			perm.setPermission("uhc.mute", true);
			perm.setPermission("uhc.kick", true);
			perm.setPermission("uhc.ban", true);
			
			if (data.getRank() == Rank.TRIAL || data.getRank() == Rank.HOST) {
				perm.setPermission("multiverse.core.teleport.*", true);
				perm.setPermission("multiverse.core.teleport", true);
				perm.setPermission("multiverse.core.confirm", true);
				perm.setPermission("multiverse.core.unload", true);
				perm.setPermission("multiverse.core.remove", true);
				perm.setPermission("multiverse.core.create", true);
				perm.setPermission("multiverse.core.import", true);
				perm.setPermission("multiverse.teleport.*", true);
				perm.setPermission("multiverse.core.load", true);
				perm.setPermission("multiverse.teleport", true);
				perm.setPermission("uhc.clearinv.other", true);
				perm.setPermission("uhc.clearxp.other", true);
				perm.setPermission("uhc.setmaxhealth", true);
				perm.setPermission("uhc.arenaadmin", true);
				perm.setPermission("uhc.heal.other", true);
				perm.setPermission("uhc.feed.other", true);
				perm.setPermission("uhc.sethealth", true);
				perm.setPermission("worldborder.*", true);
				perm.setPermission("uhc.broadcast", true);
				perm.setPermission("uhc.teamadmin", true);
				perm.setPermission("uhc.scenario", true);
				perm.setPermission("uhc.clearinv", true);
				perm.setPermission("uhc.gamemode", true);
				perm.setPermission("uhc.clearxp", true);
				perm.setPermission("uhc.giveall", true);
				perm.setPermission("uhc.spread", true);
				perm.setPermission("uhc.random", true);
				perm.setPermission("uhc.border", true);
				perm.setPermission("uhc.config", true);
				perm.setPermission("uhc.random", true);
				perm.setPermission("uhc.start", true);
				perm.setPermission("uhc.board", true);
				perm.setPermission("uhc.vote", true);
				perm.setPermission("uhc.heal", true);
				perm.setPermission("uhc.feed", true);
				perm.setPermission("uhc.pvp", true);
				perm.setPermission("uhc.end", true);
				
				if (data.getRank() == Rank.HOST) {
					perm.setPermission("multiverse.core.list.self", true);
					perm.setPermission("multiverse.core.list.*", true);
					perm.setPermission("multiverse.core.list", true);
					perm.setPermission("multiverse.core.*", true);
					perm.setPermission("multiverse.*", true);
					perm.setPermission("uhc.skull", true);
					perm.setPermission("uhc.speed", true);
					perm.setPermission("uhc.moles", true);
					perm.setPermission("uhc.invsee", true);
					perm.setPermission("uhc.unban", true);
					perm.setPermission("uhc.tp", true);
					perm.setPermission("uhc.spectateother", true);
				}
			}
		}
		
		if (uuid.equals("3be33527-be7e-4eb2-8b66-5b76d3d7ecdc")) {
			perm.setPermission("uhc.gamemode", false);
		}
	}
	
	/**
	 * Handle the permissions for the given player if he leaves.
	 * 
	 * @param player the player.
	 */
	public static void handleLeavePermissions(Player player) {
		if (Main.permissions.get(player.getName()) == null) {
			return;
		}
		
		try {
			player.removeAttachment(Main.permissions.get(player.getName()));
		} catch (Exception e) {
			
		}
		
		Main.permissions.remove(player.getName());
	}
}