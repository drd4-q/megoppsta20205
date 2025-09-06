package ru.minced.client.feature.module.impl.fight;

import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

public class ReachModule extends Module {

    public ReachModule(){
        super("Reach", "dssfdsf", Category.Fight);
        addSettings(blocksRange, entityRange);
    }

    public final SliderSetting blocksRange = new SliderSetting("BlocksRange", 3f, 0.1f, 6.0f, 0.1f);
    public final SliderSetting entityRange = new SliderSetting("EntityRange", 3f, 0.1f, 6.0f, 0.1f);
}
