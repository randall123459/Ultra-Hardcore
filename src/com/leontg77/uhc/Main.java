package com.leontg77.uhc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.uhc.cmds.ArenaCommand;
import com.leontg77.uhc.cmds.BanCommand;
import com.leontg77.uhc.cmds.BroadcastCommand;
import com.leontg77.uhc.cmds.ButcherCommand;
import com.leontg77.uhc.cmds.ClearInvCommand;
import com.leontg77.uhc.cmds.ClearXpCommand;
import com.leontg77.uhc.cmds.ConfigCommand;
import com.leontg77.uhc.cmds.ConfigCommand.Border;
import com.leontg77.uhc.cmds.EditCommand;
import com.leontg77.uhc.cmds.EndCommand;
import com.leontg77.uhc.cmds.FeedCommand;
import com.leontg77.uhc.cmds.GamemodeCommand;
import com.leontg77.uhc.cmds.GiveallCommand;
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
import com.leontg77.uhc.cmds.StartTimerCommand;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.TimeLeftCommand;
import com.leontg77.uhc.cmds.TimerCommand;
import com.leontg77.uhc.cmds.TlCommand;
import com.leontg77.uhc.cmds.TpCommand;
import com.leontg77.uhc.cmds.TpsCommand;
import com.leontg77.uhc.cmds.UnbanCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.cmds.WhitelistCommand;
import com.leontg77.uhc.listeners.ArenaListener;
import com.leontg77.uhc.listeners.BlockListener;
import com.leontg77.uhc.listeners.EntityListener;
import com.leontg77.uhc.listeners.PlayerListener;
import com.leontg77.uhc.listeners.SpecInfoListener;
import com.leontg77.uhc.listeners.WeatherListener;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.PlayerUtils;

