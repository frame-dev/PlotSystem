package ch.framedev.plotsystem.utils;



/*
 * ch.framedev.plotsystem.utils
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 22.02.2025 18:19
 */

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Plot;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class MongoManager implements IDatabase {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> collection;
    private final Gson gson = new Gson();

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
        String uri = buildConnectionUri(host, port, username, password, database);
        this.mongoClient = MongoClients.create(uri);
        this.collection = mongoClient.getDatabase(database).getCollection(DatabaseManager.getTableName());
    }

    @Override
    public void createTable() {
        // MongoDB creates the collection lazily when the first plot is stored.
    }

    @Override
    public void insertPlot(Plot plot) {
        collection.insertOne(toDocument(plot));
    }

    @Override
    public void updatePlot(Plot plot) {
        if(!existsPlot(plot.getId())) return;
        collection.replaceOne(eq("plotId", plot.getId()), toDocument(plot), new ReplaceOptions().upsert(true));
    }

    @Override
    public boolean existsPlot(int plotId) {
        return collection.find(eq("plotId", plotId)).first() != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Plot getPlotById(int id) {
        Document document = collection.find(eq("plotId", id)).first();
        if (document == null) return null;
        Object plot = document.get("plot");
        if (plot instanceof Map) return Plot.deserialize((Map<String, Object>) plot);
        return null;
    }

    @Override
    public void deletePlot(Plot plot) {
        collection.deleteOne(eq("plotId", plot.getId()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Plot> getPlots() {
        List<Document> documents = collection.find().into(new ArrayList<>());
        return documents.stream()
                .map(document -> document.get("plot"))
                .filter(Map.class::isInstance)
                .map(plot -> Plot.deserialize((Map<String, Object>) plot))
                .collect(Collectors.toList());
    }

    private Document toDocument(Plot plot) {
        Document document = new Document("plotId", plot.getId());
        document.append("plot", Document.parse(gson.toJson(plot.serialize())));
        return document;
    }

    private String buildConnectionUri(String host, int port, String username, String password, String database) {
        if (username == null || username.trim().isEmpty() || "username".equalsIgnoreCase(username)) {
            return "mongodb://" + host + ":" + port;
        }
        String encodedUser = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String encodedPassword = URLEncoder.encode(password == null ? "" : password, StandardCharsets.UTF_8);
        return "mongodb://" + encodedUser + ":" + encodedPassword + "@" + host + ":" + port + "/" + database + "?authSource=" + database;
    }
}
