package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Kings scenario class
 * 
 * @author LeonTG77
 */
public class Kings extends Scenario implements Listener, CommandExecutor {
	private ArrayList<String> kings = new ArrayList<String>();
	private boolean enabled = false;
	
	public Kings() {
		super("Kings", "Theres a king on each team, the king has 20 max hearts and resistance, if the king dies the teammates will be poisoned.");
		Main main = Main.plugin;
		
		main.getCommand("addking").setExecutor(this);
		main.getCommand("remking").setExecutor(this);
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (!enable) {
			for (String k : kings) {
				Player teams = Bukkit.getServer().getPlayer(k);
	        	
	        	if (teams == null) {
	        		continue;
	        	}
	        	
	        	for (PotionEffect effect : teams.getActivePotionEffects()) {
	        		teams.removePotionEffect(effect.getType());
	        	}
	        	teams.setMaxHealth(20);
			}
			kings.clear();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!kings.contains(player.getName())) {
        	return;
        }

        Team team = player.getScoreboard().getEntryTeam(player.getName());

        if (team == null) {
            return;
        }
        
        kings.remove(player.getName());
        PlayerUtils.broadcast(ChatColor.GOLD + "The king on team " + team.getName().substring(3) + " has died, " + kings.size() + " remaining.");
        
        for (String entry : team.getEntries()) {
        	Player teamMate = Bukkit.getServer().getPlayer(entry);
        	
        	if (teamMate != null) {
        		PotionEffect effectOne = new PotionEffect(PotionEffectType.CONFUSION, 80, 0); 
            	PotionEffect effectTwo = new PotionEffect(PotionEffectType.WEAKNESS, 3600, 0); 
            	PotionEffect effectThree = new PotionEffect(PotionEffectType.POISON, 260, 0); 
            	
            	teamMate.addPotionEffect(effectOne); 
            	teamMate.addPotionEffect(effectTwo); 
            	teamMate.addPotionEffect(effectThree);
        	}
        }
    }

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("addking")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.PREFIX + "\"Kings\" is not enabled.");
				return true;
			}
			
			if (args.length == 0) {
				sender.sendMessage(Main.PREFIX + "Usage: /setking <player>");
				return true;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "That player is not online.");
				return true;
			}
			
			PotionEffect effectOne = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 0); 
			PotionEffect effectTwo = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0); 
			PotionEffect effectThree = new PotionEffect(PotionEffectType.SPEED, 1726272000, 0); 
			
			target.addPotionEffect(effectOne); 
			target.addPotionEffect(effectTwo);
			target.addPotionEffect(effectThree);
			
			target.setMaxHealth(40);
			target.setHealth(40);
			kings.add(target.getName());
		}
		
		if (cmd.getName().equalsIgnoreCase("remking")) {
			if (!isEnabled()) {
				sender.sendMessage(Main.PREFIX + "\"Kings\" is not enabled.");
				return true;
			}
			
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Usage: /remking <player>");
				return true;
			} 
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "That player is not online.");
				return true;
			}
			
			for (PotionEffect effect : target.getActivePotionEffects()) {
				target.removePotionEffect(effect.getType());
			}
			
			target.setMaxHealth(20);
			target.setHealth(20);
			kings.remove(target.getName());
		}
		return true;
	}
}