package ru.minced.client.feature.module.impl.render;

import lombok.Getter;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

public class AspectModule extends Module {

    @Getter SliderSetting ratio = new SliderSetting("Соотношение",1.8F, 0.6F,6F,0.1F);

    public AspectModule() {
        super("Aspect","dfdsd", Category.Visuals);
        addSettings(ratio);
    }
}