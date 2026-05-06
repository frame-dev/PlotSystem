package ch.framedev.plotsystem.utils;



/*
 * ch.framedev.plotsystem.utils
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 22.02.2025 17:13
 */

import ch.framedev.plotsystem.main.Main;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteManager extends JdbcPlotDatabase {

    public SQLiteManager() {
        super(connect(), "SQLite", "CREATE TABLE IF NOT EXISTS " + DatabaseManager.getTableName() + " (plotId INTEGER PRIMARY KEY, plot TEXT NOT NULL)");
    }

    private static Connection connect() {
        // Ensure configuration exists
        ConfigurationSection dbConfig = Main.getInstance().getConfig().getConfigurationSection("database.sqlite");
        if (dbConfig == null) {
            throw new IllegalStateException("SQLite configuration section is missing in config.yml!");
        }

        // Fetch values safely
        String path = dbConfig.getString("path", Main.getInstance().getDataFolder().getPath());
        String database = dbConfig.getString("database", "database.db");

        // Ensure directory exists
        File dbFile = new File(path);
        if (!dbFile.exists()) {
            if (!dbFile.mkdirs()) {
                throw new IllegalStateException("Failed to create directory for SQLite database!");
            }
        }

        // Initialize SQLite connection
        File databaseFile = new File(dbFile, database);
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect to SQLite database!", e);
        }
    }
}
