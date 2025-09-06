package ru.minced.client.core.file.impl;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import ru.minced.client.core.file.ClientFile;
import ru.minced.client.core.file.Directories;
import ru.minced.client.core.file.expection.FileLoadException;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.core.manager.gps.GPS;
import ru.minced.client.core.manager.gps.GPSManager;
import ru.minced.client.util.network.ConnectionUtil;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GPSFile extends ClientFile {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    Map<String, List<GPS>> serverGpsPoints = new HashMap<>();

    public GPSFile() {
        super("GPS");
    }

    @Override
    public void saveToFile(File path) throws FileSaveException {
        try {
            String currentServer = ConnectionUtil.getServerDomain();
            List<GPS> currentPoints = GPSManager.getPoints();
            serverGpsPoints.put(currentServer, new ArrayList<>(currentPoints));
        } catch (Exception ignored) {
        }

        for (Map.Entry<String, List<GPS>> entry : serverGpsPoints.entrySet()) {
            String server = entry.getKey();
            List<GPS> points = entry.getValue();

            saveServerPoints(server, points);
        }
    }

    @Override
    public void loadFromFile(File path) throws FileLoadException {
        File[] files = Directories.gpsDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            String fileName = file.getName();
            String server = fileName.substring(0, fileName.length() - 5);
            
            try {
                List<GPS> points = loadServerPoints(server);
                if (!points.isEmpty()) {
                    serverGpsPoints.put(server, points);
                }
            } catch (FileLoadException ignored) {
            }
        }
        
        try {
            loadCurrentServerPoints();
        } catch (FileLoadException ignored) {
        }
    }

    public void saveCurrentServerPoints() throws FileSaveException {
        String server = ConnectionUtil.getServerDomain();
        List<GPS> points = GPSManager.getPoints();
        
        serverGpsPoints.put(server, new ArrayList<>(points));
        
        saveServerPoints(server, points);
    }

    public void loadCurrentServerPoints() throws FileLoadException {
        String server = ConnectionUtil.getServerDomain();
        List<GPS> points = loadServerPoints(server);
        
        serverGpsPoints.put(server, points);
        
        GPSManager.clearAllPoints();
        for (GPS point : points) {
            GPSManager.addPointWithoutSaving(point);
        }
    }

    private void saveServerPoints(String server, List<GPS> points) throws FileSaveException {
        JsonArray pointsArray = new JsonArray();
        
        for (GPS point : points) {
            JsonObject pointObject = getJsonObject(point);

            pointsArray.add(pointObject);
        }

        if (!Directories.gpsDirectory.exists()) {
            Directories.gpsDirectory.mkdirs();
        }
        
        File file = new File(Directories.gpsDirectory, server + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(pointsArray, writer);
        } catch (IOException e) {
            throw new FileSaveException("Failed to save GPS points to file: " + server, e);
        }
    }

    private static @NotNull JsonObject getJsonObject(GPS point) {
        JsonObject pointObject = new JsonObject();
        pointObject.addProperty("name", point.getName());
        pointObject.addProperty("x", point.getX());
        pointObject.addProperty("y", point.getY());
        pointObject.addProperty("z", point.getZ());

        JsonObject colorObject = new JsonObject();
        colorObject.addProperty("r", point.getColor().getRed());
        colorObject.addProperty("g", point.getColor().getGreen());
        colorObject.addProperty("b", point.getColor().getBlue());
        pointObject.add("color", colorObject);

        pointObject.addProperty("server", point.getServer());
        return pointObject;
    }

    private List<GPS> loadServerPoints(String server) throws FileLoadException {
        List<GPS> points = new ArrayList<>();
        File file = new File(Directories.gpsDirectory, server + ".json");
        
        if (!file.exists()) {
            return points;
        }
        
        try (FileReader reader = new FileReader(file)) {
            JsonArray pointsArray = JsonParser.parseReader(reader).getAsJsonArray();
            
            for (JsonElement element : pointsArray) {
                GPS point = getGps(server, element);
                points.add(point);
            }
            
            return points;
        } catch (IOException e) {
            throw new FileLoadException("Failed to load GPS points from file: " + server, e);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new FileLoadException("Failed to parse JSON from file: " + server, e);
        } catch (IllegalStateException e) {
            throw new FileLoadException("Invalid JSON format in file: " + server, e);
        }
    }

    private static @NotNull GPS getGps(String server, JsonElement element) {
        JsonObject pointObject = element.getAsJsonObject();

        String name = pointObject.get("name").getAsString();
        double x = pointObject.get("x").getAsDouble();
        double y = pointObject.get("y").getAsDouble();
        double z = pointObject.get("z").getAsDouble();

        JsonObject colorObject = pointObject.getAsJsonObject("color");
        int r = colorObject.get("r").getAsInt();
        int g = colorObject.get("g").getAsInt();
        int b = colorObject.get("b").getAsInt();
        Color color = new Color(r, g, b);

        String pointServer = pointObject.has("server") ?
            pointObject.get("server").getAsString() : server;

        return new GPS(name, x, y, z, color, pointServer);
    }
}