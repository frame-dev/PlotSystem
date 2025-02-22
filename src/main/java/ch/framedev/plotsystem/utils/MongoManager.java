package ch.framedev.plotsystem.utils;



/*
 * ch.framedev.plotsystem.utils
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 22.02.2025 18:19
 */

import ch.framedev.javamongodbutils.BackendMongoDBManager;
import ch.framedev.javamongodbutils.MongoDBManager;
import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Plot;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoManager implements IDatabase {

    private final BackendMongoDBManager bMongoDBManager;

    public MongoManager() {
        ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection("database.mongodb");
        if(section == null) {
            throw new IllegalStateException("MongoDB configuration section is missing in config.yml!");
        }

        // Fetch values safely
        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 27017);
        String username = section.getString("username", "username");
        String password = section.getString("password", "password");
        String database = section.getString("database", "plotsystem");
        MongoDBManager mongoDBManager = new MongoDBManager(host, username, password, port, database);
        mongoDBManager.connect();
        bMongoDBManager = new BackendMongoDBManager(mongoDBManager);
    }

    @Override
    public void createTable() {
        // not supported
    }

    @Override
    public void insertPlot(Plot plot) {
        Map<String, Object> map = new HashMap<>();
        map.put("plotId", plot.getId());
        map.put("plot", plot.serialize());
        bMongoDBManager.createData("plotId", plot.getId(), map, DatabaseManager.getTableName());
    }

    @Override
    public void updatePlot(Plot plot) {
        if(!existsPlot(plot.getId())) return;
        Map<String, Object> map = new HashMap<>();
        map.put("plotId", plot.getId());
        map.put("plot", plot.serialize());
        bMongoDBManager.updateAll("plotId", plot.getId(), map, DatabaseManager.getTableName());
    }

    @Override
    public boolean existsPlot(int plotId) {
        return bMongoDBManager.exists("plotId", plotId, DatabaseManager.getTableName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Plot getPlotById(int id) {
        return Plot.deserialize((Map<String, Object>) bMongoDBManager.getObject("plotId", id, "plot",DatabaseManager.getTableName()));
    }

    @Override
    public void deletePlot(Plot plot) {
        bMongoDBManager.removeDocument("plotId", plot.getId(), DatabaseManager.getTableName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Plot> getPlots() {
        List<Document> documents = bMongoDBManager.getAllDocuments(DatabaseManager.getTableName());
        return documents.stream().map(document -> Plot.deserialize((Map<String, Object>) document.get("plot"))).collect(Collectors.toList());
    }
}
