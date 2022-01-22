package ch.framedev.plotsystem.main;

import ch.framedev.plotsystem.commands.CreateCMD;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
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

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.limitedClaim = getConfig().getBoolean("MaxBlockClaim.Limited");
        this.limitedAmount = getConfig().getLong("MaxBlockClaim.Amount");
        this.limitedHashMap = new HashMap<>();

        new CreateCMD(this);
        new PlotsCMD(this);
        new PlotListeners(this);
        new PlayerListeners(this);
        new PlotManager();
        ConfigurationSerialization.registerClass(Plot.class);
        ConfigurationSerialization.registerClass(Cuboid.class);
        this.plots = new ArrayList<>();
        this.defaultFlags = new ArrayList<>();
        if (getConfig().contains("DefaultFlags"))
            this.defaultFlags = getConfig().getStringList("DefaultFlags");

        new BukkitRunnable() {
            @Override
            public void run() {
                if(getServer().getPluginManager().getPlugin("Vault") != null) {
                    vaultManager = new VaultManager();
                }
                if(getServer().getPluginManager().getPlugin("MySQLAPI") != null) {
                    databaseManager = new DatabaseManager(instance);
                    sql = databaseManager.isSql();
                    mysql = databaseManager.isMysql();
                }
            }
        }.runTaskLater(this, 4*20);

        new PlotSystemAPI();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§6API Enabled!");

        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cis work in progress!");
    }

    @Override
    public void onDisable() {
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
        return "§6[§bPlot§aSystem§6] §c» §7";
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
}
