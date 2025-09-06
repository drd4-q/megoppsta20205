package ru.minced.client.feature.module.impl.movement;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.IMinecraft;

public class TwerkModule extends Module implements IMinecraft {

    private boolean sneaking = false;

    public TwerkModule(){
        super("Twerk","Тверк", Category.Movement);
    }

    @EventHandler
    public void onUpdate(EventTick event) {
        if (!IMinecraft.nullCheck()) {
            mc.options.sneakKey.setPressed(sneaking = !sneaking);
        }
    }
}
