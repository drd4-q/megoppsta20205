package ru.minced.client.core.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.minced.client.core.event.api.StoppableEvent;

@Getter
@AllArgsConstructor
public class ServerConnectEvent extends StoppableEvent {
    private final String serverAddress;
}