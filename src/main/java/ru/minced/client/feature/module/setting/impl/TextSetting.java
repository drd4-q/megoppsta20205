package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.minced.client.feature.module.setting.Setting;

import java.util.function.Supplier;

@Getter
@Setter
@Accessors(chain = true)
public class TextSetting extends Setting {
    private String text;
    private int min, max;

    public TextSetting(String name,String text, int min,int max) {
        super(name, () -> true);
        this.text = text;
        this.min = min;
        this.max = max;
    }

    public TextSetting(String name,int min,int max,Supplier<Boolean> visible) {
        super(name,visible);
        this.min = min;
        this.max = max;
    }

    public TextSetting visible(Supplier<Boolean> visible) {
        setVisible(visible);
        return this;
    }
}