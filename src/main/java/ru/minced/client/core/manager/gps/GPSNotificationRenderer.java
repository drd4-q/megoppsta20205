package ru.minced.client.core.manager.gps;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.ScaleUtil;
import ru.minced.client.util.render.font.Fonts;

import java.awt.Color;

public class GPSNotificationRenderer implements IMinecraft {
    
    public static void render(DrawContext context) {
        if (IMinecraft.nullCheck() || GPS.getLastGPS() == null) return;
        
        ScaleUtil.fixScale(context, originalScale -> {
            int scaledScreenWidth = mc.getWindow().getScaledWidth() * originalScale;
            float yPos = 150;
            
            MatrixStack matrices = context.getMatrices();
            GPS activeGPS = GPS.getLastGPS();
            Color gpsColor = activeGPS.getColor();
            Color darkGpsColor = new Color(gpsColor.getRed(), gpsColor.getGreen(), gpsColor.getBlue(), 100);
            Color transparentColor = new Color(0, 0, 0, 0);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            double distance = GPS.getDistance();
            String pointText = String.format("Active point «%s» — %.0fm", activeGPS.getName(), distance);
            String iconText = "M";
            
            float textWidth = Fonts.MEDIUM.getWidth(pointText, 14.0f);
            float iconWidth = Fonts.ICONS.getWidth(iconText, 14.0f);
            
            float textX = scaledScreenWidth - 8f - 10.0f;
            float iconX = textX - textWidth - 28.0f;
            
            float styledRectWidth = textWidth + iconWidth + 50.0f;
            float xPos = scaledScreenWidth - styledRectWidth;
            
            DrawHelper.drawStyledRect(matrices, xPos, yPos, styledRectWidth, 32f, 0, transparentColor, transparentColor, darkGpsColor, darkGpsColor);
            DrawHelper.drawRect(matrices, scaledScreenWidth - 8f, yPos, 8f, 32f, 0, gpsColor);
            DrawHelper.drawVerticalCenteredText(matrix, Fonts.ICONS.getFont(14.0f), iconText, iconX, yPos, 32f, gpsColor);
            DrawHelper.drawVerticalCenteredText(matrix, Fonts.MEDIUM.getFont(14.0f), pointText, textX - textWidth, yPos, 32f, Color.WHITE);
        });
    }
}