package ru.minced.client.core.event.impl.keyboard;

import lombok.Getter;
import lombok.Setter;
import ru.minced.client.core.event.api.Event;

@Setter
@Getter
public class EventKey implements Event {
    private final int action;
    private final int key;

    public EventKey(int action, int key) {
        this.action = action;
        this.key = key;
    }

    public boolean isPressed(int key) {
        return this.getKey() == key;
    }
}

