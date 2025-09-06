package ru.minced.mixin.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.minced.client.core.event.EventManager;
import ru.minced.client.core.event.impl.packet.PacketEvent;
import ru.minced.client.util.network.PacketManager;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {


    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.Type.RECEIVE);
        EventManager.post(packetEvent);
        if (packetEvent.isStopped()) {
            info.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"),cancellable = true)
    private void send(Packet<?> packet, CallbackInfo info) {
        if (PacketManager.getSilentPackets().contains(packet)) {
            PacketManager.getSilentPackets().remove(packet);
            return;
        }

        PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.Type.SEND);
        EventManager.post(packetEvent);
        if (packetEvent.isStopped()) {
            info.cancel();
        }
    }
}