package ru.minced.client.core.draggable;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import ru.minced.client.util.IMinecraft;
import ru.minced.client.util.other.animation.Animation;
import ru.minced.client.util.other.animation.impl.FadeAnimation;
import ru.minced.client.util.render.ScaleUtil;

import java.awt.*;

import static ru.minced.client.util.other.animation.Direction.BACKWARDS;
import static ru.minced.client.util.other.animation.Direction.FORWARDS;

@Setter
@Getter
public abstract class AbstractDraggable implements Draggable, IMinecraft {
    private final Animation fadeAnimation = new FadeAnimation()
            .setMs(100)
            .setValue(1);
            
    private String name;
    private int x, y, width, height;
    private boolean dragging;
    private int dragX, dragY;
    private boolean closing = false;
    private float relativeX = -1;
    private float relativeY = -1;
    private int lastScale = -1;
    private int initialX, initialY;
    private boolean wasHovered = false;

    public AbstractDraggable(String name, int x, int y, int width, int height) {
        this.name = name;
        this.initialX = x;
        this.initialY = y;
        this.width = width;
        this.height = height;
        fadeAnimation.setDirection(FORWARDS);
    }

    private void initializeIfNeeded() {
        if (mc != null && mc.getWindow() != null && relativeX == -1 && relativeY == -1) {
            updateRelativePosition(initialX, initialY);
            x = initialX;
            y = initialY;
        }
    }

    private void updateRelativePosition(int screenX, int screenY) {
        if (mc != null && mc.getWindow() != null) {
            int screenWidth = mc.getWindow().getScaledWidth();
            int screenHeight = mc.getWindow().getScaledHeight();
            
            relativeX = (float) screenX / screenWidth;
            relativeY = (float) screenY / screenHeight;
        }
    }

    private void updateScreenPosition() {
        if (mc != null && mc.getWindow() != null) {
            int currentScale = (int) mc.getWindow().getScaleFactor();
            if (lastScale != currentScale || lastScale == -1) {
                int screenWidth = mc.getWindow().getScaledWidth();
                int screenHeight = mc.getWindow().getScaledHeight();

                if (relativeX != -1 && relativeY != -1) {
                    x = (int) (relativeX * screenWidth * currentScale);
                    y = (int) (relativeY * screenHeight * currentScale);

                    x = Math.max(0, Math.min(x, screenWidth * currentScale - width));
                    y = Math.max(0, Math.min(y, screenHeight * currentScale - height));
                }
                
                lastScale = currentScale;
            }
        }
    }

    public float animValue() {
        fadeAnimation.update();
        return fadeAnimation.getOutput().floatValue();
    }

    public Color applyAnimatedAlpha(Color color) {
        float alpha = animValue();
        return FadeAnimation.applyAlpha(color, alpha);
    }

    public Color applyAnimatedAlphaPreserveOriginal(Color color) {
        float alpha = animValue();
        float originalAlpha = color.getAlpha() / 255f;
        return new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            Math.max(0, Math.min(255, (int)(alpha * originalAlpha * 255)))
        );
    }

    public Color getAnimatedColor(int r, int g, int b) {
        float alpha = animValue();
        return new Color(r, g, b, (int)(255 * alpha));
    }

    public Color getAnimatedColor(int rgb) {
        Color color = new Color(rgb);
        return applyAnimatedAlpha(color);
    }

    public void update(float value) {
        fadeAnimation.setDirection(value > 0 ? FORWARDS : BACKWARDS);
    }

    @Override
    public boolean visible() {
        return false;
    }

    @Override
    public void tick(float delta) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int currentScale = (int) mc.getWindow().getScaleFactor();
        double guiX = mouseX * currentScale;
        double guiY = mouseY * currentScale;

        if (!isHovered(guiX, guiY)) {
            return false;
        }

        if (button == 0) {
            dragging = true;
            dragX = x - (int)guiX;
            dragY = y - (int)guiY;
            return true;
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        initializeIfNeeded();

        int currentScale = (int) mc.getWindow().getScaleFactor();
        double guiX = mouseX * currentScale;
        double guiY = mouseY * currentScale;
        
        if (dragging) {
            int screenWidth = mc.getWindow().getScaledWidth() * currentScale;
            int screenHeight = mc.getWindow().getScaledHeight() * currentScale;

            int newX = (int)guiX + dragX;
            int newY = (int)guiY + dragY;

            newX = Math.max(0, Math.min(newX, screenWidth - width));
            newY = Math.max(0, Math.min(newY, screenHeight - height));

            x = newX;
            y = newY;

            updateRelativePosition(ScaleUtil.scaleFromGuiX(newX), ScaleUtil.scaleFromGuiY(newY));
        } else {
            updateScreenPosition();
        }

        boolean currentlyHovered = isHovered(guiX, guiY);
        if (currentlyHovered != wasHovered && !dragging) {
            wasHovered = currentlyHovered;
            updateAnimation(currentlyHovered, dragging);
        }
    }

    public void renderWithFixedScale(DrawContext context, int mouseX, int mouseY, float delta) {
        render(context, mouseX, mouseY, delta);

        if (visible()) {
            ScaleUtil.fixScale(context, scale -> {
                context.getMatrices().push();
                context.getMatrices().translate(x, y, 0);
                context.getMatrices().pop();
            });
        }
    }

    public abstract void drawDraggable(DrawContext context);

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y && mouseY <= y + height;
    }

    public void updateAnimation(boolean hovered, boolean dragged) {
        update(hovered || dragged ? 1 : 0);
    }

    public void setBounds(int w, int h) {
        setWidth(w);
        setHeight(h);
    }

    public void hideDrag() {
        if (!closing) {
            closing = true;
            fadeAnimation.setDirection(BACKWARDS);
        }
    }

    public void visibleDrag() {
        if (closing) {
            closing = false;
            fadeAnimation.setDirection(FORWARDS);
        }
    }

    public boolean isCloseAnimationFinished() {
        return fadeAnimation.isFinished(BACKWARDS);
    }

    public Color textColor() {
        return changeAlpha(Color.WHITE, animValue());
    }

    public static Color changeAlpha(Color color, float alpha) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = (int) (alpha * 255);
        return new Color(r, g, b, a);
    }
}
