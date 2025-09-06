package ru.minced.client.core.event.impl.screen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.event.api.Event;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDeathScreen implements Event {
    int ticksSinceDeath;
}
