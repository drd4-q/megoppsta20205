package ru.minced.client.feature.module.impl.render;

import lombok.Getter;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.render.EventRender;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.impl.render.nametags.PlayerTagsRenderer;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;

@Getter
public class NameTagsModule extends Module {

    private final ModeListSetting tagsMode = new ModeListSetting("Mode", "Players", "Items");
    
    public NameTagsModule() {
        super("Name Tags", "Enhanced nametags for players and items", Category.Visuals);
        addSettings(tagsMode);
    }
    
    @EventHandler
    public void onRender(EventRender event) {
        if (isState()) {
            if (tagsMode.isSelected("Players")) {
                PlayerTagsRenderer.renderPlayerTags(event.getStack());
            }
        }
    }
} 