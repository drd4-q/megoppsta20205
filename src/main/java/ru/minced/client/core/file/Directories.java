package ru.minced.client.core.file;

import lombok.experimental.UtilityClass;
import ru.minced.client.util.IMinecraft;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;

@UtilityClass
public class Directories implements IMinecraft {
    public static String directoryPath;
    public static String configDirectoryPath;

    public static File directory;
    public static File filesDirectory;
    public static File configsDirectory;
    public static File gpsDirectory;

    static {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        File clientDir = new File(configDir.toFile(), "Minced");
        
        directoryPath = clientDir.getAbsolutePath();
        configDirectoryPath = directoryPath + "/config";

        directory = clientDir;
        filesDirectory = new File(directoryPath);
        configsDirectory = new File(directoryPath, "configs");
        gpsDirectory = new File(directoryPath, "gps");
    }
}
