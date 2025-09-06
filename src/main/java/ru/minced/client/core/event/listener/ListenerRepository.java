package ru.minced.client.core.event.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.Minced;
import ru.minced.client.core.event.listener.impl.PacketEventListener;
import ru.minced.client.core.event.listener.impl.ServerConnectListener;
import ru.minced.client.core.event.listener.impl.UpdateEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListenerRepository {
    final List<Listener> listeners = new ArrayList<>();

    public void setup() {
        registerListeners(
                new UpdateEventListener(),
                new PacketEventListener(),
                new ServerConnectListener()
        );
    }

    public void registerListeners(Listener... listeners) {
        this.listeners.addAll(List.of(listeners));
        Arrays.stream(listeners).forEach(listener -> Minced.getInstance().getEventManager().subscribe(listener));
    }
}
