package ru.minced.client.feature.ui.menu.components.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import ru.minced.client.feature.module.setting.Setting;
import ru.minced.client.feature.module.setting.impl.RangeSetting;
import ru.minced.client.feature.ui.menu.components.SettingComponent;
import ru.minced.client.util.font.Fonts;
import ru.minced.client.util.math.MathUtil;
import ru.minced.client.util.other.animation.AnimationHelper;
import ru.minced.client.util.render.DrawHelper;
import ru.minced.client.core.render.builders.Builder;
import ru.minced.client.core.render.builders.states.QuadColorState;
import ru.minced.client.core.render.builders.states.QuadRadiusState;
import ru.minced.client.core.render.builders.states.SizeState;
import ru.minced.client.core.render.renderers.impl.BuiltRectangle;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RangeSettingComponent extends SettingComponent {
    private RangeSetting setting;
    private boolean dragMin = false;
    private boolean dragMax = false;
    private float x = 0, y = 0, animMinX = 0, animMaxX = 0;
    private float minAnimation = 0, maxAnimation = 0;

    public RangeSettingComponent(Setting s) {
        super(s);
        setting = (RangeSetting) s;
    }

    public void render(DrawContext context, float x, float y, int mouseX, int mouseY, float partialTicks) {
        this.y = y;
        this.x = x;
        
        if (context != null) {
            MatrixStack matrixStack = context.getMatrices();
            Fonts.getSize(14).drawString(matrixStack, setting.getName(), x, y + 2, 0xFFD4D6E1);
            String valueText = String.format("%.1f - %.1f", setting.getMinValue(), setting.getMaxValue());
            Fonts.getSize(12).drawString(matrixStack, valueText, x + 66, y + 8.5f, 0xFFD4D6E1);

            DrawHelper.drawRect(matrixStack, x + 3, y + 9, 60, 4, 1.0F, new Color(12, 12, 12, 255));

            this.minAnimation = AnimationHelper.fast(minAnimation,
                    ((setting.getMinValue() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()) * 60), 10);
            this.maxAnimation = AnimationHelper.fast(maxAnimation, 
                    ((setting.getMaxValue() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()) * 60), 10);

            DrawHelper.drawRect(matrixStack, x + 3 + minAnimation, y + 9, maxAnimation - minAnimation, 4, 1.0F, new Color(42, 42, 42, 216));

            BuiltRectangle minRect = Builder.rectangle()
                    .size(new SizeState(6.5F, 6.5F))
                    .color(new QuadColorState(new Color(0xFFFFFF)))
                    .radius(new QuadRadiusState(2.3F))
                    .smoothness(1.0f)
                    .build();
            minRect.render(context.getMatrices().peek().getPositionMatrix(), x + 3 + minAnimation, y + 7.5f);

            BuiltRectangle maxRect = Builder.rectangle()
                    .size(new SizeState(6.5F, 6.5F))
                    .color(new QuadColorState(new Color(0xFFFFFF)))
                    .radius(new QuadRadiusState(2.3F))
                    .smoothness(1.0f)
                    .build();
            maxRect.render(context.getMatrices().peek().getPositionMatrix(), x + 3 + maxAnimation, y + 7.5f);
        }

        if (dragMin) {
            float minValue = (float) MathHelper.clamp(round((float) ((mouseX - x - 3) / 60 * (setting.getMaximum() - setting.getMinimum()) + setting.getMinimum()), setting.getIncrement()), 
                    setting.getMinimum(), setting.getMaxValue());
            this.setting.setMinValue(minValue);
        }
        
        if (dragMax) {
            float maxValue = (float) MathHelper.clamp(round((float) ((mouseX - x - 3) / 60 * (setting.getMaximum() - setting.getMinimum()) + setting.getMinimum()), setting.getIncrement()), 
                    setting.getMinValue(), setting.getMaximum());
            this.setting.setMaxValue(maxValue);
        }
    }

    public static double round(double num, double increment) {
        double v = Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void mouseClicked(double mouseX, double mouseY, double mouseButton) {
        if (mouseButton == 0) {
            float minPointX = x + 3 + minAnimation;
            float maxPointX = x + 3 + maxAnimation;
            
            boolean onMin = MathUtil.isHovered(minPointX - 3, y + 4, 7, 7, mouseX, mouseY);
            boolean onMax = MathUtil.isHovered(maxPointX - 3, y + 4, 7, 7, mouseX, mouseY);

            if (onMin && onMax) {
                if (Math.abs(mouseX - minPointX) < Math.abs(mouseX - maxPointX)) {
                    dragMin = true;
                } else {
                    dragMax = true;
                }
            } else if (onMin) {
                dragMin = true;
            } else if (onMax) {
                dragMax = true;
            } else if (MathUtil.isHovered(x + 3, y + 9, 60, 4, mouseX, mouseY)) {
                if (Math.abs(mouseX - minPointX) < Math.abs(mouseX - maxPointX)) {
                    dragMin = true;
                } else {
                    dragMax = true;
                }

                if (dragMin) {
                    float minValue = (float) MathHelper.clamp(round((float) ((mouseX - x - 3) / 60 * (setting.getMaximum() - setting.getMinimum()) + setting.getMinimum()), setting.getIncrement()), 
                            setting.getMinimum(), setting.getMaxValue());
                    this.setting.setMinValue(minValue);
                } else if (dragMax) {
                    float maxValue = (float) MathHelper.clamp(round((float) ((mouseX - x - 3) / 60 * (setting.getMaximum() - setting.getMinimum()) + setting.getMinimum()), setting.getIncrement()), 
                            setting.getMinValue(), setting.getMaximum());
                    this.setting.setMaxValue(maxValue);
                }
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, double button) {
        dragMin = false;
        dragMax = false;
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    public float getFullHeight() {
        return 22f;
    }
} 