package ru.minced.client.feature.module.impl.player;

import net.minecraft.network.packet.c2s.play.*;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;

public class CTLeaveModule extends Module {

    public ModeSetting leaveMode = new ModeSetting("Mode", "Invalid slot", "HW Classic");

    public CTLeaveModule() {
        super("CT Leave", "Safely exit the game during battle mode", Category.Player);
        addSettings(leaveMode);
    }

    @EventHandler
    public void onUpdate(EventTick e) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        switch (leaveMode.getSelected()) {
            case "HW Classic":
                for (int i = 0; i < 20000; i++) {
                    if (i % 5 == 0) {
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                                i % 360, i % 180, true, false));
                    }
                }
                break;

            case "Invalid slot":
                for (int i = 0; i < 10; i++) {
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(127 + i));
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(-1 - i));
                }
                break;
        }

        toggle();
    }
}
