package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@Setter
public class SliderSetting extends Setting {
    private float value, minimum, maximum, increment;
    private Consumer<Float> changeListener;

    public SliderSetting(String name, float value, float minimum, float maximum, float increment) {
        super(name,() -> true);
        this.minimum = minimum;
        this.value = value;
        this.maximum = maximum;
        this.increment = increment;
    }

    public SliderSetting(String name, float value, float minimum, float maximum, float increment, Supplier<Boolean> visible) {
        super(name,visible);
        this.minimum = minimum;
        this.value = value;
        this.maximum = maximum;
        this.increment = increment;
    }

    public float get() {
        return this.value;
    }
    
    public void setValue(float value) {
        this.value = value;
        if (changeListener != null) {
            changeListener.accept(value);
        }
    }
    
    public SliderSetting setChangeListener(Consumer<Float> changeListener) {
        this.changeListener = changeListener;
        return this;
    }
}
