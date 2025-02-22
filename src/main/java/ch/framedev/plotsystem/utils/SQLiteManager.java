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
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.spigotsqliteutils.SQLite;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLiteManager implements IDatabase {

    public SQLiteManager() {
        // Ensure configuration exists
        ConfigurationSection dbConfig = Main.getInstance().getConfig().getConfigurationSection("database.sqlite");
        if (dbConfig == null) {
            throw new IllegalStateException("SQLite configuration section is missing in config.yml!");
        }

        // Fetch values safely
        String path = dbConfig.getString("path", "path"); // Default to 'database.db' in the plugin folder
        String database = dbConfig.getString("database", "database.db");

        // Ensure directory exists
        File dbFile = new File(path);
        if (!dbFile.getParentFile().exists()) {
            if (!dbFile.getParentFile().mkdirs()) {
                throw new IllegalStateException("Failed to create directory for SQLite database!");
            }
        }

        // Initialize SQLite connection
        //noinspection InstantiationOfUtilityClass
        new SQLite(dbFile.getAbsolutePath(), database);
    }

    @Override
    public void createTable() {
        if (!SQLite.isTableExists(DatabaseManager.getTableName())) return;
        String[] columns = {
                "plotId TEXT(255)",
                "plot JSON"
        };
        SQLite.createTable(DatabaseManager.getTableName(), true, columns);
    }

    @Override
    public void insertPlot(Plot plot) {
        String json = new Gson().toJson(plot.serialize());
        String[] columns = {
                "plotId",
                "plot"
        };
        Object[] values = {
                plot.getId(),
                json
        };
        SQLite.insertData(DatabaseManager.getTableName(), values, columns);
    }

    @Override
    public boolean existsPlot(int plotId) {
        return SQLite.exists(DatabaseManager.getTableName(), "plotId", String.valueOf(plotId));
    }

    @Override
    public void updatePlot(Plot plot) {
        if(!existsPlot(plot.getId())) return;
        String json = new Gson().toJson(plot.serialize());
        String[] columns = {
                "plotId",
                "plot"
        };
        Object[] values = {
                plot.getId(),
                json
        };
        SQLite.updateData(DatabaseManager.getTableName(), columns, values, "plotId ='" + plot.getId() + "'");
    }

    @Override
    public Plot getPlotById(int id) {
        if (!existsPlot(id)) return null;
        String[] columns = {
                "plotId",
                "plot"
        };
        List<Object> values = SQLite.get(DatabaseManager.getTableName(), columns, "plotId", id);
        if (values.isEmpty()) return null;
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = new Gson().fromJson(String.valueOf(values.get(1)), type);
        return new Plot(map);
    }

    @Override
    public void deletePlot(Plot plot) {
        if(!existsPlot(plot.getId())) return;
        SQLite.deleteDataInTable(DatabaseManager.getTableName(), "plotId='" + plot.getId() + "'");
    }

    @SuppressWarnings("resource")
    @Override
    public List<Plot> getPlots() {
        List<Plot> plots = new ArrayList<>();
        try (Statement statement = SQLite.connect().createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getTableName() + ";")) {
                while (resultSet.next()) {
                    String json = resultSet.getString("plot");
                    Type type = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> map = new Gson().fromJson(json, type);
                    Plot plot = new Plot(map);
                    plots.add(plot);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plots;
    }
}
