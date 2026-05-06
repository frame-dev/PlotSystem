package ch.framedev.plotsystem.utils;



/*
 * ch.framedev.plotsystem.utils
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 22.02.2025 17:09
 */

import ch.framedev.plotsystem.main.Main;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLManager extends JdbcPlotDatabase {

    public MySQLManager() {
        super(connect(), "MySQL", "CREATE TABLE IF NOT EXISTS " + DatabaseManager.getTableName() + " (plotId INT PRIMARY KEY, plot TEXT NOT NULL)");
    }

    private static Connection connect() {
        // Ensure the configuration section exists
        ConfigurationSection dbConfig = Main.getInstance().getConfig().getConfigurationSection("database.mysql");
        if (dbConfig == null) {
            throw new IllegalStateException("MySQL configuration section is missing in config.yml!");
        }

        // Fetch values safely
        String host = dbConfig.getString("host", "localhost");
        String user = dbConfig.getString("user", "root");
        String password = dbConfig.getString("password", "");
        int port = dbConfig.getInt("port", 3306);
        String database = dbConfig.getString("database", "minecraft");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect to MySQL database!", e);
        }
    }
}
