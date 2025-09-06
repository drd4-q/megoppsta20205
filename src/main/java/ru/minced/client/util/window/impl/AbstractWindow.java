package ru.minced.client.util.window.impl;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.other.animation.Animation;
import ru.minced.client.util.other.animation.Direction;
import ru.minced.client.util.other.animation.impl.FadeAnimation;

public abstract class AbstractWindow extends AbstractComponent {
    private boolean dragging, draggable;
    private int dragX, dragY;

    @Getter
    private final Animation scaleAnimation = new FadeAnimation()
            .setValue(1)
            .setMs(100);

    public AbstractWindow() {
        scaleAnimation.setDirection(Direction.FORWARDS);
    }

    public AbstractWindow draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    @Override
    public AbstractWindow size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public AbstractWindow position(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0 && draggable) {
            dragging = true;
            dragX = (int) (x - mouseX);
            dragY = (int) (y - mouseY);
            return true;
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (dragging && draggable) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }

        float scale = scaleAnimation.getOutput().floatValue();
        MathUtil.scale(context.getMatrices(), x + (float) width / 2, y + (float) height / 2, scale, () -> {
            drawWindow(context, mouseX, mouseY, delta);
        });
    }

    protected abstract void drawWindow(DrawContext context, int mouseX, int mouseY, float delta);

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return true;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void startCloseAnimation() {
        scaleAnimation.setDirection(Direction.BACKWARDS);
    }

    public boolean isCloseAnimationFinished() {
        return scaleAnimation.isFinished(Direction.BACKWARDS);
    }
}