package ru.minced.client.core.event.impl.render;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.option.Perspective;
import ru.minced.client.core.event.api.Event;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@EqualsAndHashCode(callSuper = false)
public class PerspectiveEvent implements Event {
    Perspective perspective;
}