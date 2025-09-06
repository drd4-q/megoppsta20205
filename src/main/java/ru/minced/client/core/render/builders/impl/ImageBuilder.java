package ru.minced.client.core.render.builders.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import ru.minced.client.core.render.builders.AbstractBuilder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.renderers.impl.BuiltImage;

public final class ImageBuilder extends AbstractBuilder<BuiltImage> {

    private SizeState size;
    private QuadRadiusState radius;
    private QuadColorState color;
    private float smoothness;
    MatrixStack matrixStack;
    Identifier identifier;

    public ImageBuilder size(SizeState size) {
        this.size = size;
        return this;
    }

    public ImageBuilder radius(QuadRadiusState radius) {
        this.radius = radius;
        return this;
    }

    public ImageBuilder color(QuadColorState color) {
        this.color = color;
        return this;
    }

    public ImageBuilder smoothness(float smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    public ImageBuilder identifier(Identifier identifier1) {
        this.identifier = identifier1;
        return this;
    }

    public ImageBuilder matrix(MatrixStack stack) {
        this.matrixStack = stack;
        return this;
    }

    @Override
    protected BuiltImage _build() {
        return new BuiltImage(
                this.size,
                this.radius,
                this.color,
                this.smoothness,
                this.matrixStack,
                this.identifier
                );
    }

    @Override
    protected void reset() {
        this.size = SizeState.NONE;
        this.radius = QuadRadiusState.NO_ROUND;
        this.color = QuadColorState.WHITE;
        this.smoothness = 1.0f;
    }
}