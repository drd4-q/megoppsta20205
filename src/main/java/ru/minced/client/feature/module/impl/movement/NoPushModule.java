package ru.minced.client.feature.module.impl.movement;

import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.*;
import ru.minced.client.core.event.impl.player.*;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeListSetting;

public class NoPushModule extends Module {
    private final ModeListSetting pushTypes = new ModeListSetting("Types", "Player", "Block", "Water", "Border");
    
    public NoPushModule() {
        super("No Push", "Cancels repulsions", Category.Movement);
        addSettings(pushTypes);

        pushTypes.select("Player");
        pushTypes.select("Block");
    }

    @EventHandler
    public void onPushPlayer(EventPushPlayer eventPushPlayer) {
        if (pushTypes.isSelected("Player")) {
            eventPushPlayer.stop();
        }
    }
    
    @EventHandler
    public void onPushBlock(EventPushBlock eventPushBlock) {
        if (pushTypes.isSelected("Block")) {
            eventPushBlock.stop();
        }
    }

    @EventHandler
    public void onBlockVision(EventBlockVision eventBlockVision) {
        if (pushTypes.isSelected("Block")) {
            eventBlockVision.stop();
        }
    }

    @EventHandler
    public void onPushWater(EventPushWater eventPushWater) {
        if (pushTypes.isSelected("Water")) {
            eventPushWater.stop();
        }
    }

    @EventHandler
    public void onWorldBorder(EventWorldBorder eventWorldBorder) {
        if (pushTypes.isSelected("Border")) {
            eventWorldBorder.stop();
        }
    }
}