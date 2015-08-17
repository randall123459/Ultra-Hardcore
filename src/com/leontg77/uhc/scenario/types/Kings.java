package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.PlayerUtils;

public class Kings extends Scenario implements Listener {
	private ArrayList<String> kings = new ArrayList<String>();
	private boolean enabled = false;
	
	public Kings() {
		super("Kings", "Theres a king on each team, the king has 20 max hearts and resistance, if the king dies the teammates will be poisoned.");
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
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isEnabled()) {
            return;
        }

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
        
        for (String m8s : team.getEntries()) {
        	Player teams = Bukkit.getServer().getPlayer(m8s);
        	
        	if (teams == null) {
        		continue;
        	}
        	
        	PotionEffect sadnessEffect = new PotionEffect(PotionEffectType.CONFUSION, 80, 0); 
        	PotionEffect sadnessEffect2 = new PotionEffect(PotionEffectType.WEAKNESS, 3600, 0); 
        	PotionEffect sadnessEffect3 = new PotionEffect(PotionEffectType.POISON, 260, 0); 
        	teams.addPotionEffect(sadnessEffect); 
        	teams.addPotionEffect(sadnessEffect2); 
        	teams.addPotionEffect(sadnessEffect3);
        }
    }
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		ArrayList<String> ar = new ArrayList<String>();
		for (String arg : event.getMessage().split(" ")) {
			ar.add(arg);
		}
		String cmd = ar.get(0);
		ar.remove(0);
		final Player player = event.getPlayer();
		final String[] args = ar.toArray(new String[ar.size()]);
		
		if (cmd.equalsIgnoreCase("/setking")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "Kings are not enabled.");
				return;
			}
			
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /setking <player>");
				return;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				player.sendMessage(ChatColor.RED + "That player is not online.");
				return;
			}
			
			PotionEffect sadnessEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 0x7fffffff, 0); 
			PotionEffect sadnessEffect2 = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 0x7fffffff, 0); 
			PotionEffect sadnessEffect3 = new PotionEffect(PotionEffectType.SPEED, 0x7fffffff, 0); 
			target.addPotionEffect(sadnessEffect); 
			target.addPotionEffect(sadnessEffect2);
			target.addPotionEffect(sadnessEffect3);
			
			target.setMaxHealth(40);
			target.setHealth(40);
			kings.add(target.getName());
		}
		
		if (cmd.equalsIgnoreCase("/remking")) {
			event.setCancelled(true);
			if (!isEnabled()) {
				player.sendMessage(ChatColor.RED + "Kings are not enabled.");
				return;
			}
			
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /remking <player>");
				return;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[0]);
			
			if (target == null) {
				player.sendMessage(ChatColor.RED + "That player is not online.");
				return;
			}
			
			for (PotionEffect effect : target.getActivePotionEffects()) {
				target.removePotionEffect(effect.getType());
			}
			
			target.setMaxHealth(20);
			target.setHealth(20);
			kings.remove(target.getName());
		}
	}
}