/**
 * Main class of the UHC plugin.
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	private final Logger logger = Bukkit.getServer().getLogger();
	private Settings settings = Settings.getInstance();
	public static Main plugin;
	
	private static final long SECONDS_PER_HOUR = 3600;
	private static final long SECONDS_PER_MINUTE = 60;

	public static BukkitRunnable countdown;
	public static Recipe res;

	public static Border border;
	
	public static boolean ffa;
	public static int teamSize;
	
	public static boolean absorption;
	public static boolean ghead;
	public static boolean pearldmg;
	public static boolean godapple;
	
	public static int flintrate;
	public static int applerate;
	public static int shearrate;
	
	public static ArrayList<String> spectating = new ArrayList<String>();
	public static ArrayList<String> voted = new ArrayList<String>();
	public static ArrayList<String> mute = new ArrayList<String>();

	public static HashMap<CommandSender, CommandSender> msg = new HashMap<CommandSender, CommandSender>();
	public static HashMap<Inventory, BukkitRunnable> invsee = new HashMap<Inventory, BukkitRunnable>();
	public static HashMap<String, BukkitRunnable> relog = new HashMap<String, BukkitRunnable>();
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
		
		settings.getData().set("game.currentstate", GameState.getState().name());
		settings.saveData();
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " is now enabled.");
		plugin = this;
		
		Bukkit.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new SpecInfoListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WeatherListener(), this);

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
		getCommand("starttimer").setExecutor(new StartTimerCommand());
		getCommand("team").setExecutor(new TeamCommand());
		getCommand("timeleft").setExecutor(new TimeLeftCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("teamloc").setExecutor(new TlCommand());
		getCommand("tp").setExecutor(new TpCommand());
		getCommand("tps").setExecutor(new TpsCommand());
		getCommand("unban").setExecutor(new UnbanCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("whitelist").setExecutor(new WhitelistCommand());

		settings.setup(this);
		ScenarioManager.getManager().setup();
		Scoreboards.getManager().setup();
		Teams.getManager().setupTeams();
		Arena.getManager().setup();
		
		GameState.setState(GameState.valueOf(settings.getData().getString("game.currentstate")));
		addGoldenHeads();
		
		ffa = settings.getData().getBoolean("game.ffa");
		teamSize = settings.getData().getInt("game.teamsize");
		
		border = Border.valueOf(settings.getData().getString("options.border"));
		
		absorption = settings.getData().getBoolean("options.absorb");
		ghead = settings.getData().getBoolean("options.ghead");
		pearldmg = settings.getData().getBoolean("options.pearldmg");
		godapple = settings.getData().getBoolean("options.godapple");
		
		flintrate = settings.getData().getInt("game.flintrate");
		applerate = settings.getData().getInt("game.applerate");
		shearrate = settings.getData().getInt("game.shearrate");
		
		Bukkit.getLogger().info("�a[UHC] Config values has been setup.");

		for (Listener scen : ScenarioManager.getManager().getScenariosWithListeners()) {
			Bukkit.getServer().getPluginManager().registerEvents(scen, this);
		}
		Bukkit.getLogger().info("�a[UHC] Scenario listeners are now setup.");
		
		if (GameState.isState(GameState.LOBBY)) {
			File file = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
			File file2 = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
		
			int i = 0;
			for (File f : file.listFiles()) {
				f.delete();
				i++;
			}
			Bukkit.getLogger().info("�a[UHC] Deleted " + i + " player data files.");
			
			int j = 0;
			for (File f2 : file2.listFiles()) {
				f2.delete();
				j++;
			}
			Bukkit.getLogger().info("�a[UHC] Deleted " + j + " player stats files.");
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {	
					if (spectating.contains(online.getName())) {
						online.setFoodLevel(20);
						online.setSaturation(20);
						online.setHealth(online.getMaxHealth());
						online.setFireTicks(0);
						
						if (!online.getAllowFlight()) {
							online.setAllowFlight(true);
						}
						
						if (online.getGameMode() != GameMode.SPECTATOR) {
							online.setGameMode(GameMode.SPECTATOR);
						}
					}
					
					if (online.isOp()) {
						online.sendMessage(ChatColor.DARK_RED + "You aren't allowed to have operator status.");
						Bukkit.getLogger().info("�a[UHC] Removed " + online.getName() + "'s operator status.");
						online.setOp(false);
					}
					
					if (ScenarioManager.getManager().getScenario("Pyrophobia").isEnabled()) {
						for (ItemStack item : online.getInventory().getContents()) {
							if (item != null && item.getType() == Material.WATER_BUCKET) {
								item.setType(Material.BUCKET);
								online.sendMessage(ChatColor.DARK_RED + "You aren't allowed to have water buckets in pyrophobia.");
							}
						}
					}

					Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
					Objective tabList = sb.getObjective("HP");
					Damageable player = online;
					
					double health = player.getHealth();
					double hearts = health / 2;
					double precent = hearts * 10;
					
					if (tabList != null) {
						Score score = tabList.getScore(online.getName());
						score.setScore((int) precent);
					}
					
					Objective bellowName = sb.getObjective("HP2");
					
					if (bellowName != null) {
						Score score = bellowName.getScore(online.getName());
						score.setScore((int) precent);
					}
				}
			}
		}, 1, 1);
	}
	
	/**
	 * Start the starting countdown.
	 */
	public static void startCountdown() {
		GameState.setState(GameState.WAITING);
		Runnables.timeToStart = 3;
		countdown = new Runnables();
		countdown.runTaskTimer(plugin, 20, 20);
	}
	
	/**
	 * Stop the starting countdown.
	 */
	public static void stopCountdown() {
		countdown.cancel();
	}
	
	/**
	 * Get the UHC prefix with an ending color.
	 * @param endcolor the ending color.
	 * @return The UHC prefix.
	 */
	public static String prefix() {
		String prefix = "�4�lUHC �8�l� �7";
		return prefix;
	}
	
	/**
	 * Get the UHC prefix with an ending color.
	 * @param endcolor the ending color.
	 * @return The UHC prefix.
	 */
	public static String prefix(ChatColor endcolor) {
		String prefix = "�4�lUHC �8�l� " + endcolor;
		return prefix;
	}
	
	/**
	 * Adds the golden head recipe.
	 */
	@SuppressWarnings("deprecation")
	public static void addGoldenHeads() {
        MaterialData mater = new MaterialData (Material.SKULL_ITEM);
        mater.setData((byte) 3);
        ItemStack head = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD  + "Golden Head");
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
        head.setItemMeta(meta); 
        
        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', mater);
        Bukkit.getServer().addRecipe(goldenhead);
        res = goldenhead;
		Bukkit.getLogger().info("�a[UHC] Golden heads recipe added.");
	}

    /**
     * Converts the seconds to human readable
     * @param ticks the  number of ticks
     * @return the human readable version
     */
    public static String ticksToString(long ticks) {
        int hours = (int) Math.floor(ticks / (double) SECONDS_PER_HOUR);
        ticks -= hours * SECONDS_PER_HOUR;
        int minutes = (int) Math.floor(ticks / (double)SECONDS_PER_MINUTE);
        ticks -= minutes * SECONDS_PER_MINUTE;
        int seconds = (int) ticks;

        StringBuilder output = new StringBuilder();
        if (hours > 0) {
            output.append(hours).append('h');
            if (minutes == 0) {
            	output.append("0m");
            }
        }
        if (minutes > 0) {
            output.append(minutes).append('m');
        }
        output.append(seconds).append('s');

        return output.toString();
    }
}