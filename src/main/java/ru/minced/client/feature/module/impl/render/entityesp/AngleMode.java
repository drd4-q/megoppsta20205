package ru.minced.client.feature.module.impl.render.entityesp;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import ru.minced.client.util.IMinecraft;

import java.awt.Color;

public class AngleMode implements IMinecraft {

    public static void render(DrawContext context, Entity entity, Color color) {
        if (entity == null || entity == mc.player) return;
    }
}