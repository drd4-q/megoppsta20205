package ru.minced.client.feature.module.impl.render.entityesp;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.Renderer3D;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class BoxMode implements IMinecraft {

    public static void render(MatrixStack matrices, Entity entity, Color color) {
        if (IMinecraft.nullCheck()) return;

        var hitBox = entity.getBoundingBox();

        double tPosX = MathUtil.interpolate(entity.prevX, entity.getX(), MathUtil.getTickDelta());
        Box box = getBox(entity, hitBox, tPosX);

        Renderer3D.setupRender();
        Renderer3D.drawBox(box, matrices, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50),
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
        Renderer3D.endRender();
    }

    private static @NotNull Box getBox(Entity entity, Box hitBox, double tPosX) {
        double tPosY = MathUtil.interpolate(entity.prevY, entity.getY(), MathUtil.getTickDelta());
        double tPosZ = MathUtil.interpolate(entity.prevZ, entity.getZ(), MathUtil.getTickDelta());

        double width = (hitBox.maxX - hitBox.minX);
        double height = (hitBox.maxY - hitBox.minY);
        double depth = (hitBox.maxZ - hitBox.minZ);

        return new Box(
                tPosX - width / 2,
                tPosY,
                tPosZ - depth / 2,
                tPosX + width / 2,
                tPosY + height,
                tPosZ + depth / 2
        );
    }
}