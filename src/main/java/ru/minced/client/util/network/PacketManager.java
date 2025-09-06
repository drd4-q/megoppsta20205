package ru.minced.client.util.network;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.network.packet.Packet;
import ru.minced.client.util.IMinecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class PacketManager implements IMinecraft {

    @Getter
    private final List<Packet<?>> silentPackets = new ArrayList<>();

    public void sendSilentPacket(Packet<?> packet) {
        silentPackets.add(packet);
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
    }

    public void sendPacket(Packet<?> packet){
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
    }
}
