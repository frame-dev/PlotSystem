package ch.framedev.plotsystem.utils;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Plot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class JdbcPlotDatabase implements IDatabase {

    private final Connection connection;
    private final String backendName;
    private final String createTableSql;
    private final Gson gson = new Gson();
    private final Type plotMapType = new TypeToken<Map<String, Object>>(){}.getType();

    protected JdbcPlotDatabase(Connection connection, String backendName, String createTableSql) {
        this.connection = connection;
        this.backendName = backendName;
        this.createTableSql = createTableSql;
    }

    @Override
    public void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSql);
        } catch (SQLException e) {
            log("Error creating " + backendName + " plot table", e);
        }
    }

    @Override
    public void insertPlot(Plot plot) {
        String sql = "INSERT INTO " + DatabaseManager.getTableName() + " (plotId, plot) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, plot.getId());
            statement.setString(2, gson.toJson(plot.serialize()));
            statement.executeUpdate();
        } catch (SQLException e) {
            log("Error inserting plot into " + backendName, e);
        }
    }

    @Override
    public boolean existsPlot(int plotId) {
        String sql = "SELECT 1 FROM " + DatabaseManager.getTableName() + " WHERE plotId = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, plotId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            log("Error checking " + backendName + " plot existence", e);
            return false;
        }
    }

    @Override
    public void updatePlot(Plot plot) {
        if (!existsPlot(plot.getId())) return;
        String sql = "UPDATE " + DatabaseManager.getTableName() + " SET plot = ? WHERE plotId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gson.toJson(plot.serialize()));
            statement.setInt(2, plot.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log("Error updating " + backendName + " plot", e);
        }
    }

    @Override
    public Plot getPlotById(int id) {
        String sql = "SELECT plot FROM " + DatabaseManager.getTableName() + " WHERE plotId = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) return null;
                return parsePlot(resultSet.getString("plot"));
            }
        } catch (SQLException e) {
            log("Error reading " + backendName + " plot", e);
            return null;
        }
    }

    @Override
    public void deletePlot(Plot plot) {
        if (!existsPlot(plot.getId())) return;
        String sql = "DELETE FROM " + DatabaseManager.getTableName() + " WHERE plotId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, plot.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log("Error deleting " + backendName + " plot", e);
        }
    }

    @Override
    public List<Plot> getPlots() {
        List<Plot> plots = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT plot FROM " + DatabaseManager.getTableName() + ";")) {
            while (resultSet.next()) {
                plots.add(parsePlot(resultSet.getString("plot")));
            }
        } catch (SQLException e) {
            log("Error reading " + backendName + " plots", e);
        }
        return plots;
    }

    private Plot parsePlot(String json) {
        Map<String, Object> map = gson.fromJson(json, plotMapType);
        return new Plot(map);
    }

    private void log(String message, SQLException e) {
        Main.getInstance().getLogger().log(Level.SEVERE, message, e);
    }
}
