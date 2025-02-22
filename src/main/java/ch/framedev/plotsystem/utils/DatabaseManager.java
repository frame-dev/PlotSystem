package ch.framedev.plotsystem.utils;

import ch.framedev.plotsystem.main.Main;

public class DatabaseManager {

    private static String tableName;
    private boolean sql;
    private boolean mysql;
    private IDatabase iDatabase;

    public DatabaseManager(Main plugin) {
        if(!plugin.getConfig().getBoolean("database.use"))
            return;

        mysql = plugin.getServer().getPluginManager().getPlugin("SpigotMySQLUtils") != null;
        sql = plugin.getServer().getPluginManager().getPlugin("SpigotSQLiteUtils") != null;

        // Determine preferred database if both are available
        if (mysql && sql) {
            String preferred = plugin.getConfig().getString("database.preferred", "mysql").toLowerCase();

            switch (preferred) {
                case "mysql":
                    iDatabase = new MySQLManager();
                    break;
                case "sqlite":
                    iDatabase = new SQLiteManager();
                    break;
                default:
                    plugin.getLogger().warning("⚠ Invalid database preference: '" + preferred + "'. Falling back to MySQL.");
                    iDatabase = new MySQLManager();
                    break;
            }
        } else if (mysql) {
            iDatabase = new MySQLManager();
        } else if (sql) {
            iDatabase = new SQLiteManager();
        } else {
            iDatabase = null;
            plugin.getLogger().warning("⚠ No supported database found! The plugin may not work correctly.");
        }

        // Ensure a valid table name is present
        String configTableName = plugin.getConfig().getString("database.tableName", "plotsystem_data");
        if (configTableName.trim().isEmpty()) {
            plugin.getLogger().warning("⚠ No table name found in config.yml. Using default: 'plotsystem_data'");
            configTableName = "plotsystem_data";
        }
        tableName = configTableName;
    }

    public static String getTableName() {
        return tableName;
    }

    public IDatabase getIDatabase() {
        return iDatabase;
    }

    public boolean isDatabaseSupported() {
        if(!Main.getInstance().getConfig().getBoolean("database.use"))
            return false;
        return mysql || sql;
    }

    public boolean isMysql() {
        return mysql;
    }

    public boolean isSql() {
        return sql;
    }
}