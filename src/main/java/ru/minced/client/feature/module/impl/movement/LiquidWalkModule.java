package ru.minced.client.feature.module.impl.movement;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.*;
import ru.minced.client.core.event.impl.player.MotionEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.feature.module.setting.impl.ModeSetting;
import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.player.MovingUtil;

public class LiquidWalkModule extends Module {
    private final SliderSetting speed = new SliderSetting("Speed", 0, 0, 10, 1);
    ModeSetting mode = new ModeSetting("Mode", "SpookyTime");
    public final BooleanSetting sprint = new BooleanSetting("Sprint", false);

    public LiquidWalkModule() {
        super("Liquid Walk", "liquid bounce", Category.Movement);
        addSettings(speed, mode, sprint);

        mode.setSelected("SpookyTime");
    }

    @EventHandler
    public void onMove(MotionEvent eventMove) {
        assert mc.player != null;
        if (mc.player.isSwimming()) {

            float baseSpeed = 0.1f;
            float adjustedSpeed = baseSpeed * (speed.getValue() / 5.0f);
            double[] dirSpeed = MovingUtil.forward(adjustedSpeed);
            eventMove.setX(eventMove.getX() + dirSpeed[0]);
            eventMove.setZ(eventMove.getZ() + dirSpeed[1]);
            eventMove.stop();
        } else {
            eventMove.setX(0);
            eventMove.setZ(0);
        }
    }
}