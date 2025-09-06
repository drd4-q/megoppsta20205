package ru.minced.client.util.render;

import net.minecraft.client.gui.DrawContext;
import ru.minced.client.util.IMinecraft;

public class ScaleUtil implements IMinecraft {

    public static void fixScale(DrawContext context, ScaleRunnable runnable) {
        context.getMatrices().push();
        int currentScale = mc.options.getGuiScale().getValue();
        
        try {
            if (currentScale > 1) {
                float scaleFactor = 1.0f / currentScale;
                context.getMatrices().scale(scaleFactor, scaleFactor, 1.0f);
            }
            runnable.run(currentScale);
        } finally {
            context.getMatrices().pop();
        }
    }

    public static void fixScale(DrawContext context, double mouseX, double mouseY, ScaleMouseRunnable runnable) {
        context.getMatrices().push();
        int currentScale = mc.options.getGuiScale().getValue();
        
        try {
            if (currentScale > 1) {
                float scaleFactor = 1.0f / currentScale;
                context.getMatrices().scale(scaleFactor, scaleFactor, 1.0f);
            }

            double scaledMouseX = mouseX * currentScale;
            double scaledMouseY = mouseY * currentScale;
            
            runnable.run(currentScale, scaledMouseX, scaledMouseY);
        } finally {
            context.getMatrices().pop();
        }
    }

    public static int scaleFromGuiX(double x) {
        return (int)(x / mc.getWindow().getScaleFactor());
    }

    public static int scaleFromGuiY(double y) {
        return (int)(y / mc.getWindow().getScaleFactor());
    }

    @FunctionalInterface
    public interface ScaleRunnable {
        void run(int originalScale);
    }
    
    @FunctionalInterface
    public interface ScaleMouseRunnable {
        void run(int originalScale, double scaledMouseX, double scaledMouseY);
    }
} 