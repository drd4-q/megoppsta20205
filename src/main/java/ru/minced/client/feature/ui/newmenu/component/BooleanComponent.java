package ru.minced.client.feature.ui.newmenu.component;

import ru.minced.client.feature.module.setting.impl.BooleanSetting;
import ru.minced.client.util.animation.Animation;
import ru.minced.client.util.animation.util.Easings;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.Color;

public class BooleanComponent extends AbstractComponent {
    private final BooleanSetting setting;
    private final Animation toggleAnimation = new Animation();
    private final Animation colorAnimation = new Animation();

    public BooleanComponent(BooleanSetting setting) {
        this.setting = setting;
        this.toggleAnimation.setValue(setting.isState() ? 1 : 0);
        this.colorAnimation.setValue(setting.isState() ? 1 : 0);

        if (setting.isState()) {
            this.toggleAnimation.animate(1, 0, Easings.CUBIC_BOTH);
            this.colorAnimation.animate(1, 0, Easings.CUBIC_BOTH);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        var mediumFont = Fonts.MEDIUM.getFont(12);

        toggleAnimation.update();
        colorAnimation.update();

        DrawHelper.drawText(matrix, mediumFont, setting.getName(), x + 10, y + 10, new Color(127, 133, 172));

        float toggleRectX = x + 255 - 10 - 28;
        float circleY = y + 10 + 1;

        Color bgColor = MathUtil.interpolateColor(
                new Color(21, 22, 29),
                new Color(51, 56, 94),
                (float) colorAnimation.getValue()
        );

        Color circleColor = MathUtil.interpolateColor(
                new Color(36, 37, 48),
                new Color(125, 136, 255),
                (float) colorAnimation.getValue()
        );

        DrawHelper.drawRect(matrixStack, toggleRectX, y + 10, 28, 16, 9, bgColor);

        float startX = toggleRectX + 1;
        float endX = toggleRectX + 28 - 14 - 1;
        float circleX = startX + (float) ((endX - startX) * toggleAnimation.getValue());

        DrawHelper.drawCircle(matrix, circleX, circleY, 14, 5, circleColor);
    }

    @Override
    public boolean mouseClicked(float x, float y, double mouseX, double mouseY, int button) {
        float rectHeight = getHeight();

        if (button == 0 && MathUtil.isHovered(x, y + 10, 255, rectHeight, mouseX, mouseY)) {
            setting.set(!setting.isState());

            if (setting.isState()) {
                toggleAnimation.animate(1, 0.2, Easings.CUBIC_BOTH);
                colorAnimation.animate(1, 0.2, Easings.CUBIC_BOTH);
            } else {
                toggleAnimation.animate(0, 0.2, Easings.CUBIC_BOTH);
                colorAnimation.animate(0, 0.2, Easings.CUBIC_BOTH);
            }

            return true;
        }
        return false;
    }

    @Override
    public float getHeight() {
        return 16;
    }
}