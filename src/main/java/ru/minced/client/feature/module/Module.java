package ru.minced.client.feature.module;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.core.Minced;
import ru.minced.client.core.file.Directories;
import ru.minced.client.feature.module.impl.client.SoundsModule;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.util.ILogger;
import ru.minced.client.util.IMinecraft;

import java.util.ArrayList;
import java.util.Collections;

@Getter
@Setter
public class Module implements ILogger, IMinecraft {
    private String name;
    private String description;
    private Category category;

    private int key;
    private boolean state;

    private ArrayList<Setting> settings = new ArrayList<>();

    private static boolean directoriesChecked = false;

    public Module(String name,String description,Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void addSettings(Setting... options) {
        Collections.addAll(settings, options);
    }


    public void toggle() {
        this.state = !state;
        if (state)
            onEnabled();
        else
            onDisabled();
    }


    public void setState(boolean state) {
        boolean wasEnabled = this.state;
        this.state = state;
        if (state && !wasEnabled) {
            Minced.getInstance().getEventManager().subscribe(this);
        } else if (!state && wasEnabled) {
            Minced.getInstance().getEventManager().unsubscribe(this);
        }
    }

    public void onEnabled() {
        ensureDirectoriesExist();
        Minced.getInstance().getEventManager().subscribe(this);
        
        if (Minced.getInstance().getModuleManager().getSoundsModule().isState()) {
            Minced.getInstance().getSoundManager().play("enable", SoundsModule.getVolume().getValue());
        }
    }

    public void onDisabled() {
        Minced.getInstance().getEventManager().unsubscribe(this);
        
        if (Minced.getInstance().getModuleManager().getSoundsModule().isState()) {
            Minced.getInstance().getSoundManager().play("disable", SoundsModule.getVolume().getValue());
        }
    }
    
    private synchronized void ensureDirectoriesExist() {
        if (!directoriesChecked) {
            if (!Directories.directory.exists()) {
                Directories.directory.mkdirs();
            }
            if (!Directories.filesDirectory.exists()) {
                Directories.filesDirectory.mkdirs();
            }
            
            try {
                Minced.getInstance().getFileController().saveFiles();
            } catch (Exception ignored) {
            }
            
            directoriesChecked = true;
        }
    }
}