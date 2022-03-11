package ch.framedev.plotsystem.main;

import ch.framedev.plotsystem.commands.CreateCMD;
import ch.framedev.plotsystem.commands.PlotSystemCMDs;
import ch.framedev.plotsystem.commands.PlotsCMD;
import ch.framedev.plotsystem.listeners.PlayerListeners;
import ch.framedev.plotsystem.listeners.PlotListeners;
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.plotsystem.plots.PlotManager;
import ch.framedev.plotsystem.utils.Cuboid;
import ch.framedev.plotsystem.utils.DatabaseManager;
import ch.framedev.plotsystem.utils.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Main extends JavaPlugin {

    private static Main instance;
    private List<Plot> plots;
    private List<String> defaultFlags;
    private VaultManager vaultManager;
    private boolean mysql;
    private boolean sql;
    private DatabaseManager databaseManager;
    private boolean limitedClaim;
    private long limitedAmount;
    private HashMap<Player, Long> limitedHashMap;
    private long updateTime;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        // Singleton Init
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.limitedClaim = getConfig().getBoolean("MaxBlockClaim.Limited");
        this.limitedAmount = getConfig().getLong("MaxBlockClaim.Amount");
        this.limitedHashMap = new HashMap<>();

        this.updateTime = getConfig().getLong("UpdateTime");

        // Commands
        new CreateCMD(this);
        new PlotsCMD(this);
        new PlotSystemCMDs(this);

        // Listeners
        new PlotListeners(this);
        new PlayerListeners(this);


        // PlotManager
        new PlotManager();

        // Serialization Enable
        ConfigurationSerialization.registerClass(Plot.class);
        ConfigurationSerialization.registerClass(Cuboid.class);

        this.plots = new ArrayList<>();

        // Default Flags
        this.defaultFlags = new ArrayList<>();
        if (getConfig().contains("DefaultFlags"))
            this.defaultFlags = getConfig().getStringList("DefaultFlags");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getServer().getPluginManager().getPlugin("Vault") != null) {
                    vaultManager = new VaultManager();
                }
                if (getServer().getPluginManager().getPlugin("MySQLAPI") != null) {
                    databaseManager = new DatabaseManager(instance);
                    sql = databaseManager.isSql();
                    mysql = databaseManager.isMysql();
                }
            }
        }.runTaskLater(this, 4 * 20);

        // API
        new PlotSystemAPI();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6API Enabled!");

        // W.I.P
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cis work in progress!");

        // Permissions
        writePermissionsFile();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aYou find the Permissions in §6'plugins/PlotSystem/permissions.txt' §4§l!");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cPlotSystem Disabled!! Bye...");
    }

    public List<String> getDefaultFlags() {
        return defaultFlags;
    }

    public List<Plot> getPlots() {
        return plots;
    }

    public void setPlots(List<Plot> plots) {
        this.plots = plots;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public static Main getInstance() {
        return instance;
    }

    @SuppressWarnings("unused")
    public List<String> listDir(String dir) {
        List<String> ret = new ArrayList<>();
        File[] contents = new File(dir).listFiles();
        if (contents != null) {
            for (File f : contents) {
                ret.add(f.getAbsolutePath());
            }
        }
        return ret;
    }

    public String getPrefix() {
        String prefix = getConfig().getString("Prefix");
        if (prefix == null) return "§6[§bPlot§aSystem§6] §c» §7";
        if (prefix.contains("&"))
            prefix = prefix.replace('&', '§');
        if (prefix.contains(">>"))
            prefix = prefix.replace(">>", "»");
        return prefix;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public boolean isSql() {
        return sql;
    }

    public boolean isMysql() {
        return mysql;
    }

    public boolean isLimitedClaim() {
        return limitedClaim;
    }

    public long getLimitedAmount() {
        return limitedAmount;
    }

    public HashMap<Player, Long> getLimitedHashMap() {
        return limitedHashMap;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    // Write Permissions File permissions.txt
    public void writePermissionsFile() {
        File file = new File(getDataFolder(), "permissions.txt");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Permission permissions : getDescription().getPermissions()) {
                bufferedWriter.append(permissions.getName()).append("\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
