package ru.minced.client.core.event.impl.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.minced.client.core.event.api.StoppableEvent;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MotionEvent extends StoppableEvent {
    double x, y, z;
    float yaw, pitch;
    boolean onGround;
}