package ru.minced.client.util.other.animation.impl;

import ru.minced.client.util.other.animation.Animation;

import java.awt.Color;

public class FadeAnimation extends Animation {

    @Override
    public double calculation(double value) {
        double x = value / ms;
        return 1 - (x - 1) * (x - 1);
    }

    public Color getColorWithAlpha(Color color) {
        update();
        float alpha = getOutput().floatValue();
        return applyAlpha(color, alpha);
    }
    
    public Color applyAnimatedAlpha(Color color) {
        update();
        float alpha = getOutput().floatValue();
        return applyAlpha(color, alpha);
    }
    
    public Color applyAnimatedAlphaPreserveOriginal(Color color) {
        update();
        float alpha = getOutput().floatValue();
        int originalAlpha = color.getAlpha();
        float resultAlpha = alpha * (originalAlpha / 255f);
        return applyAlpha(color, resultAlpha);
    }

    public static Color applyAlpha(Color color, float alpha) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = Math.max(0, Math.min(255, (int)(alpha * 255)));
        return new Color(r, g, b, a);
    }
} 