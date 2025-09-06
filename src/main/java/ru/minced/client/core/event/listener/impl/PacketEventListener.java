package ru.minced.client.core.event.listener.impl;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.EventHandler;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.core.event.listener.Listener;


public class PacketEventListener implements Listener {
    public static boolean serverSprint;

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        Packet<?> packet = packetEvent.getPacket();
        if (packet instanceof ClientCommandC2SPacket clientCommandC2SPacket) {
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                serverSprint = true;
            }
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                serverSprint = false;
            }
        }
        Minced.getInstance().getAttackPerpetrator().onPacket(packetEvent);
    }
}
