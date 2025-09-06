package ru.minced.client.feature.module.impl.render;

import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

public class CameraModule extends Module {
    private final BooleanSetting noClip = new BooleanSetting("No clip", false);
    private final SliderSetting distance = new SliderSetting("Дистанция", 4.0F, 1.0F, 20.0F, 0.5F);
    private final SliderSetting horizontalOffset = new SliderSetting("Смещение", 0.0F, -3.0F, 3.0F, 0.1F);

    public CameraModule() {
        super("Camera", "Управление камерой", Category.Visuals);
        addSettings(distance, horizontalOffset, noClip);
    }

    public final float getDistance() {
        return distance.getValue();
    }

    public final float getHorizontalOffset() {
        return horizontalOffset.getValue();
    }

    public final boolean getNoClip() { return noClip.isState(); }
}