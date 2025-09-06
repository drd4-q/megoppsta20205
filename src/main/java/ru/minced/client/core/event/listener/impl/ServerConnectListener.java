package ru.minced.client.core.event.listener.impl;

import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.ServerConnectEvent;
import ru.minced.client.core.event.listener.Listener;
import ru.minced.client.core.file.expection.FileLoadException;
import ru.minced.client.core.file.impl.GPSFile;

public class ServerConnectListener implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        loadGPSPoints();
    }

    private void loadGPSPoints() {
        GPSFile gpsFile = getGPSFile();
        if (gpsFile != null) {
            try {
                gpsFile.loadCurrentServerPoints();
            } catch (FileLoadException ignored) {
            }
        }
    }

    private GPSFile getGPSFile() {
        return Minced.getInstance().getFileRepository().getClientFiles().stream()
                .filter(file -> file instanceof GPSFile)
                .map(file -> (GPSFile) file)
                .findFirst()
                .orElse(null);
    }
}