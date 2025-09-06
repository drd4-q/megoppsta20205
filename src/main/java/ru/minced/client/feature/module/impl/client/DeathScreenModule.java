package ru.minced.client.feature.module.impl.client;

import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;

public class DeathScreenModule extends Module {

    public static final BooleanSetting changeBackground = new BooleanSetting("Заменить фон", false);

    public DeathScreenModule() {
        super("Death Screen", "Модифицирует экран смерти", Category.Visuals);
        addSettings(changeBackground);
    }
}
