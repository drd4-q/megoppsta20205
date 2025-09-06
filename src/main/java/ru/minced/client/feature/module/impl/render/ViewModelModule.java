package ru.minced.client.feature.module.impl.render;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventSwingDuration;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.render.swinganimation.*;
import ru.minced.client.feature.module.impl.render.swinganimation.AbstractMode;
import ru.minced.client.feature.module.impl.render.swinganimation.ClassicSwingMode;
import ru.minced.client.feature.module.impl.render.swinganimation.DefaultSwingMode;
import ru.minced.client.feature.module.impl.render.swinganimation.SlashSwingMode;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.mode.ModeSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.IMinecraft;

public class ViewModelModule extends Module implements IMinecraft {
    public final ModeSetting<AbstractMode> mode = new ModeSetting<>("Mode", ClassicSwingMode.INSTANCE, SlashSwingMode.INSTANCE, DefaultSwingMode.INSTANCE);
    public final BooleanSetting onlyWithAura = new BooleanSetting("Only with AttackAura", false);
    public final SliderSetting duration = new SliderSetting("Swing Duration", 6, 1, 20, 1);
    public final SliderSetting strength = new SliderSetting("Strength", 60, 1, 10, 0.1f);

    public final SliderSetting mainX = new SliderSetting("Main X", 0, -1, 1, 0.1f);
    public final SliderSetting mainY = new SliderSetting("Main Y", 0, -1, 1, 0.1f);
    public final SliderSetting mainZ = new SliderSetting("Main Z", 0, -1, 1, 0.1f);
    public final SliderSetting offhandX = new SliderSetting("Offhand X", 0, -1, 1, 0.1f);
    public final SliderSetting offhandY = new SliderSetting("Offhand Y", 0, -1, 1, 0.1f);
    public final SliderSetting offhandZ = new SliderSetting("Offhand Z", 0, -1, 1, 0.1f);

    @EventHandler
    @SuppressWarnings("unused")
    public void onGetSwingDuration(EventSwingDuration e) {
        e.setDuration((int)duration.get());
    }

    public ViewModelModule() {
        super("View Model", "Changes the attack animation", Category.Visuals);
        addSettings(mode, onlyWithAura, duration, strength,
                mainX, mainY, mainZ,
                offhandX, offhandY, offhandZ);
        mode.setSelected(DefaultSwingMode.INSTANCE);
    }
} 