package ru.minced.client.core.event.impl.player;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.minecraft.entity.Entity;
import ru.minced.client.core.event.api.StoppableEvent;

@EqualsAndHashCode(callSuper = true)
@Value
public class EventAttack extends StoppableEvent {
    Entity target;
    byte eventType;
}
