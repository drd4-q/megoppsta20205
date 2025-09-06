package ru.minced.client.feature.module.impl.render;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventWorld;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.render.targetesp.TargetESPHandler;
import ru.minced.client.feature.module.setting.impl.ModeSetting;

public class TargetESPModule extends Module {
    private final ModeSetting espMode = new ModeSetting("Mode", "Marker", "Ghosts", "Jello");
    
    public TargetESPModule() {
        super("Target ESP", "Marks the target with a marker", Category.Visuals);
        addSettings(espMode);
    }
    
    @EventHandler
    public void onRender(EventWorld event) {
        if (isState()) {
            TargetESPHandler.renderESP(espMode.getSelected(), event.getStack());
        }
    }
}
