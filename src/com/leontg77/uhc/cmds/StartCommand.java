package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.GameState;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.util.PlayerUtils;

public class StartCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("start")) {
			if (sender.hasPermission("uhc.start")) {
				if (!GameState.isState(GameState.INGAME)) {
					PlayerUtils.broadcast(Main.prefix() + "The game is starting.");
					Main.startCountdown();
				} else {
					sender.sendMessage(ChatColor.RED + "The game has already started.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}