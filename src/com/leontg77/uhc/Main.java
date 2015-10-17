package com.leontg77.uhc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.cmds.ArenaCommand;
import com.leontg77.uhc.cmds.BanCommand;
import com.leontg77.uhc.cmds.BanIPCommand;
import com.leontg77.uhc.cmds.BoardCommand;
import com.leontg77.uhc.cmds.BorderCommand;
import com.leontg77.uhc.cmds.BroadcastCommand;
import com.leontg77.uhc.cmds.ButcherCommand;
import com.leontg77.uhc.cmds.ChatCommand;
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
import com.leontg77.uhc.cmds.InfoCommand;
import com.leontg77.uhc.cmds.PmCommand;
import com.leontg77.uhc.cmds.PvPCommand;
import com.leontg77.uhc.cmds.RandomCommand;
import com.leontg77.uhc.cmds.RankCommand;
import com.leontg77.uhc.cmds.ReplyCommand;
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
import com.leontg77.uhc.cmds.UHCCommand;
import com.leontg77.uhc.cmds.UnbanCommand;
import com.leontg77.uhc.cmds.UnbanIPCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.cmds.WhitelistCommand;
import com.leontg77.uhc.listeners.BlockListener;
import com.leontg77.uhc.listeners.EntityListener;
import com.leontg77.uhc.listeners.LoginListener;
import com.leontg77.uhc.listeners.PlayerListener;
import com.leontg77.uhc.listeners.PortalListener;
import com.leontg77.uhc.listeners.WorldListener;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PermsUtils;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.worlds.terrain.AntiStripmine;
import com.leontg77.uhc.worlds.terrain.BiomeSwap;

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

	public static final String PREFIX = "§4§lUHC §8» §7";
	public static final String NO_PERM_MSG = PREFIX + "§cYou can't use that command.";
	
	public static HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	public static HashMap<CommandSender, CommandSender> msg = new HashMap<CommandSender, CommandSender>();
	
	public static HashMap<Inventory, BukkitRunnable> invsee = new HashMap<Inventory, BukkitRunnable>();
	public static HashMap<Inventory, BukkitRunnable> gameInfo = new HashMap<Inventory, BukkitRunnable>();
	
	public static HashMap<String, Integer> teamKills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> kills = new HashMap<String, Integer>();
	
	public static HashMap<Player, int[]> rainbow = new HashMap<Player, int[]>();
	
	public static Recipe headRecipe;
	public static Recipe melonRecipe;
	
	private static Settings settings = Settings.getInstance();
	
	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " is now disabled.");
		
		BiomeSwap.getInstance().resetBiomes();
		saveData();
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		
		plugin = this;
		settings.setup();

		AntiStripmine.getInstance().setup();
		Announcer.getInstance().setup();
		Arena.getInstance().setup();
		
		BiomeSwap.getInstance().setup();
		Parkour.getInstance().setup();
		
		Teams.getInstance().setup();
		UBL.getInstance().reload();
		
		ScenarioManager.getInstance().setup();
		Scoreboards.getInstance().setup();
	    
		recoverData();
		addRecipes();

		ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
		PluginManager manager = Bukkit.getServer().getPluginManager();
		
	    protocol.addPacketListener(new HardcoreHearts(this));
		
		manager.registerEvents(new BlockListener(), this);
		manager.registerEvents(new EntityListener(), this);
		manager.registerEvents(new LoginListener(), this);
		manager.registerEvents(new PlayerListener(), this);
		manager.registerEvents(new PortalListener(), this);
		manager.registerEvents(new WorldListener(), this);

		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("ban").setExecutor(new BanCommand());
		getCommand("banip").setExecutor(new BanIPCommand());
		getCommand("board").setExecutor(new BoardCommand());
		getCommand("border").setExecutor(new BorderCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("butcher").setExecutor(new ButcherCommand());
		getCommand("clearchat").setExecutor(new ChatCommand());
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
		getCommand("perma").setExecutor(new InfoCommand());
		getCommand("pm").setExecutor(new PmCommand());
		getCommand("pvp").setExecutor(new PvPCommand());
		getCommand("random").setExecutor(new RandomCommand());
		getCommand("rank").setExecutor(new RankCommand());
		getCommand("reply").setExecutor(new ReplyCommand());
		getCommand("rules").setExecutor(new UHCCommand());
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
		getCommand("unbanip").setExecutor(new UnbanIPCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("whitelist").setExecutor(new WhitelistCommand());
		
		if (State.isState(State.LOBBY)) {
			File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
			File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
			
			Bukkit.getServer().setIdleTimeout(60);
		
			int totalDatafiles = 0;
			int totalStatsfiles = 0;
			
			for (File dataFiles : playerData.listFiles()) {
				try {
					dataFiles.delete();
					totalDatafiles++;
				} catch (Exception e) {
					logger.warning("Could not delete " + dataFiles.getName() + ".");
				}
			}
			
			for (File statsFiles : stats.listFiles()) {
				try {
					statsFiles.delete();
					totalStatsfiles++;
				} catch (Exception e) {
					logger.warning("Could not delete " + statsFiles.getName() + ".");
				}
			}

			plugin.getLogger().info("Deleted " + totalDatafiles + " player data files.");
			plugin.getLogger().info("Deleted " + totalStatsfiles + " player stats files.");
		}
		
		if (State.isState(State.INGAME)) {
			manager.registerEvents(new SpecInfo(), this);
		}
		
		for (Player online : PlayerUtils.getPlayers()) {	
			PermsUtils.addPermissions(online);
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PlayerInventory inv = online.getInventory();
					
					ItemStack hemlet = inv.getHelmet();
					ItemStack chestplate = inv.getChestplate();
					ItemStack leggings = inv.getLeggings();
					ItemStack boots = inv.getBoots();
					
					if (hemlet != null && hemlet.getType() == Material.LEATHER_HELMET) {
						inv.setHelmet(rainbowArmor(online, hemlet));
					}
					
					if (chestplate != null && chestplate.getType() == Material.LEATHER_HELMET) {
						inv.setChestplate(rainbowArmor(online, chestplate));
					}
					
					if (leggings != null && leggings.getType() == Material.LEATHER_HELMET) {
						inv.setLeggings(rainbowArmor(online, leggings));
					}
					
					if (boots != null && boots.getType() == Material.LEATHER_HELMET) {
						inv.setBoots(rainbowArmor(online, boots));
					}
					
					Game game = Game.getInstance();
					
					if (game.tabShowsHealthColor()) {
						String percentColor = NumberUtils.makePercent(online.getHealth()).substring(0, 2);
					    
					    online.setPlayerListName(percentColor + online.getName());
					}
					
					String uuid = online.getUniqueId().toString();
					
					if (online.isOp() && !(uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f") || uuid.equals("8b2b2e07-b694-4bd0-8f1b-ba99a267be41") || uuid.equals("31e89a33-a22c-4151-92e4-caa78586af31"))) {
						online.sendMessage(PREFIX + "§cYou are not allowed to have OP.");
						online.setOp(false);
					}

					Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
					int percent = Integer.parseInt(NumberUtils.makePercent(online.getHealth()).substring(2));
					
					Objective tabList = sb.getObjective("tabHealth");
					Objective bellowName = sb.getObjective("nameHealth");
					
					if (tabList != null) {
						Score score = tabList.getScore(online.getName());
						score.setScore(percent);
					}
					
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
	 * Gets the servers tps.
	 * 
	 * @return The servers tps.
	 */
	public static double getTps() {
		return MinecraftServer.getServer().recentTps[0];
	}
	
	/**
	 * Get the spawnpoint of the lobby.
	 * 
	 * @return The lobby spawnpoint.
	 */
	public static Location getSpawn() {
		World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world", "lobby"));
		double x = settings.getData().getDouble("spawn.x", 0.5);
		double y = settings.getData().getDouble("spawn.y", 33.0);
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

        MaterialData data = new MaterialData(Material.SKULL_ITEM, (byte) 3);
        
        ShapedRecipe goldenmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON)).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', Material.MELON);
        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', data);

        getServer().addRecipe(goldenmelon);
        getServer().addRecipe(goldenhead);

        melonRecipe = goldenmelon;
        headRecipe = goldenhead;

        getLogger().info("Golden Melon recipe added.");
        getLogger().info("Golden Head recipe added.");
	}
	
	/**
	 * Save all the data to the data file.
	 */
	public void saveData() {
		settings.getData().set("state", State.getState().name());
		
		List<String> list = new ArrayList<String>();
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			list.add(scen.getName());
		}
	
		settings.getData().set("scenarios", list);
		
		for (Entry<String, Integer> tkEntry : teamKills.entrySet()) {
			settings.getData().set("teamkills." + tkEntry.getKey(), tkEntry.getValue());
		}
		
		for (Entry<String, Integer> kEntry : kills.entrySet()) {
			settings.getData().set("kills." + kEntry.getKey(), kEntry.getValue());
		}
		
		for (Entry<String, List<String>> entry : TeamCommand.savedTeams.entrySet()) {
			settings.getData().set("teams.data." + entry.getKey(), entry.getValue());
		}
		settings.saveData();
	}
	
	/**
	 * Recover all the data from the data files.
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
					TeamCommand.savedTeams.put("teams.data." + name, settings.getData().getStringList("teams.data." + name));
				}
			}
		} catch (Exception e) {
			logger.warning("Could not recover team data.");
		}
		
		try {
			for (String scen : settings.getData().getStringList("scenarios")) {
				ScenarioManager.getInstance().getScenario(scen).enable();
			}
		} catch (Exception e) {
			logger.warning("Could not recover scenario data.");
		}
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
	 * Border types enum class.
	 * 
	 * @author LeonTG77
	 */
	public enum BorderShrink {
		NEVER(""), START("from "), PVP("at "), MEETUP("at ");
		
		private String preText;
		
		/**
		 * Constructor for BorderShrink.
		 * 
		 * @param preText The text that fits before the shink name.
		 */
		private BorderShrink(String preText) {
			this.preText = preText;
		}
		
		/**
		 * Get the border pre text.
		 * 
		 * @return Pre text.
		 */
		public String getPreText() {
			return preText;
		}
	}
	
	/**
	 * Hardcore hearts class
	 * <p> 
	 * This class manages the hardcore hearts feature.
	 *
	 * @author ghowden
	 */
	public class HardcoreHearts extends PacketAdapter {

		/**
		 * Constructor for HardcoreHearts.
		 * 
		 * @param plugin The main class of the plugin.
		 */
		public HardcoreHearts(Plugin plugin) {
			super(plugin, ListenerPriority.NORMAL, Play.Server.LOGIN);
		}

	    @Override
	    public void onPacketSending(PacketEvent event) {
	        if (event.getPacketType().equals(Play.Server.LOGIN)) {
	            event.getPacket().getBooleans().write(0, true);
	        }
	    }
	}
}