package ru.minced.client.core.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.providers.ResourceProvider;
import ru.minced.client.core.render.renderers.IRenderer;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

import java.util.Objects;

public record BuiltCircle(
        SizeState size,
        float radius,
        QuadColorState color,
        float smoothness
) implements IRenderer {

    private static final ShaderProgramKey CIRCLE_SHADER_KEY = new ShaderProgramKey(ResourceProvider.getShaderIdentifier("circle"),
            VertexFormats.POSITION_COLOR, Defines.EMPTY);

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        float width = this.size.width(), height = this.size.height();
        ShaderProgram shader = RenderSystem.setShader(CIRCLE_SHADER_KEY);
        assert shader != null;
        Objects.requireNonNull(shader.getUniform("Size")).set(width, height);
        Objects.requireNonNull(shader.getUniform("Radius")).set(this.radius);
        Objects.requireNonNull(shader.getUniform("Smoothness")).set(this.smoothness);

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, z).color(this.color.color1());
        builder.vertex(matrix, x, y + height, z).color(this.color.color2());
        builder.vertex(matrix, x + width, y + height, z).color(this.color.color3());
        builder.vertex(matrix, x + width, y, z).color(this.color.color4());

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}