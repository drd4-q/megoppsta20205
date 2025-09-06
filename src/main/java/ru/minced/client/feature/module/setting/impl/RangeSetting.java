package ru.minced.client.feature.module.setting.impl;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.feature.module.setting.Setting;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@Setter
public class RangeSetting extends Setting {
    private float minValue, maxValue, minimum, maximum, increment;
    private Consumer<float[]> changeListener;

    public RangeSetting(String name, float minValue, float maxValue, float minimum, float maximum, float increment) {
        super(name, () -> true);
        this.minimum = minimum;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.maximum = maximum;
        this.increment = increment;
    }

    public RangeSetting(String name, float minValue, float maxValue, float minimum, float maximum, float increment, Supplier<Boolean> visible) {
        super(name, visible);
        this.minimum = minimum;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.maximum = maximum;
        this.increment = increment;
    }

    public float[] get() {
        return new float[]{this.minValue, this.maxValue};
    }
    
    public void setMinValue(float minValue) {
        if (minValue > maxValue) {
            minValue = maxValue;
        }
        this.minValue = minValue;
        if (changeListener != null) {
            changeListener.accept(new float[]{minValue, maxValue});
        }
    }
    
    public void setMaxValue(float maxValue) {
        if (maxValue < minValue) {
            maxValue = minValue;
        }
        this.maxValue = maxValue;
        if (changeListener != null) {
            changeListener.accept(new float[]{minValue, maxValue});
        }
    }
    
    public RangeSetting setChangeListener(Consumer<float[]> changeListener) {
        this.changeListener = changeListener;
        return this;
    }
} 