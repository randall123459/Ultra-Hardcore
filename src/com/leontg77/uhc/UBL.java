package com.leontg77.uhc;

import static com.leontg77.uhc.Main.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Universial Ban List class.
 * <p>
 * This class contains methods for checking if a player is banned on the UBL and getting the UBL list.
 * 
 * @author XHawk87, modified by LeonTG77
 */
public class UBL implements Runnable {
    public Map<String, BanEntry> banlistByIGN;
    public Map<UUID, BanEntry> banlistByUUID;
	private static UBL manager = new UBL();
    private BukkitTask autoChecker;

	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static UBL getManager() {
		return manager;
	}

	public void setup() {
       	reload();
	}
	
	@Override
    public void run() {
        String banlistURL = "https://docs.google.com/spreadsheet/ccc?key=0AjACyg1Jc3_GdEhqWU5PTEVHZDVLYWphd2JfaEZXd2c&output=csv";
        int retries = 3;
        int maxBandwidth = 64;
        int bufferSize = (maxBandwidth * 1024) / 20;
        int timeout = 5;

        URL url;
        String data;
        BufferedReader in;
        
        try {
            url = new URL(banlistURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(timeout * 1000);
            conn.setReadTimeout(timeout * 1000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");
            
            boolean found = false;
            int tries = 0;
            StringBuilder cookies = new StringBuilder();
            
            while (!found) {
                int status = conn.getResponseCode();
                
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    String newUrl = conn.getHeaderField("Location");
                    String headerName;
                    
                    for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
                        if (headerName.equals("Set-Cookie")) {
                            String newCookie = conn.getHeaderField(i);
                            newCookie = newCookie.substring(0, newCookie.indexOf(";"));
                            String cookieName = newCookie.substring(0, newCookie.indexOf("="));
                            String cookieValue = newCookie.substring(newCookie.indexOf("=") + 1, newCookie.length());
                            if (cookies.length() != 0) {
                                cookies.append("; ");
                            }
                            cookies.append(cookieName).append("=").append(cookieValue);
                        }
                    }

                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestProperty("Cookie", cookies.toString());
                    conn.setConnectTimeout(timeout * 1000);
                    conn.setReadTimeout(timeout * 1000);
                    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                    conn.addRequestProperty("User-Agent", "Mozilla");
                    conn.addRequestProperty("Referer", "google.com");
                } 
                else if (status == HttpURLConnection.HTTP_OK) {
                    found = true;
                } 
                else {
                    tries++;
                    
                    if (tries >= retries) {
                        throw new IOException("Failed to reach " + url.getHost() + " after " + retries + " attempts");
                    }
                }
            }

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()), bufferSize);

            try {
                data = downloadBanlist(in, bufferSize, timeout * 20);
                plugin.getLogger().info("UBL has been updated.");
                
                for (Player online : PlayerUtils.getPlayers()) {
                	if (isBanned(online.getName(), online.getUniqueId())) {
                		online.kickPlayer(getBanMessage(online.getUniqueId()));
                		continue;
                	}
                	
                	if (isBanned(online.getName())) {
                		online.kickPlayer(getBanMessage(online.getName()));
                	}
                }
            } catch (IOException ex) {
                plugin.getLogger().severe("Connection was interrupted while downloading banlist from " + banlistURL);
                data = loadFromBackup();
                
                for (Player online : PlayerUtils.getPlayers()) {
                	if (isBanned(online.getName(), online.getUniqueId())) {
                		online.kickPlayer(getBanMessage(online.getUniqueId()));
                		continue;
                	}
                	
                	if (isBanned(online.getName())) {
                		online.kickPlayer(getBanMessage(online.getName()));
                	}
                }
            } catch (InterruptedException ex) {
                plugin.getLogger().log(Level.SEVERE, "Timed out while waiting for banlist server to send data", ex);
                data = loadFromBackup();
                
                for (Player online : PlayerUtils.getPlayers()) {
                	if (isBanned(online.getName(), online.getUniqueId())) {
                		online.kickPlayer(getBanMessage(online.getUniqueId()));
                		continue;
                	}
                	
                	if (isBanned(online.getName())) {
                		online.kickPlayer(getBanMessage(online.getName()));
                	}
                }
            }

