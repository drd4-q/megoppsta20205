package ru.minced.client.core.event.impl.player;

import ru.minced.client.core.event.api.StoppableEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventMove extends StoppableEvent {
    public double x, y, z;

    public EventMove( double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}