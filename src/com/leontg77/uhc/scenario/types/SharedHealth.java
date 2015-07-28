package com.leontg77.uhc.scenario.types;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

public class SharedHealth extends Scenario implements Listener {
	private Map<String, Double> damageBalance;
    private Map<String, Boolean> sharedDamage;
	private boolean enabled = false;
	
	public SharedHealth() {
		super("SharedHealth", "All teammates share their health");
		damageBalance = new HashMap<String, Double>();
        sharedDamage = new HashMap<String, Boolean>();
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!isEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        double balance = getPlayersDamageBalance(player.getName());

        if (balance != 0.0D) {
            double newHealth = (player.getHealth() + balance > 0.0D) ? player.getHealth() + balance : 0.0D;
            
            if (newHealth > player.getMaxHealth()) {
                newHealth = player.getMaxHealth();
            }
            
            player.setHealth(newHealth);
            resetPlayersDamageBalance(player.getName());
        }
    }
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        setSharedDamage(player.getName(), false);

        if ((event.getCause() == EntityDamageEvent.DamageCause.LAVA) || (event.getCause() == EntityDamageEvent.DamageCause.FIRE) || (event.getCause() == EntityDamageEvent.DamageCause.POISON)) {
            return;
        }

        final Team team = player.getScoreboard().getEntryTeam(player.getName());

        if (team == null || team.getSize() <= 1) {
            return;
        }

        double damage = event.getFinalDamage();
        double teamSize = team.getSize();
        double sharedDamage = damage / teamSize;

        for (String teammate : team.getEntries()) {
            final Player onlineTeammate = Bukkit.getServer().getPlayer(teammate);

            if (onlineTeammate != null) {
                double currentHealth = onlineTeammate.getHealth();
                double finalHealth = currentHealth - sharedDamage;

                if (finalHealth < 0.0D) {
                    finalHealth = 0.0D;
                }

                final double finalHealthAsynch = finalHealth;

                if (!onlineTeammate.getUniqueId().equals(player.getUniqueId())) {
                    setSharedDamage(onlineTeammate.getName(), true);
                    onlineTeammate.damage(0.0D);
                    onlineTeammate.setHealth(finalHealthAsynch);
                }
            } else {
                setPlayersDamageBalance(teammate, sharedDamage * -1.0D);
                setSharedDamage(teammate, true);
            }
        }

        double currentHealth = player.getHealth();
        double finalHealth = currentHealth - sharedDamage;

        if (finalHealth < 0.0D) {
            finalHealth = 0;
        }

        if (finalHealth > 0) {
            final double finalHealthAsynch = finalHealth;

            event.setDamage(0.0D);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setHealth(finalHealthAsynch);
                }
            }.runTaskLater(Main.plugin, 1);
        } else {
            event.setDamage(event.getDamage() * 1000);
        }
    }
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player p = (Player) event.getEntity();
        Team team = p.getScoreboard().getEntryTeam(p.getName());
        
        if (team != null && team.getSize() > 1) {
            double divider = team.getSize();
            double amount = event.getAmount();
            double gain = amount / divider;

            for (String offlinePlayer : team.getEntries()) {
                Player teammate = Bukkit.getPlayer(offlinePlayer);

                if (teammate != null) {
                    double teammateHealth = teammate.getHealth();
                    double finalHealth = teammateHealth + gain;
                    
                    if (finalHealth > teammate.getMaxHealth()) {
                        finalHealth = teammate.getMaxHealth();
                    }
                    
                    teammate.setHealth(finalHealth);
                } else {
                	setPlayersDamageBalance(offlinePlayer, gain);
                }
            }

            event.setCancelled(true);

        }
    }
	
	@EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (!isEnabled()) {
            return;
        }

        String playerName = event.getEntity().getName();

        Player player = event.getEntity();
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        
        String playerDisplayName = event.getEntity().getName();

        if (team != null) {
        	playerDisplayName = team.getPrefix() + playerDisplayName;
        	team.removeEntry(player.getName());
        }
        
        if (getSharedDamage().get(playerName) != null && getSharedDamage().get(playerName) == true) {
            event.setDeathMessage(playerDisplayName + ChatColor.WHITE + " died from sharing health");
        }
    }
	
    public Double getPlayersDamageBalance(String player) {
        if (damageBalance.containsKey(player)) {
            return damageBalance.get(player);
        } else {
            return 0.0D;
        }
    }

    public void setPlayersDamageBalance(String player, Double balance) {

        if (!damageBalance.containsKey(player)) {
            damageBalance.put(player, balance);
        } else {
            Double previousBalance = damageBalance.get(player);
            damageBalance.put(player, previousBalance + balance);
        }
    }

    public void resetDamageBalance() {
        this.damageBalance = new HashMap<String, Double>();
    }

    public void resetPlayersDamageBalance(String player) {
        if (damageBalance.containsKey(player)) {
            damageBalance.put(player, 0.0D);
        }
    }

    public Map<String, Boolean> getSharedDamage() {
        return sharedDamage;
    }

    public void setSharedDamage(String name, Boolean wasShared) {
        sharedDamage.put(name, wasShared);
    }

    public void resetSharedDamage() {
        sharedDamage = new HashMap<String, Boolean>();
    }
}
