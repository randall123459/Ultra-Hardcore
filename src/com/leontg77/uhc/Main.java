package com.leontg77.uhc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.cmds.AboardCommand;
import com.leontg77.uhc.cmds.ArenaCommand;
import com.leontg77.uhc.cmds.BanCommand;
import com.leontg77.uhc.cmds.BoardCommand;
import com.leontg77.uhc.cmds.BorderCommand;
import com.leontg77.uhc.cmds.BroadcastCommand;
import com.leontg77.uhc.cmds.ButcherCommand;
import com.leontg77.uhc.cmds.ClearChatCommand;
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
import com.leontg77.uhc.cmds.MatchpostCommand;
import com.leontg77.uhc.cmds.MsCommand;
import com.leontg77.uhc.cmds.MsgCommand;
import com.leontg77.uhc.cmds.MuteCommand;
import com.leontg77.uhc.cmds.NearCommand;
import com.leontg77.uhc.cmds.PermaCommand;
import com.leontg77.uhc.cmds.PmCommand;
import com.leontg77.uhc.cmds.PvPCommand;
import com.leontg77.uhc.cmds.RandomCommand;
import com.leontg77.uhc.cmds.RankCommand;
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
import com.leontg77.uhc.cmds.TempbanCommand;
import com.leontg77.uhc.cmds.TextCommand;
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
import com.leontg77.uhc.listeners.WorldListener;
import com.leontg77.uhc.managers.AntiStripmine;
import com.leontg77.uhc.managers.BiomeSwap;
import com.leontg77.uhc.managers.Parkour;
import com.leontg77.uhc.managers.UBL;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for prefixes, adding recipes, enabling and disabling.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	private Logger logger = getLogger();
	public static Main plugin;
	
	public static Recipe headRecipe;
	public static Recipe melonRecipe;
	
	public static HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	public static HashMap<CommandSender, CommandSender> msg = new HashMap<CommandSender, CommandSender>();
	
	public static HashMap<Inventory, BukkitRunnable> invsee = new HashMap<Inventory, BukkitRunnable>();
	public static HashMap<Inventory, BukkitRunnable> rules = new HashMap<Inventory, BukkitRunnable>();
	
	public static HashMap<String, Integer> teamKills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> kills = new HashMap<String, Integer>();
	
	public static HashMap<Player, int[]> rainbow = new HashMap<Player, int[]>();
	
	public static final String NO_PERMISSION_MESSAGE = Main.prefix() + ChatColor.RED + "You can't use that command.";
	private static Settings settings = Settings.getInstance();
	
	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " is now disabled.");
		
		BiomeSwap.getManager().resetBiomes();
		saveData();
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		
		plugin = this;
		settings.setup();

		ScenarioManager.getInstance().setup();
		AntiStripmine.getManager().setup();
		Scoreboards.getManager().setup();
		BiomeSwap.getManager().setup();
		Parkour.getManager().setup();
		Arena.getManager().setup();
		Teams.getManager().setup();
		UBL.getManager().setup();
		
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	    manager.addPacketListener(new HardcoreHearts(this));
	    
		recoverData();
		addRecipes();
		
		Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WorldListener(), this);

		getCommand("aboard").setExecutor(new AboardCommand());
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("ban").setExecutor(new BanCommand());
		getCommand("board").setExecutor(new BoardCommand());
		getCommand("border").setExecutor(new BorderCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("butcher").setExecutor(new ButcherCommand());
		getCommand("clearchat").setExecutor(new ClearChatCommand());
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
		getCommand("matchpost").setExecutor(new MatchpostCommand());
		getCommand("ms").setExecutor(new MsCommand());
		getCommand("msg").setExecutor(new MsgCommand());
		getCommand("mute").setExecutor(new MuteCommand());
		getCommand("near").setExecutor(new NearCommand());
		getCommand("perma").setExecutor(new PermaCommand());
		getCommand("pm").setExecutor(new PmCommand());
		getCommand("pvp").setExecutor(new PvPCommand());
		getCommand("random").setExecutor(new RandomCommand());
		getCommand("rank").setExecutor(new RankCommand());
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
		getCommand("tempban").setExecutor(new TempbanCommand());
		getCommand("text").setExecutor(new TextCommand());
		getCommand("timeleft").setExecutor(new TimeLeftCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("teamloc").setExecutor(new TlCommand());
		getCommand("tp").setExecutor(new TpCommand());
		getCommand("tps").setExecutor(new TpsCommand());
		getCommand("unban").setExecutor(new UnbanCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("whitelist").setExecutor(new WhitelistCommand());
		
		if (State.isState(State.LOBBY)) {
			File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
			File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
			
			Bukkit.getServer().setIdleTimeout(60);
		
			int totalDatafiles = 0;
			int totalStatsfiles = 0;
			
			for (File dataFiles : playerData.listFiles()) {
				dataFiles.delete();
				totalDatafiles++;
			}
			
			for (File statsFiles : stats.listFiles()) {
				statsFiles.delete();
				totalStatsfiles++;
			}

			plugin.getLogger().info("Deleted " + totalDatafiles + " player data files.");
			plugin.getLogger().info("Deleted " + totalStatsfiles + " player stats files.");
		}
		
		if (State.isState(State.INGAME)) {
			Bukkit.getServer().getPluginManager().registerEvents(new SpecInfo(), this);
		}
		
		for (Player online : PlayerUtils.getPlayers()) {	
			PlayerUtils.handlePermissions(online);
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {	
					if (Spectator.getManager().isSpectating(online) && online.getGameMode() != GameMode.SPECTATOR) {
						online.setGameMode(GameMode.SPECTATOR);
					}
					
					if (online.getInventory().getHelmet() != null && online.getInventory().getHelmet().getType() == Material.LEATHER_HELMET) {
						online.getInventory().setHelmet(rainbowArmor(online, online.getInventory().getHelmet()));
					}
               
					if (online.getInventory().getChestplate() != null && online.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE) {
						online.getInventory().setChestplate(rainbowArmor(online, online.getInventory().getChestplate()));
					}
               
					if (online.getInventory().getLeggings() != null && online.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS) {
						online.getInventory().setLeggings(rainbowArmor(online, online.getInventory().getLeggings()));
					}
               
					if (online.getInventory().getBoots() != null && online.getInventory().getBoots().getType() == Material.LEATHER_BOOTS) {
						online.getInventory().setBoots(rainbowArmor(online, online.getInventory().getBoots()));
					}
					
					if (Game.getInstance().tabShowsHealthColor()) {
						ChatColor color;

						if (online.getHealth() < 6.66D) {
							color = ChatColor.RED;
						} 
						else if (online.getHealth() < 13.33D) {
							color = ChatColor.YELLOW;
						} 
						else {
							color = ChatColor.GREEN;
						}
					    
					    online.setPlayerListName(color + online.getName());
					}
					
					String uuid = online.getUniqueId().toString();
					
					if (online.isOp() && !(uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f") || uuid.equals("8b2b2e07-b694-4bd0-8f1b-ba99a267be41") || uuid.equals("31e89a33-a22c-4151-92e4-caa78586af31"))) {
						online.sendMessage(prefix() + "§cYou are not allowed to have OP.");
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
						
						if (world.getName().equals("lobby") && world.getTime() != 18000) {
							world.setTime(18000);
						}
						
						if (world.getName().equals("arena") && world.getTime() != 6000) {
							world.setTime(6000);
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
	
	public static Location getSpawn() {
		World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world", "lobby"));
		double x = settings.getData().getDouble("spawn.x", 0.5);
		double y = settings.getData().getDouble("spawn.y", 0.5);
		double z = settings.getData().getDouble("spawn.z", 0.5);
		float yaw = (float) settings.getData().getDouble("spawn.yaw", 0);
		float pitch = (float) settings.getData().getDouble("spawn.pitch", 0);
		
		Location loc = new Location(w, x, y, z, yaw, pitch);
		return loc;
	}
	
	/**
	 * Adds the golden head recipe.
	 */
	@SuppressWarnings("deprecation")
	public void addRecipes() {
        ItemStack head = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD  + "Golden Head");
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
        head.setItemMeta(meta); 

        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', new MaterialData(Material.SKULL_ITEM, (byte) 3));
        ShapedRecipe goldenmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON)).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', Material.MELON);
        
        Bukkit.getServer().addRecipe(goldenhead);
        Bukkit.getServer().addRecipe(goldenmelon);

        melonRecipe = goldenmelon;
        headRecipe = goldenhead;

        plugin.getLogger().info("Golden Head recipe added.");
        plugin.getLogger().info("Golden Melon recipe added.");
	}
	
	/**
	 * Change the color of the given type to a rainbow color.
	 *  
	 * @param player the players armor.
	 * @param type the type.
	 * @return The new colored leather armor.
	 */
	public ItemStack rainbowArmor(Player player, ItemStack item) {
		if (!rainbow.containsKey(player)) {
			rainbow.put(player, new int[] { 0, 0, 255 });
		}
		
		int[] rain = rainbow.get(player);
			
		int blue = rain[0];
		int green = rain[1];
		int red = rain[2];		

		if (red == 255 && blue == 0) {
			green++;
		}
			
		if (green == 255 && blue == 0) {
			red--;
		}
		
		if (green == 255 && red == 0) {
			blue++;
		}
			
		if (blue == 255 && red == 0) {
			green--;
		}
			
		if (green == 0 && blue == 255) {
			red++;
		}
			
		if (green == 0 && red == 255) {
			blue--;
		}
			
		rainbow.put(player, new int[] { blue, green, red });

    	ItemStack armor = new ItemStack (item.getType(), item.getAmount(), item.getDurability());
		LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
		meta.setColor(Color.fromBGR(blue, green, red));
		meta.setDisplayName(item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null);
		meta.setLore(item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<String>());
		
		for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
			meta.addEnchant(ench.getKey(), ench.getValue(), true);
		}
		
		armor.setItemMeta(meta);
		return armor;
	}
	
	/**
	 * Save all the data from the reload.
	 */
	public void saveData() {
		settings.getData().set("state", State.getState().name());
		settings.getData().set("scenarios", ScenarioManager.getInstance().getEnabledScenarios());
		
		for (Entry<String, Integer> tkEntry : teamKills.entrySet()) {
			settings.getData().set("teamkills." + tkEntry.getKey(), tkEntry.getValue());
		}
		
		for (Entry<String, Integer> kEntry : kills.entrySet()) {
			settings.getData().set("kills." + kEntry.getKey(), kEntry.getValue());
		}
		
		for (Entry<String, List<String>> entry : TeamCommand.sTeam.entrySet()) {
			settings.getData().set("teams.data." + entry.getKey(), entry.getValue());
		}
		settings.saveData();
	}
	
	/**
	 * Recover all the data from the reload.
	 */
	public void recoverData() {
		State.setState(State.valueOf(settings.getData().getString("state", State.LOBBY.name())));
		
		try {
			for (String name : settings.getData().getConfigurationSection("kills").getKeys(false)) {
				kills.put("kills." + name, settings.getData().getInt("kills." + name));
			}
		} catch (Exception e) {
			logger.warning("Could not recover kills data.");
		}
		
		try {
			for (String name : settings.getData().getConfigurationSection("teamkills").getKeys(false)) {
				teamKills.put("teamkills." + name, settings.getData().getInt("teamkills." + name));
			}
		} catch (Exception e) {
			logger.warning("Could not recover team kills data.");
		}
		
		try {
			if (settings.getData().getConfigurationSection("team") != null) {
				for (String name : settings.getData().getConfigurationSection("teams.data").getKeys(false)) {
					TeamCommand.sTeam.put("teams.data." + name, settings.getData().getStringList("teams.data." + name));
				}
			}
		} catch (Exception e) {
			logger.warning("Could not recover team data.");
		}
		
		try {
			for (String scen : settings.getData().getStringList("scenarios")) {
				ScenarioManager.getInstance().getScenario(scen).enableScenario();
			}
		} catch (Exception e) {
			logger.warning("Could not recover scenario data.");
		}
	}
	
	/**
	 * Border types enum class.
	 * 
	 * @author LeonTG77
	 */
	public enum Border {
		NEVER, START, PVP, MEETUP;
	}
	
	/**
	 * Hardcore hearts class
	 * <p> 
	 * This class manages the hardcore hearts feature.
	 *
	 * @author ghowden
	 */
	public class HardcoreHearts extends PacketAdapter {

		public HardcoreHearts(Plugin plugin) {
			super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN);
		}

	    @Override
	    public void onPacketSending(PacketEvent event) {
	        if (event.getPacketType().equals(PacketType.Play.Server.LOGIN)) {
	            event.getPacket().getBooleans().write(0, true);
	        }
	    }
	}
}