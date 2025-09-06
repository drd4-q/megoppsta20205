package ru.minced.client.feature.module.impl.miscellaneous;

import lombok.Getter;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.screen.EventDeathScreen;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.SliderSetting;

public class AutoRevivalModule extends Module {
    
    @Getter private final SliderSetting delay = new SliderSetting("Delay", 0, 0, 10, 1);

    public AutoRevivalModule() {
        super("Auto Revival", "Automatically revives the player after death", Category.Miscellaneous);
        addSettings(delay);
    }

    @EventHandler
    public void onDeathScreen(EventDeathScreen event) {
        if (mc.player == null) return;

        if (event.getTicksSinceDeath() > Math.round(getDelay().get())) {
            mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}
