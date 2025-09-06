package ru.minced.client.core.event.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import ru.minced.client.core.event.api.StoppableEvent;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventBox extends StoppableEvent {
    Box box;
    Box changedBox;
    Entity entity;

    public EventBox(Box box, Entity entity) {
        this.box = box;
        this.entity = entity;
    }
}