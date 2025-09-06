package ru.minced.client.feature.module.setting.impl.mode;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class ModeSetting<T extends Mode> extends Setting {
    private final List<T> list;
    @Setter
    private T selected;

    @SafeVarargs
    public ModeSetting(String name, T... values) {
        super(name,() -> true);
        if (values.length == 0) {
            throw new IllegalArgumentException("Лист не может быть пустым, ошибка package" + this.getClass().getPackageName() + "class name " + this.getClass().getName());
        }
        this.list = Arrays.asList(values);
        this.selected = list.getFirst();
    }

    @SafeVarargs
    public ModeSetting(String name, Supplier<Boolean> visible, T... values) {
        super(name,visible);
        if (values.length == 0) {
            throw new IllegalArgumentException("Лист не может быть пустым, ошибка package" + this.getClass().getPackageName() + "class name " + this.getClass().getName());
        }
        this.list = Arrays.asList(values);
        this.selected = list.getFirst();
    }

    public boolean isSelected(String name) {
        return selected.getName().equalsIgnoreCase(name);
    }
}
