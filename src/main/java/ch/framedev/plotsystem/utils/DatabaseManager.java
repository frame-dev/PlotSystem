package ch.framedev.plotsystem.utils;

import ch.framedev.plotsystem.main.Main;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class DatabaseManager {

    private static String tableName;
    private boolean sql;
    private boolean mysql;
    private boolean mongoDb;
    private IDatabase iDatabase;

    public DatabaseManager(Main plugin) {
        if(!plugin.getConfig().getBoolean("database.use"))
            return;

        String configTableName = plugin.getConfig().getString("database.tableName", "plotsystem_data");
        if (configTableName.trim().isEmpty()) {
            plugin.getLogger().warning("⚠ No table name found in config.yml. Using default: 'plotsystem_data'");
            configTableName = "plotsystem_data";
        }
        if (!configTableName.matches("[A-Za-z0-9_]+")) {
            plugin.getLogger().warning("Invalid database table name '" + configTableName + "'. Using default: 'plotsystem_data'");
            configTableName = "plotsystem_data";
        }
        tableName = configTableName;

        mysql = isClassAvailable("com.mysql.cj.jdbc.Driver");
        sql = isClassAvailable("org.sqlite.JDBC");
        mongoDb = isClassAvailable("com.mongodb.client.MongoClients");

        String preferred = plugin.getConfig().getString("database.preferred", "mysql").toLowerCase(Locale.ROOT);
        switch (preferred) {
            case "mysql":
                iDatabase = createDatabase(plugin, "MySQL", mysql, MySQLManager::new);
                break;
            case "sqlite":
                iDatabase = createDatabase(plugin, "SQLite", sql, SQLiteManager::new);
                break;
            case "mongodb":
            case "mongo":
                iDatabase = createDatabase(plugin, "MongoDB", mongoDb, MongoManager::new);
                break;
            default:
                plugin.getLogger().warning("Invalid database preference: '" + preferred + "'. Falling back to the first available database.");
                break;
        }

        if (iDatabase == null && mysql) {
            iDatabase = createDatabase(plugin, "MySQL", true, MySQLManager::new);
        }
        if (iDatabase == null && sql) {
            iDatabase = createDatabase(plugin, "SQLite", true, SQLiteManager::new);
        }
        if (iDatabase == null && mongoDb) {
            iDatabase = createDatabase(plugin, "MongoDB", true, MongoManager::new);
        }

        if (iDatabase == null) {
            plugin.getLogger().warning("⚠ No supported database found! The plugin may not work correctly.");
        } else {
            iDatabase.createTable();
        }
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
        return iDatabase != null;
    }

    public boolean isMongoDb() {
        return mongoDb;
    }

    public boolean isMysql() {
        return mysql;
    }

    public boolean isSql() {
        return sql;
    }

    private boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private IDatabase createDatabase(Main plugin, String databaseName, boolean available, Supplier<IDatabase> supplier) {
        if (!available) {
            plugin.getLogger().warning(databaseName + " driver is not available.");
            return null;
        }
        try {
            return supplier.get();
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING, "Could not initialize " + databaseName + " backend.", ex);
            return null;
        }
    }
}
