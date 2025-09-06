package ru.minced.client.core.event.impl.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.packet.Packet;
import ru.minced.client.core.event.api.StoppableEvent;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PacketEvent extends StoppableEvent {
    Packet<?> packet;
    Type type;
    public boolean isSend() {
        return type == Type.SEND;
    }
    public boolean isReceive() {
        return type == Type.RECEIVE;
    }
    public enum Type {
        RECEIVE, SEND
    }
}