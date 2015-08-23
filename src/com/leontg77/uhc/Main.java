package com.leontg77.uhc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.cmds.ArenaCommand;
import com.leontg77.uhc.cmds.BanCommand;
import com.leontg77.uhc.cmds.BroadcastCommand;
import com.leontg77.uhc.cmds.ButcherCommand;
import com.leontg77.uhc.cmds.ClearInvCommand;
import com.leontg77.uhc.cmds.ClearXpCommand;
import com.leontg77.uhc.cmds.ConfigCommand;
import com.leontg77.uhc.cmds.EditCommand;
import com.leontg77.uhc.cmds.EndCommand;
import com.leontg77.uhc.cmds.FeedCommand;
import com.leontg77.uhc.cmds.GamemodeCommand;
import com.leontg77.uhc.cmds.GiveallCommand;
import com.leontg77.uhc.cmds.HOFCommand;
import com.leontg77.uhc.cmds.HealCommand;
import com.leontg77.uhc.cmds.HealthCommand;
import com.leontg77.uhc.cmds.HelpopCommand;
import com.leontg77.uhc.cmds.InvseeCommand;
import com.leontg77.uhc.cmds.KickCommand;
import com.leontg77.uhc.cmds.ListCommand;
import com.leontg77.uhc.cmds.MsCommand;
import com.leontg77.uhc.cmds.MsgCommand;
import com.leontg77.uhc.cmds.MuteCommand;
import com.leontg77.uhc.cmds.NearCommand;
import com.leontg77.uhc.cmds.PmCommand;
import com.leontg77.uhc.cmds.PvPCommand;
import com.leontg77.uhc.cmds.RandomCommand;
import com.leontg77.uhc.cmds.ReplyCommand;
import com.leontg77.uhc.cmds.RulesCommand;
import com.leontg77.uhc.cmds.ScenarioCommand;
import com.leontg77.uhc.cmds.SethealthCommand;
import com.leontg77.uhc.cmds.SetmaxhealthCommand;
import com.leontg77.uhc.cmds.SetspawnCommand;
import com.leontg77.uhc.cmds.SkullCommand;
import com.leontg77.uhc.cmds.SpectateCommand;
import com.leontg77.uhc.cmds.SpeedCommand;
import com.leontg77.uhc.cmds.SpreadCommand;
import com.leontg77.uhc.cmds.StaffChatCommand;
import com.leontg77.uhc.cmds.StartCommand;
import com.leontg77.uhc.cmds.StatsCommand;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.TimeLeftCommand;
import com.leontg77.uhc.cmds.TimerCommand;
import com.leontg77.uhc.cmds.TlCommand;
import com.leontg77.uhc.cmds.TpCommand;
import com.leontg77.uhc.cmds.TpsCommand;
import com.leontg77.uhc.cmds.UnbanCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.cmds.WhitelistCommand;
import com.leontg77.uhc.listeners.BlockListener;
import com.leontg77.uhc.listeners.EntityListener;
import com.leontg77.uhc.listeners.InventoryListener;
import com.leontg77.uhc.listeners.PlayerListener;
import com.leontg77.uhc.listeners.WeatherListener;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.NumberUtils;
import com.leontg77.uhc.util.PlayerUtils;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for prefixes, adding recipes, enabling and disabling.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	private final Logger logger = Bukkit.getServer().getLogger();
	private Settings settings = Settings.getInstance();
	public static Main plugin;
	
	public static BukkitRunnable countdown;
	public static Recipe headRecipe;
	public static Recipe melonRecipe;

	public static boolean aboard = false;
	public static boolean board = false;
	public static boolean muted = false;
	
	public static boolean ffa;
	public static int teamSize;

	public static Border border;
	public static boolean absorption;
	public static boolean goldenheads;
	public static boolean pearldamage;
	public static boolean notchapples;
	public static boolean deathlightning;
	public static boolean nether;
	public static boolean theend;
	public static boolean ghastdrops;
	public static boolean nerfedStrength;
	public static boolean tabcolors;
	public static boolean harderCrafting;

	public static boolean shears;
	public static int shearrate;
	public static int flintrate;

	public static HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	public static HashMap<CommandSender, CommandSender> msg = new HashMap<CommandSender, CommandSender>();
	public static HashMap<Inventory, BukkitRunnable> invsee = new HashMap<Inventory, BukkitRunnable>();
	public static HashMap<String, BukkitRunnable> relog = new HashMap<String, BukkitRunnable>();
	
	public static HashMap<String, Integer> kills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> teamKills = new HashMap<String, Integer>();
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
		
		settings.getData().set("state", State.getState().name());
		settings.saveData();
		
		ArrayList<String> scens = new ArrayList<String>();
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			scens.add(scen.getName());
		}
		
		settings.getData().set("scenarios", scens);
		settings.saveData();
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " is now enabled.");
		
		plugin = this;
		settings.setup();
		
		Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeatherListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new SpecInfo(), this);

		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("ban").setExecutor(new BanCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("butcher").setExecutor(new ButcherCommand());
		getCommand("clearinv").setExecutor(new ClearInvCommand());
		getCommand("clearxp").setExecutor(new ClearXpCommand());
		getCommand("config").setExecutor(new ConfigCommand());
		getCommand("edit").setExecutor(new EditCommand());
		getCommand("end").setExecutor(new EndCommand());
		getCommand("feed").setExecutor(new FeedCommand());
		getCommand("gamemode").setExecutor(new GamemodeCommand());
		getCommand("giveall").setExecutor(new GiveallCommand());
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("health").setExecutor(new HealthCommand());
		getCommand("helpop").setExecutor(new HelpopCommand());
		getCommand("hof").setExecutor(new HOFCommand());
		getCommand("invsee").setExecutor(new InvseeCommand());
		getCommand("kick").setExecutor(new KickCommand());
		getCommand("list").setExecutor(new ListCommand());
		getCommand("ms").setExecutor(new MsCommand());
		getCommand("msg").setExecutor(new MsgCommand());
		getCommand("mute").setExecutor(new MuteCommand());
		getCommand("near").setExecutor(new NearCommand());
		getCommand("pm").setExecutor(new PmCommand());
		getCommand("pvp").setExecutor(new PvPCommand());
		getCommand("random").setExecutor(new RandomCommand());
		getCommand("reply").setExecutor(new ReplyCommand());
		getCommand("rules").setExecutor(new RulesCommand());
		getCommand("scenario").setExecutor(new ScenarioCommand());
		getCommand("sethealth").setExecutor(new SethealthCommand());
		getCommand("setmaxhealth").setExecutor(new SetmaxhealthCommand());
		getCommand("skull").setExecutor(new SkullCommand());
		getCommand("spectate").setExecutor(new SpectateCommand());
		getCommand("setspawn").setExecutor(new SetspawnCommand());
		getCommand("speed").setExecutor(new SpeedCommand());
		getCommand("spread").setExecutor(new SpreadCommand());
		getCommand("ac").setExecutor(new StaffChatCommand());
		getCommand("start").setExecutor(new StartCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		getCommand("team").setExecutor(new TeamCommand());
		getCommand("timeleft").setExecutor(new TimeLeftCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("teamloc").setExecutor(new TlCommand());
		getCommand("tp").setExecutor(new TpCommand());
		getCommand("tps").setExecutor(new TpsCommand());
		getCommand("unban").setExecutor(new UnbanCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("whitelist").setExecutor(new WhitelistCommand());

		ScenarioManager.getInstance().setup();
		Scoreboards.getManager().setup();
		Arena.getManager().setup();
		Teams.getManager().setup();
		UBL.getManager().setup();
		
		State.setState(State.valueOf(settings.getData().getString("state", State.LOBBY.name())));
		addRecipes();
		
		ffa = settings.getConfig().getBoolean("game.ffa", true);
		teamSize = settings.getConfig().getInt("game.teamsize", 1);
		
		border = Border.valueOf(settings.getConfig().getString("feature.border.shrinkAt", Border.MEETUP.name()));
		absorption = settings.getConfig().getBoolean("feature.absorption.enabled", false);
		goldenheads = settings.getConfig().getBoolean("feature.goldenheads.enabled", true);
		pearldamage = settings.getConfig().getBoolean("feature.pearldamage.enabled", false);
		notchapples = settings.getConfig().getBoolean("feature.notchapples.enabled", true);
		deathlightning = settings.getConfig().getBoolean("feature.deathlightning.enabled", true);
		nether = settings.getConfig().getBoolean("feature.nether.enabled", false);
		theend = settings.getConfig().getBoolean("feature.theend.enabled", false);
		ghastdrops = settings.getConfig().getBoolean("feature.ghastdrops.enabled", true);
		nerfedStrength = settings.getConfig().getBoolean("feature.nerfedStrength.enabled", true);
		tabcolors = settings.getConfig().getBoolean("feature.tabcolors.enabled", false);
		harderCrafting = settings.getConfig().getBoolean("feature.harderCrafting.tabcolors", true);

		shears = settings.getConfig().getBoolean("rates.shears.enabled", true);
		shearrate = settings.getConfig().getInt("rates.shears.rate", 5);
		flintrate = settings.getConfig().getInt("rates.flint.rate", 35);
		
		Bukkit.getLogger().info("[UHC] Config values has been setup.");

		for (Listener scen : ScenarioManager.getInstance().getScenariosWithListeners()) {
			Bukkit.getServer().getPluginManager().registerEvents(scen, this);
		}
		
		Bukkit.getLogger().info("[UHC] Scenario listeners are now setup.");
		
		if (State.isState(State.LOBBY)) {
			File file = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
			File file2 = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
		
			int i = 0;
			for (File f : file.listFiles()) {
				f.delete();
				i++;
			}
			
			Bukkit.getLogger().info("[UHC] Deleted " + i + " player data files.");
			
			int j = 0;
			for (File f2 : file2.listFiles()) {
				f2.delete();
				j++;
			}
			
			Bukkit.getLogger().info("[UHC] Deleted " + j + " player stats files.");
		}
		
		for (String scen : settings.getData().getStringList("scenarios")) {
			ScenarioManager.getInstance().getScenario(scen).setEnabled(true);
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {	
					if (Spectator.getManager().isSpectating(online) && online.getGameMode() != GameMode.SPECTATOR) {
						online.setGameMode(GameMode.SPECTATOR);
					}
					
					if (Main.tabcolors) {
						ChatColor color;

						if (online.getHealth() < 6.66D) {
							color = ChatColor.RED;
						} else if (online.getHealth() < 13.33D) {
							color = ChatColor.YELLOW;
						} else {
							color = ChatColor.GREEN;
						}
					    
					    online.setPlayerListName(color + online.getName());
					}
					
					if (online.isOp()) {
						online.sendMessage(ChatColor.DARK_RED + "You aren't allowed to have operator status.");
						online.setOp(false);
					}

					Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
					int percent = NumberUtils.makePercent(online.getHealth());
					
					Objective tabList = sb.getObjective("tabHealth");
					
					if (tabList != null) {
						Score score = tabList.getScore(online.getName());
						score.setScore(percent);
					}
					
					Objective bellowName = sb.getObjective("nameHealth");
					
					if (bellowName != null) {
						Score score = bellowName.getScore(online.getName());
						score.setScore(percent);
					}
				}
				
				for (World world : Bukkit.getWorlds()) {
					if (world.getName().equals("lobby") || world.getName().equals("arena")) {
						if (world.getDifficulty() != Difficulty.PEACEFUL) {
							world.setDifficulty(Difficulty.PEACEFUL);
						}
					} else {
						if (world.getDifficulty() != Difficulty.HARD) {
							world.setDifficulty(Difficulty.HARD);
						}
					}
				}
			}
		}, 1, 1);
	}
	
	/**
	 * Get the UHC prefix with an ending color.
	 * @param endcolor the ending color.
	 * @return The UHC prefix.
	 */
	public static String prefix() {
		String prefix = "§4§lUHC §8» §7";
		return prefix;
	}
	
	/**
	 * Adds the golden head recipe.
	 */
	@SuppressWarnings("deprecation")
	public static void addRecipes() {
        MaterialData mater = new MaterialData (Material.SKULL_ITEM);
        mater.setData((byte) 3);
        ItemStack head = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD  + "Golden Head");
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
        head.setItemMeta(meta); 
        
        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', mater);
        Bukkit.getServer().addRecipe(goldenhead);
        headRecipe = goldenhead;
		Bukkit.getLogger().info("[UHC] Golden Head recipe added.");
		
        ItemStack melon = new ItemStack(Material.SPECKLED_MELON); 
        ShapedRecipe goldenmelon = new ShapedRecipe(melon).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', Material.MELON);
        Bukkit.getServer().addRecipe(goldenmelon);
        melonRecipe = goldenmelon;
  
		Bukkit.getLogger().info("[UHC] Golden Melon recipe added.");
	}
	
	/**
	 * The game state class.
	 * @author LeonTG77
	 */
	public enum State {
		LOBBY, SCATTER, INGAME;

		private static State currentState;
		
		/**
		 * Sets the current state to be #.
		 * @param state the state setting it to.
		 */
		public static void setState(State state) {
			Settings.getInstance().getData().set("state", state.name().toUpperCase());
			Settings.getInstance().saveData();
			
			currentState = state;
		}
		
		/**
		 * Checks if the state is #.
		 * @param state The state checking.
		 * @return True if it's the given state.
		 */
		public static boolean isState(State state) {
			return currentState == state;
		}
		
		/**
		 * Gets the current state.
		 * @return The state
		 */
		public static State getState() {
			return currentState;
		}
	}
	
	/**
	 * Border types enum class.
	 * @author LeonTG77
	 */
	public enum Border {
		NEVER, START, PVP, MEETUP;
	}
}