package com.leontg77.uhc.scenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.types.Assassins;
import com.leontg77.uhc.scenario.types.AssaultAndBattery;
import com.leontg77.uhc.scenario.types.Backpacks;
import com.leontg77.uhc.scenario.types.Barebones;
import com.leontg77.uhc.scenario.types.BestBTC;
import com.leontg77.uhc.scenario.types.BestPvE;
import com.leontg77.uhc.scenario.types.BetaZombies;
import com.leontg77.uhc.scenario.types.BigCrack;
import com.leontg77.uhc.scenario.types.BiomeParanoia;
import com.leontg77.uhc.scenario.types.BlockRush;
import com.leontg77.uhc.scenario.types.BloodDiamonds;
import com.leontg77.uhc.scenario.types.Captains;
import com.leontg77.uhc.scenario.types.Compensation;
import com.leontg77.uhc.scenario.types.Cryophobia;
import com.leontg77.uhc.scenario.types.CutClean;
import com.leontg77.uhc.scenario.types.Depths;
import com.leontg77.uhc.scenario.types.Diamondless;
import com.leontg77.uhc.scenario.types.EnchantedDeath;
import com.leontg77.uhc.scenario.types.Fallout;
import com.leontg77.uhc.scenario.types.FlowerPower;
import com.leontg77.uhc.scenario.types.GoToHell;
import com.leontg77.uhc.scenario.types.GoldRush;
import com.leontg77.uhc.scenario.types.GoldenPearl;
import com.leontg77.uhc.scenario.types.Goldless;
import com.leontg77.uhc.scenario.types.GoneFishin;
import com.leontg77.uhc.scenario.types.InfiniteEnchanter;
import com.leontg77.uhc.scenario.types.Inventors;
import com.leontg77.uhc.scenario.types.Kings;
import com.leontg77.uhc.scenario.types.Krenzinator;
import com.leontg77.uhc.scenario.types.LAFS;
import com.leontg77.uhc.scenario.types.Lootcrates;
import com.leontg77.uhc.scenario.types.MeleeFun;
import com.leontg77.uhc.scenario.types.Moles;
import com.leontg77.uhc.scenario.types.NightmareMode;
import com.leontg77.uhc.scenario.types.NoFall;
import com.leontg77.uhc.scenario.types.NoSprint;
import com.leontg77.uhc.scenario.types.Paranoia;
import com.leontg77.uhc.scenario.types.Permakill;
import com.leontg77.uhc.scenario.types.PotentialHearts;
import com.leontg77.uhc.scenario.types.PotentialPermanent;
import com.leontg77.uhc.scenario.types.Pyrophobia;
import com.leontg77.uhc.scenario.types.RewardingLongshots;
import com.leontg77.uhc.scenario.types.SharedHealth;
import com.leontg77.uhc.scenario.types.SkyClean;
import com.leontg77.uhc.scenario.types.Skyhigh;
import com.leontg77.uhc.scenario.types.SlaveMarket;
import com.leontg77.uhc.scenario.types.SlimyCrack;
import com.leontg77.uhc.scenario.types.Superheroes;
import com.leontg77.uhc.scenario.types.Timebomb;
import com.leontg77.uhc.scenario.types.TrainingRabbits;
import com.leontg77.uhc.scenario.types.VengefulSpirits;

/**
 * Scenario management class.
 * @author LeonTG77
 */
public class ScenarioManager {
	private HashSet<Scenario> scenarios = new HashSet<Scenario>();
	private static ScenarioManager manager = new ScenarioManager();
	
	/**
	 * Get the instance of this class.
	 * @return The class instance.
	 */
	public static ScenarioManager getInstance() {
		return manager;
	}
	
