package ru.minced.client.core.render.renderers.impl;

import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.providers.ResourceProvider;
import ru.minced.client.core.render.renderers.IRenderer;

public record BuiltOutline(
        SizeState size,
        QuadRadiusState radius,
        QuadColorState color,
        float thickness,
        float smoothness
    ) implements IRenderer {

    private static final ShaderProgramKey OUTLINE_SHADER_KEY = new ShaderProgramKey(ResourceProvider.getShaderIdentifier("outline"),
        VertexFormats.POSITION_COLOR, Defines.EMPTY);

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        float width = this.size.width(), height = this.size.height();
        ShaderProgram shader = RenderSystem.setShader(OUTLINE_SHADER_KEY);
        shader.getUniform("Size").set(width, height);
        shader.getUniform("Radius").set(this.radius.radius1(), this.radius.radius2(),
            this.radius.radius3(), this.radius.radius4());
        shader.getUniform("Thickness").set(this.thickness);
        shader.getUniform("Smoothness").set(this.smoothness);

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, z).color(this.color.color1());
        builder.vertex(matrix, x, y + height, z).color(this.color.color2());
        builder.vertex(matrix, x + width, y + height, z).color(this.color.color3());
        builder.vertex(matrix, x + width, y, z).color(this.color.color4());

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}