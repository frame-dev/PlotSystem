package ch.framedev.plotsystem.utils;

import ch.framedev.plotsystem.main.Main;
import de.framedev.mysqlapi.api.MySQLAPI;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getServer;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.utils
 * / ClassName DatabaseManager
 * / Date: 21.01.22
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class DatabaseManager {

    private boolean sql;
    private boolean mysql;

    public DatabaseManager(Main plugin) {
        if(getServer().getPluginManager().getPlugin("MySQLAPI") != null) {
            getConsoleSender().sendMessage(plugin.getPrefix() + "§6MySQLAPI Enabled!");
            this.sql = MySQLAPI.getInstance().isSQL();
            this.mysql = MySQLAPI.getInstance().isMySQL();
        }
    }

    public boolean isMysql() {
        return mysql;
    }

    public boolean isSql() {
        return sql;
    }
}
