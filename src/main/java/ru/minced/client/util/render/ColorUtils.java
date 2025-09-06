package ru.minced.client.util.render;

import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.systems.RenderSystem;
import ru.minced.client.util.math.MathUtil;


import java.awt.*;

public class ColorUtils {

    public static int rgb(int r, int g, int b) {
        return 255 << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static void setAlphaColor(final int color, final float alpha) {
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;
        RenderSystem.clearColor(red, green, blue, alpha);
    }

    public static int HUEtoRGB(int value) {
        float hue = (float)value / 360.0F;
        return Color.HSBtoRGB(hue, 1.0F, 1.0F);
    }

    public static Color TwoColorEffect(Color cl1, Color cl2, double speed, int index) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColorC(cl1, cl2, angle / 360f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate((int) oldValue, (int) newValue, (float) interpolationValue);
    }

    public static float[] getRGBAf(int color) {
        return new float[]{(float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >> 24 & 255) / 255.0F};
    }

    public static void setColor(int color) {
        setAlphaColor(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static int toColor(String hexColor) {
        int argb = Integer.parseInt(hexColor.substring(1), 16);
        return setAlpha(argb, 255);
    }
    public static int setAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public static float[] rgba(final int color) {
        return new float[] {
                (color >> 16 & 0xFF) / 255f,
                (color >> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >> 24 & 0xFF) / 255f
        };
    }

    public static int gradient(int start, int end, int index, int speed) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int color = interpolate(start, end, MathHelper.clamp(angle / 180f - 1, 0, 1));
        float[] hs = rgba(color);
        float[] hsb = Color.RGBtoHSB((int) (hs[0] * 255), (int) (hs[1] * 255), (int) (hs[2] * 255), null);

        hsb[1] *= 1.5F;
        hsb[1] = Math.min(hsb[1], 1.0f);

        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static int interpolate(int start, int end, float value) {
        float[] startColor = rgba(start);
        float[] endColor = rgba(end);

        return rgba((int) MathUtil.interpolate(startColor[0] * 255, endColor[0] * 255, value),
                (int) MathUtil.interpolate(startColor[1] * 255, endColor[1] * 255, value),
                (int) MathUtil.interpolate(startColor[2] * 255, endColor[2] * 255, value),
                (int) MathUtil.interpolate(startColor[3] * 255, endColor[3] * 255, value));
    }

    public static int addColoring(int color, int value) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        r = Math.min(255, Math.max(0, r + value));
        g = Math.min(255, Math.max(0, g + value));
        b = Math.min(255, Math.max(0, b + value));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int setAlpha(int argb, float alpha) {
        float clamped = alpha < 0f ? 0f : (Math.min(alpha, 1f));
        int a = Math.round(clamped * 255f);
        return setAlpha(argb, a);
    }
}