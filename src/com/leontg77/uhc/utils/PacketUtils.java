package com.leontg77.uhc.utils;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;

/**
 * Packet utilities class.
 * <p>
 * Contains packet related methods.
 * 
 * @author LeonTG77
 */
public class PacketUtils {
	
	/**
	 * Sets a tablist for the given player.
	 * 
	 * @param player the player.
	 */
	public static void setTabList(Player player) {
		Game game = Game.getInstance();

		if (game.isRecordedRound()) {
	        return;
		} 
		
		CraftPlayer craft = (CraftPlayer) player;
		
		IChatBaseComponent headerJSON = ChatSerializer.a(
	      	"{text:'§4§lArctic UHC§r\n" +
	       	"§a/rules §8- §a/ms §8- §a/lag §8- §a/post §8- §a/hof\n'}"
	    );

		String gamemode = GameUtils.getTeamSize() + game.getScenarios();
		String host = game.getHost();
		int teamsize = game.getTeamSize();
	        
		IChatBaseComponent footerJSON = ChatSerializer.a(
			"{text:'\n§7" + gamemode + (teamsize > 0 || teamsize < -1 ? 
			"\n§4Host: §a" + host : "") + "'}"
		);
	        
		PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(headerJSON);
	 
		try {
			Field field = headerPacket.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(headerPacket, footerJSON);
		}
		catch (Exception e) {
			Bukkit.getServer().getLogger().severe("§cCould not send tab list packets to " + player.getName());
			return;
		}
		        
		craft.getHandle().playerConnection.sendPacket(headerPacket);
	}
	
	/**
	 * Sends a message in action bar to the given player.
	 * 
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
	 * 
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
}