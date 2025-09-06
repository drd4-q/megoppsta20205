package ru.minced.client.feature.module.setting.impl.mode;

import ru.minced.client.core.Minced;
import lombok.Getter;

@Getter
public abstract class Mode {
    private final String name;

    public Mode(String name) {
        this.name = name;
        Minced.getInstance().getEventManager().subscribe(this);
    }
}
