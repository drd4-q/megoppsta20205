package ru.minced.client.core.file;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Formatting;
import ru.minced.client.core.file.expection.FileLoadException;
import ru.minced.client.core.file.expection.FileSaveException;
import ru.minced.client.core.file.impl.ModuleFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.minced.client.util.IMinecraft.logDirect;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    List<ClientFile> clientFiles;
    File directory, moduleConfigDirectory;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public FileController(List<ClientFile> clientFiles, File directory, File moduleConfigDirectory) {
        this.clientFiles = clientFiles;
        this.directory = directory;
        this.moduleConfigDirectory = moduleConfigDirectory;
        startAutoSave();
    }

    private void startAutoSave() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                saveFiles();
            } catch (FileSaveException e) {
            }
        }, 2, 2, TimeUnit.MINUTES);
    }

    public void stopAutoSave() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public void saveFiles() throws FileSaveException {
        if (clientFiles.isEmpty()) {
            return;
        }

        for (ClientFile clientFile : clientFiles) {
            try {
                clientFile.saveToFile(directory);
            } catch (FileSaveException e) {
                throw new FileSaveException("Failed to save file: " + clientFile.getName(), e);
            }
        }
    }

    public void removeFile(String fileName) {
        for(ClientFile clientFile : clientFiles) {
            if(clientFile instanceof ModuleFile) {
                try {
                    Files.delete(new File(moduleConfigDirectory.getPath(), fileName).toPath());
                } catch (IOException e) {
                    logDirect(String.format("Ошибка при удалении конфига! Детали: %s", e.getCause().getMessage()),
                            Formatting.RED);
                }
            }
        }
    }

    public void loadFiles() throws FileLoadException {
        if (clientFiles.isEmpty()) {
            return;
        }

        for (ClientFile clientFile : clientFiles) {
            try {
                clientFile.loadFromFile(directory);
            } catch (FileLoadException e) {
                throw new FileLoadException("Failed to load file: " + clientFile.getName(), e);
            }
        }
    }

    public void saveFile(String fileName) throws FileSaveException {
        for (ClientFile clientFile : clientFiles) {
            if (clientFile instanceof ModuleFile) {
                try {
                    clientFile.saveToFile(moduleConfigDirectory, fileName);
                } catch (FileSaveException e) {
                    throw new FileSaveException("Failed to save file: " + fileName, e);
                }
            }
        }
    }

    public void loadFile(String fileName) throws FileLoadException {
        for (ClientFile clientFile : clientFiles) {
            if (clientFile instanceof ModuleFile) {
                try {
                    clientFile.loadFromFile(moduleConfigDirectory, fileName);
                } catch (FileLoadException e) {
                    throw new FileLoadException("Failed to load file: " + fileName, e);
                }
            }
        }
    }
}