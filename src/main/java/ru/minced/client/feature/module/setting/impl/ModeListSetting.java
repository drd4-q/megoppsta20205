package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter @Setter
@Deprecated(since = "Use new settings")
public class ModeListSetting extends Setting {
    private List<String> list;
    private List<String> selected = new ArrayList<>();

    public ModeListSetting(String name, String... settings) {
        super(name, () -> true);
        list = Arrays.asList(settings);
    }

    public ModeListSetting(String name, Supplier<Boolean> visible, String... settings) {
        super(name, visible);
        list = Arrays.asList(settings);
    }

    public boolean isSelected(String name) {
        return selected.contains(name);
    }

    public String getSelectedString() {
        return selected.isEmpty() ? list.getFirst() : selected.getFirst();
    }

    public void select(String mode) {
        if (list.contains(mode)) {
            if (selected.contains(mode)) {
                selected.remove(mode);
            } else {
                selected.add(mode);
            }
        } else {
            throw new IllegalArgumentException("Mode " + mode + " is not available in the list.");
        }
    }
}