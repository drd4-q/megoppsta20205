package ru.minced.client.feature.module.impl.client;

import lombok.Getter;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

public class SoundsModule extends Module {

    @Getter
    private static final SliderSetting volume = new SliderSetting("Volume", 0.1f, 0.1f, 1.0f, 0.1f);

    public SoundsModule() {
        super("Sounds", "Adds client sounds", Category.Miscellaneous);
        addSettings(volume);

        setState(true);
    }
}