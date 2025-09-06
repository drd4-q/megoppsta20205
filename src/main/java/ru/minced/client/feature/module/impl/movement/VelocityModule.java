package ru.minced.client.feature.module.impl.movement;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;
import ru.minced.client.feature.module.setting.impl.ModeSetting;

public class VelocityModule extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Cancel");

    public VelocityModule() {
        super("Velocity", "Отменяет отдачу при получении урона", Category.Movement);
        addSettings(mode);
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.isReceive() && event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if (mc.player != null && packet.getEntityId() == mc.player.getId()) {
                if (mode.isSelected("Cancel")) {
                    event.stop();
                }
            }
        }
    }
}