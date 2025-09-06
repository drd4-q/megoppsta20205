package ru.minced.client.feature.ui.newmenu.component;

import ru.minced.client.feature.module.setting.impl.SliderSetting;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.other.animation.AnimationHelper;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.util.render.font.Fonts;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderComponent extends AbstractComponent {
    private final SliderSetting setting;
    @Getter private boolean dragging;
    private float animation = 0;

    public void mouseDragged(float x, float y, double mouseX, double mouseY, int button) {
        if (dragging) {
            updateSliderValue(x, () -> mouseX);
        }
    }

    public SliderComponent(SliderSetting setting) {
        this.setting = setting;
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        var mediumFont = Fonts.MEDIUM.getFont(12);

        DrawHelper.drawText(matrix, mediumFont, setting.getName(), x + 10, y + 10, new Color(127, 133, 172));

        float sliderY = y + 10 + mediumFont.getHeight() + 4;

        DrawHelper.drawRect(matrixStack, x + 10, sliderY, 233, 6, 5, new Color(21, 22, 29));

        this.animation = AnimationHelper.fast(animation, ((setting.getValue() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()) * 233), 10);

        DrawHelper.drawRect(matrixStack, x + 10, sliderY, animation, 6, 5, new Color(51, 56, 94));

        float circleX = x + 10 + animation;
        float circleY = sliderY + 3;
        DrawHelper.drawCircle(matrix, circleX - 6, circleY - 5, 10, 3, new Color(125, 136, 255));

        float currentValue = setting.getValue();
        String valueText = String.format("%.1f", currentValue);
        float textWidth = mediumFont.getWidth(valueText);
        DrawHelper.drawText(matrix, mediumFont, valueText, x + 10 + 233 - textWidth, y + 10, new Color(127, 133, 172));

        if (dragging) {
            updateSliderValue(x, () -> mc.mouse.getX() / mc.getWindow().getScaleFactor());
        }
    }
    
    private void updateSliderValue(float x, MouseXSupplier mouseXSupplier) {
        double mouseX = mouseXSupplier.getMouseX();
        float relativeX = (float) mouseX - (x + 10);

        if (relativeX >= 0 && relativeX <= 233) {
            float newPercentage = MathUtil.clamp(relativeX / 233f, 0f, 1f);
            
            float newValue = setting.getMinimum() + (setting.getMaximum() - setting.getMinimum()) * newPercentage;
            float snappedValue = round(newValue, setting.getIncrement());
            setting.setValue(MathUtil.clamp(snappedValue, setting.getMinimum(), setting.getMaximum()));
        }
    }
    
    @FunctionalInterface
    private interface MouseXSupplier {
        double getMouseX();
    }

    @Override
    public boolean mouseClicked(float x, float y, double mouseX, double mouseY, int button) {
        if (button == 0 && MathUtil.isHovered(x + 10, y + 10 + Fonts.MEDIUM.getFont(12).getHeight() + 4, 233, 6, mouseX, mouseY)) {
            dragging = true;
            updateSliderValue(x, () -> mouseX);
            return true;
        }
        return false;
    }
    
    private float round(float num, float increment) {
        float v = Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
    
    @Override
    public boolean mouseReleased(float x, float y, double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    @Override
    public float getHeight() {
        return 22;
    }
}