	/**
	 * Setup all the scenarios.
	 */
	public void setup() {
		/* TODO: Test */ scenarios.add(new Assassins());
		/* TODO: Test */ scenarios.add(new AssaultAndBattery());
		// TODO: scenarios.add(new Astrophobia());
		scenarios.add(new Backpacks());
		scenarios.add(new Barebones());
		scenarios.add(new BestBTC());
		scenarios.add(new BestPvE());
		scenarios.add(new BetaZombies());
		scenarios.add(new BigCrack());
		scenarios.add(new BiomeParanoia());
		scenarios.add(new BlockRush());
		scenarios.add(new BloodDiamonds());
		/* TODO: Test */ scenarios.add(new Captains());
		scenarios.add(new Compensation());
		scenarios.add(new Cryophobia());
		scenarios.add(new CutClean());
		// TODO: scenarios.add(new DamageCycle());
		scenarios.add(new Depths());
		scenarios.add(new Diamondless());
		scenarios.add(new EnchantedDeath());
		scenarios.add(new Fallout());
		scenarios.add(new FlowerPower());
		// TODO: scenarios.add(new Genie());
		scenarios.add(new GoldenPearl());
		scenarios.add(new Goldless());
		scenarios.add(new GoldRush());
		scenarios.add(new GoneFishin());
		scenarios.add(new GoToHell());
		scenarios.add(new InfiniteEnchanter());
		scenarios.add(new Inventors());
		scenarios.add(new Kings());
		scenarios.add(new Krenzinator());
		scenarios.add(new LAFS());
		scenarios.add(new Lootcrates());
		scenarios.add(new MeleeFun());
		scenarios.add(new Moles());
		// TODO: scenarios.add(new MysteryTeams());
		scenarios.add(new NightmareMode());
		scenarios.add(new NoFall());
		scenarios.add(new NoSprint());
		scenarios.add(new Paranoia());
		// TODO: scenarios.add(new PeriodOfResistance());
		scenarios.add(new Permakill());
		scenarios.add(new PotentialHearts());
		scenarios.add(new PotentialPermanent());
		scenarios.add(new Pyrophobia());
		scenarios.add(new RewardingLongshots());
		scenarios.add(new SharedHealth());
		scenarios.add(new SkyClean());
		scenarios.add(new Skyhigh());
		/* TODO: Fix */ scenarios.add(new SlaveMarket());
		scenarios.add(new SlimyCrack());
		scenarios.add(new Superheroes());
		scenarios.add(new Timebomb());
		/* TODO: Test */ scenarios.add(new TrainingRabbits());
		scenarios.add(new VengefulSpirits());
		Bukkit.getLogger().info("§a[UHC] All scenarios has been setup.");
	}
	
	/**
	 * Get a scenario by a name.
	 * @param name the name.
	 * @return The scenario, null if not found.
	 */
	public Scenario getScenario(String name) {
		for (Scenario s : scenarios) {
			if (name.equalsIgnoreCase(s.getName())) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Get a list of all scenarios.
	 * @return the list of scenarios.
	 */
	public Set<Scenario> getScenarios() {
		return scenarios;
	}

	/**
	 * Get an array of all scenarios that implements listener.
	 * @return the array of scenarios that implements listener.
	 */
	public Listener[] getScenariosWithListeners() {
		ArrayList<Listener> list = new ArrayList<Listener>();
		
		for (Scenario s : scenarios) {
			if (s instanceof Listener) {
				list.add((Listener) s);
			}
		}
		
		return list.toArray(new Listener[list.size()]);
	}

	/**
	 * Get a list of all enabled scenarios.
	 * @return the list of enabled scenarios.
	 */
	public List<Scenario> getEnabledScenarios() {
		ArrayList<Scenario> list = new ArrayList<Scenario>();
		
		for (Scenario s : scenarios) {
			if (s.isEnabled()) {
				list.add(s);
			}
		}
		
		return list;
	}

	/**
	 * Get a list of all enabled scenarios.
	 * @return the list of enabled scenarios.
	 */
	public List<Scenario> getDisabledScenarios() {
		ArrayList<Scenario> list = new ArrayList<Scenario>();
		
		for (Scenario s : scenarios) {
			if (!s.isEnabled()) {
				list.add(s);
			}
		}
		
		return list;
	}
}