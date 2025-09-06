package ru.minced.client.core.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.providers.ResourceProvider;
import ru.minced.client.core.render.renderers.IRenderer;

import java.util.Objects;

import static net.minecraft.client.render.VertexFormat.DrawMode.QUADS;
import static net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR;


public record BuiltImage(
        SizeState size,
        QuadRadiusState radius,
        QuadColorState color,
        float smoothness,
        MatrixStack matrixStack,
        Identifier identifier
) implements IRenderer, IMinecraft {

    private static final ShaderProgramKey TEXTURE_SHADER_KEY = new ShaderProgramKey(ResourceProvider.getShaderIdentifier("texture"),
            VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        int prevMinFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        int prevMagFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);

        float[] prevShaderColor = RenderSystem.getShaderColor();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableCull();

        RenderSystem.setShaderTexture(0, identifier);

        int textureId = mc.getTextureManager().getTexture(identifier).getGlId();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        float scaledWidth = this.size.width();
        float scaledHeight = this.size.height();

        ShaderProgram shader = RenderSystem.setShader(TEXTURE_SHADER_KEY);
        assert shader != null;
        Objects.requireNonNull(shader.getUniform("Size")).set(scaledWidth, scaledHeight);
        Objects.requireNonNull(shader.getUniform("Radius")).set(
                this.radius.radius1(),
                this.radius.radius2(),
                this.radius.radius3(),
                this.radius.radius4()
        );
        Objects.requireNonNull(shader.getUniform("Smoothness")).set(this.smoothness);

        BufferBuilder builder = Tessellator.getInstance().begin(QUADS, POSITION_TEXTURE_COLOR);
        {
            builder.vertex(matrix, x, y + scaledHeight, 0).texture(0, 1).color(this.color.color1());
            builder.vertex(matrix, x + scaledWidth, y + scaledHeight, 0).texture(1, 1).color(this.color.color2());
            builder.vertex(matrix, x + scaledWidth, y, 0).texture(1, 0).color(this.color.color3());
            builder.vertex(matrix, x, y, 0).texture(0, 0).color(this.color.color4());
        }

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, prevMinFilter);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, prevMagFilter);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(prevShaderColor[0], prevShaderColor[1], prevShaderColor[2], prevShaderColor[3]);
    }
}