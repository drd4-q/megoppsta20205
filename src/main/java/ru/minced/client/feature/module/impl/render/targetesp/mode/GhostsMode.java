package ru.minced.client.feature.module.impl.render.targetesp.mode;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.math.MathUtil;

import java.awt.Color;

public class GhostsMode implements IMinecraft {
    public static void render(Entity target) {
        Camera camera = mc.gameRenderer.getCamera();

        double tPosX = MathUtil.interpolate(target.prevX, target.getX(), MathUtil.getTickDelta()) - camera.getPos().x;
        double tPosY = MathUtil.interpolate(target.prevY, target.getY(), MathUtil.getTickDelta()) - camera.getPos().y;
        double tPosZ = MathUtil.interpolate(target.prevZ, target.getZ(), MathUtil.getTickDelta()) - camera.getPos().z;
        float iAge = (float) MathUtil.interpolate(target.age - 1, target.age, MathUtil.getTickDelta());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderTexture(0, IMinecraft.ghost);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        assert mc.player != null;
        if (mc.player.canSee(target)) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
        } else {
            RenderSystem.disableDepthTest();
        }

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float ageMultiplier = iAge * 2.5f;
        Color themeColor = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());

        for (int j = 0; j < 3; j++) {
            float jOffset = j * 120;
            float jMultiplier = j + 1;

            for (int i = 0; i <= 14; i++) {
                float iFloat = (float) i;
                double radians = Math.toRadians(((iFloat / 1.5f + iAge) * 8 + jOffset) % 2880);
                double sinQuad = Math.sin(Math.toRadians(ageMultiplier + i * jMultiplier) * 3f) / 1.8f;

                float offset = iFloat / 14f;
                float scale = 0.3f;

                int color = applyOpacity(themeColor.getRGB(), offset);

                double ghostX = tPosX + Math.cos(radians) * target.getWidth();
                double ghostY = tPosY + 1 + sinQuad;
                double ghostZ = tPosZ + Math.sin(radians) * target.getWidth();

                MatrixStack matrices = new MatrixStack();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
                matrices.translate(ghostX, ghostY, ghostZ);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                Matrix4f matrix = matrices.peek().getPositionMatrix();

                buffer.vertex(matrix, -scale, scale, 0).texture(0f, 1f).color(color);
                buffer.vertex(matrix, scale, scale, 0).texture(1f, 1f).color(color);
                buffer.vertex(matrix, scale, -scale, 0).texture(1f, 0).color(color);
                buffer.vertex(matrix, -scale, -scale, 0).texture(0, 0).color(color);
            }
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        if (mc.player.canSee(target)) {
            RenderSystem.depthMask(true);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static int applyOpacity(int color_int, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        Color color = new Color(color_int);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity)).getRGB();
    }
} 