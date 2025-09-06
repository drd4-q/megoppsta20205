package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.function.Supplier;

@Getter
public class ButtonSetting extends Setting {
    
    private final Runnable action;
    
    @Setter
    private boolean enabled;

    public ButtonSetting(String name, Runnable action) {
        super(name, () -> true);
        this.action = action;
        this.enabled = true;
    }

    public ButtonSetting(String name, Supplier<Boolean> visible, Runnable action) {
        super(name, visible);
        this.action = action;
        this.enabled = true;
    }

    public void executeAction() {
        if (enabled) {
            action.run();
        }
    }
} 