package ru.minced.client.core.event.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import ru.minced.client.core.event.api.Event;

@Getter
@Setter
@AllArgsConstructor
public class EventRender implements Event {
    private final MatrixStack stack;
    private final DrawContext context;
}
