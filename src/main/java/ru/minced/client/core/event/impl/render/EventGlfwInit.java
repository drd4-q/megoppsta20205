package ru.minced.client.core.event.impl.render;

import ru.minced.client.core.event.api.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventGlfwInit implements Event {
    private long handle;
}
