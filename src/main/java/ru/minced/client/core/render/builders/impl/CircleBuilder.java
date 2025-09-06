package ru.minced.client.core.render.builders.impl;

import ru.minced.client.core.render.builders.AbstractBuilder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.renderers.impl.BuiltCircle;

public final class CircleBuilder extends AbstractBuilder<BuiltCircle> {

    private SizeState size;
    private float radius;
    private QuadColorState color;
    private float smoothness;

    public CircleBuilder size(SizeState size) {
        this.size = size;
        return this;
    }

    public CircleBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    public CircleBuilder color(QuadColorState color) {
        this.color = color;
        return this;
    }

    public CircleBuilder smoothness(float smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    @Override
    protected BuiltCircle _build() {
        return new BuiltCircle(
                this.size,
                this.radius,
                this.color,
                this.smoothness
        );
    }

    @Override
    protected void reset() {
        this.size = SizeState.NONE;
        this.radius = 0.0f;
        this.color = QuadColorState.TRANSPARENT;
        this.smoothness = 1.0f;
    }
}