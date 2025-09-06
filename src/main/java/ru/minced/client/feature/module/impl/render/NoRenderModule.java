package ru.minced.client.feature.module.impl.render;

import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;

public class NoRenderModule extends Module {

    public ModeListSetting noRender = new ModeListSetting("Удаление", "HurtCam", "Totem", "Fire", "Water", "Vignette", "Scoreboard");

    public NoRenderModule() {
        super("No Render", "Удаляет выбранные эффекты", Category.Visuals);
        addSettings(noRender);
    }
}