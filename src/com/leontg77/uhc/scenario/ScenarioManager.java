package com.leontg77.uhc.scenario;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.types.Backpacks;
import com.leontg77.uhc.scenario.types.Barebones;
import com.leontg77.uhc.scenario.types.BestBTC;
import com.leontg77.uhc.scenario.types.BestPvE;
import com.leontg77.uhc.scenario.types.BetaZombies;
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
import com.leontg77.uhc.scenario.types.Superheroes;
import com.leontg77.uhc.scenario.types.Timebomb;
import com.leontg77.uhc.scenario.types.TrainingRabbits;
import com.leontg77.uhc.scenario.types.VengefulSpirits;

/**
 * Scenario management class.
 * @author LeonTG77
 */
public class ScenarioManager {
	private ArrayList<Scenario> scen = new ArrayList<Scenario>();
	
	private ScenarioManager() {}
	private static ScenarioManager manager = new ScenarioManager();
	public static ScenarioManager getManager() {
		return manager;
	}
	
	/**
	 * Setup all the scenarios.
	 */
	public void setup() {
		// TODO: scen.add(new Assassins());
		// TODO: scen.add(new Astrophobia());
		scen.add(new Backpacks());
		scen.add(new Barebones());
		scen.add(new BestBTC());
		scen.add(new BestPvE());
		scen.add(new BetaZombies());
		scen.add(new BiomeParanoia());
		scen.add(new BlockRush());
		scen.add(new BloodDiamonds());
		/* TODO: Test */ scen.add(new Captains());
		scen.add(new Compensation());
		scen.add(new Cryophobia());
		scen.add(new CutClean());
		// TODO: scen.add(new DamageCycle());
		scen.add(new Depths());
		scen.add(new Diamondless());
		scen.add(new EnchantedDeath());
		scen.add(new Fallout());
		scen.add(new FlowerPower());
		// TODO: scen.add(new Genie());
		scen.add(new GoldenPearl());
		scen.add(new Goldless());
		scen.add(new GoldRush());
		scen.add(new GoneFishin());
		scen.add(new GoToHell());
		scen.add(new InfiniteEnchanter());
		scen.add(new Inventors());
		scen.add(new Kings());
		scen.add(new Krenzinator());
		scen.add(new LAFS());
		scen.add(new Lootcrates());
		scen.add(new Moles());
		// TODO: scen.add(new MysteryTeams());
		scen.add(new NightmareMode());
		scen.add(new NoFall());
		scen.add(new NoSprint());
		scen.add(new Paranoia());
		// TODO: scen.add(new PeriodOfResistance());
		scen.add(new Permakill());
		scen.add(new PotentialHearts());
		scen.add(new PotentialPermanent());
		scen.add(new Pyrophobia());
		scen.add(new RewardingLongshots());
		scen.add(new SharedHealth());
		scen.add(new SkyClean());
		scen.add(new Skyhigh());
		/* TODO: Fix */ scen.add(new SlaveMarket());
		scen.add(new Superheroes());
		scen.add(new Timebomb());
		/* TODO: Fix */ scen.add(new TrainingRabbits());
		scen.add(new VengefulSpirits());
		Bukkit.getLogger().info("§a[UHC] All scenarios has been setup.");
	}
	
	/**
	 * Get a scenario by a name.
	 * @param name the name.
	 * @return The scenario, null if not found.
	 */
	public Scenario getScenario(String name) {
		for (Scenario s : scen) {
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
	public List<Scenario> getScenarios() {
		return scen;
	}

	/**
	 * Get a list of all enabled scenarios.
	 * @return the list of enabled scenarios.
	 */
	public List<Scenario> getEnabledScenarios() {
		ArrayList<Scenario> l = new ArrayList<Scenario>();
		for (Scenario s : scen) {
			if (s.isEnabled()) {
				l.add(s);
			}
		}
		return l;
	}

	/**
	 * Get a list of all enabled scenarios.
	 * @return the list of enabled scenarios.
	 */
	public List<Scenario> getDisabledScenarios() {
		ArrayList<Scenario> l = new ArrayList<Scenario>();
		for (Scenario s : scen) {
			if (!s.isEnabled()) {
				l.add(s);
			}
		}
		return l;
	}

	/**
	 * Get a list of all scenarios that implements listener.
	 * @return the list of scenarios that implements listener.
	 */
	public List<Listener> getScenariosWithListeners() {
		ArrayList<Listener> l = new ArrayList<Listener>();
		for (Scenario s : scen) {
			if (s instanceof Listener) {
				l.add((Listener) s);
			}
		}
		return l;
	}
}