package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.NonFinal;
import org.lwjgl.glfw.GLFW;
import ru.minced.client.feature.module.setting.Setting;

import java.util.function.Supplier;

@Setter
@Getter
public class BindSetting extends Setting {

    @NonFinal
    public int key = GLFW.GLFW_KEY_UNKNOWN;

    public BindSetting(String name, int key) {
        super(name,() -> true);
        this.key = key;
    }

    public BindSetting(String name, int key, Supplier<Boolean> visible) {
        super(name,visible);
        this.key = key;
    }
}
