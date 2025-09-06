package ru.minced.client.feature.module.impl.player;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.util.player.MovingUtil;
import ru.minced.client.util.player.PlayerIntersectionUtil;

public class NoWebModule extends Module {

    public NoWebModule() {
        super("No Web", "Prevents slowdown from various blocks", Category.Player);
    }

    @EventHandler
    public void onUpdate(EventTick eventTick) {
        if (PlayerIntersectionUtil.isPlayerInWeb()) {
            final double[] dir = MovingUtil.calculateDirection(0.6f);
            assert mc.player != null;
            mc.player.setVelocity(dir[0], 0, dir[1]);

            if (mc.options.jumpKey.isPressed())
                mc.player.setVelocity(mc.player.getVelocity().add(0, 1.4, 0));

            if (mc.options.sneakKey.isPressed())
                mc.player.setVelocity(mc.player.getVelocity().add(0, -3.7, 0));
        }
    }
}
