package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;
import ru.minced.client.feature.module.setting.Setting;

import java.util.function.Supplier;

@Setter
@Getter
public class BooleanSetting extends Setting {
    private boolean state;
    private int key = GLFW.GLFW_KEY_UNKNOWN;

    public BooleanSetting(String name, boolean state) {
        super(name,() -> true);
        this.state = state;
    }

    public BooleanSetting(String name, boolean state, Supplier<Boolean> visible) {
        super(name,visible);
        this.state = state;
    }

    public BooleanSetting(String name) {
        this(name, false);
    }

    public void set(boolean state) {
        this.state = state;
    }
}
