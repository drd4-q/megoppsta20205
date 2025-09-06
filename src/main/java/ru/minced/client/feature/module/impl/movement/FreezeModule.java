package ru.minced.client.feature.module.impl.movement;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.core.event.impl.player.EventTick;
import ru.minced.client.feature.module.Category;
import ru.minced.client.feature.module.Module;

public class FreezeModule extends Module {
    public FreezeModule() {
        super("Freeze", "Замораживает игрока для сервера", Category.Movement);
    }

    @EventHandler
    public void onUpdate(EventTick event) {
        event.stop();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket && event.getType() == PacketEvent.Type.SEND) {
            event.stop();
        }
    }
}