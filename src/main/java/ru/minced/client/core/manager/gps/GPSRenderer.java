package ru.minced.client.core.manager.gps;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.ScaleUtil;
import ru.minced.client.util.render.font.Fonts;
import ru.minced.client.util.rotation.rotation.AngleUtil;

import java.awt.Color;

public class GPSRenderer implements IMinecraft {

    public static void render2D(DrawContext context, double posX, double posY, double posZ, Color color, float scale) {
        if (mc.player == null || mc.gameRenderer == null || mc.gameRenderer.getCamera() == null) return;

        GPSNotificationRenderer.render(context);
        GPSArrowRenderer.render(context);

        Vec3d pos = new Vec3d(posX, posY, posZ);
        Vec3d playerPos = mc.player.getPos();
        double distance = playerPos.distanceTo(pos);

        Vec3d eyePos = mc.gameRenderer.getCamera().getPos();
        Vec3d toTarget = pos.subtract(eyePos);

        float yaw = mc.gameRenderer.getCamera().getYaw();
        float pitch = mc.gameRenderer.getCamera().getPitch();
        Vec3d cameraDir = AngleUtil.getVectorForRotation(pitch, yaw);
        double dotProduct = cameraDir.dotProduct(toTarget.normalize());

        if (dotProduct <= 0.0) {
            return;
        }

        try {
            Vec3d projected = MathUtil.projectCoordinates(pos);

            double x = projected.x;
            double y = projected.y;

            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();

            boolean onScreen = x >= 0 && x <= screenWidth && y >= 0 && y <= screenHeight;

            if (onScreen) {
                ScaleUtil.fixScale(context, (originalScale) -> {
                    MatrixStack matrices = context.getMatrices();

                    double scaledX = x * originalScale;
                    double scaledY = y * originalScale;
                    double iconSize = 16 * scale;
                    Matrix4f currentMatrix = matrices.peek().getPositionMatrix();
                    float iconTextSize = (float)(iconSize * 1.5);
                    float iconWidth = Fonts.ICONS.getFont(iconTextSize).getWidth("M");

                    float iconX = (float)(scaledX - iconWidth / 2);
                    float iconY = (float)(scaledY - iconSize) - 6f;

                    DrawHelper.drawText(currentMatrix, Fonts.ICONS.getFont(iconTextSize), "M", iconX, iconY, color);

                    String distanceText = String.format("%.0fm", distance);
                    float textWidth = Fonts.MEDIUM.getWidth(distanceText, 12.0f);
                    double textX = scaledX - textWidth / 2;
                    double textY = scaledY + 6;

                    DrawHelper.drawText(currentMatrix, Fonts.MEDIUM.getFont(12.0f), distanceText, (float)textX, (float)textY, Color.WHITE);
                });
            }
        } catch (Exception ignored) {}
    }
}
