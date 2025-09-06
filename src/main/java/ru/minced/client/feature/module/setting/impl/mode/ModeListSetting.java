package ru.minced.client.feature.module.setting.impl.mode;

import lombok.Getter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class ModeListSetting<T extends Mode> extends Setting {
    private final List<T> list;
    private final List<T> selected = new ArrayList<>();

    @SafeVarargs
    public ModeListSetting(String name, T... modes) {
        super(name, () -> true);
        if (modes.length == 0) {
            throw new IllegalArgumentException(
                    "Лист не может быть пустым, ошибка package " + this.getClass().getPackageName() +
                            " class name " + this.getClass().getName()
            );
        }
        this.list = Arrays.asList(modes);
    }

    @SafeVarargs
    public ModeListSetting(String name, Supplier<Boolean> visible, T... modes) {
        super(name, visible);
        if (modes.length == 0) {
            throw new IllegalArgumentException(
                    "Лист не может быть пустым, ошибка package " + this.getClass().getPackageName() +
                            " class name " + this.getClass().getName()
            );
        }
        this.list = Arrays.asList(modes);
    }

    public boolean isSelected(String name) {
        return selected.stream().anyMatch(mode -> mode.getName().equalsIgnoreCase(name));
    }

    public String getSelectedString() {
        return selected.isEmpty() ? list.getFirst().getName() : selected.getFirst().getName();
    }

    public void select(String name) {
        T mode = list.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Mode " + name + " is not available in the list."));

        if (selected.contains(mode)) {
            selected.remove(mode);
        } else {
            selected.add(mode);
        }
    }

    public void clearSelection() {
        selected.clear();
    }
}
