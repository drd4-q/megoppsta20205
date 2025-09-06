package ru.minced.client.feature.module.setting;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;


@Getter
@Setter
public abstract class Setting {
    protected String name;

    private Supplier<Boolean> visible = () -> true;

    public Setting(String name, Supplier<Boolean> visible) {
        this.name = name;
        this.visible = visible;
    }

    public Setting(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
    }

    public Supplier<Boolean> getVisible() {
        return visible;
    }
}
