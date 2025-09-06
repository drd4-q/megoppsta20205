package ru.minced.client.core;

import ru.minced.client.core.manager.socket.SocketManager;
import lombok.Getter;
import lombok.experimental.NonFinal;
import net.fabricmc.api.ClientModInitializer;
import ru.minced.client.feature.command.CommandManager;
import ru.minced.client.core.draggable.DraggableManager;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.listener.ListenerRepository;
import ru.minced.client.core.file.Directories;
import ru.minced.client.core.file.DirectoryCreator;
import ru.minced.client.core.file.FileController;
import ru.minced.client.core.file.FileRepository;
import ru.minced.client.core.file.expection.FileProcessingException;
import ru.minced.client.core.manager.discord.DiscordManager;
import ru.minced.client.core.manager.theme.ThemeManager;
import ru.minced.client.feature.module.ModuleManager;
import ru.minced.client.core.manager.SoundManager;
import ru.minced.client.util.other.TPSCalc;
import ru.minced.client.core.render.scissor.ScissorManager;
import ru.minced.client.util.rotation.attack.AttackPerpetrator;

@Getter
public class Minced implements ClientModInitializer {
    @Getter private static Minced instance;
    @NonFinal EventManager eventManager;
    private ModuleManager moduleManager;
    private DraggableManager draggableManager;
    private SoundManager soundManager;
    private ThemeManager themeManager;
    private TPSCalc tpsCalc;
    @NonFinal public ScissorManager scissorManager;
    @NonFinal DiscordManager discordManager;
    @NonFinal FileRepository fileRepository;
    @NonFinal FileController fileController;
    @NonFinal CommandManager commandManager;
    @NonFinal ListenerRepository listenerRepository;
    AttackPerpetrator attackPerpetrator;

    @Override
    public void onInitializeClient() {
        instance = this;

        soundManager = new SoundManager();
        soundManager.init();
        eventManager = new EventManager();
        attackPerpetrator = new AttackPerpetrator();
        themeManager = new ThemeManager();
        moduleManager = new ModuleManager();
        moduleManager.init();
        draggableManager = new DraggableManager();
        draggableManager.register();
        commandManager = new CommandManager();
        commandManager.register();
        scissorManager = new ScissorManager();
        initFileManager();
        initListeners();
        initDiscordRPC();
        SocketManager.initIrcChat();
        tpsCalc = new TPSCalc();
    }

    public void initDiscordRPC() {
        discordManager = new DiscordManager();
        discordManager.init();
    }

    public void initFileManager() {
        DirectoryCreator directoryCreator = new DirectoryCreator();
        directoryCreator.createDirectories(Directories.directory, Directories.filesDirectory,
                Directories.configsDirectory, Directories.gpsDirectory);

        fileRepository = new FileRepository();
        fileRepository.setup();

        fileController = new FileController(fileRepository.getClientFiles(), Directories.filesDirectory,
                Directories.configsDirectory);
        try {
            fileController.loadFiles();
        } catch (FileProcessingException ignored) {
        }
    }

    public void initListeners() {
        listenerRepository = new ListenerRepository();
        listenerRepository.setup();
    }
}