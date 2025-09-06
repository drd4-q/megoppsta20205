package ru.minced.client.core.event.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.minced.client.core.event.api.Event;


@Getter
@Setter
@AllArgsConstructor
public class EventSwingDuration implements Event {
    private int Duration;
}
