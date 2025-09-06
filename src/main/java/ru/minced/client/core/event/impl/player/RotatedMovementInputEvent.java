package ru.minced.client.core.event.impl.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.event.api.Event;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@EqualsAndHashCode(callSuper = false)
public class RotatedMovementInputEvent implements Event {
    float forward, sideways;
}
