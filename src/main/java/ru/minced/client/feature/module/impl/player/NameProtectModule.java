package ru.minced.client.feature.module.impl.player;

import lombok.Getter;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.TextSetting;

public class NameProtectModule extends Module {

    @Getter
    static TextSetting customName = new TextSetting("Name","Minced",3,16);

    public NameProtectModule() {
        super("Name Protect","Hides your name", Category.Player);
        addSettings(customName);
    }
}
