package ru.minced.client.core.manager.gps;

import ru.minced.client.core.Minced;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.core.file.impl.GPSFile;
import ru.minced.client.util.network.ConnectionUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GPSManager {
    private static final List<GPS> points = new ArrayList<>();
    
    public static void addPoint(String name, double x, double y, double z) {
        addPoint(name, x, y, z, Color.WHITE);
    }
    
    public static void addPoint(String name, double x, double y, double z, Color color) {
        String server = ConnectionUtil.getServerDomain();
        removePoint(name);
        GPS newPoint = new GPS(name, x, y, z, color, server);
        points.add(newPoint);
        GPS.setLastGPS(newPoint);
        savePoints();
    }

    public static void addPointWithoutSaving(GPS point) {
        removePoint(point.getName());
        points.add(point);
        GPS.setLastGPS(point);
    }
    
    public static boolean removePoint(String name) {
        Optional<GPS> point = points.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
                
        if (point.isPresent()) {
            GPS removedPoint = point.get();
            points.remove(removedPoint);

            if (GPS.getLastGPS() != null && GPS.getLastGPS().getName().equalsIgnoreCase(name)) {
                Optional<GPS> newLastPoint = points.stream()
                        .filter(p -> p.getServer().equals(ConnectionUtil.getServerDomain()))
                        .findFirst();
                GPS.setLastGPS(newLastPoint.orElse(null));
            }
            
            savePoints();
            return true;
        }
        return false;
    }
    
    public static void clearAllPoints() {
        points.clear();
        GPS.setLastGPS(null);
    }

    private static void savePoints() {
        try {
            GPSFile gpsFile = getGPSFile();
            if (gpsFile != null) {
                gpsFile.saveCurrentServerPoints();
            }
        } catch (FileSaveException ignored) {
        }
    }

    private static GPSFile getGPSFile() {
        return Minced.getInstance().getFileRepository().getClientFiles().stream()
                .filter(file -> file instanceof GPSFile)
                .map(file -> (GPSFile) file)
                .findFirst()
                .orElse(null);
    }

    public static List<GPS> getPoints() {
        String currentServer = ConnectionUtil.getServerDomain();
        return points.stream()
                .filter(point -> point.getServer().equals(currentServer))
                .collect(java.util.stream.Collectors.toList());
    }
    
    public static boolean hasPoint(String name) {
        return points.stream().anyMatch(p -> p.getName().equalsIgnoreCase(name));
    }
} 