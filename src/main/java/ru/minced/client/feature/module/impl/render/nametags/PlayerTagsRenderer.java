package ru.minced.client.feature.module.impl.render.nametags;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.ScaleUtil;
import ru.minced.client.util.render.font.Fonts;
import ru.minced.client.util.rotation.rotation.AngleUtil;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerTagsRenderer implements IMinecraft {
    private static final float TEXT_SIZE = 26.0f;
    private static final float MIN_SCALE = 0.35f;
    private static final float MAX_SCALE = 0.6f;
    private static final float MIN_DISTANCE = 1.0f;
    private static final float MAX_DISTANCE = 20.0f;
    


    public static void renderPlayerTags(MatrixStack matrixStack) {
        if (IMinecraft.nullCheck()) return;

        assert mc.world != null;
        List<PlayerEntity> players = mc.world.getPlayers().stream()
                .filter(PlayerTagsRenderer::shouldRenderTag)
                .collect(Collectors.toList());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        for (PlayerEntity player : players) {
            renderNameTag(matrixStack, player);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private static void renderNameTag(MatrixStack matrixStack, PlayerEntity player) {
        try {
            double x = MathUtil.interpolate(player.prevX, player.getX(), MathUtil.getTickDelta());
            double y = MathUtil.interpolate(player.prevY, player.getY(), MathUtil.getTickDelta()) + player.getHeight();
            double z = MathUtil.interpolate(player.prevZ, player.getZ(), MathUtil.getTickDelta());
            
            Vec3d pos = new Vec3d(x, y, z);
            assert mc.player != null;
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

            Vec3d projected = MathUtil.projectCoordinates(pos);

            double screenX = projected.x;
            double screenY = projected.y;
            
            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();

            if (screenX < 0 || screenX > screenWidth || screenY < 0 || screenY > screenHeight) {
                return;
            }

            float scale = calculateScale((float) distance);

            String playerName = player.getName().getString();
            float health = player.getHealth();
            
            Color healthColor = getHealthColor(health, player.getMaxHealth());
            Color bracketColor = new Color(128, 128, 128);

            DrawContext drawContext = new net.minecraft.client.gui.DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers());
            drawContext.getMatrices().multiplyPositionMatrix(matrixStack.peek().getPositionMatrix());
            
            ScaleUtil.fixScale(drawContext, (originalScale) -> {
                double scaledX = screenX * originalScale;
                double scaledY = screenY * originalScale;
                
                float nameWidth = Fonts.MEDIUM.getWidth(playerName, TEXT_SIZE * scale);
                float bracketLeftWidth = Fonts.MEDIUM.getWidth("[", TEXT_SIZE * scale);
                float healthWidth = Fonts.MEDIUM.getWidth(String.format("%.1f", health), TEXT_SIZE * scale);
                float bracketRightWidth = Fonts.MEDIUM.getWidth("]", TEXT_SIZE * scale);
                float totalWidth = nameWidth + bracketLeftWidth + healthWidth + bracketRightWidth;
                
                float scaledPadding = 4.0f * scale;

                float rightPadding = scaledPadding * 2.0f;
                float backgroundWidth = totalWidth + scaledPadding + rightPadding + 2;
                float backgroundHeight = TEXT_SIZE * scale + scaledPadding * 2;

                float verticalOffset = -backgroundHeight * 1.5f;

                double bgX = scaledX - backgroundWidth / 2;
                double bgY = scaledY + verticalOffset;
                
                Matrix4f matrix = drawContext.getMatrices().peek().getPositionMatrix();

                Color backgroundColor = new Color(41, 41, 41, 128);
                DrawHelper.drawStyledRect(drawContext.getMatrices(), (float)bgX, (float) bgY, backgroundWidth, backgroundHeight, 4.0f, backgroundColor);

                float textX = (float)bgX + scaledPadding;

                DrawHelper.drawVerticalCenteredText(matrix, Fonts.MEDIUM.getFont(TEXT_SIZE * scale), playerName, textX, (float)bgY, backgroundHeight, Color.WHITE);

                float bracketLeftX = textX + nameWidth + 4.0f * scale + 2;
                DrawHelper.drawVerticalCenteredText(matrix, Fonts.MEDIUM.getFont(TEXT_SIZE * scale), "[", bracketLeftX, (float)bgY, backgroundHeight, bracketColor);

                float healthX = bracketLeftX + bracketLeftWidth;
                DrawHelper.drawVerticalCenteredText(matrix, Fonts.MEDIUM.getFont(TEXT_SIZE * scale), String.format("%.1f", health), healthX, (float)bgY, backgroundHeight, healthColor);

                float bracketRightX = healthX + healthWidth;
                DrawHelper.drawVerticalCenteredText(matrix, Fonts.MEDIUM.getFont(TEXT_SIZE * scale), "]", bracketRightX, (float)bgY, backgroundHeight, bracketColor);
            });
            
        } catch (Exception ignored) {}
    }

    private static float calculateScale(float distance) {
        if (distance <= MIN_DISTANCE) {
            return MAX_SCALE;
        } else if (distance >= MAX_DISTANCE) {
            return MIN_SCALE;
        } else {
            float t = (distance - MIN_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE);
            return MAX_SCALE - t * (MAX_SCALE - MIN_SCALE);
        }
    }

    private static Color getHealthColor(float health, float maxHealth) {
        float ratio = maxHealth > 0 ? Math.min(health / maxHealth, 1.0f) : 0f;

        Color low = new Color(255, 0, 0);
        Color mid = new Color(255, 165, 0);
        Color high = new Color(0, 202, 0);

        if (ratio <= 0.5f) {
            float t = ratio / 0.5f;
            return MathUtil.interpolateColor(low, mid, t);
        } else {
            float t = (ratio - 0.5f) / 0.5f;
            return MathUtil.interpolateColor(mid, high, t);
        }
    }
    
    private static boolean shouldRenderTag(PlayerEntity player) {
        return player != null && player != mc.player && player.isAlive();
    }
}