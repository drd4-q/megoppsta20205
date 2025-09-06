package ru.minced.client.util.render;

import ru.minced.client.core.render.renderers.impl.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import ru.minced.client.core.render.renderers.impl.*;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.core.render.builders.Builder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.util.render.font.Instance;

import java.awt.*;

public class DrawHelper implements IMinecraft {

    public static void drawStyledRectEx(MatrixStack matrices, float x, float y, float width, float height, float[] cornerRadii, Color c) {
        drawBlurWithCustomRadius(matrices, x, y, width, height, cornerRadii, Color.WHITE, 25f);
        drawRectWithCustomRadius(matrices, x, y, width, height, cornerRadii, c);
    }
    
    public static void drawOutline(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color, float thickness) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BuiltOutline outline = Builder.outline()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .color(new QuadColorState(color))
                .thickness(thickness)
                .smoothness(1.0f)
                .build();
        outline.render(matrix, x, y, 0);
    }
    
    public static void drawOutlineWithCustomRadius(MatrixStack matrices, float x, float y, float width, float height, float[] cornerRadii, Color c, float thickness) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float[] finalRadii = new float[4];
        for (int i = 0; i < 4; i++) {
            finalRadii[i] = cornerRadii[i];
            if (cornerRadii[i] != 0) {
                finalRadii[i] = cornerRadii[i] - 2;
            }
        }
        BuiltOutline outline = Builder.outline()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(finalRadii[0], finalRadii[1], finalRadii[2], finalRadii[3]))
                .color(new QuadColorState(c))
                .thickness(thickness)
                .smoothness(1.0f)
                .build();
        outline.render(matrix, x, y, 0);
    }

    public static void drawStyledRect(MatrixStack matrices, float x, float y, float width, float height, float round, Color color) {
        drawBlur(matrices, x, y, width, height, round, Color.WHITE, 12);
        drawRect(matrices, x, y, width, height, round, color);
    }

    public static void drawStyledRect(MatrixStack matrices, float x, float y, float width, float height, float round, Color topColor, Color rightColor, Color bottomColor, Color leftColor) {
        drawBlur(matrices, x, y, width, height, round, Color.WHITE, 12);
        drawRect(matrices, x, y, width, height, round, topColor, rightColor, bottomColor, leftColor);
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, float round, Color color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float finalround = round;
        if (round != 0) {
            finalround = round - 2;
        }
        BuiltRectangle rectangle = Builder.rectangle()
                .size(new SizeState(width, height))
                .color(new QuadColorState(color))
                .radius(new QuadRadiusState(finalround))
                .smoothness(1.0f)
                .build();
        rectangle.render(matrix, x, y);
    }
    
    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, float round, Color topColor, Color rightColor, Color bottomColor, Color leftColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float finalround = round;
        if (round != 0) {
            finalround = round - 2;
        }
        BuiltRectangle rectangle = Builder.rectangle()
                .size(new SizeState(width, height))
                .color(new QuadColorState(topColor, rightColor, bottomColor, leftColor))
                .radius(new QuadRadiusState(finalround))
                .smoothness(1.0f)
                .build();
        rectangle.render(matrix, x, y);
    }

    public static void drawRectWithCustomRadius(MatrixStack matrices, float x, float y, float width, float height, float[] cornerRadii, Color c) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float[] finalRadii = new float[4];
        for (int i = 0; i < 4; i++) {
            finalRadii[i] = cornerRadii[i];
            if (cornerRadii[i] != 0) {
                finalRadii[i] = cornerRadii[i] - 2;
            }
        }
        BuiltRectangle rectangle = Builder.rectangle()
                .size(new SizeState(width, height))
                .color(new QuadColorState(c))
                .radius(new QuadRadiusState(finalRadii[0], finalRadii[1], finalRadii[2], finalRadii[3]))
                .smoothness(1.0f)
                .build();
        rectangle.render(matrix, x, y);
    }

    public static void drawCircle(Matrix4f matrix, float x, float y, float size, float radius, Color c) {
        BuiltCircle circle = Builder.circle()
                .size(new SizeState(size, size))
                .radius(radius)
                .color(new QuadColorState(c))
                .smoothness(1.0f)
                .build();

        circle.render(matrix, x, y, 0);
    }

    public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color, float blurRadius) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BuiltBlur blur = Builder.blur()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(radius))
                .color(new QuadColorState(color))
                .smoothness(1.0F)
                .blurRadius(blurRadius)
                .build();
        blur.render(matrix, x, y, 0);
    }

    public static void drawBlurWithCustomRadius(MatrixStack matrices, float x, float y, float width, float height, float[] cornerRadii, Color c, float blurRadius) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float[] finalRadii = new float[4];
        for (int i = 0; i < 4; i++) {
            finalRadii[i] = cornerRadii[i];
            if (cornerRadii[i] != 0) {
                finalRadii[i] = cornerRadii[i] - 2;
            }
        }
        BuiltBlur blur = Builder.blur()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(finalRadii[0], finalRadii[1], finalRadii[2], finalRadii[3]))
                .color(new QuadColorState(c))
                .smoothness(1.0F)
                .blurRadius(blurRadius)
                .build();
        blur.render(matrix, x, y, 0);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float width, float height, float cornerRadius, AbstractTexture texture) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float head = 8.0f / 64.0f;

        BuiltTexture builtTexture = Builder.texture()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(cornerRadius))
                .color(QuadColorState.WHITE)
                .smoothness(1.0f)
                .texture(head, head, head, head, texture)
                .build();
        builtTexture.render(matrix, x, y, 0);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float width, float height, float cornerRadius, float u, float v, float regionU, float regionV, AbstractTexture texture) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        BuiltTexture builtTexture = Builder.texture()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(cornerRadius))
                .color(QuadColorState.WHITE)
                .smoothness(1.0f)
                .texture(u, v, regionU, regionV, texture)
                .build();
        builtTexture.render(matrix, x, y, 0);
    }

    public static void drawImage(MatrixStack matrices, float x, float y, float width, float height, float cornerRadius, Identifier identifier, Color color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().set(matrix);

        BuiltImage image = Builder.image()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(cornerRadius))
                .color(new QuadColorState(color))
                .smoothness(1.0f)
                .identifier(identifier)
                .matrix(matrixStack)
                .build();

        image.render(matrix, x, y, 0);
    }

    public static void drawText(Matrix4f matrix, Instance instance, String text, float x, float y, Color color, float thickness) {
        BuiltText builtText = Builder.text()
                .font(instance.font())
                .size(instance.size())
                .text(text)
                .color(color)
                .thickness(thickness)
                .build();
        builtText.render(matrix, x, y, 0);
    }

    public static void drawCenteredText(Matrix4f matrix, Instance instance, String text, float x, float y, float width, Color color) {
        drawCenteredText(matrix, instance, text, x, y, width, color, 0.05f);
    }

    public static void drawCenteredText(Matrix4f matrix, Instance instance, String text, float x, float y, float width, Color color, float thickness) {
        float textWidth = instance.font().getWidth(text, instance.size());
        float centeredX = x + (width - textWidth) / 2.0f;
        drawText(matrix, instance, text, centeredX, y, color, thickness);
    }

    public static void drawVerticalCenteredText(Matrix4f matrix, Instance instance, String text, float x, float y, float height, Color color) {
        drawVerticalCenteredText(matrix, instance, text, x, y, height, color, 0);
    }

    public static void drawVerticalCenteredText(Matrix4f matrix, Instance instance, String text, float x, float y, float height, Color color, float thickness) {
        float textHeight = instance.font().getHeight(instance.size());
        float centeredY = y + (height - textHeight) / 2.0f;
        drawText(matrix, instance, text, x, centeredY, color, thickness);
    }

    public static void drawText(Matrix4f matrix, Instance instance, String text, float x, float y, Color color) {
        drawText(matrix, instance, text, x, y, color, 0.05f);
    }

    public static void drawFadingText(Matrix4f matrix, Instance instance, String text, float x, float y, Color color, float size, int maxLength, float thickness) {
        if (text == null || text.isEmpty()) {
            return;
        }

        float textWidth = instance.font().getWidth(text, size);

        if (textWidth <= maxLength) {
            drawText(matrix, instance, text, x, y, color, thickness);
            return;
        }

        String visibleText;
        float currentWidth = 0;
        int lastCharIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            String character = String.valueOf(text.charAt(i));
            float charWidth = instance.font().getWidth(character, size);

            if (currentWidth + charWidth > maxLength) {
                lastCharIndex = i;
                break;
            }

            currentWidth += charWidth;
            lastCharIndex = i + 1;
        }

        visibleText = text.substring(0, lastCharIndex);

        drawText(matrix, instance, visibleText, x, y, color, thickness);

        Color leftColor = new Color(21, 22, 29, 0);
        Color rightColor = new Color(21, 22, 29, 255);

        BuiltRectangle rectangle = Builder.rectangle()
                .size(new SizeState(15.0f, instance.font().getHeight(size)))
                .color(new QuadColorState(leftColor, leftColor, rightColor, rightColor))
                .radius(new QuadRadiusState(0))
                .smoothness(1.0f)
                .build();
        rectangle.render(matrix, x + maxLength - 15.0f, y);
    }
}
