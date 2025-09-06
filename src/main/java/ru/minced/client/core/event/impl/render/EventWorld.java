package ru.minced.client.core.event.impl.render;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import ru.minced.client.core.event.api.Event;
import ru.minced.client.util.IMinecraft;

@Getter
public class EventWorld implements Event, IMinecraft {
    private MatrixStack stack;
    private float partialTicks;
    private DrawContext context;

    public EventWorld(MatrixStack stack, float partialTicks)
    {
        this.stack = stack;
        this.partialTicks = partialTicks;
        this.context = new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers());
    }

}
