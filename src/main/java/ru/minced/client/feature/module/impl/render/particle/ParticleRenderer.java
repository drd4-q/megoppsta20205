package ru.minced.client.feature.module.impl.render.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import ru.minced.client.core.Minced;
import ru.minced.client.util.IMinecraft;

import java.awt.*;
import java.util.List;

import static net.minecraft.client.render.VertexFormat.DrawMode.QUADS;

public class ParticleRenderer implements IMinecraft {
    
    public static void renderParticles(List<ParticleHandler.Particle> particles, MatrixStack matrices) {
        if (particles.isEmpty() || IMinecraft.nullCheck()) return;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        net.minecraft.client.render.Camera camera = mc.gameRenderer.getCamera();

        for (ParticleHandler.Particle particle : particles) {
            double x = particle.getX() - camera.getPos().x;
            double y = particle.getY() - camera.getPos().y;
            double z = particle.getZ() - camera.getPos().z;

            matrices.push();

            matrices.translate(x, y, z);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

            String textureName = particle.getTexture();
            Identifier textureId = Identifier.of("minced", "images/particles/" + textureName.toLowerCase() + ".png");

            float size = particle.getSize();

            matrices.scale(size, size, size);

            RenderSystem.setShaderTexture(0, textureId);

            Matrix4f matrix = matrices.peek().getPositionMatrix();

            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

            BufferBuilder buffer = Tessellator.getInstance().begin(QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            Color themeColor = new Color(Minced.getInstance().getThemeManager().getColorRate().getRGB());

            int alpha = Math.max(0, Math.min(255, (int)(particle.getAlpha() * 255)));
            int color = new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), alpha).getRGB();

            float halfSize = 0.5f;
            buffer.vertex(matrix, -halfSize, halfSize, 0).texture(0f, 0f).color(color);
            buffer.vertex(matrix, halfSize, halfSize, 0).texture(1f, 0f).color(color);
            buffer.vertex(matrix, halfSize, -halfSize, 0).texture(1f, 1f).color(color);
            buffer.vertex(matrix, -halfSize, -halfSize, 0).texture(0f, 1f).color(color);

            BufferRenderer.drawWithGlobalProgram(buffer.end());

            matrices.pop();
        }

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
} 