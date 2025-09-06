package ru.minced.client.core.event.impl.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.event.api.StoppableEvent;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventChat extends StoppableEvent {
    String message;

    public EventChat(String message) {
        this.message = message;
    }
}