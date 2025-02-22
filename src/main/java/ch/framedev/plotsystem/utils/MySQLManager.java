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
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.spigotmysqlutils.MySQL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySQLManager implements IDatabase {

    public MySQLManager() {
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

        // Initialize MySQL connection
        @SuppressWarnings("unused")
        MySQL mySQL = new MySQL(host, user, password, port, database);
    }

    @Override
    public void createTable() {
        if (!MySQL.isTableExists(DatabaseManager.getTableName())) return;
        String[] columns = {
                "plotId TEXT(255)",
                "plot JSON"
        };
        MySQL.createTable(DatabaseManager.getTableName(), columns);
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
        MySQL.insertData(DatabaseManager.getTableName(), values, columns);
    }

    @Override
    public boolean existsPlot(int plotId) {
        return MySQL.exists(DatabaseManager.getTableName(), "plotId", String.valueOf(plotId));
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
        MySQL.updateData(DatabaseManager.getTableName(), columns, values, "plotId ='" + plot.getId() + "'");
    }

    @Override
    public Plot getPlotById(int id) {
        if (!existsPlot(id)) return null;
        String[] columns = {
                "plotId",
                "plot"
        };
        List<Object> values = MySQL.get(DatabaseManager.getTableName(), columns, "plotId", id);
        if (values.isEmpty()) return null;
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = new Gson().fromJson(String.valueOf(values.get(1)), type);
        return new Plot(map);
    }

    @Override
    public void deletePlot(Plot plot) {
        if(!existsPlot(plot.getId())) return;
        MySQL.deleteDataInTable(DatabaseManager.getTableName(), "plotId='" + plot.getId() + "'");
    }

    @Override
    public List<Plot> getPlots() {
        List<Plot> plots = new ArrayList<>();
        try(Statement statement = MySQL.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.getTableName() + ";")) {
            while (resultSet.next()) {
                String json = resultSet.getString("plot");
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> map = new Gson().fromJson(json, type);
                Plot plot = new Plot(map);
                plots.add(plot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plots;
    }
}
