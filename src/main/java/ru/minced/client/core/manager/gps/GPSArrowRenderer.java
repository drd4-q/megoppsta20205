package ru.minced.client.core.manager.gps;

import com.mojang.blaze3d.systems.RenderSystem;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.ScaleUtil;
import ru.minced.client.util.render.font.Fonts;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.RotationAxis;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;

import java.awt.Color;

public class GPSArrowRenderer implements IMinecraft {

    @Setter private static GPS lastGPS;
    private static final float ARROW_SIZE = 15f;

    public static void render(DrawContext context) {
        if (mc.world == null || mc.player == null || lastGPS == null) return;

        ScaleUtil.fixScale(context, originalScale -> {
            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();

            float xCenter = screenWidth / 2f;
            float yPos = screenHeight / 2f - 90f;

            float rotation = getRotationToGPS() - mc.player.getYaw();

            context.getMatrices().push();
            context.getMatrices().translate(xCenter, yPos, 0.0F);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

            Color color = lastGPS.getColor();

            DrawHelper.drawImage(context.getMatrices(), -ARROW_SIZE / 2, -ARROW_SIZE / 2, ARROW_SIZE, ARROW_SIZE, 0, IMinecraft.arrowPng, Color.WHITE);

            context.getMatrices().pop();

            String gpsName = lastGPS.getName();
            double distance = getDistanceToGPS();
            String formattedDistance = String.format("%.1fm", distance);
            String displayText = gpsName + " (" + formattedDistance + ")";

            DrawHelper.drawText(context.getMatrices().peek().getPositionMatrix(), Fonts.MEDIUM.getFont(7.0f), displayText, xCenter - Fonts.MEDIUM.getWidth(displayText, 7.0f) / 2, yPos + ARROW_SIZE / 2 + 2, color);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        });
    }

    private static float getRotationToGPS() {
        if (mc.player == null || lastGPS == null) return 0;

        float tickDelta = mc.getRenderTickCounter().getTickDelta(true);

        double x = lastGPS.getX() - MathUtil.interpolate(mc.player.prevX, mc.player.getX(), tickDelta);
        double z = lastGPS.getZ() - MathUtil.interpolate(mc.player.prevZ, mc.player.getZ(), tickDelta);

        return (float) -(Math.atan2(x, z) * (180 / Math.PI));
    }


    private static double getDistanceToGPS() {
        if (mc.player == null || lastGPS == null) return 0;

        double x = lastGPS.getX() - MathUtil.interpolate(mc.player.getX(), mc.player.prevX);
        double z = lastGPS.getZ() - MathUtil.interpolate(mc.player.getZ(), mc.player.prevZ);

        return Math.sqrt(x * x + z * z);
    }
}