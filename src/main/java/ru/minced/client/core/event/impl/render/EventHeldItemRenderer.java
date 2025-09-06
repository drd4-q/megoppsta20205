package ru.minced.client.core.event.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import ru.minced.client.core.event.api.Event;

public class EventHeldItemRenderer implements Event {

    public EventHeldItemRenderer(Hand hand, ItemStack item, float equipProgress, MatrixStack stack) {
    }
}
