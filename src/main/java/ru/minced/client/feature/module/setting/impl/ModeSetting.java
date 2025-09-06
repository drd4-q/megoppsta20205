package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
@Deprecated(since = "Use new settings")
public class ModeSetting extends Setting {

    private final List<String> list;
    @Setter private String selected;

    public ModeSetting(String name, String... values) {
        super(name,() -> true);
        if (values.length == 0) {
            throw new IllegalArgumentException("Лист не может быть пустым, ошибка package" + this.getClass().getPackageName() + "class name " + this.getClass().getName());
        }
        this.list = Arrays.asList(values);
        this.selected = list.getFirst();
    }

    public ModeSetting(String name, Supplier<Boolean> visible, String... values) {
        super(name,visible);
        if (values.length == 0) {
            throw new IllegalArgumentException("Лист не может быть пустым, ошибка package" + this.getClass().getPackageName() + "class name " + this.getClass().getName());
        }
        this.list = Arrays.asList(values);
        this.selected = list.getFirst();
    }

    public boolean isSelected(String name) {
        return selected.equalsIgnoreCase(name);
    }
}