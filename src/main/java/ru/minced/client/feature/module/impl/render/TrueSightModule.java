package ru.minced.client.feature.module.impl.render;

import lombok.Getter;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

public class TrueSightModule extends Module {

    @Getter
    private final SliderSetting opacity = new SliderSetting("Прозрачность", 0.5f, 0.1f, 1.0f, 0.1f);

    public TrueSightModule() {
        super("True Sight", "Делает невидимых сущностей прозрачными", Category.Visuals);
        addSettings(opacity);
    }
}