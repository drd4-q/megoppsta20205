package ru.minced.client.feature.module.impl.player;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.mixin.accessor.LivingEntityAccessor;
import ru.minced.mixin.accessor.MinecraftAccessor;

public class NoDelayModule extends Module {

    BooleanSetting jumpDelaySetting = new BooleanSetting("Jump");
    BooleanSetting rightClickDelaySetting = new BooleanSetting("Right click");

    public NoDelayModule() {
        super("No Delay","Removes the delay", Category.Player);
        addSettings(jumpDelaySetting,rightClickDelaySetting);

        jumpDelaySetting.setState(true);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (jumpDelaySetting.isState()) {
            resetJumpCooldown();
        }
        if (rightClickDelaySetting.isState()) {
            resetItemUseCooldown();
        }
    }

    private void resetJumpCooldown() {
        LivingEntityAccessor livingEntityAccessor = (LivingEntityAccessor) mc.player;
        if (livingEntityAccessor != null && livingEntityAccessor.getLastJumpCooldown() > 0) {
            livingEntityAccessor.setLastJumpCooldown(0);
        }
    }

    private void resetItemUseCooldown() {
        MinecraftAccessor minecraftAccessor = (MinecraftAccessor) mc;
        if (minecraftAccessor != null && minecraftAccessor.getItemUseCooldown() > 0) {
            minecraftAccessor.setItemUseCooldown(0);
        }
    }
}
