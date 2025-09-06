package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class GroupSetting extends Setting {
    private final List<Setting> settings = new ArrayList<>();
    @Setter
    private boolean expanded = false;

    public GroupSetting(String name, Setting... settings) {
        super(name, () -> true);
        this.settings.addAll(Arrays.asList(settings));
    }

    public GroupSetting(String name, Supplier<Boolean> visible, Setting... settings) {
        super(name, visible);
        this.settings.addAll(Arrays.asList(settings));
    }

    public GroupSetting add(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
        return this;
    }

    public GroupSetting remove(Setting setting) {
        this.settings.remove(setting);
        return this;
    }

    public void toggleExpanded() {
        this.expanded = !this.expanded;
    }
}