            saveToBackup(data);
        } catch (MalformedURLException ex) {
            plugin.getLogger().severe("banlist-url in the config.yml is invalid or corrupt. This must be corrected and the config reloaded before the UBL can be updated");
            data = loadFromBackup();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Banlist server " + banlistURL + " is currently unreachable", ex);
            data = loadFromBackup();
        }

        parseData(data);
	}

    /**
     * Reload configuration settings and update the banlist
     */
    public void reload() {
        cancel();
        
        reloadConfigAsync(new BukkitRunnable() {
            public void run() {
            	plugin.getLogger().info("Checking UBL for updates...");
                
                int autoCheckInterval = 60;
                schedule(autoCheckInterval);
                updateBanlist();
            }
        });
    }
    
    /**
     * Load the configuration file asynchronously, and run a task when it is
     * completed
     *
     * @param notifier The task to be run
     */
    public void reloadConfigAsync(BukkitRunnable notifier) {
        new BukkitRunnable() {
            private BukkitRunnable notifier;

            public BukkitRunnable setNotifier(BukkitRunnable notifier) {
                this.notifier = notifier;
                return this;
            }

            @Override
            public void run() {
                notifier.runTask(plugin);
            }
        }.setNotifier(notifier).runTaskAsynchronously(plugin);
    }

    /**
     * Attempt to update the banlist immediately
     */
    public void updateBanlist() {
        download();
    }

    /**
     * Check if the given player is banned on the UBL and is not exempt on this
     * server
     *
     * @param ign The in-game name of the player to check
     * @return True, if the player is banned and not exempt, otherwise false
     */
    public boolean isBanned(String ign) {
        String lname = ign.toLowerCase();
        
    	if (banlistByIGN != null) {
            return banlistByIGN.containsKey(lname);
    	}
		return false;
    }

    /**
     * Check if the given player is banned on the UBL and is not exempt on this
     * server
     *
     * @param ign The in-game name of the player to check against the exemptions
     * @param uuid The universally unique identifier of the player to check
     * @return True, if the player is banned and not exempt, otherwise false
     */
    public boolean isBanned(String ign, UUID uuid) {
    	if (banlistByUUID != null) {
            return banlistByUUID.containsKey(uuid);
    	}
		return false;
    }

    /**
     * @param ign The in-game name of the banned player
     * @return A personalised ban message for this player
     */
    public String getBanMessage(String ign) {
        BanEntry banEntry = banlistByIGN.get(ign.toLowerCase());
        
        if (banEntry == null) {
            return "Not on the UBL";
        }
        
        return "§cYou have been banned from Arctic UHC \n\n§aReason: §7UBL - " + banEntry.getData("Reason");
    }

    /**
     * @param uuid The universally unique identifier of the banned player
     * @return A personalised ban message for this player
     */
    public String getBanMessage(UUID uuid) {
        BanEntry banEntry = banlistByUUID.get(uuid);
        
        if (banEntry == null) {
            return "Not on the UBL";
        }
        
        return "§cYou have been banned from Arctic UHC \n\n§aReason: §7UBL - " + banEntry.getData("Reason");
    }

    /**
     * Update the entire ban-list using raw CSV lines, overwriting any previous
     * settings
     *
     * @param banlist The new ban-list
     */
    public void setBanList(String fieldNamesCSV, List<String> banlist) {
        String[] fieldNames = NameUtils.parseLine(fieldNamesCSV);
        
        if (!Arrays.asList(fieldNames).contains(getIGNFieldName())) {
            Bukkit.getLogger().warning("There is no matching IGN field (" + getIGNFieldName() + ") in the ban-list data. Please check the UBL spreadsheet and set 'fields.ign' in your config.yml to the correct field name");
            Bukkit.getServer().broadcast("[AutoUBL] No IGN field found in the ban-list data. If you also have no UUID field then your server will be locked to non-ops for your protection. Please see your server logs for details in how to fix this issue", "bukkit.op");
        }
        if (!Arrays.asList(fieldNames).contains(getUUIDFieldName())) {
        	Bukkit.getLogger().warning("There is no matching UUID field (" + getUUIDFieldName() + ") in the ban-list data. Please check the UBL spreadsheet and set 'fields.uuid' in your config.yml to the correct field name");
            Bukkit.getServer().broadcast("[AutoUBL] No UUID field found in the ban-list data. If Mojang has not yet allowed name-changing, this is not a problem. Otherwise, please check your server logs for details on how to fix this issue", "bukkit.op");
        }
        this.banlistByIGN = new HashMap<String, BanEntry>();
        this.banlistByUUID = new HashMap<UUID, BanEntry>();
        
        for (String rawCSV : banlist) {
            BanEntry banEntry = new BanEntry(fieldNames, rawCSV);
            String ign = banEntry.getData(getIGNFieldName());
            if (ign != null) {
                this.banlistByIGN.put(ign.toLowerCase(), banEntry);
                banEntry.setIgn(ign);
            }
            String uuidString = banEntry.getData(getUUIDFieldName()).trim();
            if (uuidString != null) {
                if (uuidString.length() == 32) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(uuidString.substring(0, 8)).append('-');
                    sb.append(uuidString.substring(8, 12)).append('-');
                    sb.append(uuidString.substring(12, 16)).append('-');
                    sb.append(uuidString.substring(16, 20)).append('-');
                    sb.append(uuidString.substring(20, 32));
                    uuidString = sb.toString();
                }
                if (uuidString.length() == 36) {
                    UUID uuid = UUID.fromString(uuidString);
                    this.banlistByUUID.put(uuid, banEntry);
                    banEntry.setUUID(uuid);
                } else {
                	Bukkit.getLogger().warning("Invalid UUID in ban-list for " + ign + ": " + uuidString);
                }
            }
        }
    }

    /**
     * @return The field name to check for the player's in-game name
     */
    public String getIGNFieldName() {
    	return "IGN";
    }

    /**
     * @return The field name to check for the player's universally unique
     * identifier
     */
    public String getUUIDFieldName() {
        return "UUID";
    }
	
	/**
     * Schedule regular updates
     *
     * @param interval How often to update in minutes
     */
    public void schedule(int interval) {
        int ticks = interval * 1200;

        cancel();

        autoChecker = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, ticks, ticks);
    }

    /**
     * Schedule an immediate update
     */
    public void download() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this);
    }

    /**
     * Stop the regular updater
     */
    public void cancel() {
        if (autoChecker != null) {
            autoChecker.cancel();
        }
    }

    /**
     * Attempt to download the ban-list from the given stream within the
     * specified time limit
     *
     * @param in The input stream
     * @param bufferSize The size of the data buffer in bytes
     * @param timeout The time limit in server ticks
     * @return The raw data
     * @throws IOException The connection errored or was terminated
     * @throws InterruptedException The time limit was exceeded
     */
    private String downloadBanlist(BufferedReader in, int bufferSize, int timeout) throws IOException, InterruptedException {
        final Thread iothread = Thread.currentThread();
        BukkitTask timer = new BukkitRunnable() {
            @Override
            public void run() {
                iothread.interrupt();
            }
        }.runTaskLaterAsynchronously(plugin, timeout);

        try {
            char[] buffer = new char[bufferSize];
            StringBuilder sb = new StringBuilder();

            while (true) {
                int bytesRead = in.read(buffer);

                if (bytesRead == -1) {
                    return sb.toString();
                }

                sb.append(buffer, 0, bytesRead);

                Thread.sleep(50);
            }
        } finally {
            timer.cancel();
        }
    }

    private void parseData(final String data) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String[] lines = data.split("\\r?\\n");
                if (lines.length < 2) {
                    plugin.getLogger().warning("Banlist is empty!");
                    return;
                }
                setBanList(lines[0], Arrays.asList(Arrays.copyOfRange(lines, 1, lines.length)));
            }
        }.runTask(plugin);
    }

    /**
     * Load raw ban-list from the backup file, if it exists.
     *
     * If there are any problems, return an empty string
     *
     * @return The raw ban-list, or an empty string
     */
    public String loadFromBackup() {
        File file = new File(plugin.getDataFolder(), "ubl.backup");
        if (!file.exists()) {
            plugin.getLogger().severe("The backup file could not be located. You are running without UBL protection!");
            return "";
        }
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[8192];

            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                sb.append(buffer, 0, bytesRead);
            }

            plugin.getLogger().info("UBL loaded from local backup");
            return sb.toString();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load UBL backup. You are running without UBL protection!", ex);
            return "";
        }
    }

    /**
     * Save the raw ban-list data to the backup file
     *
     * This should not be run on the main server thread
     *
     * @param data The raw ban-list data
     */
    public void saveToBackup(String data) {
        File file = new File(plugin.getDataFolder(), "ubl.backup");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(data);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save UBL backup", ex);
        }
    }
    
    /**
     * Represents a single entry in the ban-list
     *
     * @author XHawk87
     */
    public class BanEntry {
        private Map<String, String> data = new HashMap<>();
        private String ign;
        private UUID uuid;

        /**
         * Creates a new BanEntry from a list of pre-parsed field names, used to
         * store record values by field name, and a raw CSV record
         *
         * @param fieldNames A pre-parsed array of field names
         * @param rawCSV A raw CSV record
         */
        public BanEntry(String[] fieldNames, String rawCSV) {
            String[] parts = NameUtils.parseLine(rawCSV);
            if (parts.length != fieldNames.length) {
                throw new IllegalArgumentException("Expected " + fieldNames.length + " columns: " + rawCSV);
            }
            for (int i = 0; i < fieldNames.length; i++) {
                data.put(fieldNames[i], parts[i]);
            }
        }

        /**
         * Set the value of the in-game name for this player
         *
         * @param ign The player's in-game name
         */
        public void setIgn(String ign) {
            this.ign = ign;
        }

        /**
         * Set the value of the universally unique identifier for this player
         *
         * @param uuid The player's UUID
         */
        public void setUUID(UUID uuid) {
            this.uuid = uuid;
        }

        /**
         * @return The player's in-game name
         */
        public String getIgn() {
            return ign;
        }

        /**
         * @return The player's universally unique identifier
         */
        public UUID getUUID() {
            return uuid;
        }

        /**
         * @param fieldName The field name to retrieve a value of
         * @return The value of the given field
         */
        public String getData(String fieldName) {
            return data.get(fieldName);
        }

        /**
         * Sets the value of a given field
         * 
         * @param fieldName The field name to set a value for
         * @param value The value to set for this field
         */
        public void setData(String fieldName, String value) {
            data.put(fieldName, value);
        }

        /**
         * @return A map of all data in this ban entry
         */
        public Map<String, String> getData() {
            return data;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            
            if (obj instanceof BanEntry) {
                BanEntry other = (BanEntry) obj;
                if (other.uuid != null && uuid != null) {
                    return other.uuid.equals(uuid);
                }
                return other.ign.equalsIgnoreCase(ign);
            }
            
            return false;
        }

        @Override
        public int hashCode() {
            if (uuid != null) {
                return uuid.hashCode();
            } else {
                return ign.hashCode();
            }
        }
    }
}