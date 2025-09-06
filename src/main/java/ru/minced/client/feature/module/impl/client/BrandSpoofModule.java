package ru.minced.client.feature.module.impl.client;

import lombok.Getter;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.TextSetting;

public class BrandSpoofModule extends Module {

    @Getter
    static TextSetting customBrand = new TextSetting("Brand","Minced",3,16);

    public BrandSpoofModule(){
        super("Brand Spoof","спуфает бренд", Category.Miscellaneous);
        addSettings(customBrand);
    }
